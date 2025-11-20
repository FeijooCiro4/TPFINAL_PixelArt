package models.JSONManagement.Mappers;

import models.exceptions.NullMapperValueException;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class CollectionsMapper {
    private static final Map<Class<?>, Class<? extends AbstractMapper<?>>> MAPPER_REGISTRY = new HashMap<>();

    static {
        MAPPER_REGISTRY.put(ContraseniaHash.class, ContraseniaHashMapper.class);
        MAPPER_REGISTRY.put(Cuadricula.class, CuadriculaMapper.class);
        MAPPER_REGISTRY.put(Dibujo.class, DibujoMapper.class);
        MAPPER_REGISTRY.put(UsuarioAdministrador.class, UsuarioAdministradorMapper.class);
        MAPPER_REGISTRY.put(UsuarioNormal.class, UsuarioNormalMapper.class);
    }


    public static JSONObject mapToJSONObject(Map<?, ?> map) throws NullMapperValueException {
        if (map == null) {
            throw new NullMapperValueException("El map ingresado es nulo. Serializacion cancelada");
        }

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String keyStr = String.valueOf(entry.getKey());

            Object value = entry.getValue();
            Object serializedValue = value;

            if (value instanceof Map) {
                serializedValue = mapToJSONObject((Map<?, ?>) value);
            } else if (value instanceof Set) {
                serializedValue = setToJSONArray((Set<?>) value);
            } else if (value != null && isCustomObject(value)) {
                // ✅ CORREGIDO: Usar el mapper del registro en lugar de invocar método inexistente
                Class<?> valueClass = value.getClass();
                if (MAPPER_REGISTRY.containsKey(valueClass)) {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<? extends AbstractMapper<Object>> mapperClass =
                                (Class<? extends AbstractMapper<Object>>) MAPPER_REGISTRY.get(valueClass);
                        AbstractMapper<Object> mapper = mapperClass.getConstructor().newInstance();
                        serializedValue = mapper.objectToJSONObject(value);
                    } catch (Exception e) {
                        System.err.println("Error al serializar objeto personalizado con mapper: " + e.getMessage());
                        e.printStackTrace();
                        return null;
                    }
                } else {
                    System.err.println("No hay mapper registrado para: " + valueClass.getSimpleName());
                    return null;
                }
            }

            jsonObject.put(keyStr, serializedValue);
        }
        return jsonObject;
    }

    public static JSONArray setToJSONArray(Set<?> set) throws NullMapperValueException {
        if (set == null) {
            throw new NullMapperValueException("El set ingresado es nulo. Serializacion cancelada");
        }

        JSONArray jsonArray = new JSONArray();
        for (Object item : set) {
            Object serializedItem = item;

            if (item instanceof Map) {
                serializedItem = mapToJSONObject((Map<?, ?>) item);
            } else if (item instanceof Set) {
                serializedItem = setToJSONArray((Set<?>) item);
            } else if (item != null && isCustomObject(item)) {
                // ✅ CORREGIDO: Usar el mapper del registro
                Class<?> itemClass = item.getClass();
                if (MAPPER_REGISTRY.containsKey(itemClass)) {
                    try {
                        @SuppressWarnings("unchecked")
                        Class<? extends AbstractMapper<Object>> mapperClass =
                                (Class<? extends AbstractMapper<Object>>) MAPPER_REGISTRY.get(itemClass);
                        AbstractMapper<Object> mapper = mapperClass.getConstructor().newInstance();
                        serializedItem = mapper.objectToJSONObject(item);
                    } catch (Exception e) {
                        System.err.println("Error al serializar item con mapper: " + e.getMessage());
                        e.printStackTrace();
                    }
                } else {
                    System.err.println("No hay mapper registrado para: " + itemClass.getSimpleName());
                }
            }

            jsonArray.put(serializedItem);
        }
        return jsonArray;
    }

    public static <K, V> Map<K, V> jsonObjectToMap(JSONObject jsonObject, Class<K> keyClass, Class<V> valueClass)
            throws NullMapperValueException {

        if (jsonObject == null) {
            throw new NullMapperValueException("El objecto json no puede ser nulo. Error al deserializar a el map");
        }

        Map<K, V> map = new TreeMap<>();
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String keyStr = keys.next();
            Object value = jsonObject.get(keyStr);

            K key = convertKey(keyStr, keyClass);
            V deserializedValue = convertValue(value, valueClass);

            map.put(key, deserializedValue);
        }
        return map;
    }

    public static <T> Set<T> jsonArrayToSet(JSONArray jsonArray, Class<T> elementClass) throws NullMapperValueException {
        if (jsonArray == null) {
            throw new NullMapperValueException("El arreglo json no puede ser nulo. Error al deserializar a el set");
        }

        Set<T> set = new HashSet<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            Object item = jsonArray.get(i);

            T deserializedItem = convertValue(item, elementClass);
            set.add(deserializedItem);
        }
        return set;
    }

    private static boolean isCustomObject(Object obj) {
        return !(obj instanceof Number || obj instanceof Boolean || obj instanceof String ||
                obj instanceof Map || obj instanceof Set || obj instanceof List ||
                obj.getClass().isArray());
    }

    @SuppressWarnings("unchecked")
    private static <K> K convertKey(String keyStr, Class<K> keyClass) {
        if (keyClass == Integer.class || keyClass == int.class) {
            return (K) Integer.valueOf(keyStr);
        } else if (keyClass == Double.class || keyClass == double.class) {
            return (K) Double.valueOf(keyStr);
        }
        return (K) keyStr;
    }

    @SuppressWarnings("unchecked")
    private static <V> V convertValue(Object jsonValue, Class<V> valueClass) throws NullMapperValueException {
        if (jsonValue == null) {
            throw new NullMapperValueException("Valor json nulo. Error en la conversion");
        }

        if (valueClass.isInstance(jsonValue)) {
            return (V) jsonValue;
        }

        if (jsonValue instanceof JSONObject jsonObject) {
            if (Map.class.isAssignableFrom(valueClass)) {
                return (V) jsonObjectToMap(jsonObject, String.class, Object.class);
            } else if (MAPPER_REGISTRY.containsKey(valueClass)) {
                try {
                    Class<? extends AbstractMapper<V>> mapperClass =
                            (Class<? extends AbstractMapper<V>>) MAPPER_REGISTRY.get(valueClass);
                    AbstractMapper<V> mapper = mapperClass.getConstructor().newInstance();
                    return mapper.jsonObjectToObject(jsonObject);
                } catch (Exception e) {
                    throw new NullMapperValueException("Error al instanciar o deserializar con Mapper para " + valueClass.getSimpleName() + ": " + e.getMessage());
                }
            } else {
                throw new NullMapperValueException("No hay mapper registrado para la clase: " + valueClass.getSimpleName());
            }
        } else if (jsonValue instanceof JSONArray jsonArray) {
            if (Set.class.isAssignableFrom(valueClass)) {
                return (V) jsonArrayToSet(jsonArray, Object.class);
            } else {
                throw new NullMapperValueException("No se puede convertir JSONArray a: " + valueClass.getSimpleName());
            }
        } else {
            throw new NullMapperValueException("Clase del valor json inidentificable: " + jsonValue.getClass().getSimpleName());
        }
    }
}