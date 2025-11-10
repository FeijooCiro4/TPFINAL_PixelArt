package modelo;

import java.util.HashSet;

public class UsuarioNormal extends Usuario {


    /// atributos

    private boolean puedeCrear;

    private HashSet<Integer> dibujosCreados;

    private HashSet<Integer> dibujosPintados;



    /// Constructor


    public UsuarioNormal() {                 // aca inicializo los set por las dudas
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }

    public UsuarioNormal(boolean puedeCrear) {

        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }


    public UsuarioNormal(int idUsuario, String nombre, String hashContrasena, String salt, boolean activo, RolUsuarios rolUsuarios, boolean puedeCrear) {
        super(idUsuario, nombre, hashContrasena, salt, activo, rolUsuarios);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
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
        return dibujosCreados.add(idDibujoCreado);
    }

    public boolean eliminarDibujoCreado(int idDibujoCreado)
    {
        return dibujosCreados.remove(idDibujoCreado);
    }

    public boolean buscarDibujoCreado(int idDibujo)
    {
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




    /// recorrer el hashset // solo para prueba

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





}
