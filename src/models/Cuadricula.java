package models;

import java.util.Objects;

public class Cuadricula implements Comparable<Cuadricula> {

    /// atributoss
    private int indiceX;
    private int indiceY;
    private String color;



    /// constructores
    public Cuadricula() {
        this.indiceX = 0;
        this.indiceY = 0;
        this.color = "#FFFFFF";
    }

    public Cuadricula(int indiceX, int indiceY) {
        this.indiceX = indiceX;
        this.indiceY = indiceY;
        this.color = "#FFFFFF";
    }

    public Cuadricula(int indiceX, int indiceY, String color) {
        this.indiceX = indiceX;
        this.indiceY = indiceY;
        this.color = color;
    }



    /// getter y setters

    public int getIndiceX() {
        return indiceX;
    }

    public void setIndiceX(int indiceX) {
        this.indiceX = indiceX;
    }

    public int getIndiceY() {
        return indiceY;
    }

    public void setIndiceY(int indiceY) {
        this.indiceY = indiceY;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }



    // Metodos overrides

    /**
     * Se elige no conciderar el atributo 'color' para los métodos 'equals', 'hashCode' y 'compareTo'
     * debido a que es un atributo que estará en constante cambio y lo que verdaderamente define a
     * una cuadrícula como única en un lienzo es su posicion (indiceX e indiceY).
     * No sirve diferenciar, comparar o igualar un objeto 'Cuadricula' de otro considerando el color.
     */

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cuadricula that)) return false;
        return indiceX == that.indiceX && indiceY == that.indiceY;
    }

    @Override
    public int hashCode() {
        return Objects.hash(indiceX, indiceY);
    }

    @Override
    public int compareTo(Cuadricula cuadricula)
    {
        if (this.indiceX != cuadricula.indiceX)
        {
            return Integer.compare(this.indiceX, cuadricula.indiceX);
        }
        return Integer.compare(this.indiceY, cuadricula.indiceY);
    }

    @Override
    public String toString() {
        return "Cuadricula{" +
                "indiceX=" + indiceX +
                ", indiceY=" + indiceY +
                ", color='" + color + '\'' +
                '}';
    }
}
