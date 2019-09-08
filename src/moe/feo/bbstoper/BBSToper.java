package moe.feo.bbstoper;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;

public class BBSToper extends JavaPlugin {
	private static BBSToper bbstoper;
	private SQLer sql;

	public static BBSToper getInstance() {
		return bbstoper;
	}

	@Override
	public void onEnable() {
		bbstoper = this;
		this.saveDefaultConfig();
		Option.load();
		Message.saveDefaultConfig();
		Message.load();
		if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("mysql")) {
			sql = MySQLer.getInstance();
		} else if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("sqlite")) {
			sql = SQLiter.getInstance();
		}
		sql.load();
		CLI.getInstance().setSQLer(sql);
		Poster.setSQLer(sql);
		this.getCommand("bbstoper").setExecutor(CLI.getInstance());
		this.getCommand("bbstoper").setTabCompleter(CLI.getInstance());
		new Metrics(this);
		this.getLogger().info(Message.ENABLE.getString());
	}

	@Override
	public void onDisable() {
		bbstoper = null;

	}

	public void setSQLer(SQLer sql) {
		this.sql = sql;
	}
}
