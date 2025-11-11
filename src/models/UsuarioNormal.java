package models;

import enumerators.RolUsuarios;

import java.util.HashSet;

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
        this.dibujosCreados = dibujosCreados;
        this.dibujosPintados = dibujosPintados;
    }

    public UsuarioNormal(int idUsuario, String nombre, String hashContrasena, String salt, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear) {
        super(idUsuario, nombre, hashContrasena, salt, activo, rolUsuarios);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }

    public UsuarioNormal(int idUsuario, String nombre, String hashContrasena, String salt, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear, HashSet<Integer> dibujosCreados, HashSet<Integer> dibujosPintados) {
        super(idUsuario, nombre, hashContrasena, salt, activo, rolUsuarios);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = dibujosCreados;
        this.dibujosPintados = dibujosPintados;
    }



    ///  getter y setters

    public boolean isPuedeCrear() {
        return puedeCrear;
    }

    public void setPuedeCrear(boolean puedeCrear) {
        this.puedeCrear = puedeCrear;
    }

    public HashSet<Integer> getDibujosCreados() {
        return dibujosCreados;
    }

    public void setDibujosCreados(HashSet<Integer> dibujosCreados) {
        this.dibujosCreados = dibujosCreados;
    }

    public HashSet<Integer> getDibujosPintados() {
        return dibujosPintados;
    }

    public void setDibujosPintados(HashSet<Integer> dibujosPintados) {
        this.dibujosPintados = dibujosPintados;
    }



    /// metodos de DIBUJO CREADO

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



    /// recorrer el hashset -> solo para prueba

    public void mostrarHashsetDibuCreados()
    {
        for (Integer id : dibujosCreados)
        {
            System.out.println("\nID dibujo creado: " + id);
        }
    }

    public void mostrarHashsetDibuPintados()
    {
        for (Integer id : dibujosPintados)
        {
            System.out.println("\nID dibujo pintado: " + id);
        }
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
