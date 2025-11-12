import org.json.JSONObject;

import java.util.Objects;

public class Cuadricula implements Comparable<Cuadricula> {

    /// atributoss

    private int indiceX;

    private int indiceY;

    private String color;


    /// constructores
    public Cuadricula() {
    }

    public Cuadricula(JSONObject json) {
        this.indiceX = json.getInt("indiceX");
        this.indiceY = json.getInt("indiceY");
        this.color = json.getString("color");
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




    /// metodos overrides

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Cuadricula that)) return false;
        return indiceX == that.indiceX && indiceY == that.indiceY && Objects.equals(color, that.color);
    }

    @Override
    public int hashCode() {
        return Objects.hash(indiceX, indiceY, color);
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



}
