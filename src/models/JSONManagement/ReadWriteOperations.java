package models.JSONManagement;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ReadWriteOperations {
    /**
     * @param fileName nombre del archivo a escribir.
     * @param jsonObject objeto a escribir en el archivo.
     * @param writeAtEnd valida si se quiere escribir al final del archivo (si sobreescribir o no).
     */
    public static void writeFileWithObject(String fileName, JSONObject jsonObject, boolean writeAtEnd){
        try {
            FileWriter fileWriter = new FileWriter(fileName, writeAtEnd);
            fileWriter.write(jsonObject.toString(4));
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo " + fileName + " con un JSONObject.");
            e.printStackTrace();
        }
    }

    /**
     * @param fileName nombre del archivo a escribir.
     * @param jsonArray arreglo a escribir en el archivo.
     * @param writeAtEnd valida si se quiere escribir al final del archivo (si sobreescribir o no).
     */
    public static void writeFileWithArray(String fileName, JSONArray jsonArray, boolean writeAtEnd){
        try {
            FileWriter fileWriter = new FileWriter(fileName, writeAtEnd);
            fileWriter.write(jsonArray.toString(4));
            fileWriter.close();
        } catch (IOException e) {
            System.err.println("Error al escribir el archivo " + fileName + " con un JSONArray.");
            e.printStackTrace();
        }
    }

    public static JSONTokener readFile(String fileName) throws FileNotFoundException {
        return new JSONTokener(new FileReader(fileName));
    }

    /**
     * Verifica si el archivo existe antes de realizar cualquier operación sobre él.
     */
    public static boolean fileExists(String fileName) {
        File file = new File(fileName);
        return file.exists();
    }
}
