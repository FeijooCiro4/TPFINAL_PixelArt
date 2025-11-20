package models.JSONManagement.Mappers;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.List;

/**
 * Las Clases Mapper representan la serialización y deserialiacion de las clases a guardar (json.org).
 * La clase abstracta AbstractMapper<T> se utiliza para representar todos los comportamientos comunes de los Mapper.
 * Se requiere ser una clase abstracta y no una interfaz debido a que se requiere utilizar instanciaciones
 * del tipo de dato abstracto del Mapper genérico.
 * @param <T> Tipo de dato genérico que sirve para representar a la clase a mapear.
 */
public abstract class AbstractMapper<T> {
    /// Serializar objeto t
    abstract JSONObject objectToJSONObject(T t);

    /// Deserializar objeto t
    abstract T jsonObjectToObject(JSONObject jsonObject);

    /// Serializar lista de objetos <T>
    abstract JSONArray listToJSONArray(List<T> collection);

    /// Deserializar lista de objetos <T>
    abstract List<T> jsonArrayToList(JSONArray jsonArray);
}