package moe.feo.bbstoper;

import java.util.List;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import moe.feo.bbstoper.sql.SQLer;

public class PAPIExpansion extends PlaceholderExpansion {

	private static SQLer sql;

	public static void setSQLer(SQLer sql) {
		PAPIExpansion.sql = sql;
	}

	// 因为是插件包含的类, PAPI不能重载这个拓展
	// 这个方法的重写是必须的
	@Override
	public boolean persist() {
		return true;
	}

	// 因为是插件包含的类, 不需要检查这个
	@Override
	public boolean canRegister() {
		return true;
	}

	@Override
	public String getAuthor() {
		return BBSToper.getInstance().getDescription().getAuthors().toString();
	}

	@Override
	public String getIdentifier() {
		return BBSToper.getInstance().getDescription().getName().toLowerCase();
	}

	@Override
	public String getVersion() {
		return BBSToper.getInstance().getDescription().getVersion();
	}

	@Override
	public String onPlaceholderRequest(Player player, String identifier) {
		Poster poster;
		if (player != null) {// 有玩家
			poster = sql.getPoster(player.getUniqueId().toString());
			if (identifier.equals("bbsid")) {// BBS用户名
				if (poster == null) {
					return Message.GUI_NOTBOUND.getString();
				} else {
					return poster.getBbsname();
				}
			}
			if (identifier.equals("posttimes")) {// 顶贴次数
				if (poster == null) {
					return Message.GUI_NOTBOUND.getString();
				} else {
					return String.valueOf(poster.getTopStates().size());
				}
			}
		}
		if (identifier.equals("pageid")) {// 宣传贴id
			return Option.MCBBS_URL.getString();
		}
		if (identifier.equals("pageurl")) {// 宣传贴url
			return "https://www.mcbbs.net/thread-" + Option.MCBBS_URL.getString() + "-1-1.html";
		}
		if (identifier.equals("lastpost")) {// 上一次顶贴时间
			Crawler crawler = new Crawler();
			if (crawler.visible) {// 如果帖子可视，就获取帖子最近一次顶贴
				if (crawler.Time.size() > 0) { // 如果从没有人顶帖，就以“----”代替上次顶帖时间(原来不加判断直接get会报索引范围错误)
					return crawler.Time.get(0);
				} else {
					return "----";
				}
			} else {
				return Message.GUI_PAGENOTVISIBLE.getString();// 帖子不可视
			}
		}
		String pattern = "^top_[1-9]\\d*$";// top_正整数的正则表达式
		if (Pattern.matches(pattern, identifier)) {// 如果匹配这种格式
			int rank = Integer.parseInt(identifier.split("_")[1]);
			int index = rank - 1;
			List<Poster> listposter = sql.getTopPosters();
			if (index < listposter.size()) {
				return Message.POSTERPLAYER.getString() + ":" + listposter.get(index).getName() + " "
						+ Message.POSTERID.getString() + ":" + listposter.get(index).getBbsname() + " "
						+ Message.POSTERNUM.getString() + ":" + listposter.get(index).getCount();
			}
		}
		return null;
	}

}
