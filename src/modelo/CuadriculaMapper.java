import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CuadriculaMapper {

    public static JSONObject serializarCuadricula(Cuadricula c){
        JSONObject json=null;
        try {
            json=new JSONObject();
            json.put("indiceX", c.getIndiceX());
            json.put("indiceY", c.getIndiceY());
            json.put("color", c.getColor());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return json;
    }

    public static Cuadricula deserializarCuadricula(JSONObject json){
        Cuadricula c=null;
        try {
         c=new Cuadricula(json);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static JSONArray serializarListaCuadricula (List<Cuadricula> c){
        JSONArray arr=null;
        try {
            arr=new JSONArray();
            for (Cuadricula cd:c){
                arr.put(serializarCuadricula(cd));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return arr;
    }

    public static ArrayList<Cuadricula> deserializarListaCuadricula(JSONArray arr){
        ArrayList<Cuadricula> list=new ArrayList<>();
        try {
            for (int i = 0; i < arr.length(); i++) {
                list.add(deserializarCuadricula(arr.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return list;
    }

}
