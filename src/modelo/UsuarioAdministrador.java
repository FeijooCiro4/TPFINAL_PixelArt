package modelo;

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
        this.registroAcciones = new TreeMap<>();
    }

    public UsuarioAdministrador(PermisosAdmin nivelAdmin) { // hago este porque en el uml aparece con el treemap y me resulto raro
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>();
    }

    public UsuarioAdministrador(PermisosAdmin nivelAdmin, TreeMap<LocalDateTime, String> registroAcciones) {
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>();
    }

    public UsuarioAdministrador(int idUsuario, String nombre, String hashContrasena, String salt, boolean activo, RolUsuarios rolUsuarios, PermisosAdmin nivelAdmin) // este lo hago por si tambien se necesitaba
    {
        super(idUsuario, nombre, hashContrasena, salt, activo, rolUsuarios);
        this.nivelAdmin = nivelAdmin;
        this.registroAcciones = new TreeMap<>();
    }

    /// getter y setters


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


    /// metodos para el registro de las acciones


    public boolean ingresarAccionAlRegistro(String accion) {

        LocalDateTime fechahora = LocalDateTime.now();

        if (!registroAcciones.containsKey(fechahora))
        {
            registroAcciones.put(fechahora, accion);

            return true;
        }

        return false; // si la clave de la accion es igual no la ingresa y devuelve false (no se si necesitaran comprobar eso pero por las dudas lo hago asi)
    }


    public boolean eliminarAccionDelRegistro(LocalDateTime fechaHora, String accion)
    {
       return registroAcciones.remove(fechaHora,accion);
    }


    public List<String> buscarAcciones(int anio, int mes, int dia)
    {
        List<String> accionEncontrada = new ArrayList<>();

        for (Map.Entry<LocalDateTime, String> entrada : registroAcciones.entrySet())
        {
            LocalDateTime fechaHora = entrada.getKey();

            if (fechaHora.getYear() == anio &&
                    fechaHora.getMonthValue() == mes &&
                    fechaHora.getDayOfMonth() == dia) {

                accionEncontrada.add(entrada.getValue());
            }
        }

        return accionEncontrada;

    }















}
