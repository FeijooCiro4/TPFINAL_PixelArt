package controllers;

import models.exceptions.InvalidOrMissingHashPasswordException;
import models.ContraseniaHash;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.Objects;

/**
 * GestorContrasenia se encarga de generar los datos necesarios para el hasheo de una cntraseña, la generación
 * segura de una salt y la vaidación de entrada de una cadena con respecto al hash de una contrasenia.
 */
public class GestorContrasenia {

    /// Atributos estaticos
    private static final int TAMANIO_SALT = 16;
    private static final String ALGORITMO_CRPT = "SHA-256"; // Algoritmo de hasheo


    /// Validaciones y generadores

    /**
     * Devuelve una cadena String (adaptada a un formato donde se pueda guardar en un archivo JSON) del hash
     * de una contrasenia.
     */
    public static ContraseniaHash generarHashContrasenia(String contrasenia){
        byte[] saltBytes = generarSalt();
        byte[] hashBytes = hashearContrasenia(contrasenia, saltBytes);

        if(hashBytes == null) return null;

        /// Base64 formatea la cadena String a un formato estándard, aseptado por JSON
        String saltStr = Base64.getEncoder().encodeToString(saltBytes);
        String hashStr = Base64.getEncoder().encodeToString(hashBytes);

        try {
            return new ContraseniaHash(hashStr, saltStr);
        } catch (InvalidOrMissingHashPasswordException e) {
            throw new RuntimeException("Error al retornar la contrasenia generada");
        }
    }

    /**
     * Verifia si una contrasenia ingresada coincide con el hash enviado por parametro.
     * @param contraseniaIngresada es la contraseña a validar.
     * @param saltAlmacenada es la salt que está agregada en el hash.
     * @param hashAlmacenado es el resto del hash.
     * @return true si coinciden, o false si no es así.
     */
    public static boolean verificarContraseniaIngresada(String contraseniaIngresada, String saltAlmacenada, String hashAlmacenado){
        try {
            byte[] saltBytes = Base64.getDecoder().decode(saltAlmacenada);
            byte[] hashContrasniaBytes = hashearContrasenia(contraseniaIngresada, saltBytes);
            String hashContraseniaStr = Base64.getEncoder().encodeToString(hashContrasniaBytes);

            return hashContraseniaStr.equals(hashAlmacenado);
        } catch (IllegalArgumentException e){
            return false;
        }
    }

    /// Genera una salt segura y aleatoria
    private static byte[] generarSalt(){
        byte[] nuevaSalt = new byte[TAMANIO_SALT];
        SecureRandom random = new SecureRandom();

        random.nextBytes(nuevaSalt);     // Rellenar el array de bytes con valores seguros

        return nuevaSalt;
    }

    /**
     * Realiza el hasheo completo de una contrasenia ingresada.
     * @param contrasenia cadena ingresada que representa a la contrasenia.
     * @param salt es la salt con la que se concatenará en el hash final.
     * @return el hash resultante de las operaciones.
     */
    private static byte[] hashearContrasenia(String contrasenia, byte[] salt){
        /// Se valida que las entradas no sean nulas.
        Objects.requireNonNull(contrasenia, "La contrasenia no puede ser nula.");
        Objects.requireNonNull(salt, "La salt no puede ser nula.");

        try {
            MessageDigest md = MessageDigest.getInstance(ALGORITMO_CRPT); // Se crea la instancia con el algoritmo a utilizar
            md.update(salt); // Se inluye la salt

            /// Se retorna el hash de la concatenacion de la salt y la contraseña con el estándar UTF_8
            return md.digest(contrasenia.getBytes(StandardCharsets.UTF_8));
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error faltal: Algoritmo de hash " + ALGORITMO_CRPT+ " no encontrado.", e);
        }
    }
}