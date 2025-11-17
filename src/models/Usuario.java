package models;

import models.enumerators.RolUsuarios;
import models.exceptions.InvalidOrMissingHashPasswordException;

import java.util.Objects;

public abstract class  Usuario {

    /// Atributos

    private int idUsuario;
    private String nombre;
    private ContraseniaHash hashContrasena;
    private boolean activo;
    private RolUsuarios rolUsuarios;



    /// Constructor

    public Usuario() {
        this.idUsuario = 0;
        this.nombre = "";
        this.hashContrasena = new ContraseniaHash();
        this.activo = false;
        this.rolUsuarios = RolUsuarios.NORMAL;
    }

    public Usuario(int idUsuario, String nombre, String salt, String hash, boolean activo, RolUsuarios rolUsuarios) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;

        try {
            this.hashContrasena = new ContraseniaHash(hash, salt);
        } catch (InvalidOrMissingHashPasswordException e) {
            this.hashContrasena = new ContraseniaHash();
        }

        this.activo = activo;
        this.rolUsuarios = rolUsuarios;
    }

    public Usuario(int idUsuario, String nombre, ContraseniaHash hashContrasena, boolean activo, RolUsuarios rolUsuarios) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.hashContrasena = hashContrasena;
        this.activo = activo;
        this.rolUsuarios = rolUsuarios;
    }



    /// getter y setters

    public int getIdUsuario() {
        return idUsuario;
    }

    public void setIdUsuario(int idUsuario) {
        this.idUsuario = idUsuario;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public ContraseniaHash getHashContrasena() throws InvalidOrMissingHashPasswordException {
        try {
            return new ContraseniaHash(hashContrasena.getHash(), hashContrasena.getSalt());
        } catch (IllegalArgumentException e) {
            throw new InvalidOrMissingHashPasswordException("Error al retornar el hash de la contrasenia del usuario " + nombre);
        }
    }

    public void setHashContrasena(ContraseniaHash hashContrasena) {
        this.hashContrasena = hashContrasena;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public RolUsuarios getRolUsuarios() {
        return rolUsuarios;
    }

    public void setRolUsuarios(RolUsuarios rolUsuarios) {
        this.rolUsuarios = rolUsuarios;
    }




    /// Hash y equals

    @Override
    public boolean equals(Object o)
    {
        if(this == o) return true;
        if(o == null || getClass() != o.getClass()) return false;

        Usuario u = (Usuario) o;
        return idUsuario == u.idUsuario;
    }

    @Override
    public int hashCode() {
        // Correccion: Los datos dentro de estos metodos deben ser inmutables (para HashSet o HashMap), como la clave primaria
        return Objects.hash(idUsuario);
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", hashContrasena='" + hashContrasena + '\'' +
                ", activo=" + activo +
                ", rolUsuarios=" + rolUsuarios +
                '}';
    }
}
