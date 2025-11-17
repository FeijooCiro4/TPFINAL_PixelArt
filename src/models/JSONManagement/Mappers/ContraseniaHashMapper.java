package models.JSONManagement.Mappers;

import models.exceptions.InvalidOrMissingHashPasswordException;
import models.ContraseniaHash;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ContraseniaHashMapper extends AbstractMapper<ContraseniaHash>{
    @Override
    public JSONObject objectToJSONObject(ContraseniaHash contraseniaHash) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject();
            jsonObject.put("hash", contraseniaHash.getHash());
            jsonObject.put("salt", contraseniaHash.getSalt());
        } catch (JSONException e){
            System.err.println("Error al serializar una contrasenia:");
            e.printStackTrace();
        }

        return jsonObject;
    }

    @Override
    public ContraseniaHash jsonObjectToObject(JSONObject jsonObject) {
        ContraseniaHash contraseniaHash = new ContraseniaHash();

        try {
            String hash = jsonObject.getString("hash");
            String salt = jsonObject.getString("salt");

            contraseniaHash = new ContraseniaHash(hash,salt);
        } catch (JSONException e){
            System.err.println("Error al deserializar una contrasenia:");
            e.printStackTrace();
        } catch (InvalidOrMissingHashPasswordException e){
            System.err.println("Error en el formato del hash o salt de la contrasenia guardada:");
            e.printStackTrace();
        }

        return contraseniaHash;
    }

    @Override
    public JSONArray listToJSONArray(List<ContraseniaHash> contraseniaHashes) {
        JSONArray jsonArray = null;

        try {
            jsonArray = new JSONArray();
            for (ContraseniaHash contraseniaHash : contraseniaHashes){
                jsonArray.put(objectToJSONObject(contraseniaHash));
            }
        } catch (JSONException e){
            System.err.println("Error al serializar la lista de contrasenias:");
            e.printStackTrace();
        }

        return jsonArray;
    }

    @Override
    public List<ContraseniaHash> jsonArrayToList(JSONArray jsonArray) {
        List<ContraseniaHash> contraseniaHashes = new ArrayList<>();

        try {
            for(int i=0; i<jsonArray.length(); i++){
                contraseniaHashes.add(jsonObjectToObject(jsonArray.getJSONObject(i)));
            }
        } catch (JSONException e){
            System.err.println("Error al deserializar la lista de contrasenias:");
            e.printStackTrace();
        }

        return contraseniaHashes;
    }
}
