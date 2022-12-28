package model;

import lombok.*;
import model.base.Base;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
public class Tg_User {
    List<Product> bin = new LinkedList<>();
    private String firstName;
    private String lastName;
    private String phoneNumber;
    private String username;
    private Long chatId;

    public Tg_User(String firstName, String lastName, String phoneNumber, String username,Long chatId) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.phoneNumber = phoneNumber;
        this.username = username;
        this.chatId = chatId;
    }

    @Override
    public String toString() {
        return " User information \uD83D\uDCD1 "+ "\n" +
                " ID : "+chatId+'\n'+
                " Name :  " + firstName + '\n' +
                " Last Name :  " + lastName + '\n' +
                " Phone Number :  " + phoneNumber + '\n' +
                " Username :  " + username + '\n' +
                " Chat Id : " + chatId + '\n' +
                '}';
    }
}
