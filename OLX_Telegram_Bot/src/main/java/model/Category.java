package model;

import lombok.*;
import model.base.Base;

import java.util.LinkedList;
import java.util.List;

@NoArgsConstructor
@Getter
@Setter
@ToString(callSuper = true)
public class Category extends Base {
    List<Product> productList = new LinkedList<>();
    private int parentId;
    private String name;

    public Category(int parentId, String name) {
        this.parentId = parentId;
        this.name = name;
    }
}
