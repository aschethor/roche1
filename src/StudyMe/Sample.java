package StudyMe;

import java.sql.ResultSet;

public class Sample {
    private int id;
    private String creationTime;
    private String time;
    private double value;
    private String comment;

    public Sample(int id)throws Exception{
        this.id = id;
        ResultSet resultSet = Database.mysql.query("SELECT * FROM data WHERE ID = ?",""+id);
        resultSet.next();
        creationTime = resultSet.getString("time");
        time = resultSet.getString("data_time");
        value = resultSet.getDouble("data_value");
        comment = resultSet.getString("comment");
    }

    public Sample(int id,String creationTime,String time,double value,String comment){
        this.id = id;
        this.creationTime = creationTime;
        this.time = time;
        this.value = value;
        this.comment = comment;
    }

    public int getId(){
        return id;
    }
    public String getCreationTime() {
        return creationTime;
    }
    public String getTime(){
        return time;
    }
    public double getValue(){
        return value;
    }
    public String getComment() {
        return comment;
    }
}
