import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataBase.DataBase;
import model.Category;
import model.Product;
import model.Tg_User;
import model.base.Base;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import service.UserService;
import t_bot.OLX_bot;
import t_bot.Tg_Bot_DTO;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class Main {

    private static Gson gson = new Gson();

    public static void main(String[] args) {
        readUsersFile();
        readCategoriesFile();
        readProductsFile();
        readIdGeneratorFile();

        try {
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
            telegramBotsApi.registerBot(new OLX_bot());
        } catch (TelegramApiException e) {
            throw new RuntimeException(e);
        }
    }


    public static void readUsersFile() {
        File file = DataBase.usersFile;
        if (file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                DataBase.LIST_OF_USERS = gson.fromJson(bufferedReader, new TypeToken<List<Tg_User>>() {
                }.getType());
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void readProductsFile() {
        File file = DataBase.productsFile;
        if (file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                DataBase.LIST_OF_PRODUCTS = gson.fromJson(bufferedReader, new TypeToken<List<Product>>() {
                }.getType());
                for (Product listOfProduct : DataBase.LIST_OF_PRODUCTS) {
                    Tg_Bot_DTO.getCategoryByStringId(String.valueOf(listOfProduct.getCategoryId())).getProductList().add(listOfProduct);
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void readCategoriesFile() {
        File file = DataBase.categoriesFile;
        if (file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                DataBase.LIST_OF_CATEGORY = gson.fromJson(bufferedReader, new TypeToken<List<Category>>() {
                }.getType());
                for (Category category : DataBase.LIST_OF_CATEGORY) {
                    if (category != null) {
                        DataBase.ALL_CATEGORIES_ID.add(String.valueOf(category.getId()));
                        DataBase.ALL_CATEGORIES_ID.add("0");
                    }
                }
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void readIdGeneratorFile() {
        File file = DataBase.idGeneratorFile;
        if (file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                Base.setIdGenerator(Integer.parseInt(bufferedReader.readLine()));
            } catch (FileNotFoundException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

//    public static List<Category> getList() {
//        File file = DataBase.categoriesFile;
//        if (file.exists()) {
//            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
//                DataBase.LIST_OF_CATEGORY = gson.fromJson(bufferedReader, new TypeToken<List<Category>>() {
//                }.getType());
//                return DataBase.LIST_OF_CATEGORY;
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        return null;
//    }
}
