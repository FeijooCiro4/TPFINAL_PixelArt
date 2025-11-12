import org.json.JSONArray;
import org.json.JSONObject;

import java.util.*;

public class Dibujo {


    ///  atributos
    private static int contadorID=0;
    private int idDibujo;
    private int idPropietario;
    private String nombreDibujo;
    private boolean activo;
    private int anchoCuadricula;
    private TreeMap<Integer,String> clavesColores;
    private HashSet<Cuadricula> cuadriculas;


    /// constructores

    public Dibujo() {
    }

    public Dibujo(int idPropietario, String nombreDibujo, boolean activo, int anchoCuadricula) {
        this.contadorID++;
        this.idDibujo = contadorID;
        this.idPropietario = idPropietario;
        this.nombreDibujo = nombreDibujo;
        this.activo = activo;
        this.anchoCuadricula = anchoCuadricula;
        this.clavesColores = new TreeMap<>();
        this.cuadriculas = new HashSet<>();
    }

    public Dibujo(JSONObject x){
        this.idDibujo=x.getInt("idDibujo");
        this.idPropietario=x.getInt("idPropietario");
        this.nombreDibujo=x.getString("nombreDibujo");
        this.activo=x.getBoolean("activo");
        this.anchoCuadricula=x.getInt("anchoCuadricula");
        JSONArray arr=x.getJSONArray("clavesColores");
        for(int i=0;i<arr.length();i++){
            JSONObject json=arr.getJSONObject(i);
            int clave= json.getInt("idColor");
            String color=json.getString("color");
            this.clavesColores.put(clave,color);
        }
        JSONArray arr2=x.getJSONArray("cuadriculas");
        for(int i=0;i<arr2.length();i++){
            JSONObject json=arr2.getJSONObject(i);
            Cuadricula c= new Cuadricula(json);
            this.cuadriculas.add(c);
        }

    }


    /// getter y setters

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


    public boolean insertarColor(String color)
    {
        if (clavesColores.containsValue(color))
        {
            return false;
        } // si retorna false es porque ya existe el color en el map

        int claveaux = clavesColores.size() + 1;
        // como no le llega un parametro de la clave se lo pongo asi

        clavesColores.put(claveaux, color);

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

        if (claveColor !=null)
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



    public String colorCuadricula(int indiceX, int indiceY) // aca me retorna el color de la cuadricula
    {

        for (Cuadricula c : cuadriculas)
        {
            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {

                return c.getColor();

            }
        }
        return "Sin color en la cuadricula";
    }


    public boolean cambiarColorCuadricula(int indiceX, int indiceY, String color) // aca le meti el color como parametro porque no estaba en el diagrama pero no se si lo quieren asi.
    {
        for (Cuadricula c : cuadriculas)
        {
            if (c.getIndiceX() == indiceX && c.getIndiceY() == indiceY)
            {
                c.setColor(color);

                return true;
            }
        }

        return false;
    }


    public boolean eliminarCuadricula(int indiceX, int indiceY) {

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
