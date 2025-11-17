package controllers;

import models.JSONManagement.DataAccessObjects.DibujoDAO;
import models.JSONManagement.ReadWriteOperations;
import models.Dibujo;

import java.util.ArrayList;

public class GestorArchivoDibujo {
    private static final String NAME_FILE = "Dibujos.json";
    private static GestorArchivoUsuario gestorArchivoUsuario = new GestorArchivoUsuario();

    private final DibujoDAO dibujoDAO = new DibujoDAO();
    private ArrayList<Dibujo> dibujosGuardados = new ArrayList<>();

    public GestorArchivoDibujo(){
        if(ReadWriteOperations.fileExists(NAME_FILE)) actualizarLista();
    }

    ///  Crea un dibujo si su propietario existe
    public void crearDibujo(int idPropietario, String nombreDibujo, boolean activo, int anchoCuadricula){
        if(gestorArchivoUsuario.buscarUsuario(idPropietario) != null) {
            int idGenerado = generarIdUnico();
            dibujosGuardados.add(new Dibujo(idGenerado, idPropietario, nombreDibujo, activo, anchoCuadricula));

            guardarCambios();
            gestorArchivoUsuario.agregarDibujoCreado(idPropietario, idGenerado);
        }
    }

    ///  Modifica un dibujo si su id existe en la lista
    public void modificarDibujo(Dibujo dibujoModificado){
        Dibujo dibujoAModificar = buscarDibujoEnLista(dibujoModificado.getIdDibujo());

        if(dibujoAModificar != null){
            // Se sobreescribe en la lista
            dibujosGuardados.remove(dibujoAModificar);
            dibujosGuardados.add(dibujoModificado);
            guardarCambios();
        }
    }

    ///  Elimina un dibujo si su id existe en la lista
    public boolean eliminarDibujo(int idDibujo){
        Dibujo dibujoAEliminar = buscarDibujoEnLista(idDibujo);

        if(dibujoAEliminar != null){
            dibujosGuardados.remove(dibujoAEliminar);
            guardarCambios();
            return true;
        }

        return false;
    }

    public Dibujo buscarDibujoEnLista(int idDibujo){
        for(Dibujo dibujo : dibujosGuardados){
            if(dibujo.getIdDibujo() == idDibujo) return dibujo;
        }
        return null;
    }



    // Validadores y generadores

    private int generarIdUnico(){
        return dibujosGuardados.size() + 1;
    }

    /// Sobreescribe el archivo de dibujos con los datos de la lista
    private void guardarCambios(){
        dibujoDAO.listToFile(dibujosGuardados, NAME_FILE);
    }

    /// Sobreescribe la lista de dibujos con los datos del archivo
    private void actualizarLista(){
        this.dibujosGuardados = (ArrayList<Dibujo>) dibujoDAO.fileToList(NAME_FILE);
    }
}
