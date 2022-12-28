import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MainTest {

    @Test
    void getCatecoryByFiles(){
        Main.readCategoriesFile();
        Main.readProductsFile();
        Main.readIdGeneratorFile();
        Main.readUsersFile();

    }

}