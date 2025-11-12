import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashSet;

public class UsuarioNormal extends Usuario {


    /// atributos
    private boolean puedeCrear;
    private HashSet<Integer> dibujosCreados;
    private HashSet<Integer> dibujosPintados;

    /// Constructor


    public UsuarioNormal() {                 // aca inicializo los set por las dudas
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }

    public UsuarioNormal(boolean puedeCrear) {
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }


    public UsuarioNormal(String nombre, String hashContrasena, String salt, boolean activo, boolean puedeCrear) {
        super(nombre, hashContrasena, salt, activo, RolUsuarios.NORMAL);
        this.puedeCrear = puedeCrear;
        this.dibujosCreados = new HashSet<>();
        this.dibujosPintados = new HashSet<>();
    }


    public UsuarioNormal (JSONObject x){
        super(x);
        this.puedeCrear = x.getBoolean("puedeCrear");
        JSONArray arr=x.getJSONArray("dibujosCreados");
        for (int i=0;i<arr.length();i++) {
            this.dibujosCreados.add(arr.getInt(i));
        }
        JSONArray arr2=x.getJSONArray("dibujosPintados");
        for (int i=0;i<arr2.length();i++) {
            this.dibujosPintados.add(arr2.getInt(i));
        }
    }



    ///  getter y setters


    public boolean isPuedeCrear() {
        return puedeCrear;
    }

    public void setPuedeCrear(boolean puedeCrear) {
        this.puedeCrear = puedeCrear;
    }

    public HashSet<Integer> getDibujosCreados() {
        return dibujosCreados;
    }

    public void setDibujosCreados(HashSet<Integer> dibujosCreados) {
        this.dibujosCreados = dibujosCreados;
    }

    public HashSet<Integer> getDibujosPintados() {
        return dibujosPintados;
    }

    public void setDibujosPintados(HashSet<Integer> dibujosPintados) {
        this.dibujosPintados = dibujosPintados;
    }




    /// metodos de DIBUJO CREADO


    public boolean ingresarIdDibujoCreado(int idDibujoCreado)
    {
        return dibujosCreados.add(idDibujoCreado);
    }

    public boolean eliminarDibujoCreado(int idDibujoCreado)
    {
        return dibujosCreados.remove(idDibujoCreado);
    }

    public boolean buscarDibujoCreado(int idDibujo)
    {
        return dibujosCreados.contains(idDibujo);
    }



    /// metodos de DIBUJO PINTADO


    public boolean ingresarIdDibujoPintado(int idDibujoPintado)
    {
        return dibujosPintados.add(idDibujoPintado);
    }

    public boolean eliminarDibujoPintado(int idDibujoPintado)
    {
        return dibujosPintados.remove(idDibujoPintado);
    }

    public boolean buscarDibujoPintado(int idDibujo)
    {
        return dibujosPintados.contains(idDibujo);
    }




    /// recorrer el hashset // solo para prueba

    public void mostrarHashsetDibuCreados()
    {
        for (Integer id : dibujosCreados)
        {
            System.out.println("\nID dibujo creado: " + id);
        }
    }

    public void mostrarHashsetDibuPintados()
    {
        for (Integer id : dibujosPintados)
        {
            System.out.println("\nID dibujo pintado: " + id);
        }
    }

    @Override
    public String toString() {
        return "UsuarioNormal{" +
                "puedeCrear=" + puedeCrear +
                ", dibujosCreados=" + dibujosCreados +
                ", dibujosPintados=" + dibujosPintados +
                "} " + super.toString();
    }
}
