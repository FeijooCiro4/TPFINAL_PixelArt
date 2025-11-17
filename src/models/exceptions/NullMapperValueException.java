package models.exceptions;

/// Se utiliza para errores con respecto a la devolución de valore nulos en las clases Mapper
public class NullMapperValueException extends Exception {
    public NullMapperValueException(String message){
        super(message);
    }
}
