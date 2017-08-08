import StudyMe.*;
import StudyMe.Error;

import java.util.Scanner;
import java.util.Vector;

public class ConsoleInterface {
    private static Scanner scanner = new Scanner(System.in);
    public static Account loginDialog() throws Exception{
        while(true) {
            out("possible actions:");
            out(" ( 0) login");
            out(" ( 1) create new account");
            int choice = inInt();
            cls();
            if(choice==0) {
                out("enter username: ");
                String username = inString();
                out("enter password: ");
                String password = inString();
                cls();
                try {
                    return Login.login(username, password);
                } catch (Exception e) {
                    out("login failed, username or password wrong");
                }
            }else if(choice==1){
                out("enter username: ");
                String username = inString();
                out("enter password: ");
                String password = inString();
                out("enter name:");
                String name = inString();
                out("enter email:");
                String email = inString();
                cls();
                return Login.createAccount(username,password,name,email);
            }
        }
    }
    public static void startScreen(Account account)throws Exception{
        while(true) {
            try {
                out("Hi " + account.getName() + " ("+account.getUsername()+") !");
                out("possible actions:");
                out(" ( 0) exit");
                out(" ( 1) enter studies");
                out(" ( 2) change name");
                out(" ( 3) change username");
                out(" ( 4) change password");
                out(" ( 5) change email");
                out(" ( 6) delete account");
                int choice = inInt();
                if (choice == 0) {
                    return;
                } else if (choice == 1) {
                    cls();
                    out("studies: (ID / name)");
                    Vector<Study> studies = account.getAuthoredStudies();
                    for (Study study : studies) out("" + study.getId() + " / " + study.getName(account));
                    out("possible actions:");
                    out(" (0) exit");
                    out(" (1) create new study");
                    out(" (study ID) edit study");
                    choice = inInt();
                    if (choice == 0) {
                        cls();
                    } else if (choice == 1) {
                        cls();
                        out("enter new study name:");
                        String name = inString();
                        cls();
                        studyScreen(account, account.createStudy(name));
                    } else {
                        cls();
                        studyScreen(account, new Study(choice));
                    }
                } else if (choice == 2) {
                    cls();
                    out("enter new name:");
                    account.setName(inString());
                } else if (choice == 3) {
                    cls();
                    out("enter new username:");
                    account.setUsername(inString());
                } else if (choice == 4) {
                    cls();
                    out("enter new password:");
                    account.setPassword(inString());
                } else if (choice == 5) {
                    cls();
                    out("enter new email:");
                    account.setEmail(inString());
                } else if (choice == 6) {
                    account.deleteAccount();
                    return;
                }
                cls();
            }catch (Exception e){
                if(e instanceof Error){
                    out("Ouups, something bad happened:");
                    out(e.toString());
                }else{
                    out("Ouups, something very bad happened:");
                    e.printStackTrace();
                }
                inString();
                cls();
            }
        }
    }
    public static void studyScreen(Account account,Study study)throws Exception{
        while(true) {
            try {
                out("Study nr. " + study.getId() + " : " + study.getName(account));
                out("description: " + study.getDescription(account));
                out("possible actions:");
                out(" ( 0) exit");
                out(" ( 1) enter tags");
                out(" ( 2) enter tag links");
                out(" ( 3) enter channels");
                out(" ( 4) enter authors");
                out(" ( 5) change name");
                out(" ( 6) change description");
                out(" ( 7) similar studies");
                out(" ( 8) similar channels");
                out(" ( 9) copy study");
                out(" (10) delete study");
                int choice = inInt();
                if (choice == 0) {
                    return;
                } else if (choice == 1) {
                    cls();
                    out("tags: (ID / name)");
                    Vector<Tag> tags = study.getTags(account);
                    for (Tag tag : tags) out("" + tag.getId() + " / " + tag.getName(account));
                    out("possible actions:");
                    out(" ( 0) exit");
                    out(" ( 1) add tag");
                    out(" ( tag ID) remove tag");
                    choice = inInt();
                    if (choice == 0) {
                        cls();
                    } else if (choice == 1) {
                        cls();
                        out("enter tag name:");
                        String name = inString();
                        study.createTag(account, name);
                        cls();
                    } else {
                        study.removeTag(account, new Tag(choice));
                        cls();
                    }
                } else if (choice == 2) {
                    cls();
                    out("tags: (ID / name)");
                    Vector<Tag> tags = study.getTags(account);
                    for (Tag tag : tags) out("" + tag.getId() + " / " + tag.getName(account));
                    out("tag links: (link ID / tag ID 1 - tag ID 2)");
                    Vector<TagLink> tagLinks = study.getTagLinks(account);
                    for (TagLink tagLink : tagLinks)
                        out("" + tagLink.getId() + " / " + tagLink.getTag1().getId() + " - " + tagLink.getTag2().getId());
                    out("possible actions:");
                    out("( 0) exit");
                    out("( 1) add tag link");
                    out("( link ID) remove tag");
                    choice = inInt();
                    if (choice == 0) {
                        cls();
                    } else if (choice == 1) {
                        out("enter tag ID 1:");
                        int tagID1 = inInt();
                        out("enter tag ID 2:");
                        int tagID2 = inInt();
                        study.linkTags(account, new Tag(tagID1), new Tag(tagID2));
                        cls();
                    } else {
                        study.removeTagLink(account, new TagLink(choice));
                        cls();
                    }
                } else if (choice == 3) {
                    cls();
                    out("channels: (ID / name)");
                    Vector<Channel> channels = study.getChannels(account);
                    for (Channel channel : channels) out("" + channel.getId() + " / " + channel.getName(account));
                    out("possible actions:");
                    out(" ( 0) exit");
                    out(" ( 1) add channel");
                    out(" ( channel ID) modify channel");
                    choice = inInt();
                    if (choice == 0) {
                        cls();
                    } else if (choice == 1) {
                        cls();
                        out("enter channel name:");
                        String name = inString();
                        cls();
                        channelScreen(account, study, study.createChannel(account, name));
                    } else {
                        cls();
                        channelScreen(account, study, new Channel(choice));
                    }
                } else if (choice == 4) {
                    cls();
                    out("authors: (ID / name)");
                    Vector<Account> authors = study.getAuthors(account);
                    for (Account a : authors) out("" + a.getId() + " / " + a.getName());
                    out("possible actions:");
                    out(" ( 0) exit");
                    out(" ( 1) add author");
                    out(" ( author ID) remove author");
                    choice = inInt();
                    if (choice == 0) {
                        cls();
                    } else if (choice == 1) {
                        cls();
                        out("enter new authors username");
                        study.addAuthor(account, Account.getAccountByUsername(inString()));
                    } else {
                        cls();
                        study.removeAuthor(account, new Account(choice));
                    }
                } else if (choice == 5) {
                    cls();
                    out("enter new name:");
                    study.setName(account, inString());
                } else if (choice == 6) {
                    cls();
                    out("enter new description:");
                    study.setDescription(account, inString());
                } else if (choice == 7) {
                    cls();
                    Vector<Study> studies = study.getSimilarStudies(account);
                    out("similar studies (ID / name)");
                    for(Study s:studies)out(""+s.getId()+" / "+s.getName(account));
                    inString();
                } else if (choice == 8) {
                    cls();
                    Vector<Vector<Channel>> channels = study.getSimilarChannels(account);
                    out("similar channels (studyID / channelIDs)");
                    for(int i=0;i<channels.size();i++){
                        String output = ""+channels.get(i).get(0).getStudy(account).getId();
                        for(Channel channel:channels.get(i))output += " / "+channel.getId();
                        out(output);
                    }
                    inString();
                } else if (choice == 9) {
                    cls();
                    out("enter copied study name:");
                    study.deleteStudy(account);
                    return;
                } else if (choice == 10) {
                    study.deleteStudy(account);
                    return;
                }
                cls();
            }catch (Exception e){
                if(e instanceof Error){
                    out("Ouups, something bad happened:");
                    out(e.toString());
                }else{
                    out("Ouups, something very bad happened:");
                    e.printStackTrace();
                }
                inString();
                cls();
            }
        }
    }
    public static void channelScreen(Account account,Study study,Channel channel)throws Exception{
        while(true){
            try {
                cls();
                out("Study nr. " + study.getId() + " : " + study.getName(account) + " -> channel: " + channel.getName(account) + " [" + channel.getUnit(account) + "]");
                out("possible actions:");
                out(" ( 0) exit");
                out(" ( 1) enter tags");
                out(" ( 2) enter samples");
                out(" ( 3) change name");
                out(" ( 4) change unit");
                int choice = inInt();
                if (choice == 0) {
                    cls();
                    return;
                } else if (choice == 1) {
                    cls();
                    out("channel tags: (ID / name)");
                    for (Tag tag : channel.getTags(account)) out("" + tag.getId() + " / " + tag.getName(account));
                    out("possible actions:");
                    out(" ( 0) exit");
                    out(" ( 1) add tag");
                    out(" ( tag ID) remove tag");
                    choice = inInt();
                    if (choice == 0) {
                        cls();
                    } else if (choice == 1) {
                        cls();
                        out("tags: (ID / name)");
                        Vector<Tag> tags2 = study.getTags(account);
                        for (Tag tag : tags2) out("" + tag.getId() + " / " + tag.getName(account));
                        out("enter tag ID to add");
                        channel.appendTag(account, new Tag(inInt()));
                    } else {
                        channel.removeTag(account, new Tag(choice));
                    }
                } else if (choice == 2) {
                    cls();
                    Vector<Sample> samples = channel.getSamples(account);
                    out("samples: (ID / time / value)");
                    for (Sample sample : samples)
                        out("" + sample.getId() + " / " + sample.getTime() + " / " + sample.getValue());
                    out("possible actions:");
                    out(" ( 0) exit");
                    out(" ( 1) new sample");
                    out(" ( sample ID) remove sample");
                    choice = inInt();
                    if (choice == 0) {
                        cls();
                    } else if (choice == 1) {
                        cls();
                        out("enter time value: (leave empty if current time)");
                        String time = inString();
                        out("enter data value:");
                        double value = inDouble();
                        if (time.equals("")) channel.createSample(account, value);
                        else channel.createSample(account, time, value);
                    } else {
                        cls();
                        channel.removeSample(account, new Sample(choice));
                    }
                } else if (choice == 3) {
                    cls();
                    out("enter new name:");
                    channel.setName(account, inString());
                } else if (choice == 4) {
                    cls();
                    out("enter new unit:");
                    channel.setUnit(account, inString());
                }
            }catch (Exception e){
                if(e instanceof Error){
                    out("Ouups, something bad happened:");
                    out(e.toString());
                }else{
                    out("Ouups, something very bad happened:");
                    e.printStackTrace();
                }
                inString();
                cls();
            }
        }
    }
    public static void main(String... args) throws Exception{
        Account account = loginDialog();
        startScreen(account);
    }
    public static void out(String s){
        System.out.println(s);
    }
    public static String inString(){
        return scanner.nextLine();
    }
    public static int inInt(){
        int ret = scanner.nextInt();
        scanner.nextLine();
        return ret;
    }
    public static double inDouble(){
        double ret = scanner.nextDouble();
        scanner.nextLine();
        return ret;
    }
    public static void cls() throws Exception {
        //only for windows
        for(int clear = 0; clear < 50; clear++)
        {
            System.out.println("\n") ;
        }
    }
}