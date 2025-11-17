package models.JSONManagement.DataAccessObjects;

import models.JSONManagement.Mappers.UsuarioNormalMapper;
import models.JSONManagement.ReadWriteOperations;
import models.UsuarioNormal;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.util.List;

public class UsuarioNormalDAO implements ITranslatorDAO<UsuarioNormal>{
    private UsuarioNormalMapper usuarioNormalMapper = new UsuarioNormalMapper();

    @Override
    public void objectToFile(UsuarioNormal usuarioNormal, String fileName) {
        ReadWriteOperations.writeFileWithObject(fileName,
                usuarioNormalMapper.objectToJSONObject(usuarioNormal), false);
    }

    @Override
    public void listToFile(List<UsuarioNormal> usuarioNormales, String fileName) {
        ReadWriteOperations.writeFileWithArray(fileName,
                usuarioNormalMapper.listToJSONArray(usuarioNormales), false);
    }

    @Override
    public UsuarioNormal fileToObject(String fileName) {
        JSONTokener tokener = null;
        UsuarioNormal usuarioNormal = null;

        try {
            tokener = ReadWriteOperations.readFile(fileName);
            usuarioNormal = usuarioNormalMapper.jsonObjectToObject(new JSONObject(tokener));
        } catch (JSONException e){
            System.err.println("Error al leer el archivo " + fileName + " y deserializarlo a un objeto:");
            e.printStackTrace();
        } catch (FileNotFoundException e){
            System.err.println("Error al buscar el archivo " + fileName + ":");
            e.printStackTrace();
        }

        return usuarioNormal;
    }

    @Override
    public List<UsuarioNormal> fileToList(String fileName) {
        JSONTokener tokener = null;
        List<UsuarioNormal> usuarioNormales = null;

        try {
            tokener = ReadWriteOperations.readFile(fileName);
            usuarioNormales = usuarioNormalMapper.jsonArrayToList(new JSONArray(tokener));
        } catch (JSONException e){
            System.err.println("Error al leer el archivo " + fileName + " y deserializarlo a una lista:");
            e.printStackTrace();
        } catch (FileNotFoundException e){
            System.err.println("Error al buscar el archivo " + fileName + ":");
            e.printStackTrace();
        }

        return usuarioNormales;
    }
}
