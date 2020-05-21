package moe.feo.bbstoper;

import java.util.ArrayList;
import java.util.concurrent.TimeoutException;

import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

public class Util {

	private static BukkitTask autorewardtask;
	private static ArrayList<Integer> runningtaskidlist = new ArrayList<Integer>();

	public static void startAutoReward() {// 自动奖励的方法
		if (autorewardtask != null) {// 任务对象不为空
			boolean taskcancelled;// 是否已经取消
			try {
				taskcancelled = autorewardtask.isCancelled();
			} catch (NoSuchMethodError e) {// 1.7.10还没有这个方法
				taskcancelled = false;// 默认就当这个任务没有取消
			}
			if (!taskcancelled) {// 如果任务还被取消
				autorewardtask.cancel();// 将之前的任务取消
			}
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
					if (!crawler.visible)
						return;
					crawler.kickExpiredData();
					crawler.activeReward();
				}
			}.runTaskTimerAsynchronously(BBSToper.getInstance(), 0, period);
		}
	}

	public static void waitForAllTask() {// 此方法会阻塞直到所有此插件创建的线程结束
		int count = 0;
		try {
			while (!runningtaskidlist.isEmpty()) {// 当list非空，阻塞线程100毫秒后再判断一次
				if (count > 30000) {// 超过30秒没有关闭就算超时
					throw new TimeoutException();
				}
				Thread.sleep(100);
				count = count + 100;
			}
		} catch (InterruptedException | TimeoutException e) {
			e.printStackTrace();
		}
	}

	public static void addRunningTaskID(int i) {
		if (!runningtaskidlist.contains(i))
			runningtaskidlist.add(i);
	}

	public static void removeRunningTaskID(int i) {
		if (runningtaskidlist.contains(i))
			runningtaskidlist.remove((Integer) i);
	}

}
