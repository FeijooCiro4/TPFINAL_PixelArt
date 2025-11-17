package models.JSONManagement.Mappers;

import models.exceptions.NullMapperValueException;
import models.*;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

/**
 * CollectionsMapper es la clase Mapper encargada de serializar y deserializar colecciones sets y maps.
 * Es el único mapper que no hereda de AbstractMapper, ya que necesita una implementación especial
 * para sus métodos.
 */
public class CollectionsMapper {
    /**
     * MAPPER_REGISTRY es un map que registra y asocia cada clase personalizada con su respectivo Mapper.
     */
    private static final Map<Class<?>, Class<? extends AbstractMapper<?>>> MAPPER_REGISTRY = new HashMap<>();

    static {
        MAPPER_REGISTRY.put(ContraseniaHash.class, ContraseniaHashMapper.class);
        MAPPER_REGISTRY.put(Cuadricula.class, CuadriculaMapper.class);
        MAPPER_REGISTRY.put(Dibujo.class, DibujoMapper.class);
        MAPPER_REGISTRY.put(UsuarioAdministrador.class, UsuarioAdministradorMapper.class);
        MAPPER_REGISTRY.put(UsuarioNormal.class, UsuarioNormalMapper.class);
    }

    /**
     * Serializador de cualquier map, con cualquier tipo de dato como llave o valor.
     * Se utiiza Map<?, ?> porque se desconoce el tipo de dato que se querrá serializar.
     * @param map el mapa a serializar.
     * @return el JSONObject del mapa serializado.
     * @throws NullMapperValueException se lanza si se recibe un map nulo o si ocurre un error al serializar.
     */
    public static JSONObject mapToJSONObject(Map<?, ?> map) throws NullMapperValueException {
        if (map == null) {
            throw new NullMapperValueException("El map ingresado es nulo. Serializacion cancelada");
        }

        JSONObject jsonObject = new JSONObject();

        for (Map.Entry<?, ?> entry : map.entrySet()) {
            String keyStr = String.valueOf(entry.getKey());

            Object value = entry.getValue();
            Object serializedValue = value;

            // Si el valor es un map, se llama recursivamente al metodo.
            if (value instanceof Map) {
                serializedValue = mapToJSONObject((Map<?, ?>) value);

            // Si el valor es un set, se llama al serializador de colecciones set.
            } else if (value instanceof Set) {
                serializedValue = setToJSONArray((Set<?>) value);

            // Si el valor es una clase personalizada (validado por el metodo isCustomObject), procede a implementar su serialización
            } else if (value != null && isCustomObject(value)) {
                Class<?> modelClass = value.getClass();         // Tipo de clase
                if (MAPPER_REGISTRY.containsKey(modelClass)) {  // Se evalúa si la clase está como clave en MAPPER_REGISTRY
                    try {
                        /*
                        * Se crea una instancia del tipo de clase Mapper dentro de los valores del mapa estátio
                        * (utilizando la clase abstracta de Mapper genérico).
                        * */
                        Class<? extends AbstractMapper<?>> mapperClass = MAPPER_REGISTRY.get(modelClass);

                        /*
                        * Se crea una instancia de la clase abstracta AbstractMapper. Esto es válido ya que cualquier Mapper
                        * que herede de esta clase prodrá serializarse.
                        * */
                        AbstractMapper<?> mapper = mapperClass.getConstructor().newInstance();

                        /*
                        * Se cra una instancia del mapper utilizando el tipo de clase Mapper que sea llamado.
                        * '@SuppressWarnings("unchecked")' El IDE tirará advertencias ya que para el sistema
                        * no se sabe el tipo de clase que se querrá usar. Sin embargo, con nuestro MAPPER_REGISTRY,
                        * tenemos resuelta esa cuestión.
                        * */
                        @SuppressWarnings("unchecked")
                        AbstractMapper<Object> typedMapper = (AbstractMapper<Object>) mapper;

                        /*
                        * Se busca llamar al metodo 'objectToJSONObject' del mapper necesario.
                        * */
                        serializedValue = typedMapper.objectToJSONObject(value);
                    } catch (Exception e) {
                        System.err.println("Error al serializar objeto " + modelClass.getSimpleName() + " usando su Mapper:");
                        throw new NullMapperValueException("Fallo en serialización por Mapper: " + e.getMessage());
                    }
                }
            }

            jsonObject.put(keyStr, serializedValue);
        }
        return jsonObject;
    }

    /**
     * Serializador de cualquier map, con cualquier tipo de dato.
     * Se utiiza Set<?> porque se desconoce el tipo de dato que se querrá serializar.
     * @param set el set a serializar.
     * @return el JSONArray del set serializado (al se una colección que hereda de Collection, debe
     * tratarse como JSONArray).
     * @throws NullMapperValueException se lanza si se recibe un set nulo o si ocurre un error al serializar.
     */
    public static JSONArray setToJSONArray(Set<?> set) throws NullMapperValueException {
        if (set == null) {
            throw new NullMapperValueException("El set ingresado es nulo. Serializacion cancelada");
        }

        JSONArray jsonArray = new JSONArray();
        for (Object item : set) {       // Se recorren todos los items del set
            Object serializedItem = item;

            // Si el valor es un map, se llama al aerializador de mao.
            if (item instanceof Map) {
                serializedItem = mapToJSONObject((Map<?, ?>) item);

            // Si el valor es un set, se llama recursivamente al metodo.
            } else if (item instanceof Set) {
                serializedItem = setToJSONArray((Set<?>) item);

            // Si el valor es una clase personalizada (validado por el metodo isCustomObject), procede a implementar su serialización
            } else if (item != null && isCustomObject(item)) {
                Class<?> modelClass = item.getClass();
                if (MAPPER_REGISTRY.containsKey(modelClass)) {
                    try {
                        /*
                         * Se crea una instancia del tipo de clase Mapper dentro de los valores del mapa estátio
                         * (utilizando la clase abstracta de Mapper genérico).
                         * */
                        Class<? extends AbstractMapper<?>> mapperClass = MAPPER_REGISTRY.get(modelClass);

                        /*
                         * Se crea una instancia de la clase abstracta AbstractMapper. Esto es válido ya que cualquier Mapper
                         * que herede de esta clase prodrá serializarse.
                         * */
                        AbstractMapper<?> mapper = mapperClass.getConstructor().newInstance();

                        /*
                         * Se cra una instancia del mapper utilizando el tipo de clase Mapper que sea llamado.
                         * '@SuppressWarnings("unchecked")' El IDE tirará advertencias ya que para el sistema
                         * no se sabe el tipo de clase que se querrá usar. Sin embargo, con nuestro MAPPER_REGISTRY,
                         * tenemos resuelta esa cuestión.
                         * */
                        @SuppressWarnings("unchecked")
                        AbstractMapper<Object> typedMapper = (AbstractMapper<Object>) mapper;

                        /*
                         * Se busca llamar al metodo 'objectToJSONObject' del mapper necesario.
                         * */
                        serializedItem = typedMapper.objectToJSONObject(item);

                    } catch (Exception e) {
                        System.err.println("Error al serializar objeto " + modelClass.getSimpleName() + " usando su Mapper:");
                        throw new NullMapperValueException("Fallo en serialización por Mapper: " + e.getMessage());
                    }
                }
            }

            jsonArray.put(serializedItem);
        }
        return jsonArray;
    }

    /**
     * Deserializador de cualquier tipo de Map con cualquier tipo de dato como clave o valor (estrictamente constantes en el metodo).
     * A diferencia del serializador, acá se conocerán los tipos de datos de la clave y el valor porque se identifican
     * en el archivo y desde el Mapper que lo utilice.
     * @param jsonObject Objeto JSON a deserializar.
     * @param keyClass Clase de las claves del map a deserializar.
     * @param valueClass Clase de los valores del map a deserializar.
     * @return El mapa deserializado.
     * @param <K> El tipo de dato de las claves.
     * @param <V> El tipo de dato de los valores.
     * @throws NullMapperValueException Si el JSONObject es nulo u ocurre un error en la deserializacion del mismo.
     */
    public static <K, V> Map<K, V> jsonObjectToMap(JSONObject jsonObject, Class<K> keyClass, Class<V> valueClass)
            throws NullMapperValueException {

        if (jsonObject == null) {
            throw new NullMapperValueException("El objecto json no puede ser nulo. Error al deserializar a el map");
        }

        Map<K, V> map = new TreeMap<>();    // Se utiliza TreeMap porque el sistema así lo requiere. Sin embargo no es la convención adecuada.
        Iterator<String> keys = jsonObject.keys();

        while (keys.hasNext()) {
            String keyStr = keys.next();
            Object value = jsonObject.get(keyStr);

            K key = convertKey(keyStr, keyClass);                   // Se llama al deserializador de claves
            V deserializedValue = convertValue(value, valueClass);  // Se llama al deserializador de valores

            map.put(key, deserializedValue);
        }
        return map;
    }

    /**
     * Deserializador de cualquier tipo de Set con cualquier tipo de dato (estrictamente constante en el metodo).
     * A diferencia del serializador, acá se conocerán los tipos de datos de la clave y el valor porque se identifican
     * en el archivo y desde el Mapper que lo utilice.
     * @param jsonArray arreglo JSON a deserializar.
     * @param elementClass la clase del valor.
     * @return El set deserializado.
     * @param <T> el tipo de dato del valor del set.
     * @throws NullMapperValueException se lanza si el JSONArray es nulo u ocurre un error al deserializar.
     */
    public static <T> Set<T> jsonArrayToSet(JSONArray jsonArray, Class<T> elementClass) throws NullMapperValueException {
        if (jsonArray == null) {
            throw new NullMapperValueException("El arreglo json no puede ser nulo. Error al deserializar a el set");
        }

        Set<T> set = new HashSet<>();   // Por convencion, se utiliza HashSet
        for (int i = 0; i < jsonArray.length(); i++) {
            Object item = jsonArray.get(i);

            T deserializedItem = convertValue(item, elementClass);  // Se deserializa el valor
            set.add(deserializedItem);
        }
        return set;
    }

    /**
     * Valida si el objeto NO es una instancia de los valores comparados.
     * @param obj objeto a validar.
     * @return true si NO es una instancia, y false en caso de que SI es una instancia.
     */
    private static boolean isCustomObject(Object obj) {
        return !(obj instanceof Number || obj instanceof Boolean || obj instanceof String ||
                obj instanceof Map || obj instanceof Set || obj instanceof java.util.List ||
                obj.getClass().isArray());
    }

    /**
     * Convierte una clave en un tipo de dato correspondiente. Siendo que puede ser:
     * 1. Un tipo Integer.
     * 2. Un tipo Double.
     * 3. Un tipo cualquiera (representado por el tipo de dato genérico K).
     * @param keyStr la cadena del valor de la clave.
     * @param keyClass el tipo de clase de la clave.
     * @return la clave convertida.
     * @param <K> El tipo de dato de la clave.
     */
    @SuppressWarnings("unchecked")
    private static <K> K convertKey(String keyStr, Class<K> keyClass) {
        if (keyClass == Integer.class || keyClass == int.class) {
            return (K) Integer.valueOf(keyStr);
        } else if (keyClass == Double.class || keyClass == double.class) {
            return (K) Double.valueOf(keyStr);
        }
        return (K) keyStr;
    }

    /**
     * Convierte un valor en un tipo de dato correspondiente.
     * @param jsonValue el valor a convertir.
     * @param valueClass clase del valor a convertir.
     * @return el valor convertido.
     * @param <V> es el tipo de dato convertido.
     * @throws NullMapperValueException se lanza si el dato a convertir es nulo u ocurre un error en la conversion.
     */
    @SuppressWarnings("unchecked")
    private static <V> V convertValue(Object jsonValue, Class<V> valueClass) throws NullMapperValueException {
        if (jsonValue == null) {
            throw new NullMapperValueException("Valor json nulo. Error en la conversion");
        }

        if (valueClass.isInstance(jsonValue)) {
            return (V) jsonValue;
        }

        switch (jsonValue) {
            // Si el valor es un map, se implementa el metodo que eserializa maps.
            case JSONObject jsonObject when Map.class.isAssignableFrom(valueClass) -> {
                return (V) jsonObjectToMap(jsonObject, String.class, Object.class);
            }
            // Si el valor es un set, se implementa el metodo que eserializa sets.
            case JSONArray jsonArray when Set.class.isAssignableFrom(valueClass) -> {
                return (V) jsonArrayToSet(jsonArray, Object.class);
            }
            // Si el valor es cualquier otro tipo, se verifica si es tipo de una clase personalizada.
            case JSONObject jsonObject -> {
                if (MAPPER_REGISTRY.containsKey(valueClass)) {  // Se recorre el mapa MAPPER_REGISTRY para verificar que haya un Mapper.
                    try {
                        //Se crea una instancia del tipo de clase mapper a usar.
                        Class<? extends AbstractMapper<V>> mapperClass =
                                (Class<? extends AbstractMapper<V>>) MAPPER_REGISTRY.get(valueClass);

                        // Se crea una instancia del mapper a partir de su tipo.
                        AbstractMapper<V> mapper = mapperClass.getConstructor().newInstance();

                        // Se intenta retornar la devolución de la llamada al metodo 'jsonObjectToObject' que cada mapper debe tener
                        return mapper.jsonObjectToObject(jsonObject);
                    } catch (Exception e) {
                        throw new NullMapperValueException("Error al instanciar o deserializar con Mapper para " + valueClass.getSimpleName() + ": " + e.getMessage());
                    }
                }
            }
            default -> {
                throw new NullMapperValueException("Clase del valor json inidentificable");
            }
        }


        return (V) jsonValue;
    }
}
