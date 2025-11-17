package controllers;

import models.Cuadricula;
import models.Dibujo;
import models.exceptions.InvalidColorException;
import models.exceptions.MissingSearchException;

public class GestorLienzo {

    /// Atributos estaticos

    private static final int[] TAMANIOS_DISPONIBLES = {8, 16, 32, 48, 64};
    private static final String[] COLORES_PERMITIDOS = {
            "#FFFFFF", "#000000", "#FF0000", "#00FF00", "#0000FF",
            "#FFFF00", "#FF00FF", "#00FFFF", "#FFA500", "#800080"
    };
    private static final GestorArchivoDibujo gestorArchivoDibujo = new GestorArchivoDibujo();



    /// Atributos

    private int tamanioActual;
    private String colorSeleccionado;
    private boolean paraPintar;
    private Dibujo dibujo;



    /// Constructores

    public GestorLienzo() {
        this.tamanioActual = 32;
        this.colorSeleccionado = "#000000";
        this.paraPintar = false;
        this.dibujo = new Dibujo();
        dibujo.setAnchoCuadricula(tamanioActual);
    }

    public GestorLienzo(Dibujo dibujo) {
        this.tamanioActual = 32;
        this.colorSeleccionado = "#000000";
        this.paraPintar = true;
        this.dibujo = dibujo != null ? dibujo : new Dibujo();
        dibujo.setAnchoCuadricula(tamanioActual);
    }

    public GestorLienzo(int tamanioActual, String colorSeleccionado, boolean paraPintar, Dibujo dibujo) {
        this.tamanioActual = validarTamanioIngresado(tamanioActual) ? tamanioActual : 32;
        this.colorSeleccionado = validarColorIngresado(colorSeleccionado) ? colorSeleccionado : "#000000";
        this.paraPintar = paraPintar;
        this.dibujo = dibujo != null ? dibujo : new Dibujo();
        dibujo.setAnchoCuadricula(tamanioActual);
    }



    /// Getters y Setters

    public int getTamanioActual() {
        return tamanioActual;
    }

    public void setTamanioActual(int tamanioSeleccionado) {
        if (validarTamanioIngresado(tamanioSeleccionado)) {
            this.tamanioActual = tamanioSeleccionado;
        }
    }

    public String getColor() {
        return colorSeleccionado;
    }

    public void setColor(String hxdColor) {
        if (validarColorIngresado(hxdColor)) {
            this.colorSeleccionado = hxdColor.toUpperCase();
        }
    }

    public boolean isParaPintar() {
        return paraPintar;
    }

    public void setParaPintar(boolean paraPintar) {
        this.paraPintar = paraPintar;
    }

    public Dibujo getDibujo() {
        return dibujo;
    }

    public void setDibujo(Dibujo dibujo) {
        this.dibujo = dibujo;
    }

    public static int[] getTamaniosDisponibles() {
        return TAMANIOS_DISPONIBLES.clone();
    }

    public static String[] getColoresPermitidos() {
        return COLORES_PERMITIDOS.clone();
    }




    /// Métodos de validación

    public boolean validarColorIngresado(String hxdColor) {
        if (hxdColor == null) return false;
        return hxdColor.matches("^#[0-9A-Fa-f]{6}$");
    }

    public boolean validarTamanioIngresado(int tamanioSeleccionado) {
        for (int tam : TAMANIOS_DISPONIBLES) {
            if (tam == tamanioSeleccionado) {
                return true;
            }
        }
        return false;
    }



    // Metodos de dibujo

    /// Sirve para abrir un dibujo existenete en el archivo para pintarlo
    public void abrirDibujoCreado(int idDibujo){
        dibujo = gestorArchivoDibujo.buscarDibujoEnLista(idDibujo);
    }

    public void dibujarPixel(int ejeX, int ejeY) throws InvalidColorException {
        if (dibujo == null) {
            dibujo = new Dibujo();
        }

        // Asegurar que el color esté en la paleta
        if (!dibujo.estaColorEnMap(colorSeleccionado)) {
            dibujo.insertarColor(colorSeleccionado);
        }

        // Si la ubicacion en el lienzo no está, se crea una cuadrícula nueva
        try {
            dibujo.cambiarColorCuadricula(ejeX, ejeY, colorSeleccionado);
        } catch (MissingSearchException e) {
            dibujo.ingresarCuadricula(new Cuadricula(ejeX, ejeY, colorSeleccionado));
        } finally {
            guardarDatos();
        }
    }

    public void borrarPixel(int ejeX, int ejeY) {
        if (dibujo != null) {
            try {
                dibujo.eliminarCuadricula(ejeX, ejeY);
            } catch (MissingSearchException e) {
                System.err.println("No se pudo borrar el pixel:");
                e.printStackTrace();
            }

            guardarDatos();
        }
    }

    public String obtenerColorPixel(int ejeX, int ejeY) {
        if (dibujo != null) {
            return dibujo.colorCuadricula(ejeX, ejeY);
        }
        return null;
    }

    public void limpiarLienzo() {
        if (dibujo != null) {
            dibujo.limpiarDibujo();
        }
    }

    public void nuevoDibujo(String nombreDibujo, int anchoCuadricula) {
        this.dibujo = new Dibujo(0, 0, nombreDibujo, true, anchoCuadricula);
    }

    /// Llama a guardar los datos de dibujo en el archivo de dibujos
    private void guardarDatos(){
        gestorArchivoDibujo.modificarDibujo(dibujo);
    }
}
