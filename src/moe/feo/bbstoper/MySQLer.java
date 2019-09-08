package moe.feo.bbstoper;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class MySQLer implements SQLer {

	private final static MySQLer sqler = new MySQLer();
	private Connection conn;

	private MySQLer() {

	}

	public static MySQLer getInstance() {
		return sqler;
	}

	@Override
	public Connection getConnection() {
		return this.conn;
	}

	@Override
	public void closeConnection() {
		try {
			conn.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public String getUrl() {// 获取数据库url
		Boolean ssl = Option.DATABASE_MYSQL_SSL.getBoolean();
		String url = "jdbc:mysql://" + Option.DATABASE_MYSQL_IP.getString() + ":"
				+ Option.DATABASE_MYSQL_PORT.getString() + "/" + Option.DATABASE_MYSQL_DATABASE.getString() + "?useSSL="
				+ ssl.toString();
		return url;
	}

	@Override
	public void load() {
		connect();
		createTablePosters();
		createTableTopStates();
	}

	public void connect() {
		String driver = "com.mysql.jdbc.Driver";
		String user = Option.DATABASE_MYSQL_USER.getString();
		String password = Option.DATABASE_MYSQL_PASSWORD.getString();
		try {
			Class.forName(driver);
			this.conn = DriverManager.getConnection(getUrl(), user, password);
		} catch (ClassNotFoundException | SQLException e) {
			e.printStackTrace();
			BBSToper.getInstance().getLogger().warning(Message.FAILEDCONNECTSQL.getString());
		}
	}

	public void createTablePosters() {
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

	public void createTableTopStates() {
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
