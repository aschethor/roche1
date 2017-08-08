package StudyMe;

import java.beans.VetoableChangeListener;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

public class Study {
    private int id;

    public Study(int id){
        this.id = id;
    }

    public int getId(){
        return id;
    }

    public String getName(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't permitted to read this study");
        ResultSet resultSet = Database.mysql.query("SELECT name FROM study WHERE id = ?",""+id);
        if(!resultSet.next())throw new Error("study not found");
        return resultSet.getString("name");
    }

    public void setName(Account account,String name)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't permitted to change the name of this study");
        Database.mysql.update("UPDATE study SET name = ? WHERE id = ?",name,""+id);
    }

    public String getDescription(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't permitted to read this study");
        ResultSet resultSet = Database.mysql.query("SELECT description FROM study WHERE id = ?",""+id);
        if(!resultSet.next())throw new Error("study not found");
        return resultSet.getString("description");
    }

    public void setDescription(Account account,String description)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't permitted to change the description of this study");
        Database.mysql.update("UPDATE study SET description = ? WHERE id = ?",description,""+id);
    }

    public void addAuthor(Account account,Account author)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't permitted to add Authors");
        Database.mysql.update("INSERT INTO author(ID_person,ID_study,ID_creator) VALUES (?,?,?)",""+author.getId(),""+id,""+account.getId());
    }

    public Vector<Account> getAuthors(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        ResultSet resultSet = Database.mysql.query("SELECT a.ID_person FROM author a,person p WHERE a.ID_person=p.ID AND a.ID_study=?",""+id);
        Vector<Account> ret = new Vector<>();
        while(resultSet.next())ret.add(new Account(resultSet.getInt("ID_person")));
        return ret;
    }

    public void removeAuthor(Account account,Account author)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't permitted to remove Authors");
        //check if last author
        ResultSet resultSet1 = Database.mysql.query("SELECT ID_person FROM author WHERE ID_study = ?",""+id);
        if(!resultSet1.next())throw new Error("can't remove author! Study has no authors!");
        if(!resultSet1.next())throw new Error("can't remove author! Only one author left!");
        Database.mysql.update("DELETE FROM author WHERE ID_person = ? AND ID_study = ?",""+author.getId(),""+id);
    }

    public Vector<Channel> getChannels(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        ResultSet resultSet = Database.mysql.query("SELECT ID FROM channel WHERE ID_study = ?",""+id);
        Vector<Channel> ret = new Vector<>();
        while(resultSet.next())ret.add(new Channel(resultSet.getInt("id")));
        return ret;
    }

    public Channel createChannel(Account account,String name)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't allowed to create channels");
        try{
            int channelId = Database.mysql.update("INSERT INTO channel(name,ID_study,ID_creator,unit) VALUES(?,?,?,?)",name,""+id,""+account.getId(),"");
            return new Channel(channelId);
        }catch (Exception e){
            throw new Error("study couldn't be created. Make sure channel name is unique in this study.");
        }
    }

    public void deleteChannel(Account account,Channel channel) throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't allowed to delete channels");
        Database.mysql.update("DELETE FROM channel WHERE id = ?",""+channel.getId());
    }

    public Tag createTag(Account account,String name)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't allowed to edit this study");
        Tag tag = Tag.createTag(account,this,name);
        return tag;
    }

    public Vector<Tag> getTags(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to edit this study");
        ResultSet resultSet = Database.mysql.query("SELECT ID FROM tag_pointer WHERE ID_study = ?",""+id);
        Vector<Tag> ret = new Vector<>();
        while (resultSet.next())ret.add(new Tag(resultSet.getInt("ID")));
        return ret;
    }

    public void removeTag(Account account,Tag tag)throws Exception{
        if(!hasWritePermission(account))throw new Error("You don't have write permission to this study");
        Database.mysql.update("DELETE FROM tag_pointer WHERE ID = ? AND ID_study = ?",""+tag.getId(),""+id);
    }

    public TagLink linkTags(Account account,Tag tag1,Tag tag2)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't allowed to edit this study");
        //check if both tags contained in study
        ResultSet resultSet = Database.mysql.query("SELECT count(ID) FROM tag_pointer WHERE (ID = ? AND ID_study = ?) OR (ID = ? AND ID_study = ?)",""+tag1.getId(),""+id,""+tag2.getId(),""+id);
        resultSet.next();
        if(resultSet.getInt(1)!=2)throw new Exception("You've chosen wrong tags");
        //check link doesn't already exist
        ResultSet resultSet2 = Database.mysql.query("SELECT count(ID) FROM tag_tag WHERE (ID_tag1 = ? AND ID_tag2 = ?) OR (ID_tag1 = ? AND ID_tag2 = ?)",""+tag1.getId(),""+tag2.getId(),""+tag2.getId(),""+tag1.getId());
        if(!resultSet2.next())throw new Error("linking error");
        if(resultSet2.getInt(1)>0)throw new Error("link already exists");
        int tagLinkID = Database.mysql.update("INSERT INTO tag_tag(ID_tag1,ID_tag2,ID_creator) VALUES(?,?,?)",""+tag1.getId(),""+tag2.getId(),""+account.getId());
        return new TagLink(tagLinkID,tag1,tag2);
    }

    public Vector<TagLink> getTagLinks(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        ResultSet resultSet = Database.mysql.query("SELECT t.ID, t.ID_tag1, t.ID_tag2 FROM tag_tag t,tag_pointer p WHERE t.ID_tag1 = p.ID AND p.ID_study = ?",""+id);
        Vector<TagLink> ret = new Vector<>();
        while(resultSet.next())ret.add(new TagLink(resultSet.getInt("ID"),new Tag(resultSet.getInt("ID_tag1")),new Tag(resultSet.getInt("ID_tag2"))));
        return ret;
    }

    public void removeTagLink(Account account,TagLink tagLink)throws Exception{
        removeTagLink(account,tagLink.getTag1(),tagLink.getTag2());
    }

    public void removeTagLink(Account account,Tag tag1,Tag tag2)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't allowed to edit this study");
        //check if both tags contained in study
        ResultSet resultSet = Database.mysql.query("SELECT count(ID) FROM tag_pointer WHERE (ID = ? AND ID_study = ?) OR (ID = ? AND ID_study = ?)",""+tag1.getId(),""+id,""+tag2.getId(),""+id);
        resultSet.next();
        if(resultSet.getInt(1)!=2)throw new Exception("You've chosen wrong tags");
        Database.mysql.update("DELETE FROM tag_tag WHERE (ID_tag1 = ? AND ID_tag2 = ?) OR (ID_tag1 = ? AND ID_tag2 = ?)",""+tag1.getId(),""+tag2.getId(),""+tag2.getId(),""+tag1.getId());
    }

    public void deleteStudy(Account account)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't allowed to delete this study");
        //check last author
        ResultSet resultSet = Database.mysql.query("SELECT COUNT(ID_person) FROM author WHERE ID_study = ?",""+id);
        resultSet.next();
        if(resultSet.getInt(1)>1)throw new Error("can't delete study because there are still other authors");
        //delete study
        Database.mysql.update("DELETE FROM study WHERE id = ?",""+id);
    }

    public Vector<Study> getSimilarStudies(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        Vector<Tag> tags = getTags(account);
        Vector<TagLink> tagLinks = getTagLinks(account);
        Map<Integer,Integer> tagMap = new HashMap<>();
        for (int i=0;i<tags.size();i++)tagMap.put(tags.get(i).getId(),i);
        String query = "SELECT DISTINCT s.ID FROM study s";
        for(int i=0;i<tags.size();i++)query += ", tag_pointer t"+i;
        for(int i=0;i<tagLinks.size();i++)query += ", tag_tag l"+i;
        query += " WHERE TRUE";
        //tags correspond to tags in this study and are in the same study
        for (int i=0;i<tags.size();i++)query += " AND t"+i+".ID_tag = "+tags.get(i).getTagId(account)+" AND t"+i+".ID_study = s.ID";
        //tag-links correspond to tag-links in this study
        for (int i=0;i<tagLinks.size();i++)query += " AND ((l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID) OR " +
                "(l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID))";
        System.out.println("Monster query:");
        System.out.println(query);
        long currentTime = System.currentTimeMillis();
        ResultSet resultSet = Database.mysql.query(query);
        System.out.println("query time: "+((double)System.currentTimeMillis()-currentTime)/1000+" s");
        Vector<Study> ret = new Vector<>();
        while (resultSet.next())ret.add(new Study(resultSet.getInt("s.ID")));
        return ret;
    }

    public Vector<Study> getSimilarStudies2(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        Vector<Tag> tags = getTags(account);
        Vector<TagLink> tagLinks = getTagLinks(account);
        Map<Integer,Integer> tagMap = new HashMap<>();
        for (int i=0;i<tags.size();i++)tagMap.put(tags.get(i).getId(),i);
        String query = "SELECT DISTINCT s.ID FROM study s";
        for(int i=tags.size()-1;i>=0;i--)query += ", tag_pointer t"+i;
        for(int i=0;i<tagLinks.size();i++)query += ", tag_tag l"+i;
        query += " WHERE TRUE";
        //tags correspond to tags in this study and are in the same study
        for (int i=tags.size()-1;i>=0;i--)query += " AND t"+i+".ID_tag = "+tags.get(i).getTagId(account)+" AND t"+i+".ID_study = s.ID";
        //tag-links correspond to tag-links in this study
        for (int i=0;i<tagLinks.size();i++)query += " AND ((l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID) OR " +
                "(l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID))";
        System.out.println("Monster query:");
        System.out.println(query);
        long currentTime = System.currentTimeMillis();
        ResultSet resultSet = Database.mysql.query(query);
        System.out.println("query time: "+((double)System.currentTimeMillis()-currentTime)/1000+" s");
        Vector<Study> ret = new Vector<>();
        while (resultSet.next())ret.add(new Study(resultSet.getInt("s.ID")));
        return ret;
    }

    public Vector<Vector<Channel>> getSimilarChannels(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        Vector<Channel> channels = getChannels(account);
        if(channels.size()<=0)throw new Error("The study needs at least one channel in order to search for similar channels");
        Vector<Vector<Tag>> channelLinks = new Vector<>();
        Vector<Tag> tags = getTags(account);
        Vector<TagLink> tagLinks = getTagLinks(account);
        Map<Integer,Integer> tagMap = new HashMap<>();
        for (int i=0;i<tags.size();i++)tagMap.put(tags.get(i).getId(),i);
        String query = "SELECT s.ID";
        for(int i=0;i<channels.size();i++)query += ", c"+i+".ID";
        query += " FROM study s";
        for(int i=0;i<tags.size();i++)query += ", tag_pointer t"+i;
        for(int i=0;i<tagLinks.size();i++)query += ", tag_tag l"+i;
        for(int i=0;i<channels.size();i++){
            query += ", channel c"+i;
            channelLinks.add(channels.get(i).getTags(account));
            for(int j=0;j<channelLinks.get(i).size();j++)query += ", tag_channel cl"+i+"_"+j;
        }
        query += " WHERE TRUE";
        //tags correspond to tags in this study and are in the same study
        for (int i=0;i<tags.size();i++)query += " AND t"+i+".ID_tag = "+tags.get(i).getTagId(account)+" AND t"+i+".ID_study = s.ID";
        //tag-links correspond to tag-links in this study
        for (int i=0;i<tagLinks.size();i++)query += " AND ((l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID) OR " +
                "(l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID))";
        //channels correspond to channels in this study and are in the same study
        for (int i=0;i<channels.size();i++){
            query += " AND c"+i+".ID_study = s.ID";
            if(!channels.get(i).getName(account).equals("?"))query += " AND c"+i+".name = '"+channels.get(i).getName(account)+"'";
            if(!channels.get(i).getUnit(account).equals("?"))query += " AND c"+i+".unit = '"+channels.get(i).getUnit(account)+"'";
            for(int j=0;j<channelLinks.get(i).size();j++){
                query += " AND cl"+i+"_"+j+".ID_channel = c"+i+".ID AND cl"+i+"_"+j+".ID_tag = t"+tagMap.get(channelLinks.get(i).get(j).getId())+".ID";
            }
        }
        System.out.println("Super Monster query:");
        System.out.println(query);
        long currentTime = System.currentTimeMillis();
        ResultSet resultSet = Database.mysql.query(query);
        System.out.println("query time: "+((double)System.currentTimeMillis()-currentTime)/1000+" s");
        Vector<Vector<Channel>> ret = new Vector<>();
        while (resultSet.next()){
            Vector<Channel> channelVector = new Vector<>();
            for(int i=0;i<channels.size();i++)channelVector.add(new Channel(resultSet.getInt("c"+i+".ID")));
            ret.add(channelVector);
        }
        return ret;
    }

    public boolean hasWritePermission(Account account){
        try{
            ResultSet resultSet = Database.mysql.query("SELECT ID_person FROM author WHERE ID_person = ? AND ID_study = ?",""+account.getId(),""+id);
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