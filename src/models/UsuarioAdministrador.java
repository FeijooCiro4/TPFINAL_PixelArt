package models;

import models.enumerators.PermisosAdmin;
import models.enumerators.RolUsuarios;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class UsuarioAdministrador extends Usuario {

    /// atributos

    private PermisosAdmin nivelAdmin;
    private TreeMap<LocalDateTime,String> registroAcciones;



    /// Constructor

    public UsuarioAdministrador() {
        super();
        this.nivelAdmin = PermisosAdmin.VISUALIZANTE;
        this.registroAcciones = new TreeMap<>();
    }

    public UsuarioAdministrador(PermisosAdmin nivelAdmin) {
        super();
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>();
    }

    public UsuarioAdministrador(PermisosAdmin nivelAdmin, TreeMap<LocalDateTime, String> registroAcciones) {
        super();
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>(registroAcciones);
    }

    public UsuarioAdministrador(int idUsuario, String nombre, String salt, String hash, boolean activo, RolUsuarios rolUsuarios, PermisosAdmin nivelAdmin)
    {
        super(idUsuario, nombre, salt, hash, activo, rolUsuarios);
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>();
    }

    public UsuarioAdministrador(int idUsuario, String nombre, String salt, String hash, boolean activo, RolUsuarios rolUsuarios, PermisosAdmin nivelAdmin, TreeMap<LocalDateTime, String> registroAcciones)
    {
        super(idUsuario, nombre, salt, hash, activo, rolUsuarios);
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>(registroAcciones);
    }

    public UsuarioAdministrador(int idUsuario, String nombre, ContraseniaHash hashContrasenia, boolean activo, RolUsuarios rolUsuarios, PermisosAdmin nivelAdmin)
    {
        super(idUsuario, nombre, hashContrasenia, activo, rolUsuarios);
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>();
    }

    public UsuarioAdministrador(int idUsuario, String nombre, ContraseniaHash hashContrasenia, boolean activo, RolUsuarios rolUsuarios, PermisosAdmin nivelAdmin,TreeMap<LocalDateTime, String> registroAcciones)
    {
        super(idUsuario, nombre, hashContrasenia, activo, rolUsuarios);
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>(registroAcciones);
    }



    /// getters y setters

    public PermisosAdmin getNivelAdmin() {
        return nivelAdmin;
    }

    public void setNivelAdmin(PermisosAdmin nivelAdmin) {
        this.nivelAdmin = nivelAdmin;
    }

    public TreeMap<LocalDateTime, String> getRegistroAcciones() {
        return registroAcciones;
    }

    public void setRegistroAcciones(TreeMap<LocalDateTime, String> registroAcciones) {
        this.registroAcciones = registroAcciones;
    }



    // metodos para el registro de las acciones

    /**
     * En base a la hora actual (quitando nanosegundos), se registra una accion
     * en el map 'registroAcciones'.
     * Si se da la improvable casualidad de que haya una acción exacta
     * en fecha, se añade un segundo para que la fecha pudiera usarse
     * como clase dentro del map.
     * */
    public void ingresarAccionAlRegistro(String accion)
    {
        LocalDateTime fechaHora = LocalDateTime.now().withNano(0);

        if (registroAcciones.containsKey(fechaHora))
        {
            fechaHora = fechaHora.plusSeconds(1);
        }

        registroAcciones.put(fechaHora, accion);
    }

    /**
     * Se eliminará una accion del map 'registroAcciones' si hay una coincidncia
     * con los parámeros:
     * @param fechaHora es la llave dentro del map.
     * @param accion es el valor dentro del map.
     * @return true si la clave a borrar se ha encontrado, y el metodo 'remove' lo retornara.
     * O retorna false si no hay ninguna clave coincidiente en el map o si el metodo 'remove' lo retornara.
     * */
    public boolean eliminarAccionDelRegistro(LocalDateTime fechaHora, String accion)
    {
        LocalDateTime claveAborrar = null;

        for (Map.Entry<LocalDateTime, String> entry : registroAcciones.entrySet()) {
            if (entry.getKey().equals(fechaHora) && entry.getValue().equals(accion)) {  // Corrección: se debe comprobar la fecha completa
                claveAborrar = entry.getKey();
                break;
            }
        }

        if (claveAborrar != null) {
            registroAcciones.remove(claveAborrar);
            return true;
        }

        return false;
    }

    public void eliminarAccionDelRegistro(LocalDateTime fechaHora) {
        registroAcciones.remove(fechaHora);
    }

    /**
     * Se busca, en base a una fecha y en el rando del día de la misma,
     * la lista de acciones del administrador.
     * El metodo 'plusDays' marcó el final del ultimo dia, y el metodo 'subMap'
     * facilitó la búsqueda por rango horario entre el inicio y el fin del dia.
     * */
    public List<String> buscarAccionesDelDia(LocalDateTime fechaIngresada)
    {
        LocalDateTime inicioDia = fechaIngresada.toLocalDate().atStartOfDay();
        LocalDateTime finDia = inicioDia.plusDays(1);

        Map<LocalDateTime, String> accionesEncontradas = registroAcciones.subMap(inicioDia, finDia);

        return new ArrayList<>(accionesEncontradas.values());
    }

    @Override
    public String toString() {
        return super.toString() + "\nUsuarioAdministrador{" +
                "nivelAdmin=" + nivelAdmin +
                ", registroAcciones=" + registroAcciones +
                '}';
    }
}