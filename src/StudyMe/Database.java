package StudyMe;

import java.sql.*;

public class Database {
    private static final String dbURL = "jdbc:mysql://127.0.0.1:3306/studyme";
    private static final String dbUsername = "root";
    private static final String dbPassword = "";

    private Connection connection;
    public static Database mysql = new Database();

    public Database(){
        try {
            connection = DriverManager.getConnection(dbURL,dbUsername,dbPassword);
        }catch (Exception e){e.printStackTrace();}
    }

    public ResultSet query(String query, String... param) throws Exception{
        PreparedStatement stat = connection.prepareStatement(query);
        stat.setQueryTimeout(100);
        for(int i=0;i<param.length;i++){
            stat.setString(i+1,param[i]);
        }
        return stat.executeQuery();
    }

    public int update(String query, String... param) throws Exception{
        PreparedStatement stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        stat.setQueryTimeout(5);
        for (int i = 0; i < param.length; i++) {
            stat.setString(i + 1, param[i]);
        }
        if (stat.executeUpdate() == 0) return -1;
        ResultSet res = stat.getGeneratedKeys();
        if (!res.next()) return -1;
        return res.getInt(1);
    }
}
