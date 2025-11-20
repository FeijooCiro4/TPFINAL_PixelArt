package models.exceptions;

/// Se usa para marcar donde no se encuentre un dato en una coleccion o archivo
public class MissingSearchException extends Exception {
    public MissingSearchException(String message){
        super(message);
    }
}