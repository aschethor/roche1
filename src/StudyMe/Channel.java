package StudyMe;

import java.sql.ResultSet;
import java.util.Vector;

public class Channel {
    private int id;

    public Channel(int id){
        this.id = id;
    }

    public String getName(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission for this channel");
        ResultSet resultSet = Database.mysql.query("SELECT name FROM channel WHERE ID = ?",""+id);
        if(!resultSet.next())throw new Error("channel not found");
        return resultSet.getString("name");
    }

    public void setName(Account account,String name)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission for this channel");
        Database.mysql.update("UPDATE channel SET name = ? WHERE id = ?",name,""+id);
    }

    public String getUnit(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission for this channel");
        ResultSet resultSet = Database.mysql.query("SELECT unit FROM channel WHERE ID = ?",""+id);
        if(!resultSet.next())throw new Error("channel not found");
        return resultSet.getString("unit");
    }

    public void setUnit(Account account,String unit)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission for this channel");
        Database.mysql.update("UPDATE channel SET unit = ? WHERE id = ?",unit,""+id);
    }

    //eventually with range parameters
    public Vector<Sample> getSamples(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission for this channel");
        ResultSet resultSet = Database.mysql.query("SELECT * FROM data WHERE ID_channel = ? ORDER BY data_time ASC",""+id);
        Vector<Sample> ret = new Vector<>();
        while(resultSet.next()){
            ret.add(new Sample(resultSet.getInt("id"),resultSet.getString("time"),resultSet.getString("data_time"),resultSet.getDouble("data_value")));
        }
        return ret;
    }

    public Sample createSample(Account account,double value)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have the author rights to add data to this channel");
        int sample_id = Database.mysql.update("INSERT INTO data(ID_channel,data_value,ID_creator) VALUES (?,?,?)",""+id,""+value,""+account.getId());
        return new Sample(sample_id);
    }

    public Sample createSample(Account account,String time,double value)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have the author rights to add data to this channel");
        int sample_id = Database.mysql.update("INSERT INTO data(ID_channel,data_time,data_value,ID_creator) VALUES (?,?,?,?)",""+id,time,""+value,""+account.getId());
        return new Sample(sample_id);
    }

    public void removeSample(Account account,Sample sample)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission to this channel");
        Database.mysql.update("DELETE FROM data WHERE ID = ?",""+sample.getId());
    }

    public int getId(){
        return id;
    }

    public void appendTag(Account account,Tag tag)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have the permission to append a tag on this channel");
        Database.mysql.update("INSERT INTO tag_channel(ID_tag,ID_channel,ID_creator) VALUES(?,?,?)",""+tag.getId(),""+id,""+account.getId());
    }

    public Tag createTag(Account account,String name)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have the permission to create a tag for this channel");
        Tag tag = Tag.createTag(account,getStudy(account),name);
        appendTag(account,tag);
        return tag;
    }

    public Vector<Tag> getTags(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permissions for this study");
        ResultSet resultSet = Database.mysql.query("SELECT ID_tag FROM tag_channel WHERE ID_channel = ?",""+id);
        Vector<Tag> ret = new Vector<>();
        while(resultSet.next())ret.add(new Tag(resultSet.getInt("ID_tag")));
        return ret;
    }

    public void removeTag(Account account,Tag tag)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have the permission to edit this channel");
        Database.mysql.update("DELETE FROM tag_channel WHERE ID_tag = ? AND ID_channel = ?",""+tag.getId(),""+id);
    }

    public Study getStudy(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You don't have read permission to this study");
        ResultSet resultSet = Database.mysql.query("SELECT ID_study FROM channel WHERE ID = ?",""+id);
        resultSet.next();
        return new Study(resultSet.getInt("ID_study"));
    }

    public boolean hasWritePermission(Account account){
        try{
            ResultSet resultSet = Database.mysql.query("SELECT a.ID_person FROM author a, channel c WHERE a.ID_person = ? AND a.ID_study = c.ID_study AND c.ID = ?",""+account.getId(),""+id);
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
}
