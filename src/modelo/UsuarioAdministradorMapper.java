import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UsuarioAdministradorMapper {

    public static JSONObject serilizarUsAdmin (UsuarioAdministrador ua) {
        JSONObject obj = null;
        try {
            obj = new JSONObject();
            obj = UsuarioMapper.serializarUsuario(ua);
            obj.put("nivelAdmin", ua.getNivelAdmin().name());
            JSONArray array = new JSONArray();
            for (Map.Entry<LocalDateTime, String> entry : ua.getRegistroAcciones().entrySet()) {
                JSONObject json = new JSONObject();
                json.put("fecha", entry.getKey());
                json.put("accion", entry.getValue());
                array.put(json);
            }
            obj.put("registroAcciones", array);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return obj;
    }

    public static UsuarioAdministrador deserializarUsAdmin (JSONObject objeto) {
        UsuarioAdministrador ua = new UsuarioAdministrador();
        try {
            ua = new UsuarioAdministrador(objeto);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return ua;
    }

    public static JSONArray serializarListaUsAdmin (List<UsuarioAdministrador> uas) {
        JSONArray array = null;
        try {
            array = new JSONArray();
            for (UsuarioAdministrador ua : uas) {
                array.put(serilizarUsAdmin(ua));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return array;
    }

    public static List<UsuarioAdministrador> deserializarListaUsAdmin (JSONArray array) {
        List<UsuarioAdministrador> uas = new ArrayList<>();
        try {
            for (int i = 0; i < array.length(); i++) {
                uas.add(deserializarUsAdmin(array.getJSONObject(i)));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return uas;
    }

}
