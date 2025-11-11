package exceptions;

public class MissingKeyOrValueException extends Exception {
    public MissingKeyOrValueException(String message){
        super(message);
    }
}
