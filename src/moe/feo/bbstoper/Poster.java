package moe.feo.bbstoper;

import java.util.List;

import moe.feo.bbstoper.sql.SQLer;

public class Poster {

	public static SQLer sql;

	private String uuid = "";// 顶贴者的uuid
	private String name = "";// 顶贴者id
	private String bbsname = "";// 顶贴者bbs用户名
	private long binddate = 0;// 绑定bbs用户名的时间
	private String rewardbefore = "";// 上一次获取奖励的时间
	private int rewardtime = 0;// 上次一领取了多少奖励
	private int count = 0;// 总计的顶贴次数 (不一定有数据)

	public static void setSQLer(SQLer sql) {
		Poster.sql = sql;
	}

	public String getUuid() {
		return uuid;
	}

	public void setUuid(String uuid) {
		this.uuid = uuid;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getBbsname() {
		return bbsname;
	}

	public void setBbsname(String bbsname) {
		this.bbsname = bbsname;
	}

	public long getBinddate() {
		return binddate;
	}

	public void setBinddate(long binddate) {
		this.binddate = binddate;
	}

	public String getRewardbefore() {
		return rewardbefore;
	}

	public void setRewardbefore(String rewardbefore) {
		this.rewardbefore = rewardbefore;
	}

	public int getRewardtime() {
		return rewardtime;
	}

	public void setRewardtime(int rewardtime) {
		this.rewardtime = rewardtime;
	}
	
	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<String> getTopStates() {
		return sql.getTopStatesFromPoster(this);
	}

}
