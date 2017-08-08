import StudyMe.Database;
import StudyMe.Error;
import StudyMe.Login;

import java.io.Console;
import java.sql.*;

public class Test {
    public static void main(String... args)throws Exception{
        ResultSet resultSet2 = Database.mysql.query("SELECT count(ID) FROM tag_tag WHERE (ID_tag1 = ? AND ID_tag2 = ?) OR (ID_tag1 = ? AND ID_tag2 = ?)",""+11,""+12,""+12,""+11);
        resultSet2.next();
        System.out.println(resultSet2.getInt(1));
    }
}