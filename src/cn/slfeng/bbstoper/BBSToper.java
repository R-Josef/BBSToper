package cn.slfeng.bbstoper;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

public final class BBSToper extends JavaPlugin {

	public static String mcbbsurl = new String();
	public static long changeidcooldown;
	public static int rwoneday;
	public static int pagesize;
	public static String prefix = new String();
	public static String repeat = new String();
	public static String bdsuccess = new String();
	public static String rwsuccess = new String();
	public static String notsame = new String();
	public static String oncooldown = new String();
	public static String noaccount = new String();
	public static String reward = new String();
	public static String noreward = new String();
	public static String reload = new String();
	public static String nopermission = new String();
	public static String invalid = new String();
	public static String enable = new String();
	public static String usage = new String();
	public static String posterid = new String();
	public static String postertime = new String();
	public static String noposter = new String();
	public static String posternum = new String();
	public static String overtime = new String();
	public static String waitamin = new String();
	public static String samebinding = new String();
	public static String ownsamebinding = new String();
	public static String overpage = new String();
	public static String pageinfo = new String();
	public static List<String> cmds = new ArrayList<String>();
	public static List<String> help = new ArrayList<String>();
	public static List<String> ID = new ArrayList<String>();
	public static List<String> Time = new ArrayList<String>();
	public static Map<String, String> binding = new HashMap<>();
	private FileConfiguration poster = null;
	private File posterFile = null;

	@Override
	public void onEnable() {
		this.saveDefaultConfig();
		this.saveDefaultPoster();
		try {
			this.Load();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			getLogger().info("onEnable Error!");
		}
		getLogger().info(enable);// log输出
	}

	@Override
	public void onDisable() {// 只有玩家数据会在关服时保存
		this.savePoster();// 保存玩家数据
	}

	public void Reload() throws UnsupportedEncodingException {// 重载时不会保存任何数据
		this.reloadConfig();// 重载配置
		this.reloadPoster();// 重载数据
		this.Load();// 加载配置和数据
	}

	public void Load() throws UnsupportedEncodingException {// 加载配置和数据
		this.getConfig();
		this.getPoster();
		mcbbsurl = this.getConfig().getString("mcbbsurl");
		changeidcooldown = this.getConfig().getLong("changeidcooldown");
		rwoneday = this.getConfig().getInt("rwoneday");
		pagesize = this.getConfig().getInt("pagesize");
		cmds = this.getConfig().getStringList("rewards");
		prefix = this.getConfig().getString("messages.prefix").replaceAll("&", "§");// 前缀
		help = this.getConfig().getStringList("messages.help");// 帮助
		for (int i = 0; i < help.size(); i++) {
			help.set(i, prefix + help.get(i).replaceAll("&", "§"));
		}
		repeat = prefix + this.getConfig().getString("messages.repeat").replaceAll("&", "§");
		bdsuccess = prefix + this.getConfig().getString("messages.bdsuccess").replaceAll("&", "§");
		rwsuccess = prefix + this.getConfig().getString("messages.rwsuccess").replaceAll("&", "§");
		notsame = prefix + this.getConfig().getString("messages.notsame").replaceAll("&", "§");
		oncooldown = prefix + this.getConfig().getString("messages.oncooldown").replaceAll("&", "§");
		noaccount = prefix + this.getConfig().getString("messages.noaccount").replaceAll("&", "§");
		reward = prefix + this.getConfig().getString("messages.reward").replaceAll("&", "§");
		noreward = prefix + this.getConfig().getString("messages.noreward").replaceAll("&", "§");
		reload = prefix + this.getConfig().getString("messages.reload").replaceAll("&", "§");
		nopermission = prefix + this.getConfig().getString("messages.nopermission").replaceAll("&", "§");
		invalid = prefix + this.getConfig().getString("messages.invalid").replaceAll("&", "§");
		enable = this.getConfig().getString("messages.enable").replaceAll("&", "§");
		usage = prefix + this.getConfig().getString("messages.usage").replaceAll("&", "§");
		posterid = this.getConfig().getString("messages.posterid").replaceAll("&", "§");
		postertime = this.getConfig().getString("messages.postertime").replaceAll("&", "§");
		noposter = prefix + this.getConfig().getString("messages.noposter").replaceAll("&", "§");
		posternum = prefix + this.getConfig().getString("messages.posternum").replaceAll("&", "§");
		overtime = prefix + this.getConfig().getString("messages.overtime").replaceAll("&", "§");
		waitamin = prefix + this.getConfig().getString("messages.waitamin").replaceAll("&", "§");
		samebinding = prefix + this.getConfig().getString("messages.samebinding").replaceAll("&", "§");
		ownsamebinding = prefix + this.getConfig().getString("messages.ownsamebinding").replaceAll("&", "§");
		overpage = prefix + this.getConfig().getString("messages.overpage").replaceAll("&", "§");
		pageinfo = prefix + this.getConfig().getString("messages.pageinfo").replaceAll("&", "§");
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("poster")) {
			if (args.length == 1) {
				if (args[0].equalsIgnoreCase("binding")) {// 绑定账号
					sender.sendMessage(usage);// 提示用法
				} else if (args[0].equalsIgnoreCase("list")) {
					if (topList().size() != 0) {
						outputToplist(sender, 0);
					} else {
						sender.sendMessage(noposter);
					}
				} else if (args[0].equalsIgnoreCase("reward")) {// 回报
					try {
						if (this.getPoster().getString(sender.getName() + ".id") == null) {// 如果没有绑定账号
							sender.sendMessage(noaccount);
						} else {// 如果绑定了账号
							boolean b = new Boolean(false);
							boolean isovertime = new Boolean(false);
							List<String> list = new ArrayList<String>();
							boolean iswaitamin = new Boolean(false);
							Getter();// 开始抓取网页
							for (int i = 0; i < ID.size() && i < Time.size(); i++) {// 遍历网页抓取到的信息
								if (ID.get(i).equalsIgnoreCase(this.getPoster().getString(sender.getName() + ".id"))) {// 如果此次的ID包含玩家绑定的名字
									if (!list.contains(Time.get(i))) {// 如果list中不包含这次遍历到的时间
										list.add(Time.get(i));// 放进时间
									} else {// 如果List包含这次时间
										if (!this.getPoster().getStringList(sender.getName() + ".time")
												.contains(Time.get(i))) {// 如果配置文件中没有包含这次时间
											iswaitamin = true;// 顶帖时间重复
										}
									}
									if (!this.getPoster().getStringList(sender.getName() + ".time")
											.contains(Time.get(i))) {// 如果此次循环到的time不被包含在储存的数据中
										String datenow = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
										if (!datenow
												.equals(this.getPoster().getString(sender.getName() + ".daybefore"))) {// 如果系统日期不等于数据文件中的日期
											this.getPoster().set(sender.getName() + ".rwaday", 0);// 次数归零
											this.getPoster().set(sender.getName() + ".daybefore", datenow);// 更新数据文件中的"日期"
										}
										if ((this.getPoster().getInt(sender.getName() + ".rwaday") < rwoneday)) {// 如果玩家当日次数小于设定值
											for (int r = 0; r < cmds.size(); r++) {// 遍历奖励指令
												Bukkit.dispatchCommand(Bukkit.getConsoleSender(),
														cmds.get(r).replaceAll("%PLAYER%", sender.getName()));// 遍历领奖指令
											}
											List<String> time = this.getPoster()
													.getStringList(sender.getName() + ".time");// 此玩家的time List
											time.add(Time.get(i));// 放入此次领过奖的时间
											this.getPoster().set(sender.getName() + ".time", time);// 保存新的time List
											this.getPoster().set(sender.getName() + ".rwaday",
													this.getPoster().getInt(sender.getName() + ".rwaday") + 1);// 次数+1
											this.savePoster();// 保存数据
											sender.sendMessage(reward.replaceAll("%TIME%", Time.get(i)));// 领奖提示
											b = true;// 放入领取成功
										} else {// 如果大于设定
											isovertime = true;// 放入超过次数
										}
									}
								}
							} // 整个遍历完成
							if (b) {// 如果领取成功
								sender.sendMessage(rwsuccess);
							} else {// 如果领取不成功
								if (isovertime) {// 如果超过次数
									sender.sendMessage(overtime.replaceAll("%RWONEDAY%", String.valueOf(rwoneday)));// 提示超过次数
								} else {// 没有超过次数
									if (iswaitamin) {// 是否重复,间隔少于1min
										sender.sendMessage(waitamin);// 提示间隔问题
									} else {
										sender.sendMessage(noreward);// 没找到记录
									}
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
						sender.sendMessage("Error!");
					}
				} else if (args[0].equalsIgnoreCase("reload")) {// 重载
					if (sender.hasPermission("bbstoper.admin")) {
						try {
							this.Reload();
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
							sender.sendMessage("Error!");
						}
						sender.sendMessage(reload);
					} else {
						sender.sendMessage(nopermission);// 没权限
					}
				} else {
					sender.sendMessage(invalid);
				}
			}
			if (args.length == 2) {
				if (args[0].equalsIgnoreCase("binding")) {// 绑定账号
					try {
						String id = this.getPoster().getString(sender.getName() + ".id");
						Long date = System.currentTimeMillis() - this.getPoster().getLong(sender.getName() + ".date");
						Boolean iscd = date > changeidcooldown * 86400000;
						if (id == null || iscd) {// 如果之前没有绑定过或解绑冷却已过
							if (binding.get(sender.getName()) == null) {// 如果没有重复确认过绑定
								binding.put(sender.getName(), args[1]);
								sender.sendMessage(repeat);
							} else if (binding.get(sender.getName()).equals(args[1])) {// 如果重复正确
								if (!issame(args[1])) {// 如果没有相同
									this.getPoster().set(sender.getName() + ".id", args[1]);
									this.getPoster().set(sender.getName() + ".date", System.currentTimeMillis());// 记录时间
									this.getPoster().set(sender.getName() + ".daybefore", "");// 之前的日期
									this.getPoster().set(sender.getName() + ".rwaday", 0);// 当天次数为0
									this.savePoster();// 保存数据
									binding.put(sender.getName(), null);// 绑定成功,清空map
									sender.sendMessage(bdsuccess);
								} else {
									if (this.getPoster().getString(sender.getName() + ".id") != null
											&& this.getPoster().getString(sender.getName() + ".id").contains(args[1])) {
										sender.sendMessage(ownsamebinding);// 你已经绑定过此ID
									} else {
										sender.sendMessage(samebinding);// 已有相同绑定
									}
									binding.put(sender.getName(), null);// 已有相同,清空map
								}
							} else if (!binding.get(sender.getName()).equals(args[1])) {
								binding.put(sender.getName(), null);// 两次不一样，删除map重新输入
								sender.sendMessage(notsame);
							}
						} else {// 如果还在冷却中
							sender.sendMessage(oncooldown);
						}
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
						sender.sendMessage("Error!");
					}
				} else if (args[0].equalsIgnoreCase("list")) {// 列出顶帖者
					if (sender.hasPermission("bbstoper.admin")) {
						if (topList().size() != 0) {
							boolean b = true;
							for (int i = 0; i < args[1].length(); i++) {// 判断参数是否为数字
								if (!Character.isDigit(args[1].charAt(i))) {
									b = false;
								}
							}
							if (b) {
								int page = Integer.parseInt(args[1]) - 1;// 零基的java
								if (page + 1 <= paging().size()) {
									outputToplist(sender, page);// 输出给玩家
								} else {
									sender.sendMessage(overpage);// 超出页数
								}
							} else {
								sender.sendMessage(invalid);// 不是数字
							}
						} else {
							sender.sendMessage(noposter);
						}
					} else {
						sender.sendMessage(nopermission);
					}
				} else {
					sender.sendMessage(invalid);
				}
			}
			if (args.length == 0) {
				for (int i = 0; i < help.size(); i++) {// 遍历帮助列表
					sender.sendMessage(help.get(i));
				}
			}
			if (args.length > 2) {
				sender.sendMessage(invalid);
			}
		}
		return false;

	}

	public boolean issame(String id) throws UnsupportedEncodingException {// 判断是否有重复
		boolean b = false;
		Map<String, Object> map = this.getPoster().getValues(true);// 获取所有键值的Map
		List<String> list = new ArrayList<String>();
		for (String key : map.keySet()) {// 遍历key
			if (key.contains(".id")) {// 如果key包含子健id
				list.add((String) map.get(key));// 把这个key获取的值放进list
			}
		}
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).equalsIgnoreCase(id)) {// 如果此次循环到的ID包含传给方法的参数id
				b = true;
			}
		}
		return b;
	}

	public static synchronized void Getter() throws IOException {// 抓取网页,此方法加锁
		String url = mcbbsurl;
		Document doc = Jsoup.connect(url).get();
		Elements links = doc.select(".f_c a[target=\"_blank\"]"); // "a[href]" //带有href属性的a元素
		Elements imports = doc.select(".f_c span[title]");
		ID.clear();
		Time.clear();
		for (Element link : links) {
			ID.add(trim(link.text(), 35));
		}
		for (Element link1 : imports) {
			Time.add(link1.attr("title"));
		}
	}

	private static String trim(String s, int width) {// 截取ID
		if (s.length() > width)
			return s.substring(0, width - 1) + ".";
		else
			return s;
	}

	public static void outputToplist(CommandSender p, Integer page) {
		String[] FinalList = paging().get(page).toArray(new String[paging().get(page).size()]);
		p.sendMessage(posternum + ":" + Time.size());
		p.sendMessage(FinalList);
		p.sendMessage(pageinfo.replaceAll("%PAGE%", String.valueOf(page + 1)).replaceAll("%TOTALPAGE%",
				String.valueOf(paging().size())));
	}

	public static List<List<String>> paging() {// 分页
		List<String> list = topList();
		int totalCount = list.size(); // 总条数
		int pageCount; // 总页数
		int m = totalCount % pagesize; // 余数
		if (m > 0) {
			pageCount = totalCount / pagesize + 1;
		} else {
			pageCount = totalCount / pagesize;
		}
		List<List<String>> totalList = new ArrayList<List<String>>();
		for (int i = 1; i <= pageCount; i++) {
			if (m == 0) {
				List<String> subList = list.subList((i - 1) * pagesize, pagesize * (i));
				totalList.add(subList);
			} else {
				if (i == pageCount) {
					List<String> subList = list.subList((i - 1) * pagesize, totalCount);
					totalList.add(subList);
				} else {
					List<String> subList = list.subList((i - 1) * pagesize, pagesize * i);
					totalList.add(subList);
				}
			}
		}
		return totalList;
	}

	public static List<String> topList() {// 列出顶帖列表
		try {
			Getter();
		} catch (IOException e) {
			e.printStackTrace();
		}
		List<String> list = new ArrayList<String>();
		for (int i = 0; i < ID.size() && i < Time.size(); i++) {
			list.add(posterid + ":" + ID.get(i) + " " + postertime + ":" + Time.get(i));
		}
		return list;
	}

	public void reloadPoster() throws UnsupportedEncodingException {// 重载
		if (posterFile == null) {
			posterFile = new File(getDataFolder(), "poster.yml");
		}
		poster = YamlConfiguration.loadConfiguration(posterFile);
		Reader posterStream = new InputStreamReader(this.getResource("poster.yml"), "UTF8");// 查看jar里默认的
		if (posterStream != null) {
			YamlConfiguration post = YamlConfiguration.loadConfiguration(posterStream);
			poster.setDefaults(post);
		}
	}

	public FileConfiguration getPoster() throws UnsupportedEncodingException {// 获取
		if (poster == null) {
			reloadPoster();
		}
		return poster;
	}

	public void savePoster() {// 保存
		if (poster == null || posterFile == null) {
			return;
		}
		try {
			this.getPoster().save(posterFile);
		} catch (IOException ex) {
			getLogger().log(Level.SEVERE, "Could not save config to " + posterFile, ex);
			getLogger().info("Save poster Error!");
		}
	}

	public void saveDefaultPoster() {// 默认覆盖
		if (posterFile == null) {
			posterFile = new File(getDataFolder(), "poster.yml");
		}
		if (!posterFile.exists()) {
			this.saveResource("poster.yml", false);
		}
	}
}
