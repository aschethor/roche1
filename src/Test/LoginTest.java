package Test;

import StudyMe.Account;
import StudyMe.Login;
import org.junit.Test;

import static org.junit.Assert.*;

public class LoginTest {
    @Test
    public void loginTests() throws Exception {
        //create test account
        Login.createAccount("TestUsername","TestPassword","TestName","TestEmail");
        //log into test Account
        Account account = Login.login("TestUsername","TestPassword");
        //check account info
        assertEquals("TestUsername",account.getUsername());
        assertEquals("TestPassword",account.getPassword());
        assertEquals("TestName",account.getName());
        assertEquals("TestEmail",account.getEmail());
        //now creating account with same username has to fail!
        boolean createFail = false;
        try{
            Login.createAccount("TestUsername","pw","name","email");
        }catch (Exception e){
            createFail = true;
        }
        assertEquals(true,createFail);
        //delete test account
        account.deleteAccount();
        //now login has to fail!
        boolean loginFail = false;
        try {
            Login.login("TestUsername","TestPassword");
        }catch(Exception e){
            loginFail = true;
        }
        assertEquals(true,loginFail);
    }
}