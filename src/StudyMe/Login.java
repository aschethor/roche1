package StudyMe;

import javax.xml.crypto.Data;
import java.sql.ResultSet;

public class Login {

    public static Account createAccount(String username,String password,String name,String email) throws Exception{
        Database database = new Database();
        try {
            database.update("INSERT INTO person (username,password,name,email) VALUES (?,?,?,?)", username, password, name, email);
        }catch (Exception e){
            throw new Error("username already exists");
        }
        return login(username,password);
    }

    public static Account login(String username,String password)throws Exception{
        Database database = new Database();
        ResultSet resultSet = database.query("SELECT id FROM person WHERE username = ? AND password = ?",username,password);
        if(!resultSet.next())throw new Error("no such username or password");
        int id = resultSet.getInt("id");
        return new Account(id);
    }

}
