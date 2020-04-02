package moe.feo.bbstoper;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

public class SQLiter implements SQLer {

	private final static SQLiter sqler = new SQLiter();
	private Connection conn;

	private SQLiter() {

	}

	public static SQLiter getInstance() {
		return sqler;
	}

	@Override
	public Connection getConnection() {
		return this.conn;
	}

	@Override
	public void closeConnection() {
		try {
			if (!conn.isClosed()) {// 如果连接没有关闭，则将关闭这个连接
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {// 获取数据库url
		String folder = BBSToper.getInstance().getDataFolder().getPath();// 获取插件文件夹
		String path = Option.DATABASE_SQLITE_FOLDER.getString().replaceAll("%PLUGIN_FOLDER%", "%s");
		String url = "jdbc:sqlite:" + path + File.separator + Option.DATABASE_SQLITE_DATABASE.getString();
		String finalurl = String.format(url, folder);// 替换占位符
		return finalurl;
	}

	@Override
	public void load() {
		connect();
		createTablePosters();
		createTableTopStates();
	}

	public void connect() {
		String driver = "org.sqlite.JDBC";
		try {
			Class.forName(driver);
			this.conn = DriverManager.getConnection(getUrl());
		} catch (ClassNotFoundException | SQLException e) {
			BBSToper.getInstance().getLogger().log(Level.WARNING, Message.FAILEDCONNECTSQL.getString(), e);
		}
	}

	public void createTablePosters() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `uuid` char(36) NOT NULL, `name` varchar(255) NOT NULL, `bbsname` varchar(255) NOT NULL COLLATE NOCASE, `binddate` bigint(0) NOT NULL, `rewardbefore` char(10) NOT NULL, `rewardtimes` int(0) NOT NULL, PRIMARY KEY (`uuid`) );",
				getTableName("posters"));
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void createTableTopStates() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `id` INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, `bbsname` varchar(255) NOT NULL COLLATE NOCASE, `time` varchar(16) NOT NULL);",
				getTableName("topstates"));
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
