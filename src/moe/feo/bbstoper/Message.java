package moe.feo.bbstoper;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Message {
	PREFIX("prefix"),
	ENABLE("enable"),
	RELOAD("reload"),
	FAILEDCONNECTSQL("failedconnectsql"),
	QUERYCOOLDOWN("querycooldown"),
	POSTERID("posterid"),
	POSTERNUM("posternum"),
	OVERPAGE("overpage"),
	NOPLAYER("noplayer"),
	POSTERTIME("postertime"),
	PAGEINFO("pageinfo"),
	NOPOSTER("noposter"),
	POSTERPLAYER("posterplayer"),
	POSTERTOTAL("postertotal"),
	PAGEINFOTOP("pageinfotop"),
	NOTBOUND("notbound"),
	NOPOST("nopost"),
	OVERTIME("overtime"),
	WAITAMIN("waitamin"),
	INTERVALTOOSHORT("intervaltooshort"),
	REWARD("reward"),
	EXTRAREWARD("extrareward"),
	REWARDGIVED("rewardgived"),
	BROADCAST("broadcast"),
	ENTER("enter"),
	CANCELED("canceled"),
	REPEAT("repeat"),
	NOTSAME("notsame"),
	ONCOOLDOWN("oncooldown"),
	SAMEBIND("samebind"),
	OWNSAMEBIND("ownsamebind"),
	BINDINGSUCCESS("bindingsuccess"),
	IDOWNER("idowner"),
	IDNOTFOUND("idnotfound"),
	OWNERID("ownerid"),
	OWNERNOTFOUND("ownernotfound"),
	NOPERMISSION("nopermission"),
	INVALID("invalid"),
	INVALIDNUM("invalidnum"),
	PLAYERCMD("playercmd"),
	PAGENOTVISIBLE("pagenotvisible"),
	NONE("none"),
	FAILEDGETWEB("failedgetweb"),
	FAILEDRESOLVEWEB("failedresolveweb"),
	FAILEDUNINSTALLMO("faileduninstallmo"),
	GUI_TITLE("gui.title"),
	GUI_TOPTITLE("gui.toptitle"),
	GUI_FRAME("gui.frame"),
	GUI_SKULL("gui.skull"),
	GUI_NOTBOUND("gui.notbound"),
	GUI_CLICKBOUND("gui.clickbound"),
	GUI_CLICKREBOUND("gui.clickrebound"),
	GUI_BBSID("gui.bbsid"),
	GUI_POSTTIMES("gui.posttimes"),
	GUI_REWARDS("gui.rewards"),
	GUI_INCENTIVEREWARDS("gui.incentiverewards"),
	GUI_OFFDAYREWARDS("gui.offdayrewards"),
	GUI_CLICKGET("gui.clickget"),
	GUI_TOPS("gui.tops"),
	GUI_TOPSINFO("gui.topsinfo"),
	GUI_TOPSTOTAL("gui.topstotal"),
	GUI_TOPSMONTHLY("gui.topsmonthly"),
	GUI_TOPSDAILY("gui.topsdaily"),
	GUI_CLICKBACK("gui.clickback"),
	GUI_PAGESTATE("gui.pagestate"),
	GUI_PAGEID("gui.pageid"),
	GUI_LASTPOST("gui.lastpost"),
	GUI_EXTRAREWARDS("gui.extrarewards"),
	GUI_PAGENOTVISIBLE("gui.pagenotvisible"),
	GUI_CLICKOPEN("gui.clickopen"),
	GUI_REWARDSINFO("gui.rewardsinfo"),
	CLICKPOSTICON("clickposticon"),
	DELETESUCCESS("deletesuccess"),
	INFO("info"),
	EXTRAINFO("extrainfo"),
	HELP_TITLE("help.title"),
	HELP_HELP("help.help"),
	HELP_BINDING("help.binding"),
	HELP_REWARD("help.reward"),
	HELP_TESTREWARD("help.testreward"),
	HELP_LIST("help.list"),
	HELP_TOP("help.top"),
	HELP_CHECK("help.check"),
	HELP_DELETE("help.delete"),
	HELP_RELOAD("help.reload");

	public String path;

	private static FileConfiguration messageConfig;
	private static File messageFile;
	private String cacheString; // 缓存内容
	private List<String> cacheStringList; // 缓存内容

	Message(String path) {
		this.path = path;
	}

	public static void load() {// 加载与重载
		if (messageFile == null) {
			messageFile = new File(BBSToper.getInstance().getDataFolder(), "lang.yml");
		}
		messageConfig = YamlConfiguration.loadConfiguration(messageFile);// 加载配置
		try (Reader reader = new InputStreamReader(BBSToper.getInstance().getResource("lang.yml"),
				StandardCharsets.UTF_8)) {// 读取默认配置
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
			messageConfig.setDefaults(defConfig);// 设置默认
		} catch (IOException ioe) {
			BBSToper.getInstance().getLogger().log(Level.SEVERE, "读取默认语言文件时出错!", ioe);
		}
		// 删除缓存
		for (Message m : values()) {
			m.cacheString = null;
			m.cacheStringList = null;
		}
	}

	public static void saveDefaultConfig() {
		if (messageFile == null) {
			messageFile = new File(BBSToper.getInstance().getDataFolder(), "lang.yml");
		}
		if (!messageFile.exists()) {
			BBSToper.getInstance().saveResource("lang.yml", false);
		}
	}

	public String getString() {
		if (cacheString != null)
			return cacheString;
		return cacheString = ChatColor.translateAlternateColorCodes('&', messageConfig.getString(path));
	}

	public List<String> getStringList() {
		if (cacheStringList != null)
			return cacheStringList;
		return cacheStringList = Collections.unmodifiableList(// 禁止修改
				messageConfig.getStringList(path).stream().map(msg -> ChatColor.translateAlternateColorCodes('&', msg))
						.collect(Collectors.toList()));
	}

}
