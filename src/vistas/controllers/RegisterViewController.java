package vistas.controllers;

import controllers.GestorSesion;
import controllers.GestorArchivoUsuario;
import models.enumerators.PermisosAdmin;
import models.enumerators.RolUsuarios;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;

public class RegisterViewController {

    @FXML private TextField txtUsuario;
    @FXML private PasswordField txtContrasenia;
    @FXML private PasswordField txtConfirmarContrasenia;
    @FXML private ComboBox<String> cmbTipoUsuario;
    @FXML private Label lblMensaje;
    @FXML private Label lblUsuarioInfo;
    @FXML private Label lblContraseniaInfo;
    @FXML private Label lblTipoInfo;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCancelar;
    @FXML private Hyperlink linkLogin;

    private GestorSesion gestorSesion;
    private GestorArchivoUsuario gestorArchivoUsuario;

    @FXML
    public void initialize() {
        try {
            gestorSesion = new GestorSesion();
            gestorArchivoUsuario = new GestorArchivoUsuario();
            lblMensaje.setVisible(false);

            cmbTipoUsuario.setItems(FXCollections.observableArrayList(
                    "Usuario Normal",
                    "Administrador"
            ));
            cmbTipoUsuario.getSelectionModel().selectFirst();

            cmbTipoUsuario.setOnAction(event -> actualizarInfoTipoUsuario());

            txtContrasenia.textProperty().addListener((obs, oldVal, newVal) -> {
                if (!newVal.isEmpty()) {
                    validarContraseniaEnTiempoReal(newVal);
                }
            });

        } catch (Exception e) {
            System.err.println("Error al inicializar RegisterViewController:");
            e.printStackTrace();
            mostrarError("Error al inicializar el sistema: " + e.getMessage());
        }
    }

    @FXML
    private void handleRegistrar(ActionEvent event) {
        String usuario = txtUsuario.getText().trim();
        String contrasenia = txtContrasenia.getText();
        String confirmarContrasenia = txtConfirmarContrasenia.getText();
        String tipoUsuario = cmbTipoUsuario.getValue();

        if (!validarCampos(usuario, contrasenia, confirmarContrasenia, tipoUsuario)) {
            return;
        }

        try {
            boolean exito = false;

            if (tipoUsuario.equals("Usuario Normal")) {
                exito = gestorSesion.registroSesionUsuarioNormal(usuario, contrasenia, false);
            } else if (tipoUsuario.equals("Administrador")) {
                exito = gestorArchivoUsuario.crearUsuarioAdmin(
                        usuario,
                        contrasenia,
                        true,
                        RolUsuarios.ADMIN,
                        PermisosAdmin.SUPERADMIN
                );
            }

            if (exito) {
                if (tipoUsuario.equals("Administrador")) {
                    mostrarExito("SUPERADMIN creado exitosamente!\n\nPuedes iniciar sesion inmediatamente.");
                } else {
                    mostrarExito("Cuenta creada exitosamente!\n\nTu cuenta esta PENDIENTE DE ACTIVACION.\nUn administrador debe aprobarla para que puedas iniciar sesion.");
                }

                new Thread(() -> {
                    try {
                        Thread.sleep(4000);
                        javafx.application.Platform.runLater(this::volverAlLogin);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }).start();
            } else {
                mostrarError("No se pudo registrar el usuario.\nEl nombre de usuario puede estar en uso\nVerifique los requisitos");
            }

        } catch (Exception e) {
            System.err.println("Error durante el registro:");
            e.printStackTrace();
            mostrarError("Error al registrar usuario: " + e.getMessage());
        }
    }

    private boolean validarCampos(String usuario, String contrasenia, String confirmarContrasenia, String tipoUsuario) {
        if (usuario.isEmpty()) {
            mostrarError("Por favor, ingrese un nombre de usuario");
            txtUsuario.requestFocus();
            return false;
        }

        if (contrasenia.isEmpty()) {
            mostrarError("Por favor, ingrese una contraseña");
            txtContrasenia.requestFocus();
            return false;
        }

        if (confirmarContrasenia.isEmpty()) {
            mostrarError("Por favor, confirme su contraseña");
            txtConfirmarContrasenia.requestFocus();
            return false;
        }

        if (tipoUsuario == null || tipoUsuario.isEmpty()) {
            mostrarError("Por favor, seleccione un tipo de usuario");
            cmbTipoUsuario.requestFocus();
            return false;
        }

        if (!contrasenia.equals(confirmarContrasenia)) {
            mostrarError("Las contraseñas no coinciden");
            txtConfirmarContrasenia.clear();
            txtConfirmarContrasenia.requestFocus();
            return false;
        }

        if (contrasenia.length() <= 8) {
            mostrarError("La contraseña debe tener mas de 8 caracteres");
            return false;
        }

        if (contarDigitos(contrasenia) <= 3) {
            mostrarError("La contraseña debe contener mas de 3 numeros");
            return false;
        }

        if (!tieneMayusculas(contrasenia)) {
            mostrarError("La contraseña debe contener al menos una letra mayuscula");
            return false;
        }

        return true;
    }

    private void validarContraseniaEnTiempoReal(String contrasenia) {
        boolean longitudOk = contrasenia.length() > 8;
        boolean digitosOk = contarDigitos(contrasenia) > 3;
        boolean mayusculaOk = tieneMayusculas(contrasenia);

        if (longitudOk && digitosOk && mayusculaOk) {
            lblContraseniaInfo.setText("Contraseña valida");
            lblContraseniaInfo.setStyle("-fx-text-fill: #27ae60;");
        } else {
            lblContraseniaInfo.setText("Minimo 9 caracteres, 4 numeros y 1 mayuscula");
            lblContraseniaInfo.setStyle("-fx-text-fill: #7f8c8d;");
        }
    }

    private int contarDigitos(String cadena) {
        int cantidad = 0;
        for (char c : cadena.toCharArray()) {
            if (Character.isDigit(c)) {
                cantidad++;
            }
        }
        return cantidad;
    }

    private boolean tieneMayusculas(String cadena) {
        for (char c : cadena.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private void actualizarInfoTipoUsuario() {
        String tipoSeleccionado = cmbTipoUsuario.getValue();
        if (tipoSeleccionado != null) {
            if (tipoSeleccionado.equals("Usuario Normal")) {
                lblTipoInfo.setText("Usuario Normal: Puede crear y pintar dibujos");
            } else {
                lblTipoInfo.setText("Administrador: Puede gestionar usuarios y dibujos del sistema");
            }
        }
    }

    @FXML
    private void handleCancelar(ActionEvent event) {
        limpiarCampos();
    }

    @FXML
    private void handleVolverLogin(ActionEvent event) {
        volverAlLogin();
    }

    private void volverAlLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/LoginView.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) linkLogin.getScene().getWindow();
            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(getClass().getResource("/resources/styles/login.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("CSS no encontrado");
            }

            stage.setScene(scene);
            stage.setTitle("Login - Sistema de Dibujo");

        } catch (IOException e) {
            System.err.println("Error al volver al login:");
            e.printStackTrace();
            mostrarError("Error al volver al login: " + e.getMessage());
        }
    }

    private void mostrarError(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #e74c3c; -fx-background-color: #fadbd8; -fx-padding: 10; " +
                "-fx-background-radius: 5; -fx-border-color: #e74c3c; -fx-border-radius: 5; -fx-border-width: 1;");
        lblMensaje.setVisible(true);
    }

    private void mostrarExito(String mensaje) {
        lblMensaje.setText(mensaje);
        lblMensaje.setStyle("-fx-text-fill: #27ae60; -fx-background-color: #d5f4e6; -fx-padding: 10; " +
                "-fx-background-radius: 5; -fx-border-color: #27ae60; -fx-border-radius: 5; -fx-border-width: 1;");
        lblMensaje.setVisible(true);
    }

    private void limpiarCampos() {
        txtUsuario.clear();
        txtContrasenia.clear();
        txtConfirmarContrasenia.clear();
        cmbTipoUsuario.getSelectionModel().selectFirst();
        lblMensaje.setVisible(false);
        lblContraseniaInfo.setText("Minimo 9 caracteres, 4 numeros y 1 mayuscula");
        lblContraseniaInfo.setStyle("-fx-text-fill: #7f8c8d;");
    }
}