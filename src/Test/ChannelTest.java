package Test;

import StudyMe.*;
import org.junit.Test;

import java.util.Vector;

import static org.junit.Assert.*;

public class ChannelTest {
    @Test
    public void channelTests()throws Exception{
        Account account = Login.createAccount("TestUsername","TestPassword","TestName","TestEmail");
        Study study = account.createStudy("TestStudy");
        Channel channel1 = study.createChannel(account,"channel1");
        Channel channel2 = study.createChannel(account,"channel2");
        Vector<Channel> channels = study.getChannels(account);
        System.out.println("Channels:");
        for(Channel channel:channels)System.out.println(channel.getName(account));
        assertEquals("channel1",channels.get(0).getName(account));
        assertEquals("channel2",channels.get(1).getName(account));
        channel1.createSample(account,"2017-07-28 3:30:00",9.012);
        channel1.createSample(account,1.234);
        channel1.createSample(account,5.678);
        Vector<Sample> samples = channel1.getSamples(account);
        assertEquals(9.012,samples.get(0).getValue(),0.001);
        assertEquals(1.234,samples.get(1).getValue(),0.001);
        assertEquals(5.678,samples.get(2).getValue(),0.001);
        System.out.println("Values:");
        for(Sample sample:channel1.getSamples(account))System.out.println(sample.getTime()+" : "+sample.getValue());
        study.deleteStudy(account);
        account.deleteAccount();
    }
}