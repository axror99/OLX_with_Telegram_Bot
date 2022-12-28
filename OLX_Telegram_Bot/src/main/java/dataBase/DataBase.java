package dataBase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import model.Category;
import model.Product;
import model.Tg_User;
import model.base.Base;

import java.io.*;
import java.util.*;

public abstract class DataBase {
    public static List<Tg_User> LIST_OF_USERS = new LinkedList<>();
    public static List<Product> LIST_OF_PRODUCTS = new LinkedList<>();
    public static List<Category> LIST_OF_CATEGORY = new LinkedList<>();
    public static Set<String> ALL_CATEGORIES_ID = new HashSet<>();
    public static Set<Long> ALL_ADMIN_CHAT_ID = new HashSet<>();
    private static Gson gson =new GsonBuilder().setPrettyPrinting().create();
    public static final File usersFile = new File("C:\\Users\\HP\\Documents\\OLX_Telegram_Bot\\src\\main\\resources\\users\\usersJson.json");
    public static final File productsFile = new File("C:\\Users\\HP\\Documents\\OLX_Telegram_Bot\\src\\main\\resources\\products\\productsJson.json");
    public static final File categoriesFile = new File("C:\\Users\\HP\\Documents\\OLX_Telegram_Bot\\src\\main\\resources\\categories\\categoriesJson.json");
    public static final File idGeneratorFile = new File("C:\\Users\\HP\\Documents\\OLX_Telegram_Bot\\src\\main\\resources\\idGenerator\\idGeneratorJson.txt");

    public static void saveUserToDataBase(Tg_User user) {

        try {
            if(usersFile.createNewFile()){
                String str = "["+gson.toJson(user)+"]";
                fileWriterMethod(usersFile,str);
            }else{
                List<Tg_User> users = gson.fromJson(new FileReader(usersFile),new TypeToken<List<Tg_User>>(){}.getType());
                if(dataBaseUserChecker(users,user.getChatId())) {
                    users.add(user);
                    fileWriterMethod(usersFile, gson.toJson(users));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void saveProductToDataBase(Product product) {
        try {
            if(productsFile.createNewFile()){
                String str = "["+gson.toJson(product)+"]";
                fileWriterMethod(productsFile,str);
            }else{
                List<Product> products = gson.fromJson(new FileReader(productsFile),new TypeToken<List<Product>>(){}.getType());
                if(dataBaseProductChecker(products,product.getId())) {
                    products.add(product);
                    fileWriterMethod(productsFile, gson.toJson(products));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void saveCategoryToDataBase(Category category) {
        try {
            if(categoriesFile.createNewFile()){
                String str = "["+gson.toJson(category)+"]";
                fileWriterMethod(categoriesFile,str);

            }else{
                List<Category> categories = gson.fromJson(new FileReader(categoriesFile),new TypeToken<List<Category>>(){}.getType());
                if(dataBaseCategoryChecker(categories,category.getId())) {
                    categories.add(category);
                    fileWriterMethod(categoriesFile, gson.toJson(categories));
                }
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void fileWriterMethod(File file, String text) {
        try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(file))) {
            bufferedWriter.write(text);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }
    private static boolean dataBaseUserChecker(List<Tg_User> list,Long chatId){
        for (Tg_User tg_user : list.stream().parallel().toList()) {
            if(tg_user.getChatId().equals(chatId)){
                return false;
            }
        }
        return true;
    }
    private static boolean dataBaseProductChecker(List<Product> list,int productId){
        for (Product product : list.stream().parallel().toList()) {
            if(product.getId()==productId){
                return false;
            }
        }
        return true;
    }
    private static boolean dataBaseCategoryChecker(List<Category> list,int categoryId){
        for (Category category : list.stream().parallel().toList()) {
            if(category.getId()==categoryId){
                return false;
            }
        }
        return true;
    }

    public static void saveIdToDataBase(int id){
        try {
            idGeneratorFile.createNewFile();
            fileWriterMethod(idGeneratorFile,String.valueOf(id));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
