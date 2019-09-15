package moe.feo.bbstoper;

import java.io.File;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Message {
	PREFIX("prefix"), ENABLE("enable"), RELOAD("reload"), FAILEDCONNECTSQL("failedconnectsql"), POSTERID("posterid"),
	POSTERNUM("posternum"), OVERPAGE("overpage"), NOPLAYER("noplayer"), POSTERTIME("postertime"), PAGEINFO("pageinfo"),
	NOPOSTER("noposter"), POSTERPLAYER("posterplayer"), POSTERTOTAL("postertotal"), PAGEINFOTOP("pageinfotop"),
	NOTBOUND("notbound"), NOPOST("nopost"), OVERTIME("overtime"), WAITAMIN("waitamin"), REWARD("reward"),
	REWARDGIVED("rewardgived"), BROADCAST("broadcast"), ENTER("enter"), REPEAT("repeat"), NOTSAME("notsame"),
	ONCOOLDOWN("oncooldown"), SAMEBIND("samebind"), OWNSAMEBIND("ownsamebind"), BINDINGSUCCESS("bindingsuccess"),
	IDOWNER("idowner"), IDNOTFOUND("idnotfound"), OWNERID("ownerid"), OWNERNOTFOUND("ownernotfound"),
	NOPERMISSION("nopermission"), INVAILD("invalid"), FAILEDGETWEB("failedgetweb"), GUI_FRAME("gui.frame"),
	GUI_SKULL("gui.skull"), GUI_NOTBOUND("gui.notbound"), GUI_CLICKBOUND("gui.clickbound"),
	GUI_CLICKREBOUND("gui.clickrebound"), GUI_BBSID("gui.bbsid"), GUI_POSTTIMES("gui.posttimes"),
	GUI_REWARDS("gui.rewards"), GUI_CLICKGET("gui.clickget"), GUI_TOPS("gui.tops"), GUI_PAGESTATE("gui.pagestate"),
	GUI_PAGEID("gui.pageid"), GUI_LASTPOST("gui.lastpost"), GUI_CLICKOPEN("gui.clickopen"),
	CLICKPOSTICON("clickposticon"), DELETESUCCESS("deletesuccess"), INFO("info"), HELP_TITLE("help.title"),
	HELP_BINDING("help.binding"), HELP_REWARD("help.reward"), HELP_LIST("help.list"), HELP_TOP("help.top"),
	HELP_CHECK("help.check"), HELP_DELETE("help.delete"), HELP_RELOAD("help.reload");

	public String path;

	private static FileConfiguration messageConfig;
	private static File messageFile;

	private Message(String path) {
		this.path = path;
	}

	public static void load() {// 加载与重载
		if (messageFile == null) {
			messageFile = new File(BBSToper.getInstance().getDataFolder(), "lang.yml");
		}
		messageConfig = YamlConfiguration.loadConfiguration(messageFile);// 加载配置
		Reader reader = null;
		try {
			reader = new InputStreamReader(BBSToper.getInstance().getResource("lang.yml"), "UTF8");
		} catch (UnsupportedEncodingException e) {// 不支持的编码
			e.printStackTrace();
		}
		if (reader != null) {
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
			messageConfig.setDefaults(defConfig);// 设置默认
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
		return messageConfig.getString(path).replaceAll("&", "§");
	}

	public List<String> getStringList() {
		List<String> list = messageConfig.getStringList(path);
		List<String> replacedlist = new ArrayList<String>();
		for (String msg : list) {
			replacedlist.add(msg.replaceAll("&", "§"));
		}
		return replacedlist;
	}

}
