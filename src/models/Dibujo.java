package models;

import exceptions.MissingKeyOrValueException;
import exceptions.MissingSearchException;

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
        inicializarColoresPorDefecto();
    }

    public Dibujo(int idDibujo, int idPropietario, String nombreDibujo, boolean activo, int anchoCuadricula) {
        this.idDibujo = idDibujo;
        this.idPropietario = idPropietario;
        this.nombreDibujo = nombreDibujo;
        this.activo = activo;
        this.anchoCuadricula = anchoCuadricula;
        this.clavesColores = new TreeMap<>();
        this.cuadriculas = new HashSet<>();
        inicializarColoresPorDefecto();
    }

    public Dibujo(int idDibujo, int idPropietario, String nombreDibujo, boolean activo, int anchoCuadricula, TreeMap<Integer,String> clavesColores, HashSet<Cuadricula> cuadriculas) {
        this.idDibujo = idDibujo;
        this.idPropietario = idPropietario;
        this.nombreDibujo = nombreDibujo;
        this.activo = activo;
        this.anchoCuadricula = anchoCuadricula;
        this.clavesColores = clavesColores;
        this.cuadriculas = cuadriculas;
        inicializarColoresPorDefecto();
    }

    private void inicializarColoresPorDefecto() {       // Fragmento de código del proyecto de David
        clavesColores.put(0, "#FFFFFF");
        clavesColores.put(1, "#000000");
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
        // Mejora de la lógica de insercion basada en el codigo de David
        if(!validarEntradaColor(color)) {
            throw new MissingKeyOrValueException("El color ingresado es invalido");
        } else if (estaColorEnMap(color)) {
            throw new MissingKeyOrValueException("El color debe ser unico en la seleccion");
        } else if(color.equals("#FFFFFF")) {
            throw new MissingKeyOrValueException("El color blanco no puede ser añadidio a la seleccion");
        }

        int claveAux = clavesColores.isEmpty() ? 0 : clavesColores.lastKey() + 1;
        clavesColores.put(claveAux, color.toUpperCase());
    }

    public void eliminarColor(String color) throws MissingKeyOrValueException
    {
        if(!validarEntradaColor(color)) {
            throw new MissingKeyOrValueException("El color no tiene un formato valido");
        }

       Integer claveColor = null;

       for (Map.Entry<Integer, String> entry : clavesColores.entrySet()) {
           if (entry.getValue().equalsIgnoreCase(color)) {
               claveColor = entry.getKey();
               break;
           }
       }

       if (claveColor == null) {
           throw new MissingKeyOrValueException("El color ingresado no se encuentra en la seleccion");
       }

       clavesColores.remove(claveColor);
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
        return "#FFFFFF";
    }

    public void cambiarColorCuadricula(int indiceX, int indiceY, String color) throws MissingSearchException
    {
        boolean value = false;

        if(validarEntradaColor(color)) {
            for (Cuadricula c : cuadriculas) {
                if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY) {
                    c.setColor(color);
                    value = true;
                }
            }
        }

        if(!value) throw new MissingSearchException("La posicion indicada no existe en el dibujo");
    }

    public void eliminarCuadricula(int indiceX, int indiceY) throws MissingSearchException
    {
        // Fragmento de codigo basado en el codigo de David
        boolean value = cuadriculas.removeIf(c ->
                c.getIndiceX() == indiceX && c.getIndiceY() == indiceY
        );

        if(!value) throw new MissingSearchException("La posicion indicada no existe en el dibujo");
    }

    public void limpiarDibujo() {   // Fragmento de código de David
        cuadriculas.clear();
    }


    // Validaciones

    public boolean estaColorEnMap(String color)
    {
        return validarEntradaColor(color) && clavesColores.containsValue(color);
    }

    private boolean validarEntradaColor(String color){
        return !(color == null || color.isEmpty() || !color.matches("^#[0-9A-Fa-f]{6}$"));
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

    @Override
    public String toString() {
        return "Dibujo{" +
                "idDibujo=" + idDibujo +
                ", idPropietario=" + idPropietario +
                ", nombreDibujo='" + nombreDibujo + '\'' +
                ", activo=" + activo +
                ", anchoCuadricula=" + anchoCuadricula +
                ", clavesColores=" + clavesColores +
                ", cuadriculas=" + cuadriculas +
                '}';
    }
}
