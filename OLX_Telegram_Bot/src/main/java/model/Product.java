package model;

import lombok.*;
import model.base.Base;

import java.math.BigDecimal;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Product extends Base {
    private String name;
    private String photo;
    private int categoryId;
    private BigDecimal price;
    private String ownerName;
    private String ownerPhoneNumber;
    private String description;
    private boolean saleState;
    private String dateAndTime;

    @Override
    public String toString() {
        String forSale;
        if(saleState){
            forSale = "❌ Sotilgan";
        }else{
            forSale = "✅ Sotuvda";
        }
        return " Name \uD83D\uDCC4  :  " + name + '\n' +
                " Price \uD83D\uDCB5 :   " + price +" so`m"+"\n"+
                " Product owner :  " +ownerName+"\n"+
                " Owner phone number :  " +ownerPhoneNumber+"\n"+
                " For Sale :  " +forSale+"\n"+
                " Additional description :  " +description+"\n"+
                " Uploaded date and time : " + dateAndTime +"\n";
    }

}
