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

    public Dibujo() {
        this.idDibujo = 0;
        this.idPropietario = -1;
        this.nombreDibujo = "";
        this.activo = false;
        this.anchoCuadricula = 0;
        this.clavesColores = new TreeMap<>();
        this.cuadriculas = new HashSet<>();
    }

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

    public void insertarColor(String color) throws MissingKeyOrValueException
    {
        if (clavesColores.containsValue(color))
        {
            throw new MissingKeyOrValueException("El color debe ser unico en la seleccion");
        } else if(color.equals("#FFFFFF"))
        {
            throw new MissingKeyOrValueException("El color blanco no puede ser añadidio a la seleccion");
        }

        int claveAux = clavesColores.size() + 1;
        clavesColores.put(claveAux, color);
    }

    public void eliminarColor(String color) throws MissingKeyOrValueException
    {
       Integer claveColor = null;

       for (Map.Entry<Integer, String> entry : clavesColores.entrySet()) {
           if (entry.getValue().equalsIgnoreCase(color)) {
               claveColor = entry.getKey();
               break;
           }
       }

       if (claveColor == null)
       {
           throw new MissingKeyOrValueException("El color ingresado no se encuentra en la seleccion");
       }

       clavesColores.remove(claveColor);
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
        return "#FFFFFF"; // Cuando no se encuantra color en una cuadrícula, el color por defecto siempre será blanco
    }

    public void cambiarColorCuadricula(int indiceX, int indiceY, String color) throws MissingSearchException
    {
        boolean value = false;

        for (Cuadricula c : cuadriculas) {
            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {
                c.setColor(color);
                value = true;
            }
        }

        if(!value) throw new MissingSearchException("La posicion indicada no existe en el dibujo");
    }

    public void eliminarCuadricula(int indiceX, int indiceY) throws MissingSearchException
    {
        Iterator<Cuadricula> iterator = cuadriculas.iterator();
        boolean value = false;

        while (iterator.hasNext())
        {
            Cuadricula c = iterator.next();

            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {
                iterator.remove();
                value = true;
            }
        }

        if(!value) throw new MissingSearchException("La posicion indicada no existe en el dibujo");
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
