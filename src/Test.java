import StudyMe.Database;
import StudyMe.Error;
import StudyMe.Login;
import com.sun.org.apache.regexp.internal.RE;
import org.json.JSONObject;

import javax.xml.crypto.Data;
import java.io.Console;
import java.sql.*;

public class Test {

    public static void printResultSet(ResultSet resultSet)throws Exception{
        while (resultSet.next()){
            for(int i=1;i<=resultSet.getMetaData().getColumnCount();i++){
                System.out.print("| "+resultSet.getString(i)+" ");
            }
            System.out.println("|");
        }
    }

    public static void main(String... args)throws Exception{
        JSONObject test = new JSONObject("{login:{\"username\":\"\",\"password\":\"\"}}");
        System.out.println(test.has("login"));
        Database.mysql.update("CREATE TEMPORARY TABLE test_table AS SELECT DISTINCT s.ID as sID,t1796.ID as tID FROM study s JOIN tag_pointer t1803 ON t1803.ID_tag = 46 AND t1803.ID_study = s.ID JOIN tag_pointer t1801 ON t1801.ID_tag = 38 AND t1801.ID_study = s.ID JOIN tag_tag l0_1801 ON  ((l0_1801.ID_tag1 = t1801.ID AND l0_1801.ID_tag2 = t1803.ID) OR (l0_1801.ID_tag1 = t1803.ID AND l0_1801.ID_tag2 = t1801.ID)) JOIN tag_pointer t1789 ON t1789.ID_tag = 12 AND t1789.ID_study = s.ID JOIN tag_tag l0_1789 ON  ((l0_1789.ID_tag1 = t1789.ID AND l0_1789.ID_tag2 = t1801.ID) OR (l0_1789.ID_tag1 = t1801.ID AND l0_1789.ID_tag2 = t1789.ID)) JOIN tag_pointer t1788 ON t1788.ID_tag = 11 AND t1788.ID_study = s.ID JOIN tag_tag l0_1788 ON  ((l0_1788.ID_tag1 = t1788.ID AND l0_1788.ID_tag2 = t1789.ID) OR (l0_1788.ID_tag1 = t1789.ID AND l0_1788.ID_tag2 = t1788.ID)) JOIN tag_pointer t1796 ON t1796.ID_tag = 37 AND t1796.ID_study = s.ID JOIN tag_tag l0_1796 ON  ((l0_1796.ID_tag1 = t1796.ID AND l0_1796.ID_tag2 = t1788.ID) OR (l0_1796.ID_tag1 = t1788.ID AND l0_1796.ID_tag2 = t1796.ID)) AND EXISTS ( SELECT d.ID FROM data d, channel c WHERE d.ID_channel = c.ID AND c.ID_study = s.ID);");
        Database.mysql.update("CREATE TEMPORARY TABLE test_table2 AS SELECT c.ID FROM test_table t, tag_channel tc, channel c WHERE t.tID = tc.ID_tag AND tc.ID_channel=c.ID;\n" );
        ResultSet resultSet = Database.mysql.query("SELECT * FROM test_table2");
        printResultSet(resultSet);
    }
}