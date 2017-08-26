package StudyMe;

import java.sql.ResultSet;
import java.util.Vector;

public class Tag {
    private int id;
    public Tag(int id){
        this.id = id;
    }
    public void appendTag(Account account,Tag tag)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission to this study");
        Database.mysql.update("INSERT INTO tag_tag(ID_tag1,ID_tag2,ID_creator) VALUES(?,?,?)",""+id,""+tag.getId(),""+account.getId());
    }
    public static Tag createTag(Account account,Study study,String name)throws Exception{
        if(!study.hasWritePermission(account))throw new Error("You don't have write permission to this study");
        ResultSet resultSet = Database.mysql.query("SELECT id FROM tag WHERE name = ?",name);
        int tagId;
        if(resultSet.next()){
            tagId = resultSet.getInt("id");
        }else{
            tagId = Database.mysql.update("INSERT INTO tag(name,ID_creator) VALUES (?,?)",name,""+account.getId());
        }
        int tag_pointerId = Database.mysql.update("INSERT INTO tag_pointer(ID_tag,ID_study,ID_creator) VALUES(?,?,?)",""+tagId,""+study.getId(),""+account.getId());
        return new Tag(tag_pointerId);
    }
    public Study getStudy(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission to this study");
        ResultSet resultSet = Database.mysql.query("SELECT ID_study FROM tag_pointer WHERE ID = ?",""+id);
        resultSet.next();
        return new Study(resultSet.getInt("ID_study"));
    }
    public Vector<Tag> getConnectedTags(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission to this study");
        ResultSet resultSet = Database.mysql.query("SELECT t1.id FROM tag_pointer t1,tag_tag t2 WHERE (t1.ID = t2.ID_tag1 AND t2.ID_tag2 = ?) OR (t1.ID = t2.ID_tag2 AND t2.ID_tag1 = ?)",""+id,""+id);
        Vector<Tag> ret = new Vector<>();
        while(resultSet.next())ret.add(new Tag(resultSet.getInt("ID")));
        return ret;
    }
    public int getId(){
        return id;
    }
    public int getTagId(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission to this study");
        ResultSet resultSet = Database.mysql.query("SELECT ID_tag FROM tag_pointer WHERE ID = ?",""+id);
        resultSet.next();
        return resultSet.getInt("ID_tag");
    }
    public void setName(Account account,String name)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission for this study");
        ResultSet resultSet = Database.mysql.query("SELECT id FROM tag WHERE name = ?",name);
        int tagId;
        if(resultSet.next()){
            tagId = resultSet.getInt("id");
        }else{
            tagId = Database.mysql.update("INSERT INTO tag(name,ID_creator) VALUES (?,?)",name,""+account.getId());
        }
        Database.mysql.update("UPDATE tag_pointer SET ID_tag = ? WHERE ID = ?",""+tagId,""+id);
    }
    public String getName(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission to this study");
        ResultSet resultSet = Database.mysql.query("SELECT t1.name FROM tag t1,tag_pointer t2 WHERE t2.ID = ? AND t1.ID = t2.ID_tag",""+id);
        resultSet.next();
        return resultSet.getString("t1.name");
    }

    public void setViewX(Account account,double viewX)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission for this study");
        Database.mysql.update("UPDATE tag_pointer SET viewX = ? WHERE ID = ?",""+viewX,""+id);
    }

    public void setViewY(Account account,double viewY)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission for this study");
        Database.mysql.update("UPDATE tag_pointer SET viewY = ? WHERE ID = ?",""+viewY,""+id);
    }

    public double getViewX(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have permission to read this study");
        ResultSet resultSet = Database.mysql.query("SELECT viewX FROM tag_pointer WHERE ID = ?",""+id);
        resultSet.next();
        return resultSet.getDouble("viewX");
    }

    public double getViewY(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have permission to read this study");
        ResultSet resultSet = Database.mysql.query("SELECT viewY FROM tag_pointer WHERE ID = ?",""+id);
        resultSet.next();
        return resultSet.getDouble("viewY");
    }

    public boolean hasWritePermission(Account account){
        try{
            ResultSet resultSet = Database.mysql.query("SELECT a.ID_person FROM author a, tag_pointer t WHERE a.ID_person = ? AND a.ID_study = t.ID_study AND t.ID = ?",""+account.getId(),""+id);
            if(resultSet.next())return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return false;
    }

    public boolean hasReadPermission(Account account){
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if(!(o instanceof Tag))return false;
        if(id!=((Tag) o).id)return false;
        return true;
    }
}
