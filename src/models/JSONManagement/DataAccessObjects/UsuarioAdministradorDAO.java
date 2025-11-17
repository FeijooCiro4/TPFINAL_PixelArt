package models.JSONManagement.DataAccessObjects;

import models.JSONManagement.Mappers.UsuarioAdministradorMapper;
import models.JSONManagement.ReadWriteOperations;
import models.UsuarioAdministrador;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.util.List;

public class UsuarioAdministradorDAO implements ITranslatorDAO<UsuarioAdministrador>{
    private UsuarioAdministradorMapper usuarioAdministradorMapper = new UsuarioAdministradorMapper();

    @Override
    public void objectToFile(UsuarioAdministrador usuarioAdministrador, String fileName) {
        ReadWriteOperations.writeFileWithObject(fileName,
                usuarioAdministradorMapper.objectToJSONObject(usuarioAdministrador), false);
    }

    @Override
    public void listToFile(List<UsuarioAdministrador> usuarioAdministradores, String fileName) {
        ReadWriteOperations.writeFileWithArray(fileName,
                usuarioAdministradorMapper.listToJSONArray(usuarioAdministradores), false);
    }

    @Override
    public UsuarioAdministrador fileToObject(String fileName) {
        JSONTokener tokener = null;
        UsuarioAdministrador usuarioAdministrador = null;

        try {
            tokener = ReadWriteOperations.readFile(fileName);
            usuarioAdministrador = usuarioAdministradorMapper.jsonObjectToObject(new JSONObject(tokener));
        } catch (JSONException e){
            System.err.println("Error al leer el archivo " + fileName + " y deserializarlo a un objeto:");
            e.printStackTrace();
        } catch (FileNotFoundException e){
            System.err.println("Error al buscar el archivo " + fileName + ":");
            e.printStackTrace();
        }

        return usuarioAdministrador;
    }

    @Override
    public List<UsuarioAdministrador> fileToList(String fileName) {
        JSONTokener tokener = null;
        List<UsuarioAdministrador> usuarioAdministradores = null;

        try {
            tokener = ReadWriteOperations.readFile(fileName);
            usuarioAdministradores = usuarioAdministradorMapper.jsonArrayToList(new JSONArray(tokener));
        } catch (JSONException e){
            System.err.println("Error al leer el archivo " + fileName + " y deserializarlo a una lista:");
            e.printStackTrace();
        } catch (FileNotFoundException e){
            System.err.println("Error al buscar el archivo " + fileName + ":");
            e.printStackTrace();
        }

        return usuarioAdministradores;
    }
}
