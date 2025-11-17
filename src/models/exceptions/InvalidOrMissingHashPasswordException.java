package models.exceptions;

/// Se usa para representar errores con respecto al hasheo o validacion de una contraseña de usuario
public class InvalidOrMissingHashPasswordException extends Exception {
    public InvalidOrMissingHashPasswordException(String message){
        super(message);
    }
}
