package StudyMe;

import com.mysql.jdbc.exceptions.jdbc4.CommunicationsException;
import com.mysql.jdbc.exceptions.jdbc4.MySQLNonTransientConnectionException;

import java.sql.*;

public class Database {
    private static final String dbURL = "jdbc:mysql://127.0.0.1:3306/studyme";
    private static final String dbUsername = "root";
    private static final String dbPassword = "";

    private Connection connection;

    //use this Database connection object only for small queries!
    //otherwise other threads that are querying with this object will be blocked!
    public static Database mysql = new Database();

    public Database(){
        try {
            connection = DriverManager.getConnection(dbURL,dbUsername,dbPassword);
        }catch (Exception e){e.printStackTrace();}
    }

    public ResultSet query(String query, String... param) throws Exception{
        PreparedStatement stat;
        try {
            stat = connection.prepareStatement(query);
            stat.setQueryTimeout(100);
            for(int i=0;i<param.length;i++){
                stat.setString(i+1,param[i]);
            }
            return stat.executeQuery();
        }catch (MySQLNonTransientConnectionException e){
            connection = DriverManager.getConnection(dbURL,dbUsername,dbPassword);
            return query(query,param);
        }catch (CommunicationsException e){
            connection = DriverManager.getConnection(dbURL,dbUsername,dbPassword);
            return query(query,param);
        }
    }

    public int update(String query, String... param) throws Exception{
        PreparedStatement stat;
        try {
            stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        }catch (MySQLNonTransientConnectionException e){
            connection = DriverManager.getConnection(dbURL,dbUsername,dbPassword);
            stat = connection.prepareStatement(query, Statement.RETURN_GENERATED_KEYS);
        }
        stat.setQueryTimeout(5);
        for (int i = 0; i < param.length; i++) {
            stat.setString(i + 1, param[i]);
        }
        if (stat.executeUpdate() == 0) return -1;
        ResultSet res = stat.getGeneratedKeys();
        if (!res.next()) return -1;
        return res.getInt(1);
    }

    public static void printResultSet(ResultSet resultSet)throws Exception{
        while (resultSet.next()){
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                System.out.print("| "+resultSet.getString(i)+" ");
            }
            System.out.println("|");
        }
    }

}
