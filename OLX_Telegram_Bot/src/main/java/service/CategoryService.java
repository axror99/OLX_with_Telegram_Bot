package service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import dataBase.DataBase;
import exceptions.ObjectNull;
import model.Category;
import service.baseSerivice.BaseService;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

public class CategoryService implements BaseService<Category> {
    private static int index1;
    public void addCategoriesButton(String text){
        String[] array = text.split("-:-");
        if(array.length==2){
            int x =StringParser(array[0]);
            if(x!=-1){
                // create parent category
                add(addCategory(x,array[1]));
            }else{
                // create child category
                Category category = getCategoryByName(array[0],null);
                if(category!=null){
                    add(addCategory(category.getId(),array[1]));
                }
            }
        }
    }
    public static Category getCategoryByName(String categoryName,String categoryId){
        if(categoryName!=null) {
            for (Category category : DataBase.LIST_OF_CATEGORY.stream().parallel().toList()) {
                if (category != null) {
                    if (category.getName().equals(categoryName)) {
                        return category;
                    }
                }
            }
        }else {
            for (Category category : DataBase.LIST_OF_CATEGORY.stream().parallel().toList()) {
                if(category!=null){
                    if(String.valueOf(category.getId()).equals(categoryId)){
                        return category;
                    }
                }
            }
        }
        return null;
    }

    private int StringParser(String str){
        try {
            int x = Integer.parseInt(str);
            return x;
        }catch (Exception e){
            return -1;
        }
    }

    private Category addCategory(int parentId,String categoryName){
        Category category= new Category(parentId,categoryName);
        DataBase.ALL_CATEGORIES_ID.add(String.valueOf(category.getId()));
        return category;
    }

    @Override
    public void add(Category o) {
        if(o!=null) {
            DataBase.LIST_OF_CATEGORY.add(o);
            DataBase.saveCategoryToDataBase(o);
        }else {
            throw new ObjectNull();
        }
    }
    public static List<Category> getList() {
        Gson gson=new Gson();
        File file = DataBase.categoriesFile;
        if (file.exists()) {
            try (BufferedReader bufferedReader = new BufferedReader(new FileReader(file))) {
                DataBase.LIST_OF_CATEGORY = gson.fromJson(bufferedReader, new TypeToken<List<Category>>() {
                }.getType());
                return DataBase.LIST_OF_CATEGORY;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }
}
