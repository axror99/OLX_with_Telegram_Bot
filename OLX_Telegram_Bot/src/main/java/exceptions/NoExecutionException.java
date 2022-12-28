package exceptions;

import org.glassfish.grizzly.utils.Exceptions;

public class NoExecutionException extends RuntimeException {
    public NoExecutionException(){
        super("could not execute the message");
    }
}
