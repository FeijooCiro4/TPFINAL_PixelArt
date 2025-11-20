package models;

import models.enumerators.RolUsuarios;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class UsuarioNormal extends Usuario {

    /// atributos

    private boolean puedeCrear;
    private HashSet<Integer> dibujosCreados;
    private HashSet<Integer> dibujosPintados;



    /// Constructor

    public UsuarioNormal() {                 // aca inicializo los set por las dudas
        super();
        this.puedeCrear = false;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }

    public UsuarioNormal(boolean puedeCrear) {
        super();
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }

    public UsuarioNormal(boolean puedeCrear, HashSet<Integer> dibujosCreados, HashSet<Integer> dibujosPintados) {
        super();
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>(dibujosCreados);
        this.dibujosPintados = new HashSet<>(dibujosPintados);
    }

    public UsuarioNormal(int idUsuario, String nombre, String salt, String hash, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear) {
        super(idUsuario, nombre, salt, hash, activo, rolUsuarios);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }

    public UsuarioNormal(int idUsuario, String nombre, ContraseniaHash hashContrasenia, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear) {
        super(idUsuario, nombre, hashContrasenia, activo, rolUsuarios);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }

    public UsuarioNormal(int idUsuario, String nombre, String salt, String hash, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear, HashSet<Integer> dibujosCreados, HashSet<Integer> dibujosPintados) {
        super(idUsuario, nombre, salt, hash, activo, rolUsuarios);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>(dibujosCreados);
        this.dibujosPintados = new HashSet<>(dibujosPintados);
    }

    public UsuarioNormal(int idUsuario, String nombre, ContraseniaHash hashContrasenia, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear, HashSet<Integer> dibujosCreados, HashSet<Integer> dibujosPintados) {
        super(idUsuario, nombre, hashContrasenia, activo, rolUsuarios);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>(dibujosCreados);
        this.dibujosPintados = new HashSet<>(dibujosPintados);
    }



    ///  getter y setters

    public boolean isPuedeCrear() {
        return puedeCrear;
    }

    public void setPuedeCrear(boolean puedeCrear) {
        this.puedeCrear = puedeCrear;
    }

    public Set<Integer> getDibujosCreados() {
        return Collections.unmodifiableSet(dibujosCreados);
    }

    public void setDibujosCreados(HashSet<Integer> dibujosCreados) {
        this.dibujosCreados = dibujosCreados;
    }

    public Set<Integer> getDibujosPintados() {
        return Collections.unmodifiableSet(dibujosPintados);
    }

    public void setDibujosPintados(HashSet<Integer> dibujosPintados) {
        this.dibujosPintados = dibujosPintados;
    }



    /// metodos de DIBUJO CREADO (si 'puedeCrear' == false -> retornan falso. El usuario deberá ser autorizado por un admin)

    public boolean ingresarIdDibujoCreado(int idDibujoCreado)
    {
        if(!puedeCrear) return false;
        return dibujosCreados.add(idDibujoCreado);
    }

    public boolean eliminarDibujoCreado(int idDibujoCreado)
    {
        if(!puedeCrear) return false;
        return dibujosCreados.remove(idDibujoCreado);
    }

    public boolean buscarDibujoCreado(int idDibujo)
    {
        if(!puedeCrear) return false;
        return dibujosCreados.contains(idDibujo);
    }



    /// metodos de DIBUJO PINTADO

    public boolean ingresarIdDibujoPintado(int idDibujoPintado)
    {
        return dibujosPintados.add(idDibujoPintado);
    }

    public boolean eliminarDibujoPintado(int idDibujoPintado)
    {
        return dibujosPintados.remove(idDibujoPintado);
    }

    public boolean buscarDibujoPintado(int idDibujo)
    {
        return dibujosPintados.contains(idDibujo);
    }



    @Override
    public String toString() {
        return super.toString() + "\nUsuarioNormal{" +
                "puedeCrear=" + puedeCrear +
                ", dibujosCreados=" + dibujosCreados +
                ", dibujosPintados=" + dibujosPintados +
                '}';
    }
}