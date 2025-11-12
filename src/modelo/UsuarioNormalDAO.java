import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.ArrayList;
import java.util.List;

public class UsuarioNormalDAO {

    public static void objectTOfile (String nombreArch, UsuarioNormal un) {
        OpLectoEscritura.grabar(nombreArch, UsuarioNormalMapper.serializarUsuarioNormal(un));
    }

    public static void arrayTOfile (String nombreArch, List<UsuarioNormal> uns) {
        OpLectoEscritura.grabar(nombreArch, UsuarioNormalMapper.serializarListaUsNormal(uns));
    }

    public static List<UsuarioNormal> fileTOarray (String nombreArch) {
        JSONTokener token = OpLectoEscritura.leer(nombreArch);
        List<UsuarioNormal> uns = new ArrayList<>();
        try {
            uns=UsuarioNormalMapper.deserializarListaUsNormal(new JSONArray(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uns;
    }

    public static UsuarioNormal fileToObject(String nombreArch) {
        JSONTokener token = OpLectoEscritura.leer(nombreArch);
        UsuarioNormal un = new UsuarioNormal();
        try{
            un=UsuarioNormalMapper.deserializarUsuarioNormal(new JSONObject(token));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return un;
    }

}
