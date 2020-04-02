package moe.feo.bbstoper;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.SkullType;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;

public class GUI {

	private static SQLer sql;
	private Inventory inv;
	
	public static String getTitle() {// 获取插件的gui标题必须用此方法，因为用户可能会修改gui标题
		String title = Message.GUI_TITLE.getString().replaceAll("%PREFIX%", Message.PREFIX.getString());
		return title;
	}

	public GUI(Player player) {
		createGui(player);
		Bukkit.getScheduler().runTask(BBSToper.getInstance(), () -> player.openInventory(inv));
	}
	
	class BBSToperGUIHolder implements InventoryHolder {// 定义一个Holder用于识别此插件的GUI
		@Override
		public Inventory getInventory() {
			return getGui();
		}
	}

	@SuppressWarnings("deprecation")
	public void createGui(Player player) {
		InventoryHolder holder = new BBSToperGUIHolder();
		this.setGui(Bukkit.createInventory(holder, InventoryType.CHEST, getTitle()));
		for (int i = 0; i < inv.getSize(); i++) {// 设置边框
			if (i > 9 && i < 17)
				continue;
			inv.setItem(i, getRandomPane());
		}
		ItemStack skull = new ItemStack(Material.SKULL_ITEM, 1, (short) SkullType.PLAYER.ordinal());
		SkullMeta skullmeta = (SkullMeta) skull.getItemMeta();// 玩家头颅
		if (Option.GUI_DISPLAYHEADSKIN.getBoolean()) {// 如果开启了头颅显示，才会设置头颅的所有者
			try {
				skullmeta.setOwningPlayer(player);
			} catch (NoSuchMethodError e) {// 这里为了照顾低版本
				skullmeta.setOwner(player.getName());
			}
		}
		skullmeta.setDisplayName(Message.GUI_SKULL.getString().replaceAll("%PLAYER%", player.getName()));
		List<String> skulllores = new ArrayList<String>();
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
			if (crawler.Time.size() > 0) { // 如果从没有人顶帖，就以“----”代替上次顶帖时间(原来不加判断直接get会报索引范围错误)
				compasslores.add(Message.GUI_LASTPOST.getString().replaceAll("%TIME%", crawler.Time.get(0)));
			} else {
				compasslores.add(Message.GUI_LASTPOST.getString().replaceAll("%TIME%", "----"));
			}
		} else {
			compasslores.add(Message.GUI_PAGENOTVISIBLE.getString());
		}
		compasslores.add(Message.GUI_CLICKOPEN.getString());
		compassmeta.setLore(compasslores);
		compass.setItemMeta(compassmeta);
		inv.setItem(22, compass);
	}
	
	public ItemStack getRandomPane() {// 获取随机一种颜色的玻璃板
		short data = (short)(Math.random()* 16);// 这会随机取出0-15的数据值
		ItemStack frame = new ItemStack(Material.STAINED_GLASS_PANE, 1, data);
		ItemMeta framemeta = frame.getItemMeta();
		framemeta.setDisplayName(Message.GUI_FRAME.getString());
		frame.setItemMeta(framemeta);
		return frame;
	}

	public Inventory getGui() {
		return inv;
	}

	public void setGui(Inventory inv) {
		this.inv = inv;
	}
	
	public static void setSQLer(SQLer sql) {
		GUI.sql = sql;
	}
}
