package JSONManagement.DataAccessObjects;

import JSONManagement.Mappers.DibujoMapper;
import JSONManagement.ReadWriteOperations;
import models.Dibujo;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.util.List;

public class DibujoDAO implements ITranslatorDAO<Dibujo>{
    private DibujoMapper dibujoMapper = new DibujoMapper();

    @Override
    public void objectToFile(Dibujo dibujo, String fileName) {
        ReadWriteOperations.writeFileWithObject(fileName, dibujoMapper.objectToJSONObject(dibujo), false);
    }

    @Override
    public void listToFile(List<Dibujo> dibujos, String fileName) {
        ReadWriteOperations.writeFileWithArray(fileName, dibujoMapper.listToJSONArray(dibujos), false);
    }

    @Override
    public Dibujo fileToObject(String fileName) {
        JSONTokener tokener = null;
        Dibujo dibujo = null;

        try {
            tokener = ReadWriteOperations.readFile(fileName);
            dibujo = dibujoMapper.jsonObjectToObject(new JSONObject(tokener));
        } catch (JSONException | FileNotFoundException e){
            e.printStackTrace();
        }

        return dibujo;
    }

    @Override
    public List<Dibujo> fileToList(String fileName) {
        JSONTokener tokener = null;
        List<Dibujo> dibujos = null;

        try {
            tokener = ReadWriteOperations.readFile(fileName);
            dibujos = dibujoMapper.jsonArrayToList(new JSONArray(tokener));
        } catch (JSONException | FileNotFoundException e){
            e.printStackTrace();
        }

        return dibujos;
    }
}
