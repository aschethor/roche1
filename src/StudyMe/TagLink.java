package StudyMe;

import java.sql.ResultSet;

public class TagLink {
    private Tag tag1;
    private Tag tag2;
    private int id;

    public TagLink(int id)throws Exception{
        this.id = id;
        ResultSet resultSet = Database.mysql.query("SELECT * FROM tag_tag WHERE ID = ?",""+id);
        resultSet.next();
        tag1 = new Tag(resultSet.getInt("ID_tag1"));
        tag2 = new Tag(resultSet.getInt("ID_tag2"));
    }

    public TagLink(int id, Tag tag1, Tag tag2){
        this.id = id;
        this.tag1 = tag1;
        this.tag2 = tag2;
    }

    public int getId() {
        return id;
    }

    public Tag getTag1() {
        return tag1;
    }

    public Tag getTag2() {
        return tag2;
    }

    public boolean has(Tag tag){
        return (tag.getId()==tag1.getId()||tag.getId()==tag2.getId());
    }
}