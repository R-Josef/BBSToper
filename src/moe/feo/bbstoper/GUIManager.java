package moe.feo.bbstoper;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredListener;

public class GUIManager implements Listener {
	
	public GUIManager(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent event) {
		if (event.getWhoClicked() instanceof Player == false) return;// 如果不是玩家操作的，返回
		Player player = (Player) event.getWhoClicked();
		if (player.getOpenInventory().getTitle().equalsIgnoreCase(Message.PREFIX.getString()) ) {// 确认操作的此插件GUI
			event.setCancelled(true);
			if (event.getRawSlot() == 12) {// 点击绑定
				player.closeInventory();
				String playername = player.getName();
				RegisteredListener rglistener = IDListener.map.get(playername);
				if (rglistener != null) {// 如果这个玩家已经有一个监听器
					AsyncPlayerChatEvent.getHandlerList().unregister(rglistener);// 注销此监听器
				}
				new IDListener(playername);// 为此玩家创建一个监听器
				player.sendMessage(Message.ENTER.getString());
			}
			if (event.getRawSlot() == 13) {
				player.closeInventory();
				String[] args = {"reward"};
				CLI.getInstance().onCommand(player, null, null, args);
			}
			if (event.getRawSlot() == 22) {// 获取链接
				player.closeInventory();
				for (String msg : Message.CLICKPOSTICON.getStringList()) {
					String url = "https://www.mcbbs.net/thread-" + Option.MCBBS_URL.getString() + "-1-1.html";
					player.sendMessage(msg.replaceAll("%PAGE%", url));
				}
			}
		}
	}

}
