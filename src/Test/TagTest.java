package Test;

import StudyMe.*;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

public class TagTest {

    //TODO: write access-rights checks...
    @Test
    public void tagTests()throws Exception{
        //create Test account + study + channel
        Account account = Login.createAccount("TestUsername","TestPassword","TestName","TestEmail");
        Study study = account.createStudy("TestStudy");
        Channel channel = study.createChannel(account,"channel");
        //create 2 test tags and check get / set name
        Tag tag = study.createTag(account,"TestTag1");
        assertEquals("TestTag1",tag.getName(account));
        tag.setName(account,"TestTag2");
        assertEquals("TestTag2",tag.getName(account));
        study.createTag(account,"TestTag1");
        //check study getTags - method
        Vector<Tag> tags = study.getTags(account);
        System.out.println("TestStudy Tags:");
        for(Tag t:tags)System.out.println(t.getName(account));
        assertEquals("TestTag2",tags.get(0).getName(account));
        assertEquals("TestTag1",tags.get(1).getName(account));
        //check Tag appending
        tags.get(0).appendTag(account,tags.get(1));
        //check Tag creation + appending
        tag = study.createTag(account,"TestTag3");
        study.linkTags(account,tags.get(0),tag);
        tags = tags.get(0).getConnectedTags(account);
        System.out.println("Connected Tags to TestTag2:");
        for(Tag t:tags)System.out.println(t.getName(account));
        assertEquals("TestTag1",tags.get(0).getName(account));
        assertEquals("TestTag3",tags.get(1).getName(account));
        tags = tags.get(0).getConnectedTags(account);
        System.out.println("Connected Tags to TestTag1:");
        for(Tag t:tags)System.out.println(t.getName(account));
        assertEquals("TestTag2",tags.get(0).getName(account));
        //check channel link
        tags = tags.get(0).getConnectedTags(account);
        //check append tag
        channel.appendTag(account,tags.get(0));
        channel.appendTag(account,tags.get(1));
        //check create tag
        channel.createTag(account,"TestTag2");
        tags = channel.getTags(account);
        System.out.println("Connected Tags to channel:");
        for(Tag t:tags)System.out.println(t.getName(account));
        assertEquals("TestTag1",tags.get(0).getName(account));
        assertEquals("TestTag3",tags.get(1).getName(account));
        assertEquals("TestTag2",tags.get(2).getName(account));
        //check remove tag
        channel.removeTag(account,tags.get(0));
        tags = channel.getTags(account);
        System.out.println("Connected Tags to channel:");
        for(Tag t:tags)System.out.println(t.getName(account));
        assertEquals("TestTag3",tags.get(0).getName(account));
        assertEquals("TestTag2",tags.get(1).getName(account));
        study.deleteStudy(account);
        account.deleteAccount();
    }
}