package modelo;

import java.util.Objects;

public class Cuadricula implements Comparable<Cuadricula> {

    /// atributoss
    private int indiceX;
    private int indiceY;
    private String color;



    /// constructores
    public Cuadricula() {}

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



    /// metodos overrides

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cuadricula that)) return false;
        return indiceX == that.indiceX && indiceY == that.indiceY;
        // Eliminé el color dentro de la condición ya que necesitamos gestionar POSICIONES iguales, sin importar el color.
    }

    @Override
    public int hashCode() {
        return Objects.hash(indiceX, indiceY);
    } // Mantener el color impone diferencia entre la igualdad de objetos con la igualdad de sus códigos hash.

    @Override
    public int compareTo(Cuadricula cuadricula)
    {
        if (this.indiceX != cuadricula.indiceX)
        {
            return Integer.compare(this.indiceX, cuadricula.indiceX);
        }
        return Integer.compare(this.indiceY, cuadricula.indiceY);
    }
}
