package models.JSONManagement.Mappers;

import models.exceptions.NullMapperValueException;
import models.Cuadricula;
import models.Dibujo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.TreeMap;

public class DibujoMapper extends AbstractMapper<Dibujo> {
    @Override
    public JSONObject objectToJSONObject(Dibujo dibujo){
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            jsonObject.put("idDibujo", dibujo.getIdDibujo());
            jsonObject.put("idPropietario", dibujo.getIdPropietario());
            jsonObject.put("nombreDibujo", dibujo.getNombreDibujo());
            jsonObject.put("activo", dibujo.isActivo());
            jsonObject.put("anchoCuadricula", dibujo.getAnchoCuadricula());
            jsonObject.put("clavesColores", CollectionsMapper.mapToJSONObject(dibujo.getClavesColores()));
            jsonObject.put("cuadriculas", CollectionsMapper.setToJSONArray(dibujo.getCuadriculas()));
        } catch (JSONException e){
            System.err.println("Error al serializar un dibujo:");
            e.printStackTrace();
        } catch (NullMapperValueException e){
            System.err.println("Error al mapear las colecciones:");
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public Dibujo jsonObjectToObject(JSONObject jsonObject) {
        Dibujo dibujo = new Dibujo();

        try {
            dibujo.setIdDibujo(jsonObject.getInt("idDibujo"));
            dibujo.setIdPropietario(jsonObject.getInt("idPropietario"));
            dibujo.setNombreDibujo(jsonObject.getString("nombreDibujo"));
            dibujo.setActivo(jsonObject.getBoolean("activo"));
            dibujo.setAnchoCuadricula(jsonObject.getInt("anchoCuadricula"));
            dibujo.setClavesColores((TreeMap<Integer, String>) CollectionsMapper.jsonObjectToMap(
                    jsonObject.getJSONObject("clavesColores"), Integer.class, String.class));
            dibujo.setCuadriculas((HashSet<Cuadricula>) CollectionsMapper.jsonArrayToSet(
                    jsonObject.getJSONArray("cuadriculas"), Cuadricula.class));
        } catch (JSONException e){
            System.err.println("Error al deserializar un dibujo:");
            e.printStackTrace();
        } catch (NullMapperValueException e){
            System.err.println("Error al mapear las colecciones:");
            e.printStackTrace();
        }

        return dibujo;
    }

    @Override
    public JSONArray listToJSONArray(List<Dibujo> dibujos) {
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray();

            for(Dibujo dibujo : dibujos){
                jsonArray.put(objectToJSONObject(dibujo));
            }
        } catch (JSONException e) {
            System.err.println("Error al serializar una lista de dibujos:");
            e.printStackTrace();
        }

        return jsonArray;
    }

    @Override
    public List<Dibujo> jsonArrayToList(JSONArray jsonArray) {
        List<Dibujo> dibujos = new ArrayList<>();

        try {
            for(int i=0; i<jsonArray.length(); i++){
                dibujos.add(jsonObjectToObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e){
            System.err.println("Error al deserializar una lista de dibujos:");
            e.printStackTrace();
        }

        return dibujos;
    }
}
