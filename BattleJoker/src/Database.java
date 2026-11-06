import java.sql.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Database {
    final static Path dataDirectory = Path.of(System.getProperty("battlejoker.dataDir", "data"));
    final static String url = "jdbc:sqlite:" + dataDirectory.resolve("battleJoker.db");
    static Connection conn;

    public static void connect() throws SQLException, ClassNotFoundException {
        if (conn == null || conn.isClosed()) {
            try {
                Files.createDirectories(dataDirectory);
            } catch (IOException ex) {
                throw new SQLException("Cannot create local data directory: " + dataDirectory, ex);
            }
//            Class.forName("org.sqlite.JDBC");
            conn = DriverManager.getConnection(url);
            try (Statement statement = conn.createStatement()) {
                statement.executeUpdate("CREATE TABLE IF NOT EXISTS scores ("
                    + "id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, score INTEGER, level INTEGER, time TEXT)");
            }
        }

    }

    public static void disconnect() throws SQLException {
        if (conn != null)
            conn.close();
    }

    public static ArrayList<HashMap<String, String>> getScores() throws SQLException {
        String sql = "SELECT * FROM scores ORDER BY score DESC LIMIT 10";
        ArrayList<HashMap<String, String>> data = new ArrayList<>();
        Statement statement = conn.createStatement();
        ResultSet resultSet = statement.executeQuery(sql);
        while (resultSet.next()) {
            HashMap<String, String> m = new HashMap<>();
            m.put("name", resultSet.getString("name"));
            m.put("score", resultSet.getString("score"));
            m.put("level", resultSet.getString("level"));
            m.put("time", resultSet.getString("time"));
            data.add(m);
        }
        return data;
    }

    public static void putScore(String name, int score, int level) throws SQLException {
        String sql = String.format("INSERT INTO scores ('name', 'score', 'level', 'time') VALUES ('%s', %d, %d, datetime('now'))", name, score, level);
        Statement statement = conn.createStatement();
        statement.execute(sql);
    }

    public static void main(String[] args) throws SQLException, ClassNotFoundException {
        connect();
        putScore("Bob", 1000, 13);
        getScores().forEach(map->{
            System.out.println(map.get("name"));
        });
    }
}
