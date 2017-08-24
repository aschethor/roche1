package StudyMe;

import java.sql.ResultSet;
import java.util.Vector;

public class Account {
    private int id;

    public Account(int id){
        this.id = id;
    }

    public static Account getAccountByUsername(String username)throws Exception{
        ResultSet resultSet = Database.mysql.query("SELECT ID FROM person WHERE username = ?",username);
        resultSet.next();
        return new Account(resultSet.getInt("ID"));
    }

    public void setUsername(String username)throws Exception{
        Database.mysql.update("UPDATE person SET username = ? WHERE id = ?",username,""+id);
    }

    public void setPassword(String password)throws Exception{
        checkPasswordStrength(password);
        Database.mysql.update("UPDATE person SET password = ? WHERE id = ?",password,""+id);
    }

    public void setName(String name)throws Exception{
        Database.mysql.update("UPDATE person SET name = ? WHERE id = ?",name,""+id);
    }

    public void setEmail(String email)throws Exception{
        Database.mysql.update("UPDATE person SET email = ? WHERE id = ?",email,""+id);
    }

    public String getUsername() throws Exception{
        ResultSet resultSet = Database.mysql.query("SELECT username FROM person WHERE id = ?",""+id);
        if(!resultSet.next())throw new Error("Account not found");
        return resultSet.getString("username");
    }

    public String getPassword() throws Exception{
        ResultSet resultSet = Database.mysql.query("SELECT password FROM person WHERE id = ?",""+id);
        if(!resultSet.next())throw new Error("Account not found");
        return resultSet.getString("password");
    }

    public String getName() throws Exception{
        ResultSet resultSet = Database.mysql.query("SELECT name FROM person WHERE id = ?",""+id);
        if(!resultSet.next())throw new Error("Account not found");
        return resultSet.getString("name");
    }

    public String getEmail()throws Exception{
        ResultSet resultSet = Database.mysql.query("SELECT email FROM person WHERE id = ?",""+id);
        if(!resultSet.next())throw new Error("Account not found");
        return resultSet.getString("email");
    }

    public Vector<Study> getAuthoredStudies()throws Exception{
        ResultSet resultSet = Database.mysql.query("SELECT ID_study FROM author WHERE ID_person = ?",""+id);
        Vector<Study> ret = new Vector<>();
        while(resultSet.next())ret.add(new Study(resultSet.getInt("ID_study")));
        return ret;
    }

    public Study createStudy(String name)throws Exception{
        try{
            int studyId = Database.mysql.update("INSERT INTO study(name,description,ID_creator) VALUES(?,?,?)",name,"",""+id);
            Database.mysql.update("INSERT INTO author(ID_person,ID_study,ID_creator) VALUES (?,?,?)",""+id,""+studyId,""+id);
            return new Study(studyId);
        }catch (Exception e){
            throw new Error("study couldn't be created");
        }
    }

    public int getId(){
        return id;
    }

    public void deleteAccount()throws Exception{
        Database.mysql.update("DELETE FROM person WHERE id = ?",""+id);
    }

    public static void checkPasswordStrength(String pwd)throws Exception{
        if(pwd.length()<8)throw new Error("length of password too short");
        if(!pwd.matches(".*\\d+.*"))throw new Error("password must contain at least one digit");
        if(pwd.equals(pwd.toLowerCase()))throw new Error("password must contain at least one uppercase letter");
        if(pwd.equals(pwd.toUpperCase()))throw new Error("password must contain at least one lowercase letter");
    }
}