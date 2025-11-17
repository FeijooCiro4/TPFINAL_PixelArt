package models.JSONManagement.Mappers;

import models.Cuadricula;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class CuadriculaMapper extends AbstractMapper<Cuadricula> {
    @Override
    public JSONObject objectToJSONObject(Cuadricula cuadricula){
        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject();
            jsonObject.put("indiceX", cuadricula.getIndiceX());
            jsonObject.put("indiceY", cuadricula.getIndiceY());
            jsonObject.put("color", cuadricula.getColor());
        } catch (JSONException e) {
            System.err.println("Error al serializar una cuadricula:");
            e.printStackTrace();
        }
        return jsonObject;
    }

    @Override
    public Cuadricula jsonObjectToObject(JSONObject jsonObject){
        Cuadricula cuadricula = new Cuadricula();
        try {
            cuadricula.setIndiceX(jsonObject.getInt("indiceX"));
            cuadricula.setIndiceY(jsonObject.getInt("indiceY"));
            cuadricula.setColor(jsonObject.getString("color"));
        } catch (JSONException e) {
            System.err.println("Error al deserializar una cuadricula:");
            e.printStackTrace();
        }
        return cuadricula;
    }

    @Override
    public JSONArray listToJSONArray(List<Cuadricula> cuadriculas){
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray();

            for (Cuadricula cuadricula : cuadriculas){
                jsonArray.put(objectToJSONObject(cuadricula));
            }
        } catch (JSONException e) {
            System.err.println("Error al serializar una lista de cuadriculas:");
            e.printStackTrace();
        }
        return jsonArray;
    }

    @Override
    public List<Cuadricula> jsonArrayToList(JSONArray jsonArray){
        List<Cuadricula> cuadriculas = new ArrayList<>();
        try {
            for (int i = 0; i < jsonArray.length(); i++) {
                cuadriculas.add(jsonObjectToObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e) {
            System.err.println("Error al deserializar una lista de cuadriculas:");
            e.printStackTrace();
        }
        return cuadriculas;
    }
}
