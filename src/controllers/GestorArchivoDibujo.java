package controllers;

import models.JSONManagement.DataAccessObjects.DibujoDAO;
import models.JSONManagement.ReadWriteOperations;
import models.Dibujo;

import java.util.ArrayList;

public class GestorArchivoDibujo {
    private static final String NAME_FILE = "Dibujos.json";

    private final DibujoDAO dibujoDAO = new DibujoDAO();
    private ArrayList<Dibujo> dibusjosGuardados = new ArrayList<>();

    public GestorArchivoDibujo(){
        if(ReadWriteOperations.fileExists(NAME_FILE)) actualizarLista();
    }

    ///  Crea un dibujo si su propietario existe
    public void crearDibujo(int idPropietario, String nombreDibujo, boolean activo, int anchoCuadricula){
        int idGenerado = generarIdUnico();

        dibusjosGuardados.add(new Dibujo(idGenerado, idPropietario, nombreDibujo, activo, anchoCuadricula));

        guardarCambios();
        new GestorArchivoUsuario().agregarDibujoCreado(idPropietario, idPropietario);
    }

    ///  Modifica un dibujo si su id existe en la lista
    public void modificarDibujo(Dibujo dibujoModificado){
        Dibujo dibujoAModificar = buscarDibujoEnLista(dibujoModificado.getIdDibujo());

        if(dibujoAModificar != null){
            // Se sobreescribe en la lista
            dibusjosGuardados.remove(dibujoAModificar);
            dibusjosGuardados.add(dibujoModificado);
            guardarCambios();
        }
    }

    ///  Elimina un dibujo si su id existe en la lista
    public boolean eliminarDibujo(int idDibujo){
        Dibujo dibujoAEliminar = buscarDibujoEnLista(idDibujo);

        if(dibujoAEliminar != null){
            dibusjosGuardados.remove(dibujoAEliminar);
            guardarCambios();
            return true;
        }

        return false;
    }

    public Dibujo buscarDibujoEnLista(int idDibujo){
        for(Dibujo dibujo : dibusjosGuardados){
            if(dibujo.getIdDibujo() == idDibujo) return dibujo;
        }
        return null;
    }



    // Validadores y generadores

    private int generarIdUnico(){
        return dibusjosGuardados.size() + 1;
    }

    /// Sobreescribe el archivo de dibujos con los datos de la lista
    private void guardarCambios(){
        dibujoDAO.listToFile(dibusjosGuardados, NAME_FILE);
    }

    /// Sobreescribe la lista de dibujos con los datos del archivo
    private void actualizarLista(){
        this.dibusjosGuardados = (ArrayList<Dibujo>) dibujoDAO.fileToList(NAME_FILE);
    }
}
