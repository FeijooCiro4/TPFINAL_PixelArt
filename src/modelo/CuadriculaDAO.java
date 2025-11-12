import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class CuadriculaDAO {

    public static void objectTOfile (String nombreArch, Cuadricula c){
        OpLectoEscritura.grabar(nombreArch, CuadriculaMapper.serializarCuadricula(c));
    }

    public static void arrayTOfile (String nombreArch, ArrayList<Cuadricula> c){
        OpLectoEscritura.grabar(nombreArch, CuadriculaMapper.serializarListaCuadricula(c));
    }

    public static Cuadricula fileToobject (String nombreArch){
        JSONTokener token = OpLectoEscritura.leer(nombreArch);
        Cuadricula c = new Cuadricula();
        try {
            c=CuadriculaMapper.deserializarCuadricula(new JSONObject(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

    public static ArrayList<Cuadricula> fileToarray (String nombreArch){
        JSONTokener token = OpLectoEscritura.leer(nombreArch);
        ArrayList<Cuadricula> c = new ArrayList<>();
        try {
            c=CuadriculaMapper.deserializarListaCuadricula(new JSONArray(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return c;
    }

}
