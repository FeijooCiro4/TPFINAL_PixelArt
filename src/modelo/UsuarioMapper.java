import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class UsuarioMapper {

    public static JSONObject serializarUsuario (Usuario usuario) {
        JSONObject usuarioJSON = null;
        try {
            usuarioJSON = new JSONObject();
            usuarioJSON.put("idUsuario", usuario.getIdUsuario());
            usuarioJSON.put("nombre", usuario.getNombre());
            usuarioJSON.put("hashContrasena",  usuario.getHashContrasena());
            usuarioJSON.put("salt", usuario.getSalt());
            usuarioJSON.put("activo", usuario.isActivo());
            usuarioJSON.put("rolUsuarios", usuario.getRolUsuarios().name());
        }
        catch (JSONException e) {
            e.printStackTrace();
        }
        return usuarioJSON;
    }

    public static Usuario deserializarUsuario (JSONObject usuarioJSON) {
        Usuario usuario = null; ///hay un problema, no podes deserializar directamente un
        /// usuario porque es una clase abtracta

        return  usuario;
    }

    public static JSONArray sereliazarLista (List<Usuario> usuarios) {
        JSONArray arregloUsuarios = null;
        try {
            arregloUsuarios = new JSONArray();
            for (Usuario usuario : usuarios) {
                arregloUsuarios.put(serializarUsuario(usuario));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  arregloUsuarios;
    }

    public static ArrayList<Usuario> deserializarLista (JSONArray arregloUsuarios) {
        ArrayList<Usuario> usuarios = new ArrayList<>();
        try{
            for (int i = 0; i < arregloUsuarios.length(); i++) {
                usuarios.add(deserializarUsuario(arregloUsuarios.getJSONObject(i)));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return usuarios;
    }

}
