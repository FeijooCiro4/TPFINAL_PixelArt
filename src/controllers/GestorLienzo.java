/*
* Clase basada en el código de David
* */

package controllers;

import models.Dibujo;
import exceptions.MissingKeyOrValueException;
import exceptions.MissingSearchException;

public class GestorLienzo {

    // Atributos estaticos

    private static final int[] TAMANIOS_DISPONIBLES = {8, 16, 32, 48, 64};
    private static final String[] COLORES_PERMITIDOS = {
            "#FFFFFF", "#000000", "#FF0000", "#00FF00", "#0000FF",
            "#FFFF00", "#FF00FF", "#00FFFF", "#FFA500", "#800080"
    };



    // Atributos

    private int tamanioActual;
    private String colorSeleccionado;
    private Dibujo dibujo;



    // Constructores

    public GestorLienzo() {
        this.tamanioActual = 32;
        this.colorSeleccionado = "#000000";
        this.dibujo = new Dibujo();
    }

    public GestorLienzo(int tamanioActual, String colorSeleccionado, Dibujo dibujo) {
        this.tamanioActual = validarTamanioIngresado(tamanioActual) ? tamanioActual : 32;
        this.colorSeleccionado = validarColorIngresado(colorSeleccionado) ? colorSeleccionado : "#000000";
        this.dibujo = dibujo != null ? dibujo : new Dibujo();
    }



    // Getters y Setters

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




    // Métodos de validación

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

    // Método principal de dibujo
    public void dibujarPixel(int ejeX, int ejeY) {
        if (dibujo == null) {
            dibujo = new Dibujo();
        }

        // Asegurar que el color esté en la paleta
        if (!dibujo.estaColorEnMap(colorSeleccionado)) {
            try {
                dibujo.insertarColor(colorSeleccionado);
            } catch (MissingKeyOrValueException e) {
                e.printStackTrace();
            }
        }

        try {
            // Cambiar el color de la cuadrícula
            dibujo.cambiarColorCuadricula(ejeX, ejeY, colorSeleccionado);
        } catch (MissingSearchException e) {
            e.printStackTrace();
        }
    }

    public void borrarPixel(int ejeX, int ejeY) {
        if (dibujo != null) {
            try {
                dibujo.eliminarCuadricula(ejeX, ejeY);
            } catch (MissingSearchException e) {
                e.printStackTrace();
            }
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
}
