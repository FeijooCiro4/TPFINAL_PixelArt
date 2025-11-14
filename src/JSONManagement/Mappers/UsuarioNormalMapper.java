package JSONManagement.Mappers;

import enumerators.RolUsuarios;
import models.UsuarioNormal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class UsuarioNormalMapper extends AbstractMapper<UsuarioNormal>{
    @Override
    public JSONObject objectToJSONObject(UsuarioNormal usuarioNormal) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            ContraseniaHashMapper contraseniaHashMapper = new ContraseniaHashMapper();

            jsonObject.put("idUsuario", usuarioNormal.getIdUsuario());
            jsonObject.put("nombre", usuarioNormal.getNombre());
            jsonObject.put("contraseniaHash",
                    contraseniaHashMapper.objectToJSONObject(usuarioNormal.getHashContrasena()));
            jsonObject.put("activo", usuarioNormal.isActivo());
            jsonObject.put("RolUsuario", usuarioNormal.getRolUsuarios());

            jsonObject.put("puedeCrear", usuarioNormal.isPuedeCrear());
            jsonObject.put("dibujosCreados", CollectionsMapper.setToJSONArray(usuarioNormal.getDibujosCreados()));
            jsonObject.put("dibujosPintados", CollectionsMapper.setToJSONArray(usuarioNormal.getDibujosPintados()));
        } catch (JSONException e){
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public UsuarioNormal jsonObjectToObject(JSONObject jsonObject) {
        UsuarioNormal usuarioNormal = new UsuarioNormal();

        try {
            ContraseniaHashMapper contraseniaHashMapper = new ContraseniaHashMapper();

            usuarioNormal.setIdUsuario(jsonObject.getInt("idUsuario"));
            usuarioNormal.setNombre(jsonObject.getString("nombre"));
            usuarioNormal.setHashContrasena(contraseniaHashMapper.jsonObjectToObject(
                    jsonObject.getJSONObject("contraseniaHash")));
            usuarioNormal.setActivo(jsonObject.getBoolean("activo"));
            String rolString = jsonObject.getString("RolUsuario");
            usuarioNormal.setRolUsuarios(RolUsuarios.valueOf(rolString));

            usuarioNormal.setPuedeCrear(jsonObject.getBoolean("puedeCrear"));
            usuarioNormal.setDibujosCreados((HashSet<Integer>)
                    CollectionsMapper.jsonArrayToSet(jsonObject.getJSONArray("dibujosCreados"), Integer.class));
            usuarioNormal.setDibujosPintados((HashSet<Integer>)
                    CollectionsMapper.jsonArrayToSet(jsonObject.getJSONArray("dibujosPintados"), Integer.class));
        } catch (JSONException e){
            e.printStackTrace();
        }

        return usuarioNormal;
    }

    @Override
    public JSONArray listToJSONArray(List<UsuarioNormal> usuarioNormales) {
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray();
            for(UsuarioNormal usuarioNormal : usuarioNormales){
                jsonArray.put(objectToJSONObject(usuarioNormal));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return jsonArray;
    }

    @Override
    public List<UsuarioNormal> jsonArrayToList(JSONArray jsonArray) {
        List<UsuarioNormal> usuarioNormales = new ArrayList<>();

        try {
            for(int i=0; i<jsonArray.length(); i++){
                usuarioNormales.add(jsonObjectToObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e){
            e.printStackTrace();
        }

        return usuarioNormales;
    }
}
