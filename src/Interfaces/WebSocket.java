package Interfaces;

import Interfaces.ServerLib.WSServer;
import StudyMe.*;
import org.json.JSONObject;

import java.text.NumberFormat;
import java.util.Vector;

public class WebSocket extends WSServer.WebSocket {

    @Override
    public void onStart() throws Exception {
        System.out.println("WS-Start");

    }

    @Override
    public void onMessage(String message) throws Exception {
        System.out.println("WS-Message: "+message);
        JSONObject msg = new JSONObject(message);
        if(msg.has("login"))handleLogin((JSONObject)msg.get("login"));
        if(msg.has("signup"))handleSignup((JSONObject)msg.get("signup"));
        if(msg.has("home"))handleHome((JSONObject)msg.get("home"));
        if(msg.has("account"))handleAccount((JSONObject)msg.get("account"));
        if(msg.has("account_change"))handleAccountChange((JSONObject)msg.get("account_change"));
        if(msg.has("study"))handleStudy((JSONObject)msg.get("study"));
        if(msg.has("create_study"))handleCreateStudy((JSONObject)msg.get("create_study"));
        if(msg.has("study_change"))handleStudyChange((JSONObject)msg.get("study_change"));
        if(msg.has("channel"))handleChannel((JSONObject)msg.get("channel"));
        if(msg.has("channel_change"))handleChannelChange((JSONObject)msg.get("channel_change"));
        if(msg.has("query"))handleQuery((JSONObject)msg.get("query"));
    }

    @Override
    public void onEnd() {

    }

    public void sendError(String msg) throws Exception{
        send(new JSONObject().put("error", msg).toString());
    }

    public void sendLogout() throws Exception{
        send(new JSONObject().put("logout","").toString());
    }

    public void handleQuery(JSONObject msg)throws Exception{
        try {
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username, password);
            int study_id = msg.getInt("id");
            Study study = new Study(study_id);
            if(msg.has("similar_studies")){
                try {
                    Vector<Study> studies = study.getSimilarStudies(account);
                    for (Study s : studies)
                        send(new JSONObject().put("similar_study", new JSONObject().put("name", s.getName(account)).put("id", s.getId())).toString());
                }catch (Exception e){
                    e.printStackTrace();
                    sendError("query too complicated");
                }
            }else if(msg.has("similar_channels")){
                try {
                    Vector<Vector<Channel>> studies = study.getSimilarChannels(account);
                    for(Vector<Channel> channels:studies){
                        JSONObject obj = new JSONObject();
                        for(int i=0;i<channels.size();i++){
                            obj.put("c_"+i,new JSONObject().put("id",channels.get(i).getId()).put("name",channels.get(i).getName(account)).put("unit",channels.get(i).getUnit(account)));
                        }
                        obj.put("s_id",channels.get(0).getStudy(account).getId());
                        obj.put("s_name",channels.get(0).getStudy(account).getName(account));
                        send(new JSONObject().put("similar_channels",obj).toString());
                    }
                }catch (Exception e){
                    e.printStackTrace();
                    sendError("study needs at least one channel or query too complicated");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Ouups, something went wrong");
            sendLogout();
        }
    }

    public void handleChannelChange(JSONObject msg)throws Exception{
        try {
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username, password);
            int channel_id = msg.getInt("id");
            Channel channel = new Channel(channel_id);
            if(msg.has("create_sample")) {
                try {
                    msg = (JSONObject) msg.get("create_sample");
                    double value = Double.parseDouble(msg.getString("value"));
                    String time = msg.getString("time");
                    Sample s;
                    if (time.equals("")) s = channel.createSample(account, value);
                    else s = channel.createSample(account, time, value);
                    send(new JSONObject().put("sample", new JSONObject().put("time", s.getTime()).put("value", s.getValue()).put("id", s.getId())).toString());
                } catch (Exception e) {
                    e.printStackTrace();
                    sendError("wrong input format");
                }
            }else if(msg.has("remove_sample")){
                channel.removeSample(account,new Sample(msg.getInt("remove_sample")));
                send(new JSONObject().put("remove_sample",msg.getInt("remove_sample")).toString());
            }else if(msg.has("change_name")){
                channel.setName(account,msg.getString("change_name"));
                send(new JSONObject().put("channel_name",channel.getName(account)).toString());
            }else if(msg.has("change_unit")){
                channel.setUnit(account,msg.getString("change_unit"));
                send(new JSONObject().put("channel_unit",channel.getUnit(account)).toString());
            }else if(msg.has("link_tag")){
                channel.appendTag(account,new Tag(msg.getInt("link_tag")));
                send(new JSONObject().put("link_node",msg.getString("link_tag")).toString());
            }else if(msg.has("unlink_tag")){
                channel.removeTag(account,new Tag(msg.getInt("unlink_tag")));
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Ouups, something went wrong");
            //sendLogout();
        }
    }

    public void handleChannel(JSONObject msg)throws Exception {
        try {
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username, password);
            int channel_id = msg.getInt("id");
            Channel channel = new Channel(channel_id);
            send(new JSONObject().put("channel_name",channel.getName(account)).toString());
            send(new JSONObject().put("channel_unit",channel.getUnit(account)).toString());
            send(new JSONObject().put("study_id",channel.getStudy(account).getId()).toString());
            for(Sample s:channel.getSamples(account))send(new JSONObject().put("sample",new JSONObject().put("time",s.getTime()).put("value",s.getValue()).put("id",s.getId())).toString());
            JSONObject design = new JSONObject();
            Vector<Tag> tags = channel.getStudy(account).getTags(account);
            for(int i=0;i<tags.size();i++)design.put("tag"+i,new JSONObject().put("name",tags.get(i).getName(account)).put("id",tags.get(i).getId()).put("x",tags.get(i).getViewX(account)).put("y",tags.get(i).getViewY(account)));
            Vector<TagLink> links = channel.getStudy(account).getTagLinks(account);
            for(int i=0;i<links.size();i++)design.put("link"+i,new JSONObject().put("tag1",links.get(i).getTag1().getId()).put("tag2",links.get(i).getTag2().getId()));
            send(new JSONObject().put("design",design).toString());
            for(Tag tag:channel.getTags(account))send(new JSONObject().put("link_node",tag.getId()).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Ouups, something went wrong");
            sendLogout();
        }
    }

    public void handleStudyChange(JSONObject msg)throws Exception{
        try {
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username, password);
            int study_id = msg.getInt("id");
            Study study = new Study(study_id);
            if(msg.has("delete_study")){
                try {
                    study.deleteStudy(account);
                    send(new JSONObject().put("home", "").toString());
                }catch(Exception e){
                    sendError("only last author can delete study");
                }
            }else if(msg.has("copy_study")){
                try{
                    Study s = study.copyStudy(account,msg.getString("copy_study"));
                    send(new JSONObject().put("goto_study",s.getId()).toString());
                }catch (Exception e){
                    sendError("study name already taken");
                }
            }else if(msg.has("change_description")){
                study.setDescription(account,msg.getString("change_description"));
                send(new JSONObject().put("description",study.getDescription(account)).toString());
            }else if(msg.has("change_study_name")){
                try {
                    study.setName(account, msg.getString("change_study_name"));
                    send(new JSONObject().put("study_name",study.getName(account)).toString());
                }catch (Exception e){
                    sendError("study name already taken");
                }
            }else if(msg.has("add_author")){
                try {
                    Account author = Account.getAccountByUsername(msg.getString("add_author"));
                    study.addAuthor(account, author);
                    send(new JSONObject().put("author", new JSONObject().put("name", author.getName()).put("id", author.getId())).toString());
                }catch (Exception e){
                    sendError("username not found or already author");
                }
            }else if(msg.has("remove_author")){
                Account author = new Account(msg.getInt("remove_author"));
                try {
                    study.removeAuthor(account, author);
                    if(account.getId()==author.getId())send(new JSONObject().put("home","").toString());
                    else send(new JSONObject().put("remove_author",author.getId()).toString());
                }catch (Exception e){
                    sendError("study needs at least one author");
                }
            }else if(msg.has("create_channel")){
                Channel channel = study.createChannel(account,msg.getString("create_channel"));
                send(new JSONObject().put("channel",new JSONObject().put("name",channel.getName(account)).put("id",channel.getId()).put("unit",channel.getUnit(account))).toString());
            }else if(msg.has("remove_channel")){
                Channel channel = new Channel(msg.getInt("remove_channel"));
                study.deleteChannel(account,channel);
                send(new JSONObject().put("remove_channel",channel.getId()).toString());
            }else if(msg.has("create_tag")){
                Tag tag = study.createTag(account,msg.getString("create_tag"));
                send(new JSONObject().put("add_tag",new JSONObject().put("name",tag.getName(account)).put("id",tag.getId())).toString());
            }else if(msg.has("move_tag")){
                msg = (JSONObject)msg.get("move_tag");
                Tag tag = new Tag(msg.getInt("id"));
                double x = msg.getDouble("x");
                double y = msg.getDouble("y");
                tag.setViewX(account,x);
                tag.setViewY(account,y);
            }else if(msg.has("remove_tag")){
                Tag tag = new Tag(msg.getInt("remove_tag"));
                study.removeTag(account,tag);
            }else if(msg.has("add_link")){
                msg = (JSONObject)msg.get("add_link");
                Tag tag1 = new Tag(msg.getInt("tag1"));
                Tag tag2 = new Tag(msg.getInt("tag2"));
                study.linkTags(account,tag1,tag2);
            }else if(msg.has("remove_link")){
                msg = (JSONObject)msg.get("remove_link");
                Tag tag1 = new Tag(msg.getInt("tag1"));
                Tag tag2 = new Tag(msg.getInt("tag2"));
                study.removeTagLink(account,tag1,tag2);
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Ouups, something went wrong");
            sendLogout();
        }
    }

    public void handleCreateStudy(JSONObject msg)throws Exception {
        try {
            String username = msg.getString("username");
            String password = msg.getString("password");
            String study_name = msg.getString("study_name");
            Account account = Login.login(username, password);
            try {
                Study study = account.createStudy(study_name);
                send(new JSONObject().put("goto_study", study.getId()).toString());
            } catch (Exception e) {
                sendError("Study name already taken");
            }
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Ouups, something went wrong");
            sendLogout();
        }
    }

    public void handleStudy(JSONObject msg)throws Exception {
        try {
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username, password);
            int study_id = msg.getInt("id");
            Study study = new Study(study_id);
            send(new JSONObject().put("study_name",study.getName(account)).toString());
            send(new JSONObject().put("description",study.getDescription(account)).toString());
            for(Account a:study.getAuthors(account))send(new JSONObject().put("author",new JSONObject().put("name",a.getName()).put("id",a.getId())).toString());
            for(Channel c:study.getChannels(account))send(new JSONObject().put("channel",new JSONObject().put("name",c.getName(account)).put("id",c.getId()).put("unit",c.getUnit(account))).toString());
            JSONObject design = new JSONObject();
            Vector<Tag> tags = study.getTags(account);
            for(int i=0;i<tags.size();i++)design.put("tag"+i,new JSONObject().put("name",tags.get(i).getName(account)).put("id",tags.get(i).getId()).put("x",tags.get(i).getViewX(account)).put("y",tags.get(i).getViewY(account)));
            Vector<TagLink> links = study.getTagLinks(account);
            for(int i=0;i<links.size();i++)design.put("link"+i,new JSONObject().put("tag1",links.get(i).getTag1().getId()).put("tag2",links.get(i).getTag2().getId()));
            send(new JSONObject().put("design",design).toString());
        } catch (Exception e) {
            e.printStackTrace();
            sendError("Ouups, something went wrong");
            //sendLogout();
        }
    }

    public void handleAccountChange(JSONObject msg)throws Exception{
        try{
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username,password);
            if(msg.has("change_username")){
                try {
                    account.setUsername(msg.getString("change_username"));
                    send(new JSONObject().put("username",account.getUsername()).toString());
                }catch (Exception e){
                    sendError("username already taken");
                }
            }else if(msg.has("change_password")){
                account.setPassword(msg.getString("change_password"));
                send(new JSONObject().put("password",account.getPassword()).toString());
            }else if(msg.has("change_name")){
                account.setName(msg.getString("change_name"));
                send(new JSONObject().put("name",account.getName()).toString());
            }else if(msg.has("change_email")){
                account.setEmail(msg.getString("change_email"));
                send(new JSONObject().put("email",account.getEmail()).toString());
            }else if(msg.has("delete_account")){
                account.deleteAccount();
                sendLogout();
            }
        }catch (Exception e){
            sendError("Ouups, something went wrong");
            sendLogout();
        }
    }

    public void handleAccount(JSONObject msg)throws Exception{
        try{
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username,password);
            send(new JSONObject().put("name",account.getName()).toString());
            send(new JSONObject().put("username",account.getUsername()).toString());
            send(new JSONObject().put("email",account.getEmail()).toString());
        }catch (Exception e){
            sendError("Ouups, something went wrong");
            sendLogout();
        }
    }

    public void handleHome(JSONObject msg)throws Exception{
        try{
            String username = msg.getString("username");
            String password = msg.getString("password");
            Account account = Login.login(username,password);
            send(new JSONObject().put("name",account.getName()).toString());
            Vector<Study> studies = account.getAuthoredStudies();
            for(Study study:studies)send(new JSONObject().put("study",new JSONObject().put("name",study.getName(account)).put("id",study.getId())).toString());
        }catch (Exception e){
            e.printStackTrace();
            sendError("Ouups, something went wrong");
            sendLogout();
        }
    }

    public void handleSignup(JSONObject msg)throws Exception{
        try{
            String username = msg.getString("username");
            String password = msg.getString("password");
            String name = msg.getString("name");
            String email = msg.getString("email");
            Login.createAccount(username,password,name,email);
            send(new JSONObject().put("home","").toString());
        }catch (Exception e){
            sendError("username already taken");
        }
    }

    public void handleLogin(JSONObject msg)throws Exception{
        try{
            String username = msg.getString("username");
            String password = msg.getString("password");
            System.out.println("login: "+username+" : "+password);
            Account account = Login.login(username,password);
            send(new JSONObject().put("home","").toString());
        }catch (Exception e){
            sendError("password or username is incorrect");
        }
    }
}
