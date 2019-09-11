package moe.feo.bbstoper;

import java.util.List;
import org.bukkit.configuration.file.FileConfiguration;

public enum Option {
	DATABASE_TYPE("database.type"), DATABASE_PREFIX("database.prefix"), DATABASE_MYSQL_IP("database.mysql.ip"),
	DATABASE_MYSQL_PORT("database.mysql.port"), DATABASE_MYSQL_DATABASE("database.mysql.database"),
	DATABASE_MYSQL_USER("database.mysql.user"), DATABASE_MYSQL_PASSWORD("database.mysql.password"),
	DATABASE_MYSQL_SSL("database.mysql.ssl"), DATABASE_SQLITE_FOLDER("database.sqlite.folder"),
	DATABASE_SQLITE_DATABASE("database.sqlite.database"), MCBBS_URL("mcbbs.url"), MCBBS_PAGESIZE("mcbbs.pagesize"),
	MCBBS_CHANGEIDCOOLDOWN("mcbbs.changeidcooldown"), MCBBS_JOINMESSAGE("mcbbs.joinmessage"),
	REWARD_AUTO("reward.auto"), REWARD_PERIOD("reward.period"), REWARD_TIMES("reward.times"),
	REWARD_COMMANDS("reward.commands");

	private static FileConfiguration config;
	private String path;

	private Option(String path) {
		this.path = path;
	}

	public static void load() {// 加载与重载
		if (config == null) {// 为空表示初次加载
			config = BBSToper.getInstance().getConfig();
		} else {// 不为空则重载然后更新引用
			BBSToper.getInstance().reloadConfig();
			config = BBSToper.getInstance().getConfig();
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
