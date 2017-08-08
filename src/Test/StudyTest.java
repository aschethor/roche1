package Test;

import StudyMe.Account;
import StudyMe.Channel;
import StudyMe.Login;
import StudyMe.Study;
import org.junit.Test;

import static org.junit.Assert.*;

public class StudyTest {
    @Test
    public void studyTests()throws Exception{
        //create and login test account
        Account account = Login.createAccount("TestUsername","TestPassword","TestName","TestEmail");
        //create test study
        Study study = account.createStudy("TestStudy");
        assertEquals("TestStudy",study.getName(account));
        assertEquals("",study.getDescription(account));
        //change name and description of study
        study.setName(account,"TestStudy2");
        study.setDescription(account,"TestDescription");
        assertEquals("TestStudy2",study.getName(account));
        assertEquals("TestDescription",study.getDescription(account));
        //create co-author test account and add this account to study
        Account account1 = Login.createAccount("TestUsername2","TestPassword","","");
        //account1 can't create a new channel
        boolean createFail = false;
        try{
            Channel channel = study.createChannel(account1,"TestChannel");
        }catch (Exception e){
            createFail = true;
        }
        assertEquals(true,createFail);
        //add account1 to study so it can create a new channel
        study.addAuthor(account,account1);
        Channel channel = study.createChannel(account1,"TestChannel");
        //now deleting the study should fail
        boolean deleteFail = false;
        try{
            study.deleteStudy(account);
        }catch (Exception e){
            deleteFail = true;
        }
        assertEquals(true,deleteFail);
        System.out.println("Authors:");
        for(Account a:study.getAuthors(account))System.out.println(a.getName());
        //remove co-author in order to delete study
        study.removeAuthor(account,study.getAuthors(account).get(1));
        study.deleteStudy(account);
        account.deleteAccount();
        account1.deleteAccount();
    }
}