package model.base;

import dataBase.DataBase;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public abstract class Base {
    static int idGenerator=0;
    int id;
    private String callBackData;  // User = U, Category = C, Product = P;

    public Base(){
        idGenerator++;
        this.id = idGenerator;
        DataBase.saveIdToDataBase(this.id);
    }

    public void setCallBackData(String callBackData){
        this.callBackData = callBackData;
    }

    public static void setIdGenerator(int idGenerator) {
        Base.idGenerator = idGenerator;
    }
}
