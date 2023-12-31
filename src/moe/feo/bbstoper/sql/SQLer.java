package moe.feo.bbstoper.sql;

import moe.feo.bbstoper.Option;
import moe.feo.bbstoper.Poster;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public abstract class SQLer {
	
	public static final ReadWriteLock lock = new ReentrantReadWriteLock();
	public static final Lock readlock = lock.readLock();
	public static final Lock writelock = lock.writeLock();

	public String getTableName(String name) {// 获取数据表应有的名字
		return Option.DATABASE_PREFIX.getString() + name;
	}

	public void addPoster(Poster poster) {
		readlock.lock();
		String sql = String.format(
				"INSERT INTO `%s` (`uuid`, `name`, `bbsname`, `binddate`, `rewardbefore`, `rewardtimes`) VALUES (?, ?, ?, ?, ?, ?);",
				getTableName("posters"));
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
			pstmt.setString(1, poster.getUuid());
			pstmt.setString(2, poster.getName());
			pstmt.setString(3, poster.getBbsname());
			pstmt.setLong(4, poster.getBinddate());
			pstmt.setString(5, poster.getRewardbefore());
			pstmt.setInt(6, poster.getRewardtime());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
	}

	public void updatePoster(Poster poster) {
		readlock.lock();
		String sql = String.format(
				"UPDATE `%s` SET `name`=?, `bbsname`=?, `binddate`=?, `rewardbefore`=?, `rewardtimes`=? WHERE `uuid`=?;",
				getTableName("posters"));
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
			pstmt.setString(1, poster.getName());
			pstmt.setString(2, poster.getBbsname());
			pstmt.setLong(3, poster.getBinddate());
			pstmt.setString(4, poster.getRewardbefore());
			pstmt.setInt(5, poster.getRewardtime());
			pstmt.setString(6, poster.getUuid());
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
	}

	public void addTopState(String mcbbsname, String time) { // 记录一个顶贴
		readlock.lock();
		String sql = String.format("INSERT INTO `%s` (`bbsname`, `time`) VALUES (?, ?);", getTableName("topstates"));
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
			pstmt.setString(1, mcbbsname);
			pstmt.setString(2, time);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
	}

	public Poster getPoster(String uuid) {// 返回一个顶贴者
		readlock.lock();
		String sql = String.format("SELECT * from `%s` WHERE `uuid`=?;", getTableName("posters"));
		Poster poster = null;
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
			pstmt.setString(1, uuid);
			try (ResultSet rs = pstmt.executeQuery();) {
				try {
					if (rs.isClosed())
						return poster;
				} catch (AbstractMethodError e) {
				}

				if (rs.next()) {
					poster = new Poster();
					poster.setUuid(rs.getString("uuid"));
					poster.setName(rs.getString("name"));
					poster.setBbsname(rs.getString("bbsname"));
					poster.setBinddate(rs.getLong("binddate"));
					poster.setRewardbefore(rs.getString("rewardbefore"));
					poster.setRewardtime(rs.getInt("rewardtimes"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return poster;
	}

	public List<String> getTopStatesFromPoster(Poster poster) {// 返回一个顶贴者的顶贴列表
		readlock.lock();
		List<String> list = new ArrayList<String>();
		String sql = String.format("SELECT `time` from `%s` WHERE `bbsname`=?;", getTableName("topstates"));
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
			pstmt.setString(1, poster.getBbsname());
			try (ResultSet rs = pstmt.executeQuery();) {
				try {
					if (rs.isClosed())
						return list;
				} catch (AbstractMethodError e) {
				}

				while (rs.next()) {
					list.add(rs.getString("time"));
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return list;
	}

	public String bbsNameCheck(String bbsname) {// 检查这个bbsname并返回一个uuid
		readlock.lock();
		String sql = String.format("SELECT `uuid` from `%s` WHERE `bbsname`=?;", getTableName("posters"));
		String uuid = null;
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
			pstmt.setString(1, bbsname);
			try (ResultSet rs = pstmt.executeQuery();) {
				try {
					if (rs.isClosed())// 如果查询是空的sqlite就会把结果关闭
						return uuid;
				} catch (AbstractMethodError e) {// 低版本没有这个特性
				}

				if (rs.next()) {// 但是mysql却会返回一个空结果集
					uuid = rs.getString("uuid");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return uuid;
	}

	public boolean checkTopstate(String bbsname, String time) {// 查询是否存在这条记录，如果存在返回true，不存在返回false
		readlock.lock();
		String sql = String.format("SELECT * FROM `%s` WHERE `bbsname`=? AND `time`=? LIMIT 1;",
				getTableName("topstates"));
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);) {
			pstmt.setString(1, bbsname);
			pstmt.setString(2, time);
			try (ResultSet rs = pstmt.executeQuery()) {
				try {
					if (rs.isClosed()) {// sqlite会关闭这个结果
						return false;
					}
				} catch (AbstractMethodError e) {// 但是低版本使用这个方法会报错
				}

				if (!rs.next()) {// mysql会返回一个空结果集，里面什么都没有
					return false;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return true;
	}
	
	public List<Poster> getTopPosters() {// 按排名返回poster，并给poster写上count属性，不会返回没有顶过贴的玩家
		readlock.lock();
		String sql = String.format("SELECT bbsname,COUNT(*) FROM `%s` GROUP BY bbsname ORDER BY COUNT(*) DESC;",
				getTableName("topstates"));
		List<Poster> list = new ArrayList<Poster>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String uuid = bbsNameCheck(rs.getString("bbsname"));
				Poster poster = getPoster(uuid);
				if (poster == null) continue;
				poster.setCount(rs.getInt("COUNT(*)"));
				list.add(poster);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return null;
	}

	public List<Poster> getTopPostersMonthly() {// 按排名返回每月poster，并给poster写上count属性，不会返回没有顶过贴的玩家
		readlock.lock();
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-");
		String formattedDate = currentDate.format(formatter);
		String sql = String.format("SELECT bbsname,COUNT(*) FROM `%s` WHERE LEFT(time, %s) = '%s' GROUP BY bbsname ORDER BY COUNT(*) DESC;",
				getTableName("topstates"), formattedDate.length(), formattedDate);
		List<Poster> list = new ArrayList<Poster>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String uuid = bbsNameCheck(rs.getString("bbsname"));
				Poster poster = getPoster(uuid);
				if (poster == null) continue;
				poster.setCount(rs.getInt("COUNT(*)"));
				list.add(poster);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return null;
	}

	public List<Poster> getTopPostersDaily() {// 按排名返回每日poster，并给poster写上count属性，不会返回没有顶过贴的玩家
		readlock.lock();
		LocalDate currentDate = LocalDate.now();
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-M-d");
		String formattedDate = currentDate.format(formatter);
		String sql = String.format("SELECT bbsname,COUNT(*) FROM `%s` WHERE LEFT(time, %s) = '%s' GROUP BY bbsname ORDER BY COUNT(*) DESC;",
				getTableName("topstates"), formattedDate.length(), formattedDate);
		List<Poster> list = new ArrayList<Poster>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery();) {

			while (rs.next()) {
				String uuid = bbsNameCheck(rs.getString("bbsname"));
				Poster poster = getPoster(uuid);
				if (poster == null) continue;
				poster.setCount(rs.getInt("COUNT(*)"));
				list.add(poster);
			}
			return list;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return null;
	}
	
	public List<Poster> getNoCountPosters() {// 由于上面的方法只会返回有顶贴的玩家
		readlock.lock();
		String sql = String.format("SELECT * FROM `%s` WHERE `rewardbefore`='';", getTableName("posters"));
		List<Poster> posterlist = new ArrayList<Poster>();
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);
			 ResultSet rs = pstmt.executeQuery();) {
			while (rs.next()) {
				Poster poster = new Poster();
				poster.setUuid(rs.getString("uuid"));
				poster.setName(rs.getString("name"));
				poster.setBbsname(rs.getString("bbsname"));
				poster.setBinddate(rs.getLong("binddate"));
				poster.setRewardbefore(rs.getString("rewardbefore"));
				poster.setRewardtime(rs.getInt("rewardtimes"));
				poster.setCount(0);
				posterlist.add(poster);
			}
			return posterlist;
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
		return null;
	}

	public void deletePoster(String uuid) {
		readlock.lock();
		String sql = String.format("DELETE FROM `%s` WHERE `uuid`=?;", getTableName("posters"));
		try (PreparedStatement pstmt = getConnection().prepareStatement(sql);
		) {
			pstmt.setString(1, uuid);
			pstmt.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			readlock.unlock();
		}
	}

	// 获取当前sql的连接
	protected abstract Connection getConnection();

	// 关闭sql连接
	protected abstract void closeConnection();

	// 加载，插件启动时调用
	protected abstract void load();

}
