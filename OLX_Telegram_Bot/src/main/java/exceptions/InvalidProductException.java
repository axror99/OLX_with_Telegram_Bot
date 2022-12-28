package exceptions;

public class InvalidProductException extends RuntimeException{
    public InvalidProductException() {
        super("Product is null");
    }
}
