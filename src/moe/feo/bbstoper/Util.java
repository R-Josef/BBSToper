package moe.feo.bbstoper;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Util {
	
	public static SQLer sql;
	private static BukkitTask autorewardtask;
	private static BukkitTask timingreconnecttask;
	private static ArrayList<Integer> runningtaskidlist = new ArrayList<Integer>();
	
	public static void initializeSQLer() {// 初始化或重载数据库
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
		Crawler.setSQLer(sql);
		Poster.setSQLer(sql);
		Reminder.setSQLer(sql);
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
					addRunningTaskID(this.getTaskId());
					initializeSQLer();// 重载数据库
					removeRunningTaskID(this.getTaskId());
				}
			}.runTaskTimerAsynchronously(BBSToper.getInstance(), period, period);
		}
	}
	
	public static void startAutoReward() {// 自动奖励的方法
		if (autorewardtask != null && !autorewardtask.isCancelled()) {// 将之前的任务取消(如果存在)
			autorewardtask.cancel();
		}
		int period = Option.REWARD_AUTO.getInt() * 20;
		if (period > 0) {
			autorewardtask = new BukkitRunnable() {// 自动奖励，异步执行
				@Override
				public void run() {
					addRunningTaskID(this.getTaskId());
					task();
					removeRunningTaskID(this.getTaskId());
				}
				public void task() {
					Crawler crawler = new Crawler();
					if (!crawler.visible) return;
					crawler.kickExpiredData();
					crawler.activeReward();
				}
			}.runTaskTimerAsynchronously(BBSToper.getInstance(), 0, period);
		}
	}
	
	public static boolean isAllTaskFinished() {// 此方法永远不会返回false
		int count = 0;
		while (!runningtaskidlist.isEmpty()) {// 当list非空，阻塞线程100毫秒后再判断一次
			try {
				if (count > 30000) {// 超过30秒没有关闭就算超时
					throw new TimeoutException();
				}
				Thread.sleep(100);
				count = count + 100;
			} catch (InterruptedException | TimeoutException e) {
				e.printStackTrace();
			}
		}
		return true;
	}
	
	public static void addRunningTaskID(int i) {
		if (!runningtaskidlist.contains(i))
			runningtaskidlist.add(i);
	}
	
	public static void removeRunningTaskID(int i) {
		if (runningtaskidlist.contains(i))
			runningtaskidlist.remove((Integer)i);
	}

}
