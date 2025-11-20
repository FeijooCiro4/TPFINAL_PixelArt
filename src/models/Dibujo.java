package models;

import models.exceptions.InvalidColorException;
import models.exceptions.MissingSearchException;

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

    public Map<Integer, String> getClavesColores() {
        return Collections.unmodifiableMap(clavesColores);
    }

    public void setClavesColores(TreeMap<Integer, String> clavesColores) {
        this.clavesColores = clavesColores;
    }

    public Set<Cuadricula> getCuadriculas() {
        return Collections.unmodifiableSet(cuadriculas);
    }

    public void setCuadriculas(HashSet<Cuadricula> cuadriculas) {
        this.cuadriculas = cuadriculas;
    }



    // Métodos para clavesColores

    /**
     * Se ingresa un color al map de 'clavesColores', validando que
     * tenga un formato válio (hexadecimal de 6 digitos) y que no se
     * encuantre en el map.
     * */
    public void insertarColor(String color) throws InvalidColorException
    {
        if(!validarEntradaColor(color)) {
            throw new InvalidColorException("El color ingresado es invalido");
        }
        // Capa de codigo conflictiva
        /*else if (estaColorEnMap(color)) {
            throw new InvalidColorException("El color ya se encuentra en la seleccion");
        }*/

        int claveAux = clavesColores.isEmpty() ? 0 : clavesColores.lastKey() + 1;
        clavesColores.put(claveAux, color.toUpperCase());
    }

    /**
     * Para eliminar el color que se ingresó por parámetro, este debe tener un formato válido
     * y estar en el map de 'clavesColores'. Se lanzará InvalidColorException de lo contrario.
     * */
    public void eliminarColor(String color) throws InvalidColorException
    {
        if(!validarEntradaColor(color)) {
            throw new InvalidColorException("El color no tiene un formato valido");
        }

        Integer claveColor = null;

        for (Map.Entry<Integer, String> entry : clavesColores.entrySet()) {
            if (entry.getValue().equalsIgnoreCase(color)) {
                claveColor = entry.getKey();
                break;
            }
        }

        if (claveColor == null) {
            throw new InvalidColorException("El color ingresado no se encuentra en la seleccion");
        }

        clavesColores.remove(claveColor);
    }



    /// metodos de la cuadricula

    public boolean buscarCuadricula(int indiceX, int indiceY)
    {
        Cuadricula cuadricula = new Cuadricula(indiceX, indiceY);
        return cuadriculas.contains(cuadricula);    // Al tener implementado hashCode e equals, es más eficiente hacer una copia que un bucle
    }

    /**
     * En base a una posición (cuadrícula), se retorna el color. Si esa posición
     * no existe, se retorna el color blanco (por defecto en un lienzo vacío).
     * */
    public String colorCuadricula(int indiceX, int indiceY)
    {
        Cuadricula cuadricula = new Cuadricula(indiceX, indiceY);

        /* Metodo de filtrado Stream enriquecido por la imlementacion de equals y hashDode En la clase 'Cuadricula'.
           Al tratarse de un HashSet, no podemos obligar al codigo a tratar conuna búsqueda secuencial.
           Como definimos equals y hashCode de Cuadricula basados en las coordenadas indiceX e indiceY,
           podemos aplicar estas técnicas de filtrado.
         */
        Optional<Cuadricula> resultado = cuadriculas.stream()
                .filter(c -> c.equals(cuadricula))
                .findFirst();

        return resultado.map(Cuadricula::getColor).orElse("#FFFFFF");
    }

    /**
     * Si el tamanio del dibujo es igual al del lienzo (anchoCuadricula X 2),
     * no se podrá ingresar otra cuadrícula al set de 'cuadriculas'.
     * Esto debido a que el tamaño de un lienzo es representado por dicho cálcula.
     * Ej: Ancho 16 pixeles -> cálculo: 16X16 = total de cuadriculas en el lienzo (256).
     * */
    public boolean ingresarCuadricula(Cuadricula cuadricula){
        if(cuadriculas.size() < anchoCuadricula*2) {
            return cuadriculas.add(cuadricula);
        }
        return false;
    }

    /**
     * Se valida que el parámetro sea un color válido y, en base a los índices 'indiceX' e 'indiceY',
     * se encuentra la posición (o cuadricula) cuyo color se cambiará.
     * */
    public void cambiarColorCuadricula(int indiceX, int indiceY, String color) throws MissingSearchException, InvalidColorException
    {
        if(!validarEntradaColor(color)) {
            throw new InvalidColorException("El formato del color ingresado es invalido");
        }

        for (Cuadricula c : cuadriculas) {
            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY) {
                c.setColor(color);
                return;
            }
        }

        throw new MissingSearchException("La posicion indicada no existe en el dibujo");
    }

    /**
     * Se elimina la cuadrícula de la coleccion si su pisicion se encuentra en la misma.
     * */
    public void eliminarCuadricula(int indiceX, int indiceY) throws MissingSearchException
    {
        boolean value = cuadriculas.removeIf(c ->
                c.getIndiceX() == indiceX && c.getIndiceY() == indiceY
        );

        if(!value) throw new MissingSearchException("La posicion indicada no existe en el dibujo");
    }

    public void limpiarDibujo() {
        cuadriculas.clear();
    }


    // Validaciones

    /** Formato válido:
     * Empiece con la almohadilla, especificado con '^#'.
     * Números aceptados entre 0 y 9, y letras aceptadas entre a y f  (mayusculas y minusculas).
     * Tenga estritamente 6 dígitos.
     * No se permite ningún otro carácter (especificado con '$').
     */
    private boolean validarEntradaColor(String color){
        return color != null && !color.isEmpty() && color.matches("^#[0-9A-Fa-f]{6}$");
    }

    public boolean estaColorEnMap(String color)
    {
        if(!validarEntradaColor(color)) return false;

        String colorEstand = color.toUpperCase();
        return clavesColores.containsValue(colorEstand);
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