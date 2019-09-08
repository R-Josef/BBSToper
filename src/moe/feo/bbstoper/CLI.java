package moe.feo.bbstoper;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class CLI implements TabExecutor {

	private SQLer sql;
	private Map<String, String> cache = new HashMap<>();// 这个map是为了暂存玩家的绑定信息的

	private static CLI cli = new CLI();

	private CLI() {

	}

	public static CLI getInstance() {
		return cli;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command cmd, String label, String[] args) {
		if (args.length == 1) {
			List<String> list = new ArrayList<String>();
			if (sender.hasPermission("bbstoper.reward")) {
				list.add("reward");
			}
			if (sender.hasPermission("bbstoper.binding")) {
				list.add("binding");
			}
			if (sender.hasPermission("bbstoper.list")) {
				list.add("list");
			}
			if (sender.hasPermission("bbstoper.check")) {
				list.add("check");
			}
			if (sender.hasPermission("bbstoper.delete")) {
				list.add("delete");
			}
			if (sender.hasPermission("bbstoper.reload")) {
				list.add("reload");
			}
			return list;
		}
		if (args.length == 2) {
			if (args[0].equalsIgnoreCase("check")) {
				List<String> list = new ArrayList<String>();
				if (sender.hasPermission("bbstoper.check")) {
					list.add("bbsid");
					list.add("player");
				}
				return list;
			}
		}
		return null;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (args.length == 0) {// 没有带参数
					sender.sendMessage(Message.PREFIX.getString() + Message.HELP_TITLE.getString());
					if (sender.hasPermission("bbstoper.reward")) {
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_REWARD.getString());
					}
					if (sender.hasPermission("bbstoper.binding")) {
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_BINDING.getString());
					}
					if (sender.hasPermission("bbstoper.list")) {
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_LIST.getString());
					}
					if (sender.hasPermission("bbstoper.check")) {
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_CHECK.getString());
					}
					if (sender.hasPermission("bbstoper.delete")) {
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_DELETE.getString());
					}
					if (sender.hasPermission("bbstoper.reload")) {
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_RELOAD.getString());
					}
					
					
					
					
					return;
				}
				Crawler crawler;
				switch (args[0].toLowerCase()) {
				case "binding": {
					if (!(sender.hasPermission("bbstoper.binding"))) {
						sender.sendMessage(Message.PREFIX.getString() + Message.NOPERMISSION.getString());
						return;
					}
					if (args.length == 2) {
						Player player = Bukkit.getPlayer(sender.getName());
						String uuid = player.getUniqueId().toString();
						Poster poster = sql.getPoster(uuid);
						boolean isrecording = true;
						if (poster != null) {
							long cd = System.currentTimeMillis() - poster.getBinddate();// 已经过了的cd
							long settedcd = Option.MCBBS_CHANGEIDCOOLDOWN.getInt() * (long) 86400000;// 设置的cd
							if (cd < settedcd) {// 如果还在cd那么直接return;
								Long leftcd = settedcd - cd;//剩下的cd
								Long leftcdtodays = leftcd/86400000;
								sender.sendMessage(Message.PREFIX.getString()
										+ Message.ONCOOLDOWN.getString().replaceAll("%COOLDOWN%", leftcdtodays.toString()));
								return;
							}
						} else {
							poster = new Poster();
							isrecording = false;
						}
						String ownersuuid = sql.bbsNameCheck(args[1]);
						if (ownersuuid == null) {// 没有人绑定过这个论坛id
							if (cache.get(uuid) != null && cache.get(uuid).equals(args[1])) {
								poster.setUuid(uuid);
								poster.setName(sender.getName());
								poster.setBbsname(args[1]);
								poster.setBinddate(System.currentTimeMillis());
								if (isrecording) {
									sql.updatePoster(poster);
								} else {
									sql.addPoster(poster);
								}
								cache.put(uuid, null);
								sender.sendMessage(Message.PREFIX.getString() + Message.BINDINGSUCCESS.getString());
							} else if (cache.get(uuid) == null) {
								cache.put(uuid, args[1]);
								sender.sendMessage(Message.PREFIX.getString() + Message.REPEAT.getString());
							} else {
								sender.sendMessage(Message.PREFIX.getString() + Message.NOTSAME.getString());
								cache.put(uuid, null);
							}
							return;
						} else if (ownersuuid.equals(uuid)) {// 自己绑定了这个论坛id
							sender.sendMessage(Message.PREFIX.getString() + Message.OWNSAMEBIND.getString());
							return;
						} else {
							sender.sendMessage(Message.PREFIX.getString() + Message.SAMEBIND.getString());
							return;
						}
					} else {
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_BINDING.getString());
						return;
					}
				}
				case "reward": {
					if (!(sender.hasPermission("bbstoper.reward"))) {
						sender.sendMessage(Message.PREFIX.getString() + Message.NOPERMISSION.getString());
						return;
					}
					Player player = Bukkit.getPlayer(sender.getName());
					String uuid = player.getUniqueId().toString();
					Poster poster = sql.getPoster(uuid);
					if (poster == null) {// 没有绑定
						sender.sendMessage(Message.PREFIX.getString() + Message.NOTBOUND.getString());
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_BINDING.getString());
						return;
					}
					crawler = new Crawler();
					crawler.kickExpiredData();// 剔除过期数据
					String bbsname = poster.getBbsname();
					List<String> cache = new ArrayList<String>();// 这个缓存是用来判断玩家的顶贴粒度是否小于一分钟
					boolean issucceed = false;
					boolean isovertime = false;
					boolean iswaitamin = false;
					boolean havepost = false;
					for (int i = 0; i < crawler.ID.size(); i++) {// 对ID进行遍历
						if (crawler.ID.get(i).equalsIgnoreCase(bbsname)) {// 如果ID等于poster的论坛名字
							List<String> topstates = poster.getTopStates();
							if (cache.contains(crawler.Time.get(i))) {// 判断玩家的顶贴粒度是否小于一分钟了
								if (!topstates.contains(crawler.Time.get(i))) {// 如果缓存里面包含，数据库里面不包含说明这个玩家是刚刚才来领取奖励
									iswaitamin = true;// 我们这里只会提醒玩家一次
								}
							}
							if (!topstates.contains(crawler.Time.get(i))) {
								havepost = true;
								String datenow = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
								if (!datenow.equals(poster.getRewardbefore())) {
									poster.setRewardbefore(datenow);
									poster.setRewardtime(0);
								}
								if (poster.getRewardtime() < Option.REWARD_TIMES.getInt()) {
									Bukkit.getScheduler().runTask(BBSToper.getInstance(), new Runnable() {
										@Override
										public void run() {
											for (int x = 0; x < Option.REWARD_COMMANDS.getStringList().size(); x++) {
												String cmd = Option.REWARD_COMMANDS.getStringList().get(x)
														.replaceAll("%PLAYER%", sender.getName());
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
											}
										}
									});
									sender.sendMessage(Message.PREFIX.getString() + Message.REWARD.getString().replaceAll("%TIME%", crawler.Time.get(i)));
									sql.addTopState(poster.getBbsname(), crawler.Time.get(i));
									poster.setRewardtime(1);
									issucceed = true;
								} else {
									isovertime = true;
								}
							}
						}
					}
					sql.updatePoster(poster);
					if (issucceed) {
						sender.sendMessage(Message.PREFIX.getString() + Message.REWARDGIVED.getString());
					}
					if (isovertime) {
						Integer rewardtimes = Option.REWARD_TIMES.getInt();
						sender.sendMessage(Message.PREFIX.getString()
								+ Message.OVERTIME.getString().replaceAll("%REWARDTIMES%", rewardtimes.toString()));
					}
					if (iswaitamin) {
						sender.sendMessage(Message.PREFIX.getString() + Message.WAITAMIN.getString());
					}
					if (!havepost) {
						sender.sendMessage(Message.PREFIX.getString() + Message.NOPOST.getString());
					}
					break;
				}

				case "list": {
					if (!(sender.hasPermission("bbstoper.list"))) {
						sender.sendMessage(Message.PREFIX.getString() + Message.NOPERMISSION.getString());
						return;
					}
					int page = 1;
					if (args.length == 2) {
						for (int i = 0; i < args[1].length(); i++) {// 判断参数是否为数字
							if (!Character.isDigit(args[1].charAt(i))) {
								sender.sendMessage(Message.PREFIX.getString() + Message.INVAILD.getString());
								sender.sendMessage(Message.PREFIX.getString() + Message.HELP_LIST.getString());
								return;
							}
						}
						page = Integer.parseInt(args[1]);
					} else if (args.length > 2) {
						sender.sendMessage(Message.PREFIX.getString() + Message.INVAILD.getString());
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_LIST.getString());
						return;
					}
					crawler = new Crawler();
					int totalpage = (int) Math.ceil((double) crawler.ID.size() / Option.MCBBS_PAGESIZE.getInt());
					if (page > totalpage) {
						sender.sendMessage(Message.PREFIX.getString() + Message.OVERPAGE.getString());
						return;
					}
					List<String> msglist = new ArrayList<String>();
					msglist.add(Message.PREFIX.getString() + Message.POSTERNUM.getString() + ":" + crawler.ID.size());
					for (int i = (page - 1) * Option.MCBBS_PAGESIZE.getInt(); i < page
							* Option.MCBBS_PAGESIZE.getInt(); i++) {
						if (i >= crawler.ID.size())
							break;// 当i不再小于顶贴人数，该停了
						msglist.add(Message.POSTERID.getString() + ":" + crawler.ID.get(i) + " "
								+ Message.POSTERTIME.getString() + ":" + crawler.Time.get(i));
					}
					if (msglist.size() == 1)
						msglist.add(Message.NOPOSTER.getString());
					String pageinfo = Message.PAGEINFO.getString();
					pageinfo = pageinfo.replaceAll("%PAGE%", Integer.toString(page));
					pageinfo = pageinfo.replaceAll("%TOTALPAGE%", Integer.toString(totalpage));
					msglist.add(Message.PREFIX.getString() + pageinfo);
					for (int i = 0; i < msglist.size(); i++) {
						sender.sendMessage(msglist.get(i));
					}
					break;
				}
				case "reload": {
					if (!(sender.hasPermission("bbstoper.reload"))) {
						sender.sendMessage(Message.PREFIX.getString() + Message.NOPERMISSION.getString());
						return;
					}
					BBSToper.getInstance().saveDefaultConfig();
					Option.load();
					Message.saveDefaultConfig();
					Message.load();
					sql.closeConnection();
					if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("mysql")) {
						sql = MySQLer.getInstance();
					} else if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("sqlite")) {
						sql = SQLiter.getInstance();
					}
					sql.load();
					BBSToper.getInstance().setSQLer(sql);
					Poster.setSQLer(sql);
					sender.sendMessage(Message.PREFIX.getString() + Message.RELOAD.getString());
					break;
				}
				case "check": {
					if (!(sender.hasPermission("bbstoper.check"))) {
						sender.sendMessage(Message.PREFIX.getString() + Message.NOPERMISSION.getString());
						return;
					}
					if (args.length != 3) {
						sender.sendMessage(Message.PREFIX.getString() + Message.INVAILD.getString());
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_CHECK.getString());
						return;
					}
					switch (args[1].toLowerCase()) {
					case "bbsid": {
						String owneruuid = sql.bbsNameCheck(args[2]);
						if (owneruuid == null) {
							sender.sendMessage(Message.PREFIX.getString() + Message.IDNOTFOUND.getString());
							return;
						}
						OfflinePlayer owner = Bukkit.getOfflinePlayer(UUID.fromString(owneruuid));
						String ownername = owner.getName();
						sender.sendMessage(Message.PREFIX.getString() + Message.IDOWNER.getString()
								.replaceAll("%PLAYER%", ownername).replaceAll("%UUID%", owneruuid));
						return;
					}
					case "player": {
						@SuppressWarnings("deprecation")
						UUID owneruuid = Bukkit.getOfflinePlayer(args[2]).getUniqueId();
						Poster poster = sql.getPoster(owneruuid.toString());
						if (poster == null) {
							sender.sendMessage(Message.PREFIX.getString() + Message.OWNERNOTFOUND.getString());
							return;
						}
						String mcbbsname = poster.getBbsname();
						sender.sendMessage(
								Message.PREFIX.getString() + Message.OWNERID.getString().replaceAll("%ID%", mcbbsname));
						return;
					}
					}
				}
				case "delete": {
					if (!(sender.hasPermission("bbstoper.delete"))) {
						sender.sendMessage(Message.PREFIX.getString() + Message.NOPERMISSION.getString());
						return;
					}
					if (args.length != 2) {
						sender.sendMessage(Message.PREFIX.getString() + Message.INVAILD.getString());
						sender.sendMessage(Message.PREFIX.getString() + Message.HELP_CHECK.getString());
						return;
					}
					@SuppressWarnings("deprecation")
					UUID uuid = Bukkit.getOfflinePlayer(args[1]).getUniqueId();
					Poster poster = sql.getPoster(uuid.toString());
					if (poster == null) {
						sender.sendMessage(Message.PREFIX.getString() + Message.OWNERNOTFOUND.getString());
						return;
					}
					sql.deletePoster(uuid.toString());
					sender.sendMessage(Message.PREFIX.getString() + Message.DELETESUCCESS.getString());
					return;
				}
				default: {
					sender.sendMessage(Message.PREFIX.getString() + Message.INVAILD.getString());
					return;
				}
				}
			}
		}.runTaskAsynchronously(BBSToper.getInstance());
		return true;
	}

	public void setSQLer(SQLer sql) {
		this.sql = sql;
	}

}