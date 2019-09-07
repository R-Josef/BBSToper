package moe.feo.bbstoper;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public interface SQLer {

	public default String getTableName(String name) {// 获取数据表应有的名字
		return Option.DATABASE_PREFIX.getString() + name;
	}

	public default void addPoster(Poster poster) {
		String sql = String.format(
				"INSERT INTO `%s` (`uuid`, `name`, `bbsname`, `binddate`, `rewardbefore`, `rewardtimes`) VALUES (?, ?, ?, ?, ?, ?);",
				getTableName("posters"));
		try {
			PreparedStatement pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, poster.getUuid());
			pstmt.setString(2, poster.getName());
			pstmt.setString(3, poster.getBbsname());
			pstmt.setLong(4, poster.getBinddate());
			pstmt.setString(5, poster.getRewardbefore());
			pstmt.setInt(6, poster.getRewardtime());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public default void updatePoster(Poster poster) {
		String sql = String.format(
				"UPDATE `%s` SET `name`=?, `bbsname`=?, `binddate`=?, `rewardbefore`=?, `rewardtimes`=? WHERE `uuid`=?;",
				getTableName("posters"));
		try {
			PreparedStatement pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, poster.getName());
			pstmt.setString(2, poster.getBbsname());
			pstmt.setLong(3, poster.getBinddate());
			pstmt.setString(4, poster.getRewardbefore());
			pstmt.setInt(5, poster.getRewardtime());
			pstmt.setString(6, poster.getUuid());
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public default void addTopState(String mcbbsname, String time) { // 记录一个顶贴
		String sql = String.format("INSERT INTO `%s` (`bbsname`, `time`) VALUES (?, ?);", getTableName("topstates"));
		try {
			PreparedStatement pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, mcbbsname);
			pstmt.setString(2, time);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public default Poster getPoster(String uuid) {// 返回一个顶贴者
		String sql = String.format("SELECT * from `%s` WHERE `uuid`=?;", getTableName("posters"));
		PreparedStatement pstmt;
		Poster poster = null;
		try {
			pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, uuid);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isClosed())
				return poster;
			if (rs.next()) {
				poster = new Poster();
				poster.setUuid(rs.getString("uuid"));
				poster.setName(rs.getString("name"));
				poster.setBbsname(rs.getString("bbsname"));
				poster.setBinddate(rs.getLong("binddate"));
				poster.setRewardbefore(rs.getString("rewardbefore"));
				poster.setRewardtime(rs.getInt("rewardtimes"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return poster;
	}

	public default List<String> getTopStatesFromPoster(Poster poster) {// 返回一个顶贴者的顶贴列表
		List<String> list = new ArrayList<String>();
		String sql = String.format("SELECT `time` from `%s` WHERE `bbsname`=?;", getTableName("topstates"));
		try {
			PreparedStatement pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, poster.getBbsname());
			ResultSet rs = pstmt.executeQuery();
			if (rs.isClosed())
				return list;
			while (rs.next()) {
				list.add(rs.getString("time"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}

	public default String bbsNameCheck(String bbsname) {// 检查这个bbsname并返回一个uuid
		String sql = String.format("SELECT `uuid` from `%s` WHERE `bbsname`=?;", getTableName("posters"));
		String uuid = null;
		try {
			PreparedStatement pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, bbsname);
			ResultSet rs = pstmt.executeQuery();
			if (rs.isClosed())
				return uuid;// 如果查询是空的sqlite就会把结果关闭
			if (rs.next()) {// 但是mysql却会返回一个空结果集
				uuid = rs.getString("uuid");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return uuid;
	}
	
	public default void delectPoster(String uuid) {
		String sql = String.format("DELETE FROM `%s` WHERE `uuid`=?;", getTableName("posters"));
		try {
			PreparedStatement pstmt = getConnection().prepareStatement(sql);
			pstmt.setString(1, uuid);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// 获取当前sql的连接
	public Connection getConnection();

	// 关闭sql连接
	public void closeConnection();

	// 加载，插件启动时调用
	public void load();

}
