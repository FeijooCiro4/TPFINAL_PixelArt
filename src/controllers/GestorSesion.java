package controllers;

import enumerators.PermisosAdmin;
import enumerators.RolUsuarios;
import models.Usuario;

public class GestorSesion {
    private static final GestorArchivoUsuario gestorArchivoUsuario = new GestorArchivoUsuario();

    public boolean inicioSesion(String nombreUsuario, String contraseniaIngresada, RolUsuarios rolUsuarios){
        Usuario usuario = gestorArchivoUsuario.buscarUsuario(nombreUsuario, rolUsuarios);

        if(usuario == null) return false;

        return GestorContrasenia.verificarContraseniaIngresada(
                contraseniaIngresada,
                usuario.getHashContrasena().getSalt(),
                usuario.getHashContrasena().getHash());
    }

    public boolean registroSesionUsuarioAdmin(String nombre, String contrasenia, boolean activo){
        if(validarContrasenia(contrasenia)){
            return gestorArchivoUsuario.crearUsuarioAdmin(nombre, contrasenia, activo, RolUsuarios.ADMIN, PermisosAdmin.VISUALIZANTE);
        }
        return false;
    }

    public boolean registroSesionUsuarioNormal(String nombre, String contrasenia, boolean activo){
        if(validarContrasenia(contrasenia)){
            return gestorArchivoUsuario.crearUsuarioNormal(nombre, contrasenia, activo, RolUsuarios.NORMAL, false);
        }
        return false;
    }

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
