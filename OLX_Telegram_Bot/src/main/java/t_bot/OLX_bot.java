package t_bot;

import constants.AdminConstants;
import constants.UserConstants;
import dataBase.DataBase;
import exceptions.NoExecutionException;
import model.Product;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageCaption;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageMedia;
import org.telegram.telegrambots.meta.api.methods.updatingmessages.EditMessageText;
import org.telegram.telegrambots.meta.api.objects.*;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import service.UserService;

import java.util.AbstractMap;
import java.util.List;

public class OLX_bot extends TelegramLongPollingBot implements Olx_botBase{
    private int cntCategoryAdd = 0;
    private int photoAdd = 0;
    private static int photoSlide = 0;

    public int getPhotoSlide() {
        return photoSlide;
    }

    public void setPhotoSlide(int photoSlide) {
        this.photoSlide = photoSlide;
    }

    @Override
    public String getBotUsername() {
        return BOT_USERNAME;
    }

    @Override
    public String getBotToken() {
        return BOT_TOKEN;
    }

    @Override
    public void onUpdateReceived(Update update) {

        //System.out.println(update.getMessage().getFrom());
        System.out.println(update);
        System.out.println(update.getCallbackQuery());
        if (update.getCallbackQuery() != null) {
            CallbackQuery callbackQuery = update.getCallbackQuery();
            String text = callbackQuery.getData();
            Message message = callbackQuery.getMessage();
            Long chatId = callbackQuery.getMessage().getChatId();
            if (text.equals("-1add")) {
                String editedText = "OK. Please send Category's name like format on the below\n(Note: if there is no parent category just put 0)\n*Format* - _ParentCategoryName-:-CurrentCategoryName_";
                myExecute(null, null, editedText, chatId, "Markdown");
                cntCategoryAdd++;
            } else if (text.equals("/main")) {
                Tg_Bot_DTO.userBinCounter=0;
                try{
                    editMessageExecute(message, Tg_Bot_DTO.adminMenuInlineMarkup(DataBase.LIST_OF_CATEGORY, "0", chatId), "Main categories", chatId, null);
                }catch (Exception e){
                    myExecute(null,Tg_Bot_DTO.adminMenuInlineMarkup(DataBase.LIST_OF_CATEGORY,"0",chatId),"Main categories", chatId, null);
                }
            } else if (DataBase.ALL_CATEGORIES_ID.contains(text)) {
                Tg_Bot_DTO.showCategoriesMenu(text, message, chatId);
            } else if (text.equals(AdminConstants.addProductCallBackQuery)) {
                String editedText = "OK. Please send the photo of product";
                myExecute(null, null, editedText, chatId, null);
                photoAdd++;
            } else if (text.startsWith("P_back")){
                String id = text.substring(6);
                setPhotoSlide(--photoSlide);
                Tg_Bot_DTO.backOrForwardOfProduct(id,message,chatId);
            } else if(text.startsWith("P_forw")){
                String id = text.substring(6);
                setPhotoSlide(++photoSlide);
                Tg_Bot_DTO.backOrForwardOfProduct(id,message,chatId);
            } else if(text.startsWith("back_Info")){
                Tg_Bot_DTO.addProductToBin(chatId,Tg_Bot_DTO.getProductById(text.substring(9)));
            }else if(text.equals("-1P_back")){
                setPhotoSlide(--photoSlide);
                Tg_Bot_DTO.showUserFavoriteList(message,chatId);
            }else if(text.equals("-1P_forward")){
                setPhotoSlide(++photoSlide);
                Tg_Bot_DTO.showUserFavoriteList(message,chatId);
            }else if(text.startsWith("P_buy")){
                Product product = Tg_Bot_DTO.getProductById(text.substring(5));
                if(product!=null) {
                    if(Tg_Bot_DTO.purchaseMessageForUsers(product, chatId)) {
                        Tg_Bot_DTO.notifyAdminAboutSoldProduct(product, chatId,callbackQuery.getFrom());
                    }
                }else throw new NoExecutionException();
            }

        } else if (update.hasMessage()) {
            Message message = update.getMessage();
            Long chatId = message.getChatId();
            if (message.hasPhoto()&&photoAdd==1) {
                Tg_Bot_DTO.setProductPhoto(message);
                String editedText = "OK. now please send the product info exactly like format on the below\n*Format* - _:Product:/Category name/Product name/Name of Owner of product/Product price/Phone number/Description_\n(Note: *:Product:/* must be on the beginning of info)";
                myExecute(null, null, editedText, chatId, "Markdown");

            }else {
                String text = message.getText();

                if (text.equals("/start")) {
                    Tg_Bot_DTO.main(message);
                } else if (text.equals(AdminConstants.addCategoryButton) || text.equals(UserConstants.categories)) {
                    myExecute(null, Tg_Bot_DTO.adminMenuInlineMarkup(DataBase.LIST_OF_CATEGORY, "0", chatId), "Main categories", chatId, null);
                } else if (text.contains("-:-") && cntCategoryAdd == 1) {
                    Tg_Bot_DTO.backButtonAfterCreatingCategory(text, "Category has been created successfully.", chatId);
                    cntCategoryAdd = 0;
                } else if (text.startsWith(":Product:/")) {
                    Tg_Bot_DTO.setProductInfo(text);
                    Tg_Bot_DTO.backButtonAfterCreatingCategory(null, "Product has been added to the Market successfully.", chatId);
                    photoAdd=0;
                } else if(text.equals("/help")){
                    String sendText = "For more help, Please contact with our [Admin](tg://user?id="+AdminConstants.adminChatId+")";
                    myExecute(null,null,sendText,chatId,"Markdown");
                }else if(text.equals(UserConstants.favoriteProducts)){
                    Tg_Bot_DTO.showUserFavoriteList(message,chatId);
                }else if(text.equals(AdminConstants.sendMessageToAllButton)&&(DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)||chatId.equals(AdminConstants.adminChatId))){
                    myExecute(null,null,"OK. Please send me text of your message which you want to send to all users.\n(Note: your text message must begin with *:Send:* word.)",chatId,"Markdown");
                }else if(text.equals(AdminConstants.setNewAdminButton)&&(DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)||chatId.equals(AdminConstants.adminChatId))){
                    myExecute(null,null,"OK. Please send me the user name of new Admin.\n(Note: user name must begin with '@' sign)",chatId,null);
                }else if(text.startsWith(":Send:")&&(DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)||chatId.equals(AdminConstants.adminChatId))){
                    Tg_Bot_DTO.sendMessageToAllUser(text);
                    myExecute(null,null,"✅ All users have been alerted with your message.",chatId,null);
                }else if(text.startsWith("@")&&(DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)||chatId.equals(AdminConstants.adminChatId))){
                    Tg_Bot_DTO.setNewAdmin(text.substring(1),chatId);
                }
                else{
                    myExecute(null,null,"*Unknown command*. Please try again !",chatId,"Markdown");
                }
            }
        }
    }

    public void myExecute(ReplyKeyboardMarkup r, InlineKeyboardMarkup i, String text, Long chatId, String parseMode) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(String.valueOf(chatId));
        sendMessage.setReplyMarkup((r != null) ? r : i);
        sendMessage.setParseMode(parseMode);

        try {
            execute(sendMessage);
        } catch (TelegramApiException e) {
            throw new NoExecutionException();
        }
    }

    public void editMessageExecute(Message message, InlineKeyboardMarkup i, String editedText, Long chatId, String parseMode) {
        EditMessageText editMessageText = new EditMessageText();
        editMessageText.setMessageId(message.getMessageId());
        editMessageText.setChatId(String.valueOf(chatId));
        editMessageText.setText(editedText);
        editMessageText.setReplyMarkup(i);
        editMessageText.setParseMode(parseMode);

        try {
            execute(editMessageText);
        } catch (TelegramApiException e) {
            throw new NoExecutionException();
        }
    }

    public void myExecuteForPhoto(String photo,InlineKeyboardMarkup i,String caption,Long chatId){ //Axror =======
        SendPhoto sendPhoto = new SendPhoto();
        InputFile inputFile = new InputFile(photo);
        sendPhoto.setPhoto(inputFile);
        sendPhoto.setReplyMarkup(i);
        sendPhoto.setCaption(caption);
        sendPhoto.setChatId(String.valueOf(chatId));
        try {
            execute(sendPhoto);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }
    public void editMessageMedia(InputMedia inputMedia,InlineKeyboardMarkup i,Message message,Long chatId){ // Axror ============
        EditMessageMedia editMessageMedia = new EditMessageMedia();
        editMessageMedia.setMedia(inputMedia);
        editMessageMedia.setMessageId(message.getMessageId());
        editMessageMedia.setChatId(String.valueOf(chatId));
        editMessageMedia.setReplyMarkup(i);
        try {
            execute(editMessageMedia);
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }

}
