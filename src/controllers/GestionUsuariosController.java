package controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import models.Usuario;
import models.UsuarioAdministrador;
import models.UsuarioNormal;
import models.enumerators.PermisosAdmin;
import models.enumerators.RolUsuarios;

import java.util.ArrayList;
import java.util.Optional;

public class GestionUsuariosController {

    @FXML private Label lblPermisos;
    @FXML private Button btnCrearUsuario;
    @FXML private TextField txtBuscar;
    @FXML private TableView<Usuario> tableUsuarios;
    @FXML private TableColumn<Usuario, String> colId;
    @FXML private TableColumn<Usuario, String> colNombre;
    @FXML private TableColumn<Usuario, String> colRol;
    @FXML private TableColumn<Usuario, String> colPermisos;
    @FXML private TableColumn<Usuario, String> colActivo;
    @FXML private TableColumn<Usuario, String> colDibujos;
    @FXML private TableColumn<Usuario, Void> colAcciones;
    @FXML private Label lblTotalUsuarios;
    @FXML private Label lblUsuariosActivos;
    @FXML private Label lblUsuariosInactivos;
    @FXML private Button btnVolver;

    private GestorArchivoUsuario gestor;
    private PermisosAdmin permisosActuales;
    private int idAdminActual;

    @FXML
    public void initialize() {
        gestor = new GestorArchivoUsuario();
        configurarTabla();
    }

    public void setAdminActual(int idAdmin, PermisosAdmin permisos) {
        this.idAdminActual = idAdmin;
        this.permisosActuales = permisos;

        lblPermisos.setText("Permisos: " + permisos);

        switch (permisos) {
            case VISUALIZANTE:
                btnCrearUsuario.setVisible(false);
                break;
            case SOLICITARIO:
            case SUPERADMIN:
                btnCrearUsuario.setVisible(true);
                break;
        }

        cargarUsuarios();
    }

    private void configurarTabla() {
        colId.setCellValueFactory(data -> new SimpleStringProperty(String.valueOf(data.getValue().getIdUsuario())));
        colNombre.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getNombre()));
        colRol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getRolUsuarios().toString()));

        colPermisos.setCellValueFactory(data -> {
            if (data.getValue() instanceof UsuarioAdministrador) {
                return new SimpleStringProperty(((UsuarioAdministrador) data.getValue()).getNivelAdmin().toString());
            }
            return new SimpleStringProperty("-");
        });

        colActivo.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().isActivo() ? "Activo" : "Inactivo"));

        colDibujos.setCellValueFactory(data -> {
            if (data.getValue() instanceof UsuarioNormal) {
                UsuarioNormal normal = (UsuarioNormal) data.getValue();
                int total = normal.getDibujosPintados().size();
                return new SimpleStringProperty(total + " completados");
            }
            return new SimpleStringProperty("-");
        });

        colAcciones.setCellFactory(param -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    Usuario usuario = getTableView().getItems().get(getIndex());
                    HBox botones = crearBotonesAccion(usuario);
                    setGraphic(botones);
                }
            }
        });
    }

    private HBox crearBotonesAccion(Usuario usuario) {
        HBox hbox = new HBox(5);
        hbox.setAlignment(Pos.CENTER);

        Button btnVer = new Button("Ver");
        btnVer.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; -fx-padding: 5 10;");
        btnVer.setOnAction(e -> verDetalles(usuario));
        hbox.getChildren().add(btnVer);

        if (permisosActuales == PermisosAdmin.SOLICITARIO || permisosActuales == PermisosAdmin.SUPERADMIN) {
            if (usuario.getRolUsuarios() == RolUsuarios.NORMAL) {
                Button btnToggle = new Button(usuario.isActivo() ? "Desactivar" : "Activar");
                btnToggle.setStyle("-fx-background-color: " +
                        (usuario.isActivo() ? "#e67e22" : "#27ae60") +
                        "; -fx-text-fill: white; -fx-padding: 5 10;");
                btnToggle.setOnAction(e -> toggleActivo(usuario));
                hbox.getChildren().add(btnToggle);
            }
        }

        if (permisosActuales == PermisosAdmin.SUPERADMIN) {
            Button btnEditar = new Button("Editar");
            btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; -fx-padding: 5 10;");
            btnEditar.setOnAction(e -> editarUsuario(usuario));

            Button btnEliminar = new Button("Eliminar");
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; -fx-padding: 5 10;");
            btnEliminar.setOnAction(e -> eliminarUsuario(usuario));

            hbox.getChildren().addAll(btnEditar, btnEliminar);
        }

        return hbox;
    }

    private void cargarUsuarios() {
        tableUsuarios.getItems().clear();

        // RECARGAR gestor desde archivo para obtener datos frescos
        gestor = new GestorArchivoUsuario();

        ArrayList<Usuario> usuarios = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            UsuarioNormal normal = gestor.buscarUsuarioNormal(i);
            if (normal != null) {
                usuarios.add(normal);
            }
        }

        for (int i = 1; i <= 100; i++) {
            UsuarioAdministrador admin = gestor.buscarUsuarioAdmin(i);
            if (admin != null) {
                usuarios.add(admin);
            }
        }

        tableUsuarios.getItems().addAll(usuarios);
        actualizarEstadisticas(usuarios);
    }

    private void actualizarEstadisticas(ArrayList<Usuario> usuarios) {
        int total = usuarios.size();
        int activos = (int) usuarios.stream().filter(Usuario::isActivo).count();
        int inactivos = total - activos;

        lblTotalUsuarios.setText("Total: " + total);
        lblUsuariosActivos.setText("Activos: " + activos);
        lblUsuariosInactivos.setText("Inactivos: " + inactivos);
    }

    private void verDetalles(Usuario usuario) {
        StringBuilder detalles = new StringBuilder();
        detalles.append("ID: ").append(usuario.getIdUsuario()).append("\n");
        detalles.append("Nombre: ").append(usuario.getNombre()).append("\n");
        detalles.append("Rol: ").append(usuario.getRolUsuarios()).append("\n");
        detalles.append("Estado: ").append(usuario.isActivo() ? "Activo" : "Inactivo").append("\n\n");

        if (usuario instanceof UsuarioNormal) {
            UsuarioNormal normal = (UsuarioNormal) usuario;
            detalles.append("Dibujos completados: ").append(normal.getDibujosPintados().size()).append("\n");
            detalles.append("Puede crear: ").append(normal.isPuedeCrear() ? "Si" : "No");
        } else if (usuario instanceof UsuarioAdministrador) {
            UsuarioAdministrador admin = (UsuarioAdministrador) usuario;
            detalles.append("Permisos: ").append(admin.getNivelAdmin()).append("\n");
            detalles.append("Acciones registradas: ").append(admin.getRegistroAcciones().size());
        }

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Detalles de Usuario");
        alert.setHeaderText("Informacion de " + usuario.getNombre());
        alert.setContentText(detalles.toString());
        alert.showAndWait();
    }

    private void toggleActivo(Usuario usuario) {
        if (usuario instanceof UsuarioNormal) {
            UsuarioNormal normal = (UsuarioNormal) usuario;
            normal.setActivo(!normal.isActivo());
            gestor.modificarUsuarioNormal(normal);
            cargarUsuarios();

            String accion = normal.isActivo() ? "activado" : "desactivado";
            mostrarInfo("Usuario " + accion, "Usuario " + normal.getNombre() + " " + accion);
        }
    }

    private void editarUsuario(Usuario usuario) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Editar Usuario");
        dialog.setHeaderText("Editando: " + usuario.getNombre());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField(usuario.getNombre());
        CheckBox chkActivo = new CheckBox();
        chkActivo.setSelected(usuario.isActivo());

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Activo:"), 0, 1);
        grid.add(chkActivo, 1, 1);

        if (usuario instanceof UsuarioNormal) {
            UsuarioNormal normal = (UsuarioNormal) usuario;
            CheckBox chkPuedeCrear = new CheckBox();
            chkPuedeCrear.setSelected(normal.isPuedeCrear());
            grid.add(new Label("Puede crear:"), 0, 2);
            grid.add(chkPuedeCrear, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                normal.setNombre(txtNombre.getText());
                normal.setActivo(chkActivo.isSelected());
                normal.setPuedeCrear(chkPuedeCrear.isSelected());
                gestor.modificarUsuarioNormal(normal);
                cargarUsuarios();
            }
        } else {
            UsuarioAdministrador admin = (UsuarioAdministrador) usuario;
            ComboBox<PermisosAdmin> cmbPermisos = new ComboBox<>();
            cmbPermisos.getItems().addAll(PermisosAdmin.values());
            cmbPermisos.setValue(admin.getNivelAdmin());
            grid.add(new Label("Permisos:"), 0, 2);
            grid.add(cmbPermisos, 1, 2);

            dialog.getDialogPane().setContent(grid);
            dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                admin.setNombre(txtNombre.getText());
                admin.setActivo(chkActivo.isSelected());
                admin.setNivelAdmin(cmbPermisos.getValue());
                gestor.modificarUsuarioAdmin(admin);
                cargarUsuarios();
            }
        }
    }

    private void eliminarUsuario(Usuario usuario) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Usuario");
        confirmacion.setHeaderText("Eliminar usuario?");
        confirmacion.setContentText("Usuario: " + usuario.getNombre() + "\n\nEsta accion no se puede deshacer.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            if (usuario instanceof UsuarioNormal) {
                gestor.eliminarUsuarioNormal(usuario.getIdUsuario());
            } else {
                gestor.eliminarUsuarioAdmin(usuario.getIdUsuario());
            }
            cargarUsuarios();
            mostrarInfo("Eliminado", "Usuario eliminado correctamente");
        }
    }

    @FXML
    private void handleCrearUsuario(ActionEvent event) {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Crear Usuario");
        dialog.setHeaderText("Nuevo Usuario");

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);

        TextField txtNombre = new TextField();
        PasswordField txtPassword = new PasswordField();
        ComboBox<String> cmbTipo = new ComboBox<>();
        cmbTipo.getItems().addAll("Usuario Normal", "Administrador");
        cmbTipo.setValue("Usuario Normal");

        grid.add(new Label("Nombre:"), 0, 0);
        grid.add(txtNombre, 1, 0);
        grid.add(new Label("Contraseña:"), 0, 1);
        grid.add(txtPassword, 1, 1);
        grid.add(new Label("Tipo:"), 0, 2);
        grid.add(cmbTipo, 1, 2);

        Label lblRequisitos = new Label("Minimo 9 caracteres, 4 numeros y 1 mayuscula");
        lblRequisitos.setStyle("-fx-font-size: 10px; -fx-text-fill: #666;");
        grid.add(lblRequisitos, 1, 3);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            String nombre = txtNombre.getText().trim();
            String password = txtPassword.getText();

            if (nombre.isEmpty() || password.isEmpty()) {
                mostrarError("Campos vacios");
                return;
            }

            if (password.length() <= 8) {
                mostrarError("La contraseña debe tener mas de 8 caracteres");
                return;
            }

            int digitos = 0;
            boolean tieneMayuscula = false;
            for (char c : password.toCharArray()) {
                if (Character.isDigit(c)) digitos++;
                if (Character.isUpperCase(c)) tieneMayuscula = true;
            }

            if (digitos <= 3) {
                mostrarError("La contraseña debe tener mas de 3 numeros");
                return;
            }

            if (!tieneMayuscula) {
                mostrarError("La contraseña debe tener al menos 1 mayuscula");
                return;
            }

            boolean exito;
            if (cmbTipo.getValue().equals("Usuario Normal")) {
                exito = gestor.crearUsuarioNormal(nombre, password, false, RolUsuarios.NORMAL, false);
            } else {
                exito = gestor.crearUsuarioAdmin(nombre, password, true, RolUsuarios.ADMIN, PermisosAdmin.VISUALIZANTE);
            }

            if (exito) {
                cargarUsuarios();
                mostrarInfo("Exito", "Usuario creado. Inactivo por defecto.");
            } else {
                mostrarError("Error al crear usuario");
            }
        }
    }

    @FXML
    private void handleBuscar(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();

        if (busqueda.isEmpty()) {
            cargarUsuarios();
            return;
        }

        tableUsuarios.getItems().clear();
        ArrayList<Usuario> filtrados = new ArrayList<>();

        for (int i = 1; i <= 100; i++) {
            UsuarioNormal normal = gestor.buscarUsuarioNormal(i);
            if (normal != null && normal.getNombre().toLowerCase().contains(busqueda)) {
                filtrados.add(normal);
            }

            UsuarioAdministrador admin = gestor.buscarUsuarioAdmin(i);
            if (admin != null && admin.getNombre().toLowerCase().contains(busqueda)) {
                filtrados.add(admin);
            }
        }

        tableUsuarios.getItems().addAll(filtrados);
        actualizarEstadisticas(filtrados);
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}