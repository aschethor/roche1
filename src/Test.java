import StudyMe.Database;
import StudyMe.Error;
import StudyMe.Login;
import org.json.JSONObject;

import java.io.Console;
import java.sql.*;

public class Test {
    public static void main(String... args)throws Exception{
        JSONObject test = new JSONObject("{login:{\"username\":\"\",\"password\":\"\"}}");
        System.out.println(test.has("login"));
    }
}