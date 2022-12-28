package service;

import com.sun.tools.javac.Main;
import exceptions.ObjectNull;
import model.Category;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CategoryServiceTest {
    // assertNotEquals(); -> bir  biriga teng bolmasa  == passed boladi
    // assertTrue();  ->  boolean methodlarni  true bolsa == passed boladi
    // assertFalse();  ->  boolean methodlarni  false bo'lsa == passed boladi
    // assertNull();  -> method Null qaytarsa == passed boladi
    // assertNotNull();  -> method Null qaytarmasa == passed bo'ladi
    // assertThrows();  -> method exception uloqtirsa  == passed  bo'ladi

    // Lampda bn yaratiladi void methodlar

    private CategoryService categoryService;
    private Category category;
    @BeforeEach
    void setUp(){
        this.category=new Category(0,"Electronics");
    }
    @Test
    @DisplayName("getCategoryByName() powered by Axror")
    void getCategoryByName(){
        CategoryService.getList();
        assertEquals(this.category,CategoryService.getCategoryByName("Electronics","1"));
    }

    @Test
    @DisplayName("addInTest() powered  by Axror")
    void addInTest(){
        // ObjectNull ni orniga RuntimeExceptionda  ishlaydi !
        assertThrows(ObjectNull.class,() ->categoryService.add(null));
    }


}