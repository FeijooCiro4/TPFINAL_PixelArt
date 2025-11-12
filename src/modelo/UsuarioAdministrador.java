import org.json.JSONArray;
import org.json.JSONObject;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

    public class UsuarioAdministrador extends Usuario {
        /// atributos
        private PermisosAdmin nivelAdmin;
        private TreeMap<LocalDateTime,String> registroAcciones;

        /// Constructores
        public UsuarioAdministrador() {
            this.registroAcciones = new TreeMap<>();
        }

        public UsuarioAdministrador(PermisosAdmin nivelAdmin) { // hago este porque en el uml aparece con el treemap y me resulto raro
            this.nivelAdmin = nivelAdmin;
            this.registroAcciones = new TreeMap<>();
        }

        public UsuarioAdministrador(PermisosAdmin nivelAdmin, TreeMap<LocalDateTime, String> registroAcciones) {
            this.nivelAdmin = nivelAdmin;
            this.registroAcciones = new TreeMap<>();
        }

        public UsuarioAdministrador(String nombre, String hashContrasena, String salt, boolean activo, PermisosAdmin nivelAdmin) // este lo hago por si tambien se necesitaba
        {
            super(nombre, hashContrasena, salt, activo, RolUsuarios.ADMIN);
            this.nivelAdmin = nivelAdmin;
            this.registroAcciones = new TreeMap<>();
        }

        public UsuarioAdministrador(JSONObject x){
            super(x);
            if (x.has("rolUsuarios")){
                this.nivelAdmin = PermisosAdmin.valueOf(x.getString("nivelAdmin").toUpperCase());
            } else {
                this.nivelAdmin=PermisosAdmin.VISUALIZANTE; ///valor por defecto, podemos cambiarlo
            }
            JSONArray arr= x.getJSONArray("registroAcciones");
            for (int i=0; i<arr.length();i++) {
                JSONObject obj= arr.getJSONObject(i);
                LocalDateTime fecha= LocalDateTime.parse(obj.getString("fecha"));
                String nombre= obj.getString("accion");
                this.registroAcciones.put(fecha, nombre);
            }
        }

        /// getter y setters


        public PermisosAdmin getNivelAdmin() {
            return nivelAdmin;
        }

        public void setNivelAdmin(PermisosAdmin nivelAdmin) {
            this.nivelAdmin = nivelAdmin;
        }

        public TreeMap<LocalDateTime, String> getRegistroAcciones() {
            return registroAcciones;
        }

        public void setRegistroAcciones(TreeMap<LocalDateTime, String> registroAcciones) {
            this.registroAcciones = registroAcciones;
        }


        /// metodos para el registro de las acciones


        public boolean ingresarAccionAlRegistro(String accion) {

            LocalDateTime fechahora = LocalDateTime.now();

            if (!registroAcciones.containsKey(fechahora))
            {
                registroAcciones.put(fechahora, accion);

                return true;
            }

            return false; // si la clave de la accion es igual no la ingresa y devuelve false (no se si necesitaran comprobar eso pero por las dudas lo hago asi)
        }


        public boolean eliminarAccionDelRegistro(LocalDateTime fechaHora, String accion)
        {
            return registroAcciones.remove(fechaHora,accion);
        }


        public List<String> buscarAcciones(int anio, int mes, int dia)
        {
            List<String> accionEncontrada = new ArrayList<>();

            for (Map.Entry<LocalDateTime, String> entrada : registroAcciones.entrySet())
            {
                LocalDateTime fechaHora = entrada.getKey();

                if (fechaHora.getYear() == anio &&
                        fechaHora.getMonthValue() == mes &&
                        fechaHora.getDayOfMonth() == dia) {

                    accionEncontrada.add(entrada.getValue());
                }
            }

            return accionEncontrada;

        }

        @Override
        public String toString() {
            return "UsuarioAdministrador{" +
                    "nivelAdmin=" + nivelAdmin +
                    ", registroAcciones=" + registroAcciones +
                    "} " + super.toString();
        }
    }
