package moe.feo.bbstoper;

import org.bstats.bukkit.Metrics;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

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
		// 选择将SQLer对象主动传给其他类是因为其他类无法确定要获取sqlite还是mysql的实列
		// 而让其他类永久持有BBSToper中sql这个对象的引用也会导致重载时可能会导致该类还持有对象
		CLI.getInstance().setSQLer(sql);
		Poster.setSQLer(sql);
		this.getCommand("bbstoper").setExecutor(CLI.getInstance());
		this.getCommand("bbstoper").setTabCompleter(CLI.getInstance());
		new Reminder(this);
		new GUIManager(this);
		int period = Option.REWARD_AUTO.getInt() * 20;
		if (period > 0) {
			new BukkitRunnable() {// 自动奖励，异步执行
				@Override
				public void run() {
					Crawler crawler = new Crawler();
					crawler.kickExpiredData();
					crawler.activeReward();
				}
			}.runTaskTimerAsynchronously(this, 0, period);
		}
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

	public SQLer getSQLer() {
		return sql;
	}
}
