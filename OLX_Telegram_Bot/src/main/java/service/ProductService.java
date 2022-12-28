package service;

import dataBase.DataBase;
import exceptions.InvalidProductException;
import model.Category;
import model.Product;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.PhotoSize;
import service.baseSerivice.BaseService;
import t_bot.Tg_Bot_DTO;

import java.math.BigDecimal;
import java.util.List;
import java.util.Objects;

public class ProductService implements BaseService<Product> {
    private static String photoOfProduct=null;
    @Override
    public void add(Product product){
        if(product!=null){
            DataBase.LIST_OF_PRODUCTS.add(product);
            DataBase.saveProductToDataBase(product);
        }else throw new InvalidProductException();
    }

    public void getProductFromAdmin(Message message){
        List<PhotoSize> list=message.getPhoto();
        photoOfProduct = list.get(0).getFileId();

    }

    public void getInformationOfProduct(String text){
        // :Product:/Category name/Product name/Name of Owner of product/Product price/Phone number/Description
        String[] productInfo = text.split("/");
        Product product = new Product();
        if(photoOfProduct!=null){
            product.setPhoto(photoOfProduct);
        }
        if(productInfo.length==7){
            product.setName(productInfo[2]);
            product.setOwnerName(productInfo[3]);
            product.setPrice(BigDecimal.valueOf(Long.parseLong(productInfo[4])));
            product.setOwnerPhoneNumber(productInfo[5]);
            product.setDescription(productInfo[6]);
            Category category = Objects.requireNonNull(CategoryService.getCategoryByName(productInfo[1], null));
            category.getProductList().add(product);
            product.setCategoryId(category.getId());
            product.setDateAndTime(Tg_Bot_DTO.getCurrentDateAndTime());
            add(product);
        }
        photoOfProduct = null;
    }

    public Product getProductByStringId(String productId){
        for (Product product : DataBase.LIST_OF_PRODUCTS.stream().parallel().toList()) {
            if(product!=null){
                if(String.valueOf(product.getId()).equals(productId)){
                    return product;
                }
            }
        }
        return null;
    }
}
