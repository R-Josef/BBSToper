package moe.feo.bbstoper;

import java.util.List;

public class Poster {

	public static SQLer sql;

	private String uuid = "";
	private String name = "";
	private String bbsname = "";
	private long binddate = 0;
	private String rewardbefore = "";
	private int rewardtime = 0;

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

	public List<String> getTopStates() {
		return sql.getTopStatesFromPoster(this);
	}

}
