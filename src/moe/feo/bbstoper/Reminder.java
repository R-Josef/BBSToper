package moe.feo.bbstoper;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;

public class Reminder implements Listener {

	public Reminder(Plugin plugin) {
		plugin.getServer().getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void onPlayerJoin(PlayerJoinEvent event) {
		if (!Option.MCBBS_JOINMESSAGE.getBoolean()) {// 如果设置了不提示消息则直接返回
			return;
		}
		boolean isbinded = true;
		boolean isposted = true;
		SQLer sql = BBSToper.getInstance().getSQLer();
		UUID uuid = event.getPlayer().getUniqueId();
		Poster poster = sql.getPoster(uuid.toString());
		String datenow = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
		if (poster == null) {// 玩家未绑定
			isbinded = false;
			isposted = false;
		} else if (!datenow.equals(poster.getRewardbefore())) {// 玩家上一次顶贴不是今天
			isposted = false;
		}
		if (!isposted) {// 没有顶贴
			List<String> list = Message.INFO.getStringList();
			String url = "https://www.mcbbs.net/thread-" + Option.MCBBS_URL.getString() + "-1-1.html";
			for (String msg : list) {
				event.getPlayer().sendMessage(Message.PREFIX.getString() + msg.replaceAll("%PAGE%", url));
			}
		}
		if (!isbinded) {// 没有绑定
			event.getPlayer().sendMessage(Message.PREFIX.getString() + Message.HELP_BINDING.getString());
		}
	}
}
