package Test;

import StudyMe.Account;
import StudyMe.Login;
import org.junit.Test;

import static org.junit.Assert.*;

public class AccountTest {

    @Test
    public void accountTests() throws Exception{
        //create and login test account
        Account account = Login.createAccount("TestUsername","TestPassword","TestName","TestEmail");
        //check account info
        assertEquals("TestUsername",account.getUsername());
        assertEquals("TestPassword",account.getPassword());
        assertEquals("TestName",account.getName());
        assertEquals("TestEmail",account.getEmail());
        //change values
        account.setUsername("TestUsername2");
        account.setPassword("TestPassword2");
        account.setName("TestName2");
        account.setEmail("TestEmail2");
        //check new values
        assertEquals("TestUsername2",account.getUsername());
        assertEquals("TestPassword2",account.getPassword());
        assertEquals("TestName2",account.getName());
        assertEquals("TestEmail2",account.getEmail());
        //clean up
        account.deleteAccount();
    }

}