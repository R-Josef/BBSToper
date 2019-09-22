package moe.feo.bbstoper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GUI {

	private Inventory inv;

	public GUI(Player player) {
		createGui(player);
		Bukkit.getScheduler().runTask(BBSToper.getInstance(), () -> player.openInventory(inv));
	}

	@SuppressWarnings("deprecation")
	public void createGui(Player player) {
		this.setGui(Bukkit.createInventory(null, InventoryType.CHEST, Message.PREFIX.getString()));
		// ItemStack frame = new ItemStack(Material.BLACK_STAINED_GLASS_PANE);
		ItemStack frame = new ItemStack(Material.STAINED_GLASS_PANE, 1, (short) 15);// GUI的边框
		ItemMeta framemeta = frame.getItemMeta();
		framemeta.setDisplayName(Message.GUI_FRAME.getString());
		frame.setItemMeta(framemeta);
		for (int i = 0; i < inv.getSize(); i++) {
			if (i > 9 && i < 17)
				continue;
			inv.setItem(i, frame);
		}
		// ItemStack skull = new ItemStack(Material.PLAYER_HEAD);
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();// 玩家头颅
		try {
			skullmeta.setOwningPlayer(player);
		} catch (NoSuchMethodError e) {// 这里为了照顾低版本
			skullmeta.setOwner(player.getName());
		}
		skullmeta.setDisplayName(Message.GUI_SKULL.getString().replaceAll("%PLAYER%", player.getName()));
		List<String> skulllores = new ArrayList<String>();
		SQLer sql = BBSToper.getInstance().getSQLer();
		Poster poster = sql.getPoster(player.getUniqueId().toString());
		if (poster == null) {
			skulllores.add(Message.GUI_NOTBOUND.getString());
			skulllores.add(Message.GUI_CLICKBOUND.getString());
		} else {
			skulllores.add(Message.GUI_BBSID.getString().replaceAll("%BBSID%", poster.getBbsname()));
			skulllores.add(Message.GUI_POSTTIMES.getString().replaceAll("%TIMES%", "" + poster.getTopStates().size()));
			skulllores.add(Message.GUI_CLICKREBOUND.getString());
		}
		skullmeta.setLore(skulllores);
		skull.setItemMeta(skullmeta);
		inv.setItem(12, skull);
		ItemStack sunflower = new ItemStack(Material.DOUBLE_PLANT);
		ItemMeta sunflowermeta = sunflower.getItemMeta();
		sunflowermeta.setDisplayName(Message.GUI_REWARDS.getString());
		List<String> sunflowerlores = new ArrayList<String>(Message.GUI_REWARDSINFO.getStringList());// 自定义奖励信息
		if (sunflowerlores.isEmpty()) {// 如果没有自定义奖励信息
			sunflowerlores = Option.REWARD_COMMANDS.getStringList();// 直接显示命令
		}
		sunflowerlores.add(Message.GUI_CLICKGET.getString());
		sunflowermeta.setLore(sunflowerlores);
		sunflower.setItemMeta(sunflowermeta);
		inv.setItem(13, sunflower);
		ItemStack star = new ItemStack(Material.NETHER_STAR);
		ItemMeta starmeta = star.getItemMeta();
		starmeta.setDisplayName(Message.GUI_TOPS.getString());
		List<String> starlores = new ArrayList<String>();
		List<Poster> listposter = sql.getTopPosters();
		for (int i = 0; i < sql.getTopPosters().size(); i++) {
			if (i >= Option.GUI_TOPPLAYERS.getInt())
				break;
			starlores.add(Message.POSTERPLAYER.getString() + ":" + listposter.get(i).getName() + " "
					+ Message.POSTERID.getString() + ":" + listposter.get(i).getBbsname() + " "
					+ Message.POSTERNUM.getString() + ":" + listposter.get(i).getCount());
		}
		starmeta.setLore(starlores);
		star.setItemMeta(starmeta);
		inv.setItem(14, star);
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta compassmeta = compass.getItemMeta();
		compassmeta.setDisplayName(Message.GUI_PAGESTATE.getString());
		List<String> compasslores = new ArrayList<String>();
		compasslores.add(Message.GUI_PAGEID.getString().replaceAll("%PAGEID%", Option.MCBBS_URL.getString()));
		Crawler crawler = new Crawler();
		if (crawler.visible) {// 如果帖子可视，就获取帖子最近一次顶贴
			compasslores.add(Message.GUI_LASTPOST.getString().replaceAll("%TIME%", crawler.Time.get(0)));
		} else {
			compasslores.add(Message.GUI_PAGENOTVISIBLE.getString());
		}
		compasslores.add(Message.GUI_CLICKOPEN.getString());
		compassmeta.setLore(compasslores);
		compass.setItemMeta(compassmeta);
		inv.setItem(22, compass);
	}

	public Inventory getGui() {
		return inv;
	}

	public void setGui(Inventory inv) {
		this.inv = inv;
	}
}
