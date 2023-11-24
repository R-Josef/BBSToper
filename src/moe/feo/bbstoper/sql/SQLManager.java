package moe.feo.bbstoper.sql;

import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.CLI;
import moe.feo.bbstoper.Crawler;
import moe.feo.bbstoper.Option;
import moe.feo.bbstoper.PAPIExpansion;
import moe.feo.bbstoper.Poster;
import moe.feo.bbstoper.Reminder;
import moe.feo.bbstoper.Util;
import moe.feo.bbstoper.gui.GUI;
import moe.feo.bbstoper.gui.TopGUI;

public class SQLManager {
	public static SQLer sql;
	private static BukkitTask timingreconnecttask;

	public static void initializeSQLer() {// 初始化或重载数据库
		SQLer.writelock.lock();
		try {
			if (sql != null) {
				sql.closeConnection();// 此方法会在已经建立过连接的情况下关闭连接
			}
			if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("mysql")) {
				sql = MySQLer.getInstance();
			} else if (Option.DATABASE_TYPE.getString().equalsIgnoreCase("sqlite")) {
				sql = SQLiter.getInstance();
			}
			sql.load();
			CLI.setSQLer(sql);
			GUI.setSQLer(sql);
			TopGUI.setSQLer(sql);
			Crawler.setSQLer(sql);
			Poster.setSQLer(sql);
			Reminder.setSQLer(sql);
			if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
				PAPIExpansion.setSQLer(sql);
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			SQLer.writelock.unlock();
		}
	}

	public static void closeSQLer() {// 关闭数据库
		sql.closeConnection();
		sql = null;
	}

	public static void startTimingReconnect() {// 自动重连数据库的方法
		if (timingreconnecttask != null && !timingreconnecttask.isCancelled()) {// 将之前的任务取消(如果存在)
			timingreconnecttask.cancel();
		}
		int period = Option.DATABASE_TIMINGRECONNECT.getInt() * 20;
		if (period > 0) {
			timingreconnecttask = new BukkitRunnable() {
				@Override
				public void run() {
					Util.addRunningTaskID(this.getTaskId());
					initializeSQLer();// 重载数据库
					Util.removeRunningTaskID(this.getTaskId());
				}
			}.runTaskTimerAsynchronously(BBSToper.getInstance(), period, period);
		}
	}
}
