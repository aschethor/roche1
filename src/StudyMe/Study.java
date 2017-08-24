package StudyMe;

import java.beans.VetoableChangeListener;
import java.sql.ResultSet;
import java.util.HashMap;
import java.util.Map;
import java.util.PriorityQueue;
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
        ResultSet resultSet = Database.mysql.query("SELECT a.ID_person FROM author a,person p WHERE a.ID_person=p.ID AND a.ID_study=? ORDER BY a.time",""+id);
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
        ResultSet resultSet = Database.mysql.query("SELECT ID FROM channel WHERE ID_study = ? ORDER BY name",""+id);
        Vector<Channel> ret = new Vector<>();
        while(resultSet.next())ret.add(new Channel(resultSet.getInt("id")));
        return ret;
    }

    public Channel createChannel(Account account,String name)throws Exception{
        if(!hasWritePermission(account))throw new Error("You aren't allowed to create channels");
        try{
            int channelId = Database.mysql.update("INSERT INTO channel(name,ID_study,ID_creator,unit,comment) VALUES(?,?,?,?,\"\")",name,""+id,""+account.getId(),"");
            return new Channel(channelId);
        }catch (Exception e){
            e.printStackTrace();
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
        if(tag1.getId()==tag2.getId())throw new Error("you have to choose 2 different tags!");
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

    public Study copyStudy(Account account,String name)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        Study ret = account.createStudy(name);
        ret.setDescription(account,getDescription(account));
        Vector<Tag> tags = getTags(account);
        Map<Integer,Integer> tagMap = new HashMap<>();
        for (int i=0;i<tags.size();i++)tagMap.put(tags.get(i).getId(),i);
        Vector<TagLink> tagLinks = getTagLinks(account);
        Vector<Channel> channels = getChannels(account);
        Vector<Tag> newTags = new Vector<>();
        for(int i=0;i<tags.size();i++){
            newTags.add(ret.createTag(account,tags.get(i).getName(account)));
            newTags.get(i).setViewX(account,tags.get(i).getViewX(account));
            newTags.get(i).setViewY(account,tags.get(i).getViewY(account));
        }
        for(TagLink tl:tagLinks)ret.linkTags(account,newTags.get(tagMap.get(tl.getTag1().getId())),newTags.get(tagMap.get(tl.getTag2().getId())));
        Vector<Channel> newChannels = new Vector<>();
        for(int i=0;i<channels.size();i++){
            newChannels.add(ret.createChannel(account,channels.get(i).getName(account)));
            newChannels.get(i).setUnit(account,channels.get(i).getUnit(account));
            for(Tag t:channels.get(i).getTags(account))newChannels.get(i).appendTag(account,newTags.get(tagMap.get(t.getId())));
            for(Sample s:channels.get(i).getSamples(account))newChannels.get(i).createSample(account,s.getTime(),s.getValue());
        }
        return ret;
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

    public Vector<Study> getSimilarStudiesSuperNaive(Account account)throws Exception{
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

    public Vector<Study> getSimilarStudiesNaive(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        Vector<Tag> tags = getTags(account);
        Vector<TagLink> tagLinks = getTagLinks(account);
        Map<Integer,Integer> tagMap = new HashMap<>();
        for (int i=0;i<tags.size();i++)tagMap.put(tags.get(i).getId(),i);
        String query = "SELECT DISTINCT s.ID FROM study s WHERE TRUE";
        //tags correspond to tags in this study and are in the same study
        for (int i=0;i<tags.size();i++)query += " AND EXISTS ( SELECT t"+i+".ID FROM tag_pointer t"+i+" WHERE t"+i+".ID_tag = "+tags.get(i).getTagId(account)+" AND t"+i+".ID_study = s.ID";
        for (int i=0;i<tagLinks.size();i++)query += " AND EXISTS ( SELECT l"+i+".ID FROM tag_tag l"+i+" WHERE" +
                " ((l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID) OR" +
                " (l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID))";
        //at least one data sample in study
        query += " AND EXISTS ( SELECT d.ID FROM data d, channel c WHERE d.ID_channel = c.ID AND c.ID_study = s.ID)";
        for (int i=0;i<tags.size();i++)query += ")";
        for (int i=0;i<tagLinks.size();i++)query += ")";

        System.out.println("Monster query:");
        System.out.println(query);
        long currentTime = System.currentTimeMillis();
        ResultSet resultSet = Database.mysql.query(query);
        System.out.println("query time: "+((double)System.currentTimeMillis()-currentTime)/1000+" s");
        Vector<Study> ret = new Vector<>();
        while (resultSet.next())ret.add(new Study(resultSet.getInt("s.ID")));
        return ret;
    }

    private class PriorityTag implements Comparable{
        Tag tag;
        int linkedElements; //number of channels & tags in the query that are linked to this tag
        int tagFrequency;   //how frequent is this tag in the database?
        PriorityTag(Tag t,int Tag_ID)throws Exception{//Tag_ID: pointer to "real tag"
            tag = t;
            linkedElements=0;
            ResultSet resultSet = Database.mysql.query("SELECT count(*) FROM tag_pointer p WHERE p.ID_tag=?",""+Tag_ID);
            resultSet.next();
            tagFrequency=resultSet.getInt(1);
        }
        void incrementLinkedElements(){
            linkedElements++;
        }
        @Override
        public int compareTo(Object o) {
            PriorityTag priorityTag = (PriorityTag)o;
            //System.out.println("Compare "+tag.getId()+" to "+priorityTag.tag.getId());
            //number of linked elements should be large
            if(linkedElements<priorityTag.linkedElements)return +1;
            if(linkedElements>priorityTag.linkedElements)return -1;
            //tag frequency should be small
            if(tagFrequency>priorityTag.tagFrequency)return +1;
            if(tagFrequency<priorityTag.tagFrequency)return -1;
            return 0;
        }
    }

    public Vector<Study> getSimilarStudies(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        PriorityQueue<PriorityTag> remainingTags = new PriorityQueue<>();
        Vector<PriorityTag> priorityTagVector = new Vector<>();
        for(Tag t:getTags(account)){
            PriorityTag priorityTag = new PriorityTag(t,t.getTagId(account));
            remainingTags.add(priorityTag);
            priorityTagVector.add(priorityTag);
        }
        Vector<TagLink> remainingTagLinks = getTagLinks(account);
        Vector<TagLink> stagedTagLinks = new Vector<>();
        int numberOfTags = remainingTags.size();
        int numberOfLinks = remainingTagLinks.size();
        String query = "SELECT DISTINCT s.ID FROM study s WHERE TRUE";
        while(remainingTags.size()!=0){
            PriorityTag tag = remainingTags.poll();
            //System.out.println("Tag poll: id: "+tag.tag.getId()+" name: "+tag.tag.getName(account)+" linkedElements: "+tag.linkedElements);
            query += " AND EXISTS ( SELECT t"+tag.tag.getId()+".ID FROM tag_pointer t"+tag.tag.getId()+" WHERE t"+tag.tag.getId()+".ID_tag = "+tag.tag.getTagId(account)+" AND t"+tag.tag.getId()+".ID_study = s.ID";
            for(int i=0;i<stagedTagLinks.size();i++){
                if(stagedTagLinks.get(i).has(tag.tag)){
                    query += " AND EXISTS ( SELECT l"+i+".ID FROM tag_tag l"+i+" WHERE" +
                            " ((l"+i+".ID_tag1 = t"+stagedTagLinks.get(i).getTag1().getId()+".ID"+
                            " AND l"+i+".ID_tag2 = t"+stagedTagLinks.get(i).getTag2().getId()+".ID) OR" +
                            " (l"+i+".ID_tag1 = t"+stagedTagLinks.get(i).getTag2().getId()+".ID"+
                            " AND l"+i+".ID_tag2 = t"+stagedTagLinks.get(i).getTag1().getId()+".ID))";
                    stagedTagLinks.remove(i);
                    i--;
                }
            }
            for(int i=0;i<remainingTagLinks.size();i++){
                if(remainingTagLinks.get(i).has(tag.tag)){
                    //increment linked elements in remainingTags - PriorityQueue
                    if(remainingTagLinks.get(i).getTag1().getId()==tag.tag.getId()){
                        for(int j=0;j<priorityTagVector.size();j++){
                            if(remainingTagLinks.get(i).getTag2().getId()==priorityTagVector.get(j).tag.getId()){
                                priorityTagVector.get(j).incrementLinkedElements();
                                remainingTags.remove(priorityTagVector.get(j));
                                remainingTags.add(priorityTagVector.get(j));
                            }
                        }
                    }else{
                        for(int j=0;j<priorityTagVector.size();j++){
                            if(remainingTagLinks.get(i).getTag1().getId()==priorityTagVector.get(j).tag.getId()){
                                priorityTagVector.get(j).incrementLinkedElements();
                                remainingTags.remove(priorityTagVector.get(j));
                                remainingTags.add(priorityTagVector.get(j));
                            }
                        }
                    }
                    stagedTagLinks.add(remainingTagLinks.get(i));
                    remainingTagLinks.remove(i);
                    i--;
                }
            }
        }
        query += " AND EXISTS ( SELECT d.ID FROM data d, channel c WHERE d.ID_channel = c.ID AND c.ID_study = s.ID)";
        for(int i=0;i<numberOfTags+numberOfLinks;i++)query+=")";
        System.out.println("Monster query:");
        System.out.println(query);
        long currentTime = System.currentTimeMillis();
        ResultSet resultSet = Database.mysql.query(query);
        System.out.println("query time: "+((double)System.currentTimeMillis()-currentTime)/1000+" s");
        Vector<Study> ret = new Vector<>();
        while (resultSet.next())ret.add(new Study(resultSet.getInt("s.ID")));
        return ret;
    }

    public Vector<Vector<Channel>> getSimilarChannelsSuperNaive(Account account)throws Exception{
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

    public Vector<Vector<Channel>> getSimilarChannelsNaive(Account account)throws Exception{
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
        for(int i=0;i<channels.size();i++){
            query += ", channel c"+i;
            channelLinks.add(channels.get(i).getTags(account));
        }
        query += " WHERE TRUE";
        for (int i=0;i<channels.size();i++){
            query += " AND c"+i+".ID_study = s.ID";
            if(!channels.get(i).getName(account).equals("?"))query += " AND c"+i+".name = '"+channels.get(i).getName(account)+"'";
            if(!channels.get(i).getUnit(account).equals("?"))query += " AND c"+i+".unit = '"+channels.get(i).getUnit(account)+"'";
        }
        //tags correspond to tags in this study and are in the same study
        for (int i=0;i<tags.size();i++)query += " AND EXISTS ( SELECT t"+i+".ID FROM tag_pointer t"+i+" WHERE t"+i+".ID_tag = "+tags.get(i).getTagId(account)+" AND t"+i+".ID_study = s.ID";
        for (int i=0;i<tagLinks.size();i++)query += " AND EXISTS ( SELECT l"+i+".ID FROM tag_tag l"+i+" WHERE" +
                " ((l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID) OR" +
                " (l"+i+".ID_tag1 = t"+tagMap.get(tagLinks.get(i).getTag2().getId())+".ID"+
                " AND l"+i+".ID_tag2 = t"+tagMap.get(tagLinks.get(i).getTag1().getId())+".ID))";
        //channels correspond to channels in this study and are in the same study
        for (int i=0;i<channels.size();i++){
            for(int j=0;j<channelLinks.get(i).size();j++){
                query += " AND EXISTS ( SELECT cl"+i+"_"+j+".ID FROM tag_channel cl"+i+"_"+j+" WHERE cl"+i+"_"+j+".ID_channel = c"+i+".ID AND cl"+i+"_"+j+".ID_tag = t"+tagMap.get(channelLinks.get(i).get(j).getId())+".ID";
            }
        }
        //at least one data sample in study
        query += " AND EXISTS ( SELECT d.ID FROM data d, channel c WHERE d.ID_channel = c.ID AND c.ID_study = s.ID)";

        for (int i=0;i<tags.size();i++)query += ")";
        for (int i=0;i<tagLinks.size();i++)query += ")";
        for (int i=0;i<channels.size();i++)for(int j=0;j<channelLinks.get(i).size();j++)query+=")";

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

    public Vector<Vector<Channel>> getSimilarChannels(Account account)throws Exception{
        if(!hasReadPermission(account))throw new Error("You aren't allowed to read this study");
        Vector<Channel> channels = getChannels(account);
        if(channels.size()<=0)throw new Error("The study needs at least one channel in order to search for similar channels");
        Vector<Vector<Tag>> channelLinks = new Vector<>();
        PriorityQueue<PriorityTag> remainingTags = new PriorityQueue<>();
        Vector<PriorityTag> priorityTagVector = new Vector<>();
        for(Tag t:getTags(account)){
            PriorityTag priorityTag = new PriorityTag(t,t.getTagId(account));
            remainingTags.add(priorityTag);
            priorityTagVector.add(priorityTag);
        }
        Vector<TagLink> remainingTagLinks = getTagLinks(account);
        Vector<TagLink> stagedTagLinks = new Vector<>();
        int numberOfTags = remainingTags.size();
        int numberOfLinks = remainingTagLinks.size();
        String query = "SELECT s.ID";
        for(int i=0;i<channels.size();i++)query += ", c"+i+".ID";
        query += " FROM study s";
        for(int i=0;i<channels.size();i++){
            query += ", channel c"+i;
            channelLinks.add(channels.get(i).getTags(account));
            for(Tag tag:channelLinks.get(i)) {
                for (int j = 0; j < priorityTagVector.size(); j++) {
                    if (tag.getId() == priorityTagVector.get(j).tag.getId()) {
                        priorityTagVector.get(j).incrementLinkedElements();
                        remainingTags.remove(priorityTagVector.get(j));
                        remainingTags.add(priorityTagVector.get(j));
                    }
                }
            }
        }
        query += " WHERE TRUE";
        for (int i=0;i<channels.size();i++){
            query += " AND c"+i+".ID_study = s.ID";
            if(!channels.get(i).getName(account).equals("?"))query += " AND c"+i+".name = '"+channels.get(i).getName(account)+"'";
            if(!channels.get(i).getUnit(account).equals("?"))query += " AND c"+i+".unit = '"+channels.get(i).getUnit(account)+"'";
        }
        while(remainingTags.size()!=0){
            PriorityTag tag = remainingTags.poll();
            //System.out.println("Tag poll: id: "+tag.tag.getId()+" name: "+tag.tag.getName(account)+" linkedElements: "+tag.linkedElements);
            query += " AND EXISTS ( SELECT t"+tag.tag.getId()+".ID FROM tag_pointer t"+tag.tag.getId()+" WHERE t"+tag.tag.getId()+".ID_tag = "+tag.tag.getTagId(account)+" AND t"+tag.tag.getId()+".ID_study = s.ID";
            for(int i=0;i<channelLinks.size();i++){
                for(Tag t:channelLinks.get(i)) {
                    if (t.getId() == tag.tag.getId()) {
                        query += " AND EXISTS ( SELECT cl.ID FROM tag_channel cl WHERE cl.ID_channel = c" + i + ".ID AND cl.ID_tag = t" + tag.tag.getId() + ".ID";
                    }
                }
            }
            for(int i=0;i<stagedTagLinks.size();i++){
                if(stagedTagLinks.get(i).has(tag.tag)){
                    query += " AND EXISTS ( SELECT l"+i+".ID FROM tag_tag l"+i+" WHERE" +
                            " ((l"+i+".ID_tag1 = t"+stagedTagLinks.get(i).getTag1().getId()+".ID"+
                            " AND l"+i+".ID_tag2 = t"+stagedTagLinks.get(i).getTag2().getId()+".ID) OR" +
                            " (l"+i+".ID_tag1 = t"+stagedTagLinks.get(i).getTag2().getId()+".ID"+
                            " AND l"+i+".ID_tag2 = t"+stagedTagLinks.get(i).getTag1().getId()+".ID))";
                    stagedTagLinks.remove(i);
                    i--;
                }
            }
            for(int i=0;i<remainingTagLinks.size();i++){
                if(remainingTagLinks.get(i).has(tag.tag)){
                    //increment linked elements in remainingTags - PriorityQueue
                    if(remainingTagLinks.get(i).getTag1().getId()==tag.tag.getId()){
                        for(int j=0;j<priorityTagVector.size();j++){
                            if(remainingTagLinks.get(i).getTag2().getId()==priorityTagVector.get(j).tag.getId()){
                                priorityTagVector.get(j).incrementLinkedElements();
                                remainingTags.remove(priorityTagVector.get(j));
                                remainingTags.add(priorityTagVector.get(j));
                            }
                        }
                    }else{
                        for(int j=0;j<priorityTagVector.size();j++){
                            if(remainingTagLinks.get(i).getTag1().getId()==priorityTagVector.get(j).tag.getId()){
                                priorityTagVector.get(j).incrementLinkedElements();
                                remainingTags.remove(priorityTagVector.get(j));
                                remainingTags.add(priorityTagVector.get(j));
                            }
                        }
                    }
                    stagedTagLinks.add(remainingTagLinks.get(i));
                    remainingTagLinks.remove(i);
                    i--;
                }
            }
        }
        query += " AND EXISTS ( SELECT d.ID FROM data d, channel c WHERE d.ID_channel = c.ID AND c.ID_study = s.ID)";
        for(int i=0;i<numberOfTags+numberOfLinks;i++)query+=")";
        for(Vector<Tag> cl:channelLinks)for(Tag t:cl)query+=")";
        query += "ORDER BY s.ID";
        for(int i=0;i<channels.size();i++)query+=", c"+i+".name";
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