package modelo;

import java.util.Objects;

public abstract class  Usuario {


    /// Atributos

    private int idUsuario;

    private String nombre;

    private String hashContrasena;

    private String salt;

    private boolean activo;

    private RolUsuarios rolUsuarios;


    /// Constructor

    public Usuario() {
    }


    public Usuario(int idUsuario, String nombre, String hashContrasena, String salt, boolean activo, RolUsuarios rolUsuarios) {
        this.idUsuario = idUsuario;
        this.nombre = nombre;
        this.hashContrasena = hashContrasena;
        this.salt = salt;
        this.activo = activo;
        this.rolUsuarios = rolUsuarios;
    }



    /// getter y setters


    public int getIdUsuario() {
        return idUsuario;
    }


    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getHashContrasena() {
        return hashContrasena;
    }


    public String getSalt() {
        return salt;
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
    public boolean equals(Object o) {
        if (!(o instanceof Usuario usuario)) return false;
        return idUsuario == usuario.idUsuario && activo == usuario.activo && Objects.equals(nombre, usuario.nombre) && Objects.equals(hashContrasena, usuario.hashContrasena) && Objects.equals(salt, usuario.salt) && rolUsuarios == usuario.rolUsuarios;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idUsuario, nombre, hashContrasena, salt, activo, rolUsuarios);
    }
}
