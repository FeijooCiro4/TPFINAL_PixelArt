package modelo;

import java.util.*;

public class Dibujo {

    ///  atributos
    private int idDibujo;
    private int idPropietario;
    private String nombreDibujo;
    private boolean activo;
    private int anchoCuadricula;
    private TreeMap<Integer,String> clavesColores;
    private HashSet<Cuadricula> cuadriculas;



    /// constructores

    public Dibujo() {}

    public Dibujo(int idDibujo, int idPropietario, String nombreDibujo, boolean activo, int anchoCuadricula) {
        this.idDibujo = idDibujo;
        this.idPropietario = idPropietario;
        this.nombreDibujo = nombreDibujo;
        this.activo = activo;
        this.anchoCuadricula = anchoCuadricula;
        this.clavesColores = new TreeMap<>();
        this.cuadriculas = new HashSet<>();
    }

    public Dibujo(int idDibujo, int idPropietario, String nombreDibujo, boolean activo, int anchoCuadricula, TreeMap<Integer,String> clavesColores, HashSet<Cuadricula> cuadriculas) {
        this.idDibujo = idDibujo;
        this.idPropietario = idPropietario;
        this.nombreDibujo = nombreDibujo;
        this.activo = activo;
        this.anchoCuadricula = anchoCuadricula;
        this.clavesColores = clavesColores;
        this.cuadriculas = cuadriculas;
    }



    /// getters y setters

    public int getIdDibujo() {
        return idDibujo;
    }

    public void setIdDibujo(int idDibujo) {
        this.idDibujo = idDibujo;
    }

    public int getIdPropietario() {
        return idPropietario;
    }

    public void setIdPropietario(int idPropietario) {
        this.idPropietario = idPropietario;
    }

    public String getNombreDibujo() {
        return nombreDibujo;
    }

    public void setNombreDibujo(String nombreDibujo) {
        this.nombreDibujo = nombreDibujo;
    }

    public boolean isActivo() {
        return activo;
    }

    public void setActivo(boolean activo) {
        this.activo = activo;
    }

    public int getAnchoCuadricula() {
        return anchoCuadricula;
    }

    public void setAnchoCuadricula(int anchoCuadricula) {
        this.anchoCuadricula = anchoCuadricula;
    }

    public TreeMap<Integer, String> getClavesColores() {
        return clavesColores;
    }

    public void setClavesColores(TreeMap<Integer, String> clavesColores) {
        this.clavesColores = clavesColores;
    }

    public HashSet<Cuadricula> getCuadriculas() {
        return cuadriculas;
    }

    public void setCuadriculas(HashSet<Cuadricula> cuadriculas) {
        this.cuadriculas = cuadriculas;
    }



    // Métodos para clavesColores

    public boolean insertarColor(String color)
    {
        if (clavesColores.containsValue(color))
        {
            return false;
        }

        int claveAux = clavesColores.size() + 1;

        clavesColores.put(claveAux, color);
        return true;
    }

    public boolean eliminarColor(String color)
    {
       Integer claveColor = null;

        for (Map.Entry<Integer,String> entry : clavesColores.entrySet())
        {
            if (entry.getValue().equalsIgnoreCase(color))
            {
                claveColor = entry.getKey();
                break;
            }
        }

        if (claveColor != null)
        {
            clavesColores.remove(claveColor);
            return true;
        }
        return false;
    }

    public boolean estaColorEnMap(String color)
    {
        return clavesColores.containsValue(color);
    }



    /// metodos de la cuadricula

    public boolean buscarCuadricula(int indiceX, int indiceY)
    {
        for (Cuadricula c : cuadriculas)
        {
            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {
                return true;
            }
        }
        return false;
    }

    public String colorCuadricula(int indiceX, int indiceY)
    {
        for (Cuadricula c : cuadriculas)
        {
            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {
                return c.getColor();
            }
        }
        return "#0000"; // Cuando no se encuantra color en una cuadrícula, el color por defecto siempre será blanco
    }

    public boolean cambiarColorCuadricula(int indiceX, int indiceY, String color)
    {
        for (Cuadricula c : cuadriculas) {
            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {
                c.setColor(color);
                return true;
            }
        }
        return false;
    }

    public boolean eliminarCuadricula(int indiceX, int indiceY)
    {
        Iterator<Cuadricula> iterator = cuadriculas.iterator();

        while (iterator.hasNext())
        {
            Cuadricula c = iterator.next();

            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {
                iterator.remove();
                return true;
            }
        }
        return false;
    }



    // Overrides

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Dibujo dibujo)) return false;
        return idDibujo == dibujo.idDibujo;
    }

    @Override
    public int hashCode() {
        return Objects.hash(idDibujo);
    }





}
