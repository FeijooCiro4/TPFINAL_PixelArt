import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;

public class DibujoDAO {

    public static void objectTOfile (String nombreArch, Dibujo d){
        OpLectoEscritura.grabar(nombreArch, DibujoMapper.serializarDibujo(d));
    }

    public static void arrayTOfile (String nombreArch, ArrayList<Dibujo> d){
        OpLectoEscritura.grabar(nombreArch, DibujoMapper.serilizarListDibujo(d));
    }

    public static ArrayList<Dibujo> fileTOarray (String nombreArch){
        JSONTokener token = OpLectoEscritura.leer(nombreArch);
        ArrayList<Dibujo> d= new ArrayList<>();
        try {
            d = DibujoMapper.deserilizarListDibujo(new JSONArray(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return d;
    }

    public static Dibujo fileTOobeject (String nombreArch){
        JSONTokener token = OpLectoEscritura.leer(nombreArch);
        Dibujo d = new Dibujo();
        try{
            d=DibujoMapper.deserializarDibujo(new JSONObject(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return d;
    }

}
