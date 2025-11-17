package models.JSONManagement.DataAccessObjects;

import java.util.List;

/**
 * Interfaz que se encarga de representar la interacción de un mapper con el manejo de archivos
 * de las respectivas clases modelo.
 * @param <T> El tipo de clase a la que acceder en el archivo.
 */
public interface ITranslatorDAO<T> {
    /// Pasa un objeto Java a un objeto JSONObject.
    void objectToFile(T t, String fileName);

    ///  Pasa una lista Java a un arrego JSONArray.
    void listToFile(List<T> t, String fileName);

    /// Pasa un objeto JSONObject  a un objeto Java.
    T fileToObject(String fileName);

    /// Pasa un arreglo JSONArray a una lista Java.
    List<T> fileToList(String fileName);
}
