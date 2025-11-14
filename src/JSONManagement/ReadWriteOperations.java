package JSONManagement;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadWriteOperations {
    public static void writeFileWithObject(String fileName, JSONObject jsonObject, boolean writeAtEnd){
        try {
            FileWriter fileWriter = new FileWriter(fileName, writeAtEnd);
            fileWriter.write(jsonObject.toString(4));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeFileWithArray(String fileName, JSONArray jsonArray, boolean writeAtEnd){
        try {
            FileWriter fileWriter = new FileWriter(fileName, writeAtEnd);
            fileWriter.write(jsonArray.toString(4));
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONTokener readFile(String fileName) throws FileNotFoundException {
        return new JSONTokener(new FileReader(fileName));
    }

    public static boolean archivoExiste(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
}
