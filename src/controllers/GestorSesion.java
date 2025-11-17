package controllers;

import models.enumerators.PermisosAdmin;
import models.enumerators.RolUsuarios;
import models.exceptions.InvalidOrMissingHashPasswordException;
import models.Usuario;

public class GestorSesion {
    private static final GestorArchivoUsuario gestorArchivoUsuario = new GestorArchivoUsuario();

    /**
     * Metodo principal para el inicio de sesion.
     * Busca el no,bre del usuario en el achivo para saber si existe.
     * En caso afirmativo, alida si la contraseña ingresada coincide con la guardada en el archivo.
     * @param nombreUsuario nombre del usuario ingresado.
     * @param contraseniaIngresada contraseña ingresada.
     * @return true si el usuario existe y la contraseña es coincidiente con la del archivo.
     * @throws InvalidOrMissingHashPasswordException se lanza si el formato dela contraseña no es válido.
     */
    public boolean inicioSesion(String nombreUsuario, String contraseniaIngresada) throws InvalidOrMissingHashPasswordException {
        Usuario usuario = gestorArchivoUsuario.buscarUsuario(nombreUsuario);

        if(usuario == null) return false;

        try {
            return GestorContrasenia.verificarContraseniaIngresada(
                    contraseniaIngresada,
                    usuario.getHashContrasena().getSalt(),
                    usuario.getHashContrasena().getHash());
        } catch (InvalidOrMissingHashPasswordException e){
            throw new InvalidOrMissingHashPasswordException("Formato de la contrasenia invalido. El usuaio " + nombreUsuario + " no pudo iniciar sesion.");
        }
    }

    /// Metodo para registrar un usuario administrador en el archivo.
    public boolean registroSesionUsuarioAdmin(String nombre, String contrasenia, boolean activo){
        if(validarContrasenia(contrasenia)){
            // Por defecto, un admin es visualizante
            return gestorArchivoUsuario.crearUsuarioAdmin(nombre, contrasenia, activo, RolUsuarios.ADMIN, PermisosAdmin.VISUALIZANTE);
        }
        return false;
    }

    /// Metodo para registrar un usuario normal en el archivo.
    public boolean registroSesionUsuarioNormal(String nombre, String contrasenia, boolean activo){
        if(validarContrasenia(contrasenia)){
            // Por defeto no puede crear dibujos
            return gestorArchivoUsuario.crearUsuarioNormal(nombre, contrasenia, activo, RolUsuarios.NORMAL, false);
        }
        return false;
    }

    /// Valida si la contrasenia cumple con el estándar interno del programa
    private boolean validarContrasenia(String contrasenia){
        return contrasenia.length() > 8 && cantidadDigitos(contrasenia) > 3 && hayMayusculasEnCadenaTexto(contrasenia);
    }

    private int cantidadDigitos(String cadena){
        int cantidad = 0;
        for(int i=0; i<cadena.length(); i++){
            if(Character.isDigit(cadena.charAt(i))){
                cantidad++;
            }
        }

        return cantidad;
    }

    private boolean hayMayusculasEnCadenaTexto(String cadena){
        for(int i=0; i<cadena.length(); i++){
            if(Character.isUpperCase(cadena.charAt(i))){
                return true;
            }
        }

        return false;
    }
}
