import org.json.JSONObject;

import java.util.Objects;

public abstract class Usuario {

        /// Atributos
        private static int contadorid = 0;
        private int idUsuario;

        private String nombre;

        private String hashContrasena;

        private String salt;

        private boolean activo;

        private RolUsuarios rolUsuarios;


        /// Constructor

        //se me ocurre esto mejor como constructor vacio por default
        public Usuario(){
            this.idUsuario=0;
            this.nombre="Usuario Nulo";
            this.hashContrasena="";
            this.salt="";
            this.activo=false;
            this.rolUsuarios=null;
        }

        //constructor para deserializar JSON:
        public Usuario(JSONObject x){
            this.idUsuario=x.getInt("idUsuario");
            this.nombre=x.getString("nombre");
            this.hashContrasena=x.getString("hashContrasena");
            this.salt=x.getString("salt");
            this.activo=x.getBoolean("activo");
            if (x.has("rolUsuarios")){
                this.rolUsuarios = RolUsuarios.valueOf(x.getString("rolUsuarios").toUpperCase());
            } else {
                this.rolUsuarios=RolUsuarios.NORMAL; ///valor por defecto
            }
        }

        public Usuario(String nombre, String hashContrasena, String salt, boolean activo, RolUsuarios rolUsuarios) {
            this.contadorid++;
            this.idUsuario = contadorid;
            this.nombre = nombre;
            this.hashContrasena = hashContrasena;
            this.salt = salt;
            this.activo = activo;
            this.rolUsuarios = rolUsuarios;
        }



        /// getter y setters


        public int getIdUsuario() {
            return idUsuario;
        }


        public String getNombre() {
            return nombre;
        }

        public void setNombre(String nombre) {
            this.nombre = nombre;
        }

        public String getHashContrasena() {
            return hashContrasena;
        }


        public String getSalt() {
            return salt;
        }


        public boolean isActivo() {
            return activo;
        }

        public void setActivo(boolean activo) {
            this.activo = activo;
        }

        public RolUsuarios getRolUsuarios() {
            return rolUsuarios;
        }

        public void setRolUsuarios(RolUsuarios rolUsuarios) {
            this.rolUsuarios = rolUsuarios;
        }


        /// Hash y equals

        @Override
        public boolean equals(Object o) {
            if (!(o instanceof Usuario usuario)) return false;
            return idUsuario == usuario.idUsuario && activo == usuario.activo && Objects.equals(nombre, usuario.nombre) && Objects.equals(hashContrasena, usuario.hashContrasena) && Objects.equals(salt, usuario.salt) && rolUsuarios == usuario.rolUsuarios;
        }

        @Override
        public int hashCode() {
            return Objects.hash(idUsuario, nombre, hashContrasena, salt, activo, rolUsuarios);
        }

    @Override
    public String toString() {
        return "Usuario{" +
                "idUsuario=" + idUsuario +
                ", nombre='" + nombre + '\'' +
                ", hashContrasena='" + hashContrasena + '\'' +
                ", salt='" + salt + '\'' +
                ", activo=" + activo +
                ", rolUsuarios=" + rolUsuarios +
                '}';
    }
}
