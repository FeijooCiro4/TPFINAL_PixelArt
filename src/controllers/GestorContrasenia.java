package controllers;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;

public class GestorContrasenia {
    private static final int tamanioArrSalt = 16;
    private byte[] hashContraseniaFinal;

    public GestorContrasenia(String contrasenia) {
        this.hashContraseniaFinal = hashearContrasenia(contrasenia, generarSalt());
    }

    public byte[] getHashContraseniaFinal() {
        return hashContraseniaFinal;
    }

    public byte[] generarSalt(){
        byte[] salt = new byte[tamanioArrSalt];
        SecureRandom random = new SecureRandom();

        random.nextBytes(salt);     // Rellenar el array de bytes con valores seguros

        return salt;
    }

    public byte[] hashearContrasenia(String contrasenia, byte[] salt){
        MessageDigest md = null;

        try {
            md = MessageDigest.getInstance("SHA-256");  // Asignacion de tipo de algoritmo criptografico
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Error con el procesamiento de algoritmo criptografico:");
            e.printStackTrace();
        }

        if (
                md != null ||
                !(contrasenia == null || contrasenia.isEmpty()) ||
                salt != null
        ) {
            md.update(salt);
            return md.digest(contrasenia.getBytes(StandardCharsets.UTF_8));
            /*
            * Se aplica la contraseña despues de aplicar la salt en el digestor.
            * Se obtiene el hash final.
            * */
        }

        return null;
    }

    public String bytesToString(byte[] arrBytes){
        if(arrBytes == null) return null;

        // Convierte un arreglo de bytes en un String guardable para una base de datos o archivo JSON
        return Base64.getEncoder().encodeToString(arrBytes);
    }
}
