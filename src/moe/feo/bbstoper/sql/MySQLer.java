package moe.feo.bbstoper.sql;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.logging.Level;

import moe.feo.bbstoper.BBSToper;
import moe.feo.bbstoper.Message;
import moe.feo.bbstoper.Option;

public class MySQLer extends SQLer {

	private final static MySQLer sqler = new MySQLer();
	private Connection conn;

	private MySQLer() {

	}

	public static MySQLer getInstance() {
		return sqler;
	}

	@Override
	protected Connection getConnection() {
		return this.conn;
	}

	@Override
	protected void closeConnection() {
		try {
			if (!conn.isClosed()) {// 如果连接没有关闭，则将关闭这个连接
				conn.close();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {// 获取数据库url
		Boolean ssl = Option.DATABASE_MYSQL_SSL.getBoolean();
		String url = "jdbc:mysql://" + Option.DATABASE_MYSQL_IP.getString() + ":"
				+ Option.DATABASE_MYSQL_PORT.getString() + "/" + Option.DATABASE_MYSQL_DATABASE.getString() + "?useSSL="
				+ ssl.toString() + "&serverTimezone=UTC" + "&autoReconnect=true" + "&allowPublicKeyRetrieval=true" + "&characterEncoding=utf8";
		return url;
	}

	@Override
	protected void load() {
		connect();
		createTablePosters();
		createTableTopStates();
	}

	protected void connect() {
		String driver = "com.mysql.jdbc.Driver";
		String user = Option.DATABASE_MYSQL_USER.getString();
		String password = Option.DATABASE_MYSQL_PASSWORD.getString();
		try {
			Class.forName(driver);
			this.conn = DriverManager.getConnection(getUrl(), user, password);
		} catch (ClassNotFoundException | SQLException e) {
			BBSToper.getInstance().getLogger().log(Level.WARNING, Message.FAILEDCONNECTSQL.getString(), e);
		}
	}

	protected void createTablePosters() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `uuid` char(36) NOT NULL, `name` varchar(255) NOT NULL, `bbsname` varchar(255) NOT NULL, `binddate` bigint(0) NOT NULL, `rewardbefore` char(10) NOT NULL, `rewardtimes` int(0) NOT NULL, PRIMARY KEY (`uuid`) ) CHARACTER SET utf8 COLLATE utf8_unicode_ci;",
				getTableName("posters"));
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	protected void createTableTopStates() {
		String sql = String.format(
				"CREATE TABLE IF NOT EXISTS `%s` ( `id` int(0) NOT NULL AUTO_INCREMENT, `bbsname` varchar(255) NOT NULL, `time` varchar(16) NOT NULL, PRIMARY KEY (`id`) ) CHARACTER SET utf8 COLLATE utf8_unicode_ci;",
				getTableName("topstates"));
		try {
			Statement stmt = conn.createStatement();
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
