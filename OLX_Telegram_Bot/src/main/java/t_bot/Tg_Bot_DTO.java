package t_bot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import constants.AdminConstants;
import constants.UserConstants;
import dataBase.DataBase;
import exceptions.InvalidProductException;
import exceptions.NoExecutionException;
import exceptions.UserNotFoundException;
import model.Category;
import model.Product;
import model.Tg_User;
import org.glassfish.grizzly.threadpool.FixedThreadPool;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import service.CategoryService;
import service.ProductService;
import service.UserService;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public abstract class Tg_Bot_DTO {

    private static OLX_bot bot = new OLX_bot();
    private static UserService userService = new UserService();
    private static CategoryService categoryService = new CategoryService();
    private static ProductService productService = new ProductService();
    private static Gson  gson = new GsonBuilder().setPrettyPrinting().create();
    static int userBinCounter;
    public synchronized static void main(Message message){
        try {
            userService.add(userService.saveUserToData(message));
        } catch (UserNotFoundException e) {
            throw new RuntimeException(e);
        }
        Long chatId = message.getChatId();
        User user = message.getFrom();
        User admin = UserService.setAdminForBot(message);
        if(admin!=null||DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)){
            bot.myExecute(adminMenuReplyKeys(),null,"Hello  *Admin "+user.getFirstName()+"* \nWelcome to our _Telegram OLX_ bot.",chatId,"Markdown");
        }else{
            bot.myExecute(userMenuReplyKeys(),null,"Hello *"+user.getFirstName()+"* \nWelcome to our _Telegram OLX_ bot.",chatId,"Markdown");

        }
    }
    public static void backButtonAfterCreatingCategory(String text,String text1,Long chatId){
        InlineKeyboardMarkup i = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        rows.add(onlyOneButton("⬅ Back main menu","/main"));
        i.setKeyboard(rows);
        if(text!=null) {
            categoryService.addCategoriesButton(text);
        }
        bot.myExecute(null,i,"✅ "+text1,chatId,null);
    }
    public static InlineKeyboardMarkup adminMenuInlineMarkup(List<Category> list,String parentId,Long chatId){
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = null;
        if(list.isEmpty()){
            if(chatId.equals(AdminConstants.adminChatId)||DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)) {
                rows.add(onlyTwoButtons("➕ Add Category", "-1add","➕ Add Product",AdminConstants.addProductCallBackQuery));
            }
            if(!parentId.equals("0")){
                rows.add(onlyOneButton("⬅ Back",String.valueOf(getCategoryByStringId(parentId).getParentId())));
            }
            inlineKeyboardMarkup.setKeyboard(rows);
            return inlineKeyboardMarkup;
        }
        for(int i=0; i<list.size(); i++){
            if(String.valueOf(list.get(i).getParentId()).equals(parentId)) {
                inlineKeyboardButton = new InlineKeyboardButton(list.get(i).getName());
                inlineKeyboardButton.setCallbackData(String.valueOf(list.get(i).getId()));
                row.add(inlineKeyboardButton);
                if ((i + 1) % 2 == 0) {
                    rows.add(row);
                    row = new ArrayList<>();
                }
            }else if(parentId.equals("all")){
                inlineKeyboardButton = new InlineKeyboardButton(list.get(i).getName());
                inlineKeyboardButton.setCallbackData(String.valueOf(list.get(i).getId()));
                row.add(inlineKeyboardButton);
                if ((i + 1) % 2 == 0) {
                    rows.add(row);
                    row = new ArrayList<>();
                }
            }
        }
        if(row.size()>0){
            rows.add(row);
        }
        if(chatId.equals(AdminConstants.adminChatId)||DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)) {
            rows.add(onlyTwoButtons("➕ Add Category", "-1add","➕ Add Product",AdminConstants.addProductCallBackQuery));
        }
        if(!parentId.equals("0")){
            rows.add(onlyOneButton("⬅ Back",String.valueOf(getCategoryByStringId(parentId).getParentId())));
        }
        inlineKeyboardMarkup.setKeyboard(rows);
        return inlineKeyboardMarkup;
    }

    public static void showCategoriesMenu(String text,Message message,Long chatId){
        InlineKeyboardMarkup i = Tg_Bot_DTO.innerCategories(text,DataBase.LIST_OF_CATEGORY,chatId);
        if(i!=null){
            Category category = getCategoryByStringId(text);
            String editedText;
            if(category!=null){
                editedText = "Inner Category of *"+category.getName()+"*";
            }else{
                editedText = "Main categories";
            }
            if(message.hasPhoto()){
                InputMedia inputMedia = new InputMediaPhoto();
                inputMedia.setMedia(message.getPhoto().get(0).toString());
                bot.myExecute(null,i,editedText,chatId,"Markdown");
            }else {
                bot.editMessageExecute(message, i, editedText, chatId, "Markdown");
            }
        }else{
            String editedText=null;
            Category category = getCategoryByStringId(text);
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            if(!category.getProductList().isEmpty()){
                Product product = category.getProductList().get(bot.getPhotoSlide());
                rows.addAll(List.of(
                        categoryROW(button("◀️","P_back"+category.getId()),button("Add to Favorites","back_Info"+product.getId()),button("Buy","P_buy"+product.getId()),button("▶️","P_forw"+category.getId()))
                ));

            }
            if(chatId.equals(AdminConstants.adminChatId)||DataBase.ALL_ADMIN_CHAT_ID.contains(chatId)) {
                if(category.getProductList().isEmpty()) {
                    rows.add(onlyTwoButtons("➕ Add Category", "-1add", "➕ Add Product", AdminConstants.addProductCallBackQuery));
                }
            }
            rows.add(onlyOneButton("⬅️Back",String.valueOf(getCategoryByStringId(text).getParentId())));
            try {
                i = new InlineKeyboardMarkup();
                i.setKeyboard(rows);
                if(!category.getProductList().isEmpty()){
                    Product product = category.getProductList().get(bot.getPhotoSlide());
                    bot.myExecuteForPhoto(product.getPhoto(),i,product.toString(),chatId);
                    return;
                }else{
                    editedText = category.getName()+" category";
                }
                bot.editMessageExecute(message,i,editedText,chatId,null);
            }catch (Exception e){
                throw new NullPointerException();
            }
        }
    }

    public static void backOrForwardOfProduct(String categoryID,Message message, Long chatId){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardMarkup i = new InlineKeyboardMarkup();
        Category category = CategoryService.getCategoryByName(null,categoryID);
        if(category!=null) {
            if(bot.getPhotoSlide()<0){
                bot.setPhotoSlide(category.getProductList().size()-1);
            }else if(bot.getPhotoSlide()>category.getProductList().size()-1){
                bot.setPhotoSlide(0);
            }
            Product product =category.getProductList().get(bot.getPhotoSlide());
            InputMedia inputMedia = new InputMediaPhoto(product.getPhoto());
            inputMedia.setCaption(product.toString());
            rows.addAll(List.of(
                    categoryROW(button("◀️", "P_back"+category.getId()), button("Add to Favorites", "back_Info"+product.getId()), button("Buy", "P_buy"+product.getId()), button("▶️", "P_forw"+category.getId()))
            ));
            rows.add(onlyOneButton("⬅️Back",String.valueOf(category.getParentId())));
            i.setKeyboard(rows);
            bot.editMessageMedia(inputMedia,i,message,chatId);
        }else{
            throw new NoExecutionException();
        }
    }
    public static void showUserFavoriteList(Message message,Long chatId){
        List<List<InlineKeyboardButton>> rows = new ArrayList<>();
        InlineKeyboardMarkup i = new InlineKeyboardMarkup();
        Tg_User user = userService.getUserById(chatId);
        if(user!=null){
            if(bot.getPhotoSlide()<0){
                bot.setPhotoSlide(user.getBin().size()-1);
            }else if(bot.getPhotoSlide()>user.getBin().size()-1){
                bot.setPhotoSlide(0); //========================================================================================
            }
            Product product =user.getBin().get(bot.getPhotoSlide());
            InputMedia inputMedia = new InputMediaPhoto(product.getPhoto());
            inputMedia.setCaption(product.toString());
            rows.addAll(List.of(
                    categoryROW(button("◀️", "-1P_back"), button("Add to Favorites", "back_Info"+product.getId()), button("Buy", "P_buy"+product.getId()), button("▶️", "-1P_forward"))
            ));
            rows.add(onlyOneButton("⬅ Back main menu","/main"));
            i.setKeyboard(rows);
            Product product1 = user.getBin().get(bot.getPhotoSlide());
            if(userBinCounter==0){
                bot.myExecuteForPhoto(product1.getPhoto(),i,product1.toString(),chatId);
                userBinCounter++;
            }else{
                bot.editMessageMedia(inputMedia,i,message,chatId);
            }
        }
    }


    public static InlineKeyboardMarkup innerCategories(String categoryId,List<Category> DataBaseList,Long chatId) {
        List<Category> list1 = new LinkedList<>();
        for (Category category : DataBase.LIST_OF_CATEGORY.stream().parallel().toList()) {
            if(String.valueOf(category.getParentId()).equals(categoryId)){
                list1.add(category);
            }
        }
        if(!list1.isEmpty()){
            return adminMenuInlineMarkup(list1,categoryId,chatId);
        }
        return null;
    }

    private static InlineKeyboardButton button(String text, String callBackData){
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton();
        inlineKeyboardButton.setText(text);
        inlineKeyboardButton.setCallbackData(callBackData);
        return inlineKeyboardButton;
    }
    public static List<InlineKeyboardButton> onlyTwoButtons(String buttonName1,String callBackData1, String buttonName2,String callBackData2){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(buttonName1);
        inlineKeyboardButton.setCallbackData(callBackData1);
        InlineKeyboardButton inlineKeyboardButton1 = new InlineKeyboardButton(buttonName2);
        inlineKeyboardButton1.setCallbackData(callBackData2);
        row.add(inlineKeyboardButton);
        row.add(inlineKeyboardButton1);

        return row;
    }

    private static List<InlineKeyboardButton> categoryROW(InlineKeyboardButton... inlineButtons) {  // Axror ===========
        ArrayList<InlineKeyboardButton> arrayList = new ArrayList<>();
        arrayList.addAll(List.of(inlineButtons));
        return arrayList;
    }
    private static List<InlineKeyboardButton> onlyOneButton(String buttonName,String callBackData){
        List<InlineKeyboardButton> row = new ArrayList<>();
        InlineKeyboardButton inlineKeyboardButton = new InlineKeyboardButton(buttonName);
        inlineKeyboardButton.setCallbackData(callBackData);
        row.add(inlineKeyboardButton);
        return row;
    }

    public static ReplyKeyboardMarkup adminMenuReplyKeys(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(new KeyboardRow(
                List.of(new KeyboardButton(AdminConstants.addCategoryButton),
                        new KeyboardButton(AdminConstants.sendMessageToAllButton))
        ));
        rows.add(new KeyboardRow(
                List.of(new KeyboardButton(AdminConstants.setNewAdminButton))
        ));
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rows);

        return replyKeyboardMarkup;
    }

    public static ReplyKeyboardMarkup userMenuReplyKeys(){
        ReplyKeyboardMarkup replyKeyboardMarkup = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = new ArrayList<>();
        rows.add(new KeyboardRow(
                List.of(new KeyboardButton(UserConstants.categories),
                        new KeyboardButton(UserConstants.favoriteProducts))
        ));
        replyKeyboardMarkup.setResizeKeyboard(true);
        replyKeyboardMarkup.setKeyboard(rows);

        return replyKeyboardMarkup;

    }

    public static Category getCategoryByStringId(String categoryId){
        return CategoryService.getCategoryByName(null,categoryId);
    }

    public static void setProductPhoto(Message message){
        productService.getProductFromAdmin(message);
    }
    public static void setProductInfo(String text){
        productService.getInformationOfProduct(text);
    }

    public static String getCurrentDateAndTime(){
        LocalDateTime localDateTime = LocalDateTime.now();
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("'Time: 'HH:mm ' Date:' dd/MM/yyyy");
        return dateTimeFormatter.format(localDateTime);
    }

    public static void addProductToBin(Long chatId,Product product){
        Tg_User user = userService.getUserById(chatId);
        if(user!=null&&product!=null){
            InlineKeyboardMarkup i = new InlineKeyboardMarkup();
            List<List<InlineKeyboardButton>> rows = new ArrayList<>();
            rows.add(onlyOneButton("⬅ Back main menu","/main"));
            i.setKeyboard(rows);
            user.getBin().add(product);
            bot.myExecute(null,i,"✅ Product has been added to Favorites successfully",chatId,null);
            DataBase.fileWriterMethod(DataBase.usersFile,gson.toJson(DataBase.LIST_OF_USERS));
        }else throw new NoExecutionException();
    }

    public static Product getProductById(String productId){
        Product product = productService.getProductByStringId(productId);
        if(product!=null){
            return product;
        }
        return null;
    }

    public static void setNewAdmin(String userName,Long chatId){
        for (Tg_User user : DataBase.LIST_OF_USERS.stream().parallel().toList()) {
            if(user!=null){
                if(user.getUsername()!=null){
                    if(!DataBase.ALL_ADMIN_CHAT_ID.contains(user.getChatId())){
                        if(user.getUsername().equals(userName)){
                            DataBase.ALL_ADMIN_CHAT_ID.add(user.getChatId());
                            bot.myExecute(null,null,"✅ New [Admin](tg://user?id="+user.getChatId()+") have been successfully added.",chatId,"Markdown");
                            return;
                        }
                    }
                }
            }
        }
        bot.myExecute(null,null,"❌ Sorry, I could not find any user with this user name, Please try again.",chatId,null);
    }

    public static void sendMessageToAllUser(String textMessage){
        for (Tg_User user : DataBase.LIST_OF_USERS.stream().parallel().toList()) {
            if(user!=null){
                if(!DataBase.ALL_ADMIN_CHAT_ID.contains(user.getChatId())&&!AdminConstants.adminChatId.equals(user.getChatId())){
                    bot.myExecute(null,null,textMessage.substring(6),user.getChatId(),null);
                }
            }
        }
    }
    public static boolean purchaseMessageForUsers(Product product,Long chatId){
        if(product.isSaleState()){
            bot.myExecute(null,null,"❌ Sorry, This product has already been bought by another User, you can search for ohter product.",chatId,null);
            return false;
        }else {
            product.setSaleState(true);
            bot.myExecute(null, null, "✅ You have been bought this product, now you can contact with the owner of this product. Phone number is on the caption of product's photo. Thanks for your purchase",chatId,null);
            DataBase.fileWriterMethod(DataBase.productsFile, gson.toJson(DataBase.LIST_OF_PRODUCTS));
            return true;
        }
    }

    public static void notifyAdminAboutSoldProduct(Product product,Long chatId,User user){
        String text = "Product on the above has just been bought by ["+user.getFirstName()+"](tg://user?id="+chatId+") user";
        for (Tg_User tg_user : DataBase.LIST_OF_USERS.stream().parallel().toList()) {
            if(tg_user!=null){
                if(DataBase.ALL_ADMIN_CHAT_ID.contains(tg_user.getChatId())||tg_user.getChatId().equals(AdminConstants.adminChatId)){
                    bot.myExecuteForPhoto(product.getPhoto(),null,product.toString(), tg_user.getChatId());
                    bot.myExecute(null,null,text, tg_user.getChatId(), "Markdown");
                }
            }
        }
    }
}
