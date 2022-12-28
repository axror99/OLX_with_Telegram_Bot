package service;

import exceptions.UserNotFoundException;
import model.Tg_User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.telegram.telegrambots.meta.api.objects.Message;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    private UserService userService;
    private Tg_User tg_user;
    private Message message;

    @BeforeEach
    void setUp(){
        this.tg_user =new Tg_User("Axror","Baxromaliyev","+99899 845 47 46","@Emro_00",1999L);
    }

    @Test
    @DisplayName("addUserInTest()")
    void addUserInTest(){
        // NullPointerException da  ishlaydi Nega ?
        assertThrows(UserNotFoundException.class,() ->this.userService.add(this.tg_user));
    }


    @BeforeEach
     void setUp1(){
        this.message=new Message();
    }
    @Test
    @DisplayName("saveUserToDataInTest() ")
    void saveUserToDataInTest(){
        assertEquals(null,this.userService.saveUserToData(this.message));
    }

}