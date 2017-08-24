package StudyMe;

import javax.xml.crypto.Data;
import java.sql.ResultSet;

public class Login {

    public static Account createAccount(String username,String password,String name,String email) throws Exception{
        Account.checkPasswordStrength(password);
        try {
            Database.mysql.update("INSERT INTO person (username,password,name,email) VALUES (?,?,?,?)", username, password, name, email);
        }catch (Exception e){
            throw new Error("username already exists");
        }
        return login(username,password);
    }

    public static Account login(String username,String password)throws Exception{
        ResultSet resultSet = Database.mysql.query("SELECT id FROM person WHERE username = ? AND password = ?",username,password);
        if(!resultSet.next())throw new Error("no such username or password");
        int id = resultSet.getInt("id");
        return new Account(id);
    }

}
