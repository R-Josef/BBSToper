package moe.feo.bbstoper;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class Crawler {
	public List<String> ID = new ArrayList<String>();
	public List<String> Time = new ArrayList<String>();
	
	public Crawler() {
		resolveWebData();
	}
	
	public void resolveWebData() {
		String url = Option.MCBBS_URL.getString();
		if (!url.contains("http")) {
			url = "https://www.mcbbs.net/forum.php?mod=misc&action=viewthreadmod&tid=" + url + "&mobile=no";
		}
		Document doc = null;
		try {
			doc = Jsoup.connect(url).get();
		} catch (IOException e) {
			e.printStackTrace();
			BBSToper.getInstance().getLogger().warning(Message.FAILEDGETWEB.getString());
		}
		Elements listclass = doc.getElementsByClass("list");
		Element list = listclass.get(0);
		Element listbody = list.getElementsByTag("tbody").get(0);
		for (Element rows : listbody.getElementsByTag("tr")) {// 表的一行
			Elements cells = rows.getElementsByTag("td");
			Element idcell = cells.get(0);// id那一个单元格
			String id = idcell.getElementsByTag("a").get(0).text();

			Element timecell = cells.get(1);// time那个单元格
			String time = "";
			Element timespan = timecell.select("span").first();
			if (timespan != null) {
				time = timespan.attr("title");
			} else {
				time = timecell.text();
			}
			ID.add(id);
			Time.add(time);
		}
	}
	
	public void kickExpiredData() {// 剔除过期的数据
		// 注意mcbbs的日期格式，月份和天数都是非零开始，小时分钟是从零开始
		SimpleDateFormat sdfm = new SimpleDateFormat("yyyy-M-d HH:mm");
		Date now = new Date();
		long validtime = Option.REWARD_PERIOD.getInt() * 24 * 60 * 60 * 1000L;// 有效期
		Date expirydate = new Date(now.getTime() - validtime);// 过期时间，如果小于这个时间则表示过期
		for (int i = 0; i < Time.size(); i++) {
			Date date = null;
			try {
				date = sdfm.parse(Time.get(i));
			} catch (ParseException e) {
				e.printStackTrace();
				return;
			}
			if (date.before(expirydate)) {// 过期了
				Time.remove(i);
				ID.remove(i);
				i--;//这里要吧序数往前退一个
			}
		}
		
	}
	
}
