package controllers;

import JSONManagement.DataAccessObjects.UsuarioAdministradorDAO;
import JSONManagement.DataAccessObjects.UsuarioNormalDAO;
import JSONManagement.ReadWriteOperations;
import enumerators.PermisosAdmin;
import enumerators.RolUsuarios;
import models.ContraseniaHash;
import models.Usuario;
import models.UsuarioAdministrador;
import models.UsuarioNormal;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;

public class GestorArchivoUsuario {
    private static final String NAME_FILE_ADMINS = "UsuariosAdministradores.json";
    private static final String NAME_FILE_USERS = "UsuariosNormales.json";

    private final UsuarioAdministradorDAO usuarioAdministradorDAO = new UsuarioAdministradorDAO();
    private final UsuarioNormalDAO usuarioNormalDAO = new UsuarioNormalDAO();
    private ArrayList<UsuarioAdministrador> administradores;
    private ArrayList<UsuarioNormal> normales = new ArrayList<>();
    private HashSet<Usuario> usuariosTotales = new HashSet<>();

    public GestorArchivoUsuario(){
        if(ReadWriteOperations.archivoExiste(NAME_FILE_ADMINS)) {
            actualizarListaAdmins();
        }

        if(ReadWriteOperations.archivoExiste(NAME_FILE_ADMINS)){
            actualizarListaUsuariosNormales();
        }
    }

    public boolean crearUsuarioNormal(String nombre, String contrasenia, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear){
        if(rolUsuarios.equals(RolUsuarios.NORMAL)){
            ContraseniaHash contraseniaHash = GestorContrasenia.generarHashContrasenia(contrasenia);
            normales.add(new UsuarioNormal(
                    generarIdUsuario(),
                    nombre,
                    contraseniaHash.getSalt(),
                    contraseniaHash.getHash(),
                    activo,
                    rolUsuarios,
                    puedeCrear)
            );

            guardarCambios();
            actualizarListaUsuariosNormales();
            return true;
        }

        return false;
    }

    public boolean crearUsuarioAdmin(String nombre, String contrasenia, boolean activo, RolUsuarios rolUsuarios, PermisosAdmin permisoAdmin){
        if(rolUsuarios.equals(RolUsuarios.ADMIN)){
            ContraseniaHash contraseniaHash = GestorContrasenia.generarHashContrasenia(contrasenia);
            administradores.add(new UsuarioAdministrador(
                    generarIdUsuario(),
                    nombre,
                    contraseniaHash.getSalt(),
                    contraseniaHash.getHash(),
                    activo,
                    rolUsuarios,
                    permisoAdmin)
            );

            guardarCambios();
            actualizarListaAdmins();
            return true;
        }

        return false;
    }

    public void modificarUsuarioAdmin(UsuarioAdministrador administradorModificado){
        UsuarioAdministrador administradorAModificar = buscarUsuarioAdmin(administradorModificado.getIdUsuario());

        if(administradorAModificar != null){
            administradores.remove(administradorAModificar);
            administradores.add(administradorModificado);
            guardarCambios();
            actualizarListaAdmins();
        }
    }

    public void modificarUsuarioNormal(UsuarioNormal normalModificado){
        UsuarioNormal normalAModificar = (UsuarioNormal) buscarUsuarioNormal(normalModificado.getIdUsuario());

        if(normalAModificar != null){
            normales.remove(normalAModificar);
            normales.add(normalModificado);
            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public void eliminarUsuarioAdmin(int idUsuario){
        UsuarioAdministrador administradorAEliminar = buscarUsuarioAdmin(idUsuario);

        if(administradorAEliminar != null){
            administradores.remove(administradorAEliminar);
            guardarCambios();
            actualizarListaAdmins();
        }
    }

    public void eliminarUsuarioNormal(int idUsuario){
        UsuarioNormal normalAEliminar = buscarUsuarioNormal(idUsuario);

        if(normalAEliminar != null){
            normales.remove(normalAEliminar);
            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public Usuario buscarUsuario(int idUsuario, RolUsuarios rolUsuario){
        Usuario usuario = null;

        if(rolUsuario.equals(RolUsuarios.NORMAL)){
            usuario = buscarUsuarioNormal(idUsuario);
        } else if (rolUsuario.equals(RolUsuarios.ADMIN)) {
            usuario = buscarUsuarioAdmin(idUsuario);
        }

        return usuario;
    }

    public Usuario buscarUsuario(String nombre, RolUsuarios rolUsuario){
        Usuario usuario = null;

        if(rolUsuario.equals(RolUsuarios.NORMAL)){
            usuario = buscarUsuarioNormal(nombre);
        } else if (rolUsuario.equals(RolUsuarios.ADMIN)){
            usuario = buscarUsuarioAdmin(nombre);
        }

        return usuario;
    }

    public UsuarioNormal buscarUsuarioNormal(int idUsuario){
        RolUsuarios rolUsuarioBuscado = null;

        for(Usuario usuario : usuariosTotales){
            if(usuario.getIdUsuario() == idUsuario){
                rolUsuarioBuscado = usuario.getRolUsuarios();
                break;
            }
        }

        if(rolUsuarioBuscado != null && rolUsuarioBuscado.equals(RolUsuarios.NORMAL)) {
            for (UsuarioNormal normal : normales) {
                if (normal.getIdUsuario() == idUsuario) {
                    return normal;
                }
            }
        }

        return null;
    }

    public UsuarioAdministrador buscarUsuarioAdmin(int idUsuario){
        RolUsuarios rolUsuarioBuscado = null;

        for(Usuario usuario : usuariosTotales){
            if(usuario.getIdUsuario() == idUsuario){
                rolUsuarioBuscado = usuario.getRolUsuarios();
                break;
            }
        }

        if(rolUsuarioBuscado != null && rolUsuarioBuscado.equals(RolUsuarios.ADMIN)) {
            for (UsuarioAdministrador administrador : administradores) {
                if (administrador.getIdUsuario() == idUsuario) {
                    return administrador;
                }
            }
        }

        return null;
    }

    public UsuarioNormal buscarUsuarioNormal(String nombre){
        RolUsuarios rolUsuarioBuscado = null;

        for(Usuario usuario : usuariosTotales){
            if(usuario.getNombre().equals(nombre)){
                rolUsuarioBuscado = usuario.getRolUsuarios();
                break;
            }
        }

        if(rolUsuarioBuscado != null && rolUsuarioBuscado.equals(RolUsuarios.NORMAL)) {
            for (UsuarioNormal normal : normales) {
                if (normal.getNombre().equals(nombre)) {
                    return normal;
                }
            }
        }

        return null;
    }

    public UsuarioAdministrador buscarUsuarioAdmin(String nombre){
        RolUsuarios rolUsuarioBuscado = null;

        for(Usuario usuario : usuariosTotales){
            if(usuario.getNombre().equals(nombre)){
                rolUsuarioBuscado = usuario.getRolUsuarios();
                break;
            }
        }

        if(rolUsuarioBuscado != null && rolUsuarioBuscado.equals(RolUsuarios.ADMIN)) {
            for (UsuarioAdministrador administrador : administradores) {
                if (administrador.getNombre().equals(nombre)) {
                    return administrador;
                }
            }
        }

        return null;
    }



    // Gestion de dibujos pintados y creados

    public void agregarDibujoPintado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        if(usuarioNormal != null){
            usuarioNormal.ingresarIdDibujoPintado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public void agregarDibujoCreado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        if(usuarioNormal != null){
            usuarioNormal.ingresarIdDibujoCreado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public void eliminarDibujoPintado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        if(usuarioNormal != null){
            usuarioNormal.eliminarDibujoPintado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public void eliminarDibujoCreado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        if(usuarioNormal != null){
            usuarioNormal.eliminarDibujoCreado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }


    // Gestion de acciones

    public void agregarAccion(int idAdmin, String accion){
        UsuarioAdministrador administrador = buscarUsuarioAdmin(idAdmin);

        if(administrador != null){
            administrador.ingresarAccionAlRegistro(accion);
        }
    }

    public void eliminarAccion(int idAdmin, LocalDateTime idAccion, String accion){
        UsuarioAdministrador administrador = buscarUsuarioAdmin(idAdmin);

        if(administrador != null){
            administrador.eliminarAccionDelRegistro(idAccion, accion);
        }
    }



    // Validadores y generadores

    public boolean nombreUsuarioUnico(String nombreUsuario){
        for(Usuario usuario : usuariosTotales){
            if(usuario.getNombre().equals(nombreUsuario)) return false;
        }

        return true;
    }

    private int generarIdUsuario(){
        return usuariosTotales.size() + 1;
    }

    private void guardarCambios(){
        if(administradores != null && !administradores.isEmpty()) usuarioAdministradorDAO.listToFile(administradores, NAME_FILE_ADMINS);
        if(normales != null && !normales.isEmpty()) usuarioNormalDAO.listToFile(normales, NAME_FILE_USERS);
    }

    private void actualizarListas(){
        this.usuariosTotales.clear();

        if(administradores != null && !administradores.isEmpty()) this.usuariosTotales.addAll(administradores);
        if(normales != null && !normales.isEmpty()) this.usuariosTotales.addAll(normales);
    }

    private void actualizarListaAdmins(){
        this.administradores = (ArrayList<UsuarioAdministrador>) usuarioAdministradorDAO.fileToList(NAME_FILE_ADMINS);
        actualizarListas();
    }

    private void actualizarListaUsuariosNormales(){
        this.normales = (ArrayList<UsuarioNormal>) usuarioNormalDAO.fileToList(NAME_FILE_USERS);
        actualizarListas();
    }
}
