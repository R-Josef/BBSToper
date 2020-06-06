package moe.feo.bbstoper;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeoutException;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Util {

	private static BukkitTask autorewardtask;
	private static ArrayList<Integer> runningtaskidlist = new ArrayList<Integer>();

	public static void startAutoReward() {// 自动奖励的方法
		if (autorewardtask != null) {// 任务对象不为空
			boolean taskcancelled;// 是否已经取消
			try {
				taskcancelled = autorewardtask.isCancelled();
			} catch (NoSuchMethodError e) {// 1.7.10还没有这个方法
				taskcancelled = false;// 默认就当这个任务没有取消
			}
			if (!taskcancelled) {// 如果任务还被取消
				autorewardtask.cancel();// 将之前的任务取消
			}
		}
		int period = Option.REWARD_AUTO.getInt() * 20;
		if (period > 0) {
			autorewardtask = new BukkitRunnable() {// 自动奖励，异步执行
				@Override
				public void run() {
					addRunningTaskID(this.getTaskId());
					task();
					removeRunningTaskID(this.getTaskId());
				}

				public void task() {
					Crawler crawler = new Crawler();
					if (!crawler.visible)
						return;
					crawler.activeReward();
				}
			}.runTaskTimerAsynchronously(BBSToper.getInstance(), 0, period);
		}
	}

	public static void waitForAllTask() {// 此方法会阻塞直到所有此插件创建的线程结束
		int count = 0;
		try {
			while (!runningtaskidlist.isEmpty()) {// 当list非空，阻塞线程100毫秒后再判断一次
				if (count > 30000) {// 超过30秒没有关闭就算超时
					throw new TimeoutException();
				}
				Thread.sleep(100);
				count = count + 100;
			}
		} catch (InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	public static void addRunningTaskID(int i) {
		if (!runningtaskidlist.contains(i))
			runningtaskidlist.add(i);
	}

	public static void removeRunningTaskID(int i) {
		if (runningtaskidlist.contains(i))
			runningtaskidlist.remove((Integer) i);
	}
	
	public static String getExtraReward(Crawler crawler) {// 获取会获得的额外奖励(可为空)
		boolean incentive = false;// 是否符合激励奖励条件
		boolean offday = false;// 是否符合休息日奖励条件
		Calendar current = Calendar.getInstance();// 当前时间
		Calendar lastpost = Calendar.getInstance();// 上一次顶贴的时间
		if (crawler.Time.size() > 0) {// 如果有顶贴记录的话
			SimpleDateFormat bbsformat = new SimpleDateFormat("yyyy-M-d HH:mm");// mcbbs的日期格式
			Date lastpostdate = null;
			try {
				lastpostdate = bbsformat.parse(crawler.Time.get(0));
			} catch (ParseException e) {
				e.printStackTrace();
			}
			lastpost.setTime(lastpostdate);
		}
		if (Reward.canIncentiveReward(current, lastpost)) {
			incentive = true;
		}
		if (Reward.canOffDayReward(current)) {
			offday = true;
		}
		String extra = null;
		if (incentive) {
			// 如果休息日奖励也达成了, 并且激励奖励和休息日奖励都不是额外奖励, 不会发放激励奖励(只会发放休息日奖励)
			if (!(offday && Option.REWARD_INCENTIVEREWARD_EXTRA.getBoolean() == false
					&& Option.REWARD_OFFDAYREWARD_EXTRA.getBoolean() == false)) {
				extra = new String(Message.GUI_INCENTIVEREWARDS.getString());
			}
		}
		if (offday) {
			if (extra == null) {
				extra = new String(Message.GUI_OFFDAYREWARDS.getString());
			} else {
				extra = extra + "+" + Message.GUI_OFFDAYREWARDS.getString();
			}
		}
		return extra;
	}

}
