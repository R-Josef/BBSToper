package moe.feo.bbstoper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Reward {
	private Player player; // 发放奖励的对象
	private Crawler crawler; // 一个爬虫对象
	private int index; // 要发放奖励的那条记录的序号

	// current指需要判断的时间, before指上一个顶贴的时间
	public static boolean canIncentiveReward(Calendar current, Calendar before) {
		boolean result = false;
		if (Option.REWARD_INCENTIVEREWARD_ENABLE.getBoolean() == true) {// 开启了激励奖励
			Calendar copyofcurrent = (Calendar) before.clone();// 一个上次顶贴时间的副本
			copyofcurrent.add(Calendar.MINUTE, Option.REWARD_INCENTIVEREWARD_PERIOD.getInt());// 加上设定好的激励时间
			if (copyofcurrent.before(current)) {// 如果这个时间已经处于"当前领奖的记录"之前
				result = true;
			}
		}
		return result;
	}

	// current指需要判断的时间
	public static boolean canOffDayReward(Calendar current) {
		boolean result = false;
		if (Option.REWARD_OFFDAYREWARD_ENABLE.getBoolean() == true) {// 开启了休息日奖励
			for (String day : Option.REWARD_OFFDAYREWARD_OFFDAYS.getStringList()) {
				Pattern upcasepattern = Pattern.compile("^[A-Z]+$");// 全大写英文字符串
				Pattern datepattern = Pattern.compile("^\\d{2}-\\d{2}$");// 00-00格式的字符串
				if (upcasepattern.matcher(day).matches()) {// 如果是全大写英文字符
					Class<?> clazz = Calendar.class;
					int dayofweek = 0;
					try {
						// https://docs.oracle.com/javase/8/docs/api/java/util/Calendar.html
						// 根据Calendar终态静态变量将字符串转换成星期
						dayofweek = clazz.getField(day).getInt(null);

					} catch (IllegalArgumentException e) {// 非法参数
						e.printStackTrace();
					} catch (IllegalAccessException e) {// 非法访问
						e.printStackTrace();
					} catch (NoSuchFieldException e) {// 没有这个属性
						e.printStackTrace();
					} catch (SecurityException e) {// 安全
						e.printStackTrace();
					}
					int dayofweekcurrent = current.get(Calendar.DAY_OF_WEEK);// 当前领奖的记录是星期几
					if (dayofweekcurrent == dayofweek) {// 如果当前就是设定的日子
						result = true;
					}
				} else if (datepattern.matcher(day).matches()) {// 如果是00-00这种字符串
					SimpleDateFormat offdayformat = new SimpleDateFormat("MM-dd");
					Calendar offdaycalendar = Calendar.getInstance();// 设定的假日日期(1970年的...)
					try {
						Date offdaydate = offdayformat.parse(day);
						offdaycalendar.setTime(offdaydate);
					} catch (ParseException e) {
						e.printStackTrace();
					}
					// 如果当前领奖的记录的月份和日号与设定值一样
					if (current.get(Calendar.MONTH) == offdaycalendar.get(Calendar.MONTH)
							&& current.get(Calendar.DAY_OF_MONTH) == offdaycalendar.get(Calendar.DAY_OF_MONTH)) {
						result = true;
					}
				}
			}
		}
		return result;
	}

	public boolean isIntervalTooShort(Calendar thispost, int index) {// 判断顶贴间隔是否过短
		SimpleDateFormat bbsformat = new SimpleDateFormat("yyyy-M-d HH:mm");// mcbbs的日期格式
		Date thispostdate = thispost.getTime();
		int x = index + 1;// 从下一个顶贴记录开始遍历
		while (true) {
			Date lastdate = null;
			// 当记录已经全部遍历完，就不用再继续了
			if (x >= crawler.Time.size()) {
				break;
			}
			try {
				lastdate = bbsformat.parse(crawler.Time.get(x));
			} catch (ParseException e) {
				e.printStackTrace();
			} // 遍历再上一次的顶贴时间
			// 当这次遍历到的时间减去当前领奖的时间已经大于设定的时间了，就不用继续遍历了
			if ((thispostdate.getTime() - lastdate.getTime()) / (1000 * 60) > Option.REWARD_INTERVAL.getInt()) {
				break;
			}
			// 当遍历到和这次领奖记录的bbsid一样的记录，说明这个人顶贴间隔小于设定时间了
			if (crawler.ID.get(x).equals(crawler.ID.get(index))) {
				return true;
			}
			x++;
		}
		return false;
	}

	public Reward(Player player, Crawler crawler, int index) {
		this.player = player;
		this.crawler = crawler;
		this.index = index;
	}

	public void award() {
		List<String> cmds = new ArrayList<String>();
		boolean incentive = false;// 是否符合激励奖励条件
		boolean offday = false;// 是否符合休息日奖励条件
		boolean normal = true;// 是否发放普通奖励
		SimpleDateFormat bbsformat = new SimpleDateFormat("yyyy-M-d HH:mm");// mcbbs的日期格式
		Calendar thispost = Calendar.getInstance();// "当前领奖的记录"的时间
		try {
			Date thipostdate = bbsformat.parse(crawler.Time.get(index));
			thispost.setTime(thipostdate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		// 如果顶贴间隔短于设定值则不进行操作
		if (Option.REWARD_INTERVAL.getInt() > 0 && isIntervalTooShort(thispost, index)) {
			player.sendMessage(Message.PREFIX.getString() + Message.INTERVALTOOSHORT.getString()
			.replaceAll("%TIME%", crawler.Time.get(index)).replaceAll("%INTERVAL%", Option.REWARD_INTERVAL.getString()));
			return;
		}
		Calendar lastpost = Calendar.getInstance();// 上一次顶贴的时间
		if (crawler.Time.size() > index + 1) {// 如果有这一次之前的顶贴记录
			try {
				Date lastpostdate = bbsformat.parse(crawler.Time.get(index + 1));// 同理这里应该获取之前那一条记录
				lastpost.setTime(lastpostdate);
			} catch (ParseException e) {
				e.printStackTrace();
			}
		} else {
			lastpost.setTime(new Date(0));// 没人顶过贴, 将时间设置为1970
		}
		if (canIncentiveReward(thispost, lastpost)) {
			incentive = true;
		}
		if (canOffDayReward(thispost)) {
			offday = true;
		}
		String extra = null;
		if (incentive) {// 如果激励奖励条件达成
			// 如果休息日奖励也达成了, 并且激励奖励和休息日奖励都不是额外奖励, 不会发放激励奖励(只会发放休息日奖励)
			if (!(offday && Option.REWARD_INCENTIVEREWARD_EXTRA.getBoolean() == false
					&& Option.REWARD_OFFDAYREWARD_EXTRA.getBoolean() == false)) {
				cmds.addAll(Option.REWARD_INCENTIVEREWARD_COMMANDS.getStringList());
				extra = new String(Message.GUI_INCENTIVEREWARDS.getString());
			}
			if (Option.REWARD_INCENTIVEREWARD_EXTRA.getBoolean() == false) {
				// 如果这不是额外奖励, 则普通奖励不会发放
				normal = false;
			}
		}
		if (offday) {// 如果休息日奖励条件达成
			cmds.addAll(Option.REWARD_OFFDAYREWARD_COMMANDS.getStringList());
			if (extra == null) {
				extra = new String(Message.GUI_OFFDAYREWARDS.getString());
			} else {
				extra = extra + "+" + Message.GUI_OFFDAYREWARDS.getString();
			}
			if (Option.REWARD_OFFDAYREWARD_EXTRA.getBoolean() == false) {
				// 如果这不是额外奖励, 则普通奖励不会发放
				normal = false;
			}
		}
		if (normal) {
			cmds.addAll(Option.REWARD_COMMANDS.getStringList());
		}
		// 让主线程执行
		Bukkit.getScheduler().runTask(BBSToper.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (String cmd : cmds) {
					cmd = cmd.replaceAll("%PLAYER%", player.getName());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
				}
			}
		});
		// 给玩家发个消息表示祝贺
		player.sendMessage(
				Message.PREFIX.getString() + Message.REWARD.getString().replaceAll("%TIME%", crawler.Time.get(index)));
		if (extra != null) {
			player.sendMessage(
					Message.PREFIX.getString() + Message.EXTRAREWARD.getString().replaceAll("%EXTRA%", extra));
		}
	}

	public void testAward(String type) {
		List<String> cmds = new ArrayList<>();
		switch (type) {
			case "NORMAL": {
				cmds.addAll(Option.REWARD_COMMANDS.getStringList());
				break;
			}
			case "INCENTIVE": {
				cmds.addAll(Option.REWARD_INCENTIVEREWARD_COMMANDS.getStringList());
				break;
			}
			case "OFFDAY": {
				cmds.addAll(Option.REWARD_OFFDAYREWARD_COMMANDS.getStringList());
				break;
			}
		}
		// 让主线程执行
		Bukkit.getScheduler().runTask(BBSToper.getInstance(), new Runnable() {
			@Override
			public void run() {
				for (String cmd : cmds) {
					cmd = cmd.replaceAll("%PLAYER%", player.getName());
					Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
				}
			}
		});
	}
}
