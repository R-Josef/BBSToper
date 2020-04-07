package moe.feo.bbstoper;

import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BBSToper extends JavaPlugin {
	private static BBSToper bbstoper;

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
		Util.initializeSQLer();
		this.getCommand("bbstoper").setExecutor(CLI.getInstance());
		this.getCommand("bbstoper").setTabCompleter(CLI.getInstance());
		new Reminder(this);
		new GUIManager(this);
		Util.startTimingReconnect();
		Util.startAutoReward();
		new Metrics(this);
		this.getLogger().info(Message.ENABLE.getString());
	}

	@Override
	public void onDisable() {
		Bukkit.getScheduler().cancelTasks(bbstoper);
		Thread thread = new Thread(new Runnable(){
			@Override
			public void run() {
				Util.waitForAllTask();// 此方法会阻塞
				Util.closeSQLer();
				bbstoper = null;
			}
		});
		thread.setDaemon(true);
		thread.start();
	}

}
