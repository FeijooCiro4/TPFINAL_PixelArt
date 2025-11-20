package models.exceptions;

/// Se usa para validar la correcta entrada y validacion de un color hexadecimal
public class InvalidColorException extends Exception {
    public InvalidColorException(String message){
        super(message);
    }
}