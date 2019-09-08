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
		Elements listclass = doc.getElementsByClass("list");// 获取一个class名为list的元素的合集
		Element list = listclass.get(0);// mcbbs顶贴列表页面只会有一个list，直接使用即可
		Element listbody = list.getElementsByTag("tbody").get(0);// tbody表示表的身体而不是表头
		for (Element rows : listbody.getElementsByTag("tr")) {// tr是表的一行
			Elements cells = rows.getElementsByTag("td");// td表示一行的单元格，cells为单元格的合集
			Element idcell = cells.get(0);// 第一个单元格中包含有id
			String id = idcell.getElementsByTag("a").get(0).text();
			Element timecell = cells.get(1);// 第二个单元格就是time了
			String time = "";
			Element timespan = timecell.getElementsByTag("span").first();//time有两种，一种在span标签里面
			if (timespan != null) {
				time = timespan.attr("title");// attr用于获取元素的属性值，这个值就是我们要的time
			} else {
				time = timecell.text();// 6天过后的时间将直接被包含在单元格中
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
