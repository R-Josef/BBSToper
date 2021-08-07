package moe.feo.bbstoper;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public enum Option {
	DEBUG("debug"),
	DATABASE_TYPE("database.type"),
	DATABASE_PREFIX("database.prefix"),
	DATABASE_TIMINGRECONNECT("timingreconnect"),
	DATABASE_MYSQL_IP("database.mysql.ip"),
	DATABASE_MYSQL_PORT("database.mysql.port"),
	DATABASE_MYSQL_DATABASE("database.mysql.database"),
	DATABASE_MYSQL_USER("database.mysql.user"),
	DATABASE_MYSQL_PASSWORD("database.mysql.password"),
	DATABASE_MYSQL_SSL("database.mysql.ssl"),
	DATABASE_SQLITE_FOLDER("database.sqlite.folder"),
	DATABASE_SQLITE_DATABASE("database.sqlite.database"),
	MCBBS_URL("mcbbs.url"), MCBBS_PAGESIZE("mcbbs.pagesize"),
	MCBBS_CHANGEIDCOOLDOWN("mcbbs.changeidcooldown"),
	MCBBS_QUERYCOOLDOWN("mcbbs.querycooldown"),
	MCBBS_JOINMESSAGE("mcbbs.joinmessage"),
	GUI_TOPPLAYERS("gui.topplayers"),
	GUI_DISPLAYHEADSKIN("gui.displayheadskin"),
	GUI_USECHATGETID("gui.usechatgetid"),
	GUI_CANCELKEYWORDS("gui.cancelkeywords"),
	REWARD_AUTO("reward.auto"),
	REWARD_PERIOD("reward.period"),
	REWARD_INTERVAL("reward.interval"),
	REWARD_TIMES("reward.times"),
	REWARD_COMMANDS("reward.commands"),
	REWARD_INCENTIVEREWARD_ENABLE("reward.incentivereward.enable"),
	REWARD_INCENTIVEREWARD_EXTRA("reward.incentivereward.extra"),
	REWARD_INCENTIVEREWARD_PERIOD("reward.incentivereward.period"),
	REWARD_INCENTIVEREWARD_COMMANDS("reward.incentivereward.commands"),
	REWARD_OFFDAYREWARD_ENABLE("reward.offdayreward.enable"),
	REWARD_OFFDAYREWARD_EXTRA("reward.offdayreward.extra"),
	REWARD_OFFDAYREWARD_OFFDAYS("reward.offdayreward.offdays"),
	REWARD_OFFDAYREWARD_COMMANDS("reward.offdayreward.commands");

	private static File file;
	private static FileConfiguration config;
	private String path;

	private Option(String path) {
		this.path = path;
	}

	public static void load() {
		if (file == null) {
			file = new File(BBSToper.getInstance().getDataFolder(), "config.yml");
		}
		config = YamlConfiguration.loadConfiguration(file);// 用这个方法加载配置可以解决编码问题
		try (Reader reader = new InputStreamReader(BBSToper.getInstance().getResource("config.yml"),
				StandardCharsets.UTF_8)) {// 读取默认配置
			YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
			config.setDefaults(defConfig);// 设置默认
		} catch (IOException ioe) {
			BBSToper.getInstance().getLogger().log(Level.SEVERE, "读取默认配置文件时出错!", ioe);
		}
	}

	public String getString() {
		return config.getString(path);
	}

	public List<String> getStringList() {
		return config.getStringList(path);
	}

	public boolean getBoolean() {
		return config.getBoolean(path);
	}

	public int getInt() {
		return config.getInt(path);
	}
}
