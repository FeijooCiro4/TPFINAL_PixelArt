package controllers;

import models.JSONManagement.DataAccessObjects.UsuarioAdministradorDAO;
import models.JSONManagement.DataAccessObjects.UsuarioNormalDAO;
import models.JSONManagement.ReadWriteOperations;
import models.enumerators.PermisosAdmin;
import models.enumerators.RolUsuarios;
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
    private ArrayList<UsuarioAdministrador> administradores = new ArrayList<>();
    private ArrayList<UsuarioNormal> normales = new ArrayList<>();
    private HashSet<Usuario> usuariosTotales = new HashSet<>();

    public GestorArchivoUsuario(){
        if(ReadWriteOperations.fileExists(NAME_FILE_ADMINS)) {
            actualizarListaAdmins();
        }

        if(ReadWriteOperations.fileExists(NAME_FILE_USERS)){
            actualizarListaUsuariosNormales();
        }
    }

    /// Metodo encargado de la creacion de un usuario normal, validando el rol ingresado, y si el nombre ingresado es único
    public boolean crearUsuarioNormal(String nombre, String contrasenia, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear){
        if(nombreUsuarioUnico(nombre) && rolUsuarios.equals(RolUsuarios.NORMAL)){
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

    /// Metodo encargado de la creacion de un usuario administrador, validando el rol ingresado, y si el nombre ingresado es único
    public boolean crearUsuarioAdmin(String nombre, String contrasenia, boolean activo, RolUsuarios rolUsuarios, PermisosAdmin permisoAdmin){
        if(nombreUsuarioUnico(nombre) && rolUsuarios.equals(RolUsuarios.ADMIN)){
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



    /**
     * Los metodos de modificacion primero validan si el usuario existe. En caso de ser así,
     * se sobreescribe el dato viejo del usuario con el nuevo en la coleccion.
     */

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
        UsuarioNormal normalAModificar = buscarUsuarioNormal(normalModificado.getIdUsuario());

        if(normalAModificar != null){
            normales.remove(normalAModificar);
            normales.add(normalModificado);
            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }


    /**
     * Los metodos de eliminacion primero validan si el usuario existe. En caso de ser así,
     * se remueve el usuario indicado.
     */

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


    /**
     * Los metodos de busqueda de usuarios generales itera sobre el set 'usuariosTotales'
     * para validar si el id o el nombre coinciden con alguno en el archivo.
     */

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



    /**
     * Los metodos de busqueda de usuarios especificos itera sobre la lista correspondiente al tipo de usuario
     * para validar si el id o el nombre coinciden con alguno en el archivo.
     */

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



    /// Gestion de dibujos pintados y creados

    public void agregarDibujoPintado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        // Valida si existe el usuario antes de añadir el id del dibujo a su lista de dibujos pintados
        if(usuarioNormal != null){
            usuarioNormal.ingresarIdDibujoPintado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public void agregarDibujoCreado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        // Valida si existe el usuario antes de añadir el id del dibujo a su lista de dibujos creados
        if(usuarioNormal != null){
            usuarioNormal.ingresarIdDibujoCreado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public void eliminarDibujoPintado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        /*
         * Valida si existe el usuario antes de añadir el id del dibujo a su lista de dibujos pintados y
         * valida si el dibujo está en el archivo.
         */
        if(usuarioNormal != null){
            usuarioNormal.eliminarDibujoPintado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }

    public void eliminarDibujoCreado(int idPropietario, int idDibujo){
        UsuarioNormal usuarioNormal = buscarUsuarioNormal(idPropietario);

        /*
         * Valida si existe el usuario antes de añadir el id del dibujo a su lista de dibujos creados y
         * valida si el dibujo está en el archivo.
         */
        if(usuarioNormal != null){
            usuarioNormal.eliminarDibujoCreado(idDibujo);

            guardarCambios();
            actualizarListaUsuariosNormales();
        }
    }



    /// Gestion de acciones

    public void agregarAccion(int idAdmin, String accion){
        UsuarioAdministrador administrador = buscarUsuarioAdmin(idAdmin);

        // Valida si el administrador existe
        if(administrador != null){
            administrador.ingresarAccionAlRegistro(accion);
        }
    }

    public void eliminarAccion(int idAdmin, LocalDateTime idAccion, String accion){
        UsuarioAdministrador administrador = buscarUsuarioAdmin(idAdmin);

        // Valida si el administrador existe
        if(administrador != null){
            administrador.eliminarAccionDelRegistro(idAccion, accion);
        }
    }



    /// Validadores y generadores

    public boolean nombreUsuarioUnico(String nombreUsuario){
        for(Usuario usuario : usuariosTotales){
            if(usuario.getNombre().equals(nombreUsuario)) return false;
        }

        return true;
    }

    private int generarIdUsuario(){
        return usuariosTotales.size() + 1;
    }

    /// Reescribe los archivos a partir de los datos de las colecciones
    private void guardarCambios(){
        if(administradores == null) this.administradores = new ArrayList<>();
        if(normales == null) this.normales = new ArrayList<>();

        usuarioAdministradorDAO.listToFile(administradores, NAME_FILE_ADMINS);
        usuarioNormalDAO.listToFile(normales, NAME_FILE_USERS);
    }

    /// Reescribe las colecciones a partir de los datos de los archvos
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
