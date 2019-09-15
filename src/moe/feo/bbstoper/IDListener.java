package moe.feo.bbstoper;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.EventExecutor;
import org.bukkit.plugin.RegisteredListener;
import org.bukkit.scheduler.BukkitRunnable;

public class IDListener implements Listener {
	
	public static Map<String, RegisteredListener> map = new HashMap<String, RegisteredListener>();
	
	private RegisteredListener rglistener;
	private String playername;
	private boolean state;
	
	private EventExecutor idexecutor = new EventExecutor() {// 定义一个事件执行器
		@Override
		public void execute(Listener listener, Event event) throws EventException {
			if (!(listener instanceof IDListener)) return;
			((IDListener) listener).onPlayerChat((AsyncPlayerChatEvent) event);// 将事件交给onPlayerChat处理
		}
	};
	
	public IDListener(String playername) {// 在构造函数中初始化RegisteredListener和PlayerName
		this.playername = playername;
		this.rglistener = new RegisteredListener(this, idexecutor, EventPriority.HIGH, BBSToper.getInstance(), false);
		AsyncPlayerChatEvent.getHandlerList().register(rglistener);
		this.state = false;
		new BukkitRunnable() {
			@Override
			public void run() {
				RegisteredListener rglistener = IDListener.map.get(playername);
				if (rglistener != null) {// 检查这个玩家是否有绑定监听器
					AsyncPlayerChatEvent.getHandlerList().unregister(rglistener);
				}
			}
		}.runTaskLater(BBSToper.getInstance(), 2*60*20);// 如果这个监听器还存在，那么将在2分钟后被取消
		map.put(this.playername, this.rglistener);
	}
	
	public RegisteredListener getRGListener() {// 获取这个监听器的RegisteredListener
		return this.rglistener;
	}
	
	public String getPlayerName() {// 获取玩家名
		return this.playername;
	}

	public void onPlayerChat(AsyncPlayerChatEvent event) {// 处理事件
		if (!event.getPlayer().getName().equals(playername)) return;// 判断是否为此监听器监听的玩家
		Player player = event.getPlayer();
		String msg = event.getMessage();
		event.setCancelled(true);
		List<String> list = new ArrayList<String>(Arrays.asList(msg.split("\\s+")));
		list.add(0, "binding");
		String[] args = list.toArray(new String[list.size()]);
		CLI.getInstance().onCommand(player, null, null, args);
		if (state) {// isfirst为true说明这是第二次进入这个方法
			AsyncPlayerChatEvent.getHandlerList().unregister(rglistener);
			map.remove(playername);
		} else {// isfirst为false说明是第一次进入
			state = true;
		}
	}
	
}
