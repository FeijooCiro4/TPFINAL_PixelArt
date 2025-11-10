import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class OpLectoEscritura {

    public static void grabar(JSONArray x, String nombreArch){
        try (FileWriter fw = new FileWriter(nombreArch)){
            fw.write(x.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void grabar(JSONObject x, String nombreArch){
        try (FileWriter fw = new FileWriter(nombreArch)){
            fw.write(x.toString(4));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static JSONTokener leer(String nombreArch){
        JSONTokener token=null;
        try {
            token=new JSONTokener(new FileReader(nombreArch));
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
        return token;
    }
}
