package service;

import dataBase.DataBase;
import exceptions.UserNotFoundException;
import model.Tg_User;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.chatmember.ChatMemberAdministrator;
import service.baseSerivice.BaseService;

public class UserService implements BaseService<Tg_User>{
    @Override
    public void add(Tg_User user) throws UserNotFoundException {
        if(user!=null) {
            DataBase.LIST_OF_USERS.add(user);
            DataBase.saveUserToDataBase(user);
        }else{
            throw new UserNotFoundException();
        }
    }
    public Tg_User saveUserToData(Message message){
        if(message!=null) {
            Tg_User user = new Tg_User();
            user.setChatId(message.getChatId());
            user.setFirstName(message.getFrom().getFirstName());
            user.setLastName(message.getFrom().getLastName());
            user.setPhoneNumber("");
            user.setUsername(message.getFrom().getUserName());
            return user;
        }else return null;
    }

    public static User setAdminForBot(Message message){
        User user = message.getFrom();
        if(user.getId().equals(477854864L)) {
            ChatMemberAdministrator chatMemberAdministrator = new ChatMemberAdministrator(user, true, "blabla", false, true, true, true, true, true, true, true, true, true, false);
            return chatMemberAdministrator.getUser();
        }else return null;
    }

    public Tg_User getUserById(Long chatId){
        for (Tg_User user : DataBase.LIST_OF_USERS.stream().parallel().toList()) {
            if(user!=null){
                if(user.getChatId().equals(chatId)){
                    return user;
                }
            }
        }
        return null;
    }

}
