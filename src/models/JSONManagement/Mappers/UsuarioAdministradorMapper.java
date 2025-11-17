package models.JSONManagement.Mappers;

import models.enumerators.PermisosAdmin;
import models.enumerators.RolUsuarios;
import models.exceptions.InvalidOrMissingHashPasswordException;
import models.exceptions.NullMapperValueException;
import models.UsuarioAdministrador;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeMap;

public class UsuarioAdministradorMapper extends AbstractMapper<UsuarioAdministrador>{
    @Override
    public JSONObject objectToJSONObject(UsuarioAdministrador usuarioAdministrador) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            ContraseniaHashMapper contraseniaHashMapper = new ContraseniaHashMapper();

            jsonObject.put("idUsuario", usuarioAdministrador.getIdUsuario());
            jsonObject.put("nombre", usuarioAdministrador.getNombre());
            jsonObject.put("contraseniaHash",
                    contraseniaHashMapper.objectToJSONObject(usuarioAdministrador.getHashContrasena()));
            jsonObject.put("activo", usuarioAdministrador.isActivo());
            jsonObject.put("RolUsuario", usuarioAdministrador.getRolUsuarios());

            jsonObject.put("PermisosAdmin", usuarioAdministrador.getNivelAdmin());
            jsonObject.put("registroAcciones",
                    CollectionsMapper.mapToJSONObject(usuarioAdministrador.getRegistroAcciones()));
        } catch (JSONException e){
            System.err.println("Error al serializar un administrador:");
            e.printStackTrace();
        } catch (InvalidOrMissingHashPasswordException e){
            System.err.println("Error al ingresar la contrasenia de un administrador:");
            e.printStackTrace();
        } catch (NullMapperValueException e){
            System.err.println("Error al mapear el registro de acciones de un administrador:");
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public UsuarioAdministrador jsonObjectToObject(JSONObject jsonObject) {
        UsuarioAdministrador usuarioAdministrador = new UsuarioAdministrador();

        try {
            ContraseniaHashMapper contraseniaHashMapper = new ContraseniaHashMapper();

            usuarioAdministrador.setIdUsuario(jsonObject.getInt("idUsuario"));
            usuarioAdministrador.setNombre(jsonObject.getString("nombre"));
            usuarioAdministrador.setHashContrasena(contraseniaHashMapper.jsonObjectToObject(
                    jsonObject.getJSONObject("contraseniaHash")));
            usuarioAdministrador.setActivo(jsonObject.getBoolean("activo"));
            String rolString = jsonObject.getString("RolUsuario");
            usuarioAdministrador.setRolUsuarios(RolUsuarios.valueOf(rolString));

            String permisoStr = jsonObject.getString("PermisosAdmin");
            usuarioAdministrador.setNivelAdmin(PermisosAdmin.valueOf(permisoStr));
            usuarioAdministrador.setRegistroAcciones((TreeMap<LocalDateTime, String>) CollectionsMapper.jsonObjectToMap(
                            jsonObject.getJSONObject("registroAcciones"), LocalDateTime.class, String.class));
        } catch (JSONException e){
            System.err.println("Error al deserializar un administrador:");
            e.printStackTrace();
        } catch (IllegalArgumentException e){
            System.err.println("El valor JSON para 'RolUsuario' o 'PermisosUsuario' no es válido para el enum correspondiente.");
            e.printStackTrace();
        } catch (NullMapperValueException e){
            System.err.println("Error al mapear el registro de acciones de un administrador:");
            e.printStackTrace();
        }

        return usuarioAdministrador;
    }

    @Override
    public JSONArray listToJSONArray(List<UsuarioAdministrador> usuarioAdministradores) {
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray();
            for(UsuarioAdministrador usuarioAdministrador : usuarioAdministradores){
                jsonArray.put(objectToJSONObject(usuarioAdministrador));
            }
        } catch (JSONException e){
            System.err.println("Error al serializar una lista de administradores:");
            e.printStackTrace();
        }

        return jsonArray;
    }

    @Override
    public List<UsuarioAdministrador> jsonArrayToList(JSONArray jsonArray) {
        List<UsuarioAdministrador> usuarioAdministradores = new ArrayList<>();

        try {
            for(int i=0; i<jsonArray.length(); i++){
                usuarioAdministradores.add(jsonObjectToObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e){
            System.err.println("Error al deserializar una lista de administradore:");
            e.printStackTrace();
        }

        return usuarioAdministradores;
    }
}
