package moe.feo.bbstoper.gui;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.Message;
import moe.feo.bbstoper.Option;
import moe.feo.bbstoper.Poster;
import moe.feo.bbstoper.sql.SQLer;

public class TopGUI {

    private static SQLer sql;
    private Inventory inv;

    public static String getTitle() {// 获取插件的gui标题必须用此方法，因为用户可能会修改gui标题
        return Message.GUI_TOPTITLE.getString().replaceAll("%PREFIX%", Message.PREFIX.getString());
    }

    public TopGUI(Player player) {
        Bukkit.getScheduler().runTaskAsynchronously(BBSToper.getInstance(), () -> {
            createGui();
            player.openInventory(inv);
        });
    }

    class BBSToperGUIHolder implements InventoryHolder {// 定义一个Holder用于识别此插件的GUI
        @Override
        public Inventory getInventory() {
            return getGui();
        }
    }

    @SuppressWarnings("deprecation")
    public void createGui() {
        InventoryHolder holder = new BBSToperGUIHolder();
        this.setGui(Bukkit.createInventory(holder, InventoryType.CHEST, getTitle()));
        for (int i = 0; i < inv.getSize(); i++) {// 设置边框
            if (i > 9 && i < 17)
                continue;
            inv.setItem(i, getRandomPane());
        }
        // 总榜
        ItemStack star = new ItemStack(Material.NETHER_STAR);
        ItemMeta starmeta = star.getItemMeta();
        starmeta.setDisplayName(Message.GUI_TOPSTOTAL.getString());
        List<String> starlores = new ArrayList<>();
        List<Poster> listposter = sql.getTopPosters();
        for (int i = 0; i < listposter.size(); i++) {
            if (i >= Option.GUI_TOPPLAYERS.getInt())
                break;
            starlores.add(Message.POSTERPLAYER.getString() + ":" + listposter.get(i).getName() + " "
                    + Message.POSTERID.getString() + ":" + listposter.get(i).getBbsname() + " "
                    + Message.POSTERNUM.getString() + ":" + listposter.get(i).getCount());
        }
        starmeta.setLore(starlores);
        star.setItemMeta(starmeta);
        inv.setItem(12, star);
        // 月榜
        star = new ItemStack(Material.NETHER_STAR);
        starmeta = star.getItemMeta();
        starmeta.setDisplayName(Message.GUI_TOPSMONTHLY.getString());
        starlores = new ArrayList<>();
        listposter = sql.getTopPostersMonthly();
        for (int i = 0; i < listposter.size(); i++) {
            if (i >= Option.GUI_TOPPLAYERSMONTHLY.getInt())
                break;
            starlores.add(Message.POSTERPLAYER.getString() + ":" + listposter.get(i).getName() + " "
                    + Message.POSTERID.getString() + ":" + listposter.get(i).getBbsname() + " "
                    + Message.POSTERNUM.getString() + ":" + listposter.get(i).getCount());
        }
        starmeta.setLore(starlores);
        star.setItemMeta(starmeta);
        inv.setItem(13, star);
        // 日榜
        star = new ItemStack(Material.NETHER_STAR);
        starmeta = star.getItemMeta();
        starmeta.setDisplayName(Message.GUI_TOPSDAILY.getString());
        starlores = new ArrayList<>();
        listposter = sql.getTopPostersDaily();
        for (int i = 0; i < listposter.size(); i++) {
            if (i >= Option.GUI_TOPPLAYERSDAILY.getInt())
                break;
            starlores.add(Message.POSTERPLAYER.getString() + ":" + listposter.get(i).getName() + " "
                    + Message.POSTERID.getString() + ":" + listposter.get(i).getBbsname() + " "
                    + Message.POSTERNUM.getString() + ":" + listposter.get(i).getCount());
        }
        starmeta.setLore(starlores);
        star.setItemMeta(starmeta);
        inv.setItem(14, star);
        // 返回
        ItemStack paper = new ItemStack(Material.PAPER);
        ItemMeta papermeta = paper.getItemMeta();
        papermeta.setDisplayName(Message.GUI_CLICKBACK.getString());
        paper.setItemMeta(papermeta);
        inv.setItem(22, paper);
    }

    public ItemStack getRandomPane() {// 获取随机一种颜色的玻璃板
        short data = (short)(Math.random()* 16);// 这会随机取出0-15的数据值
        while (data == 8) {// 8号亮灰色染色玻璃板根本没有颜色
            data = (short)(Math.random()* 16);
        }
        ItemStack frame;
        try {
            frame = new ItemStack(Material.STAINED_GLASS_PANE, 1, data);

        } catch (NoSuchFieldError e) {// 某些高版本服务端不兼容旧版写法
            String[] glasspanes = {"WHITE_STAINED_GLASS_PANE", "ORANGE_STAINED_GLASS_PANE", "MAGENTA_STAINED_GLASS_PANE",
                    "LIGHT_BLUE_STAINED_GLASS_PANE", "YELLOW_STAINED_GLASS_PANE", "LIME_STAINED_GLASS_PANE", "PINK_STAINED_GLASS_PANE",
                    "GRAY_STAINED_GLASS_PANE", "LIGHT_GRAY_STAINED_GLASS_PANE", "CYAN_STAINED_GLASS_PANE", "PURPLE_STAINED_GLASS_PANE",
                    "BLUE_STAINED_GLASS_PANE", "BROWN_STAINED_GLASS_PANE", "GREEN_STAINED_GLASS_PANE", "RED_STAINED_GLASS_PANE",
                    "BLACK_STAINED_GLASS_PANE"};
            frame = new ItemStack(Material.getMaterial(glasspanes[data]), 1);
        }
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
        TopGUI.sql = sql;
    }
}
