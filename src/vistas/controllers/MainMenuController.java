package vistas.controllers;

import controllers.GestorArchivoDibujo;
import controllers.GestorArchivoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Cuadricula;
import models.Dibujo;
import models.UsuarioNormal;
import models.enumerators.RolUsuarios;

import vistas.controllers.CreateDibujoController;
import vistas.controllers.ColorearDibujoController;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class MainMenuController {

    @FXML private Label lblUsuario;
    @FXML private Label lblRol;
    @FXML private Button btnCerrarSesion;
    @FXML private TextField txtBuscar;
    @FXML private Button btnBuscar;
    @FXML private Button btnMostrarTodos;
    @FXML private Button btnCrearDibujo;
    @FXML private Button btnGestionarUsuarios;
    @FXML private ScrollPane scrollPane;
    @FXML private VBox vboxDibujos;
    @FXML private VBox vboxSinDibujos;
    @FXML private Label lblEstado;
    @FXML private Label lblCantidadDibujos;

    private String nombreUsuario;
    private RolUsuarios rolUsuario;
    private int idUsuario;
    private GestorArchivoDibujo gestorArchivoDibujo;
    private GestorArchivoUsuario gestorArchivoUsuario;
    private ArrayList<Dibujo> dibujosActuales;

    @FXML
    public void initialize() {
        try {
            gestorArchivoDibujo = new GestorArchivoDibujo();
            gestorArchivoUsuario = new GestorArchivoUsuario();
            dibujosActuales = new ArrayList<>();

            System.out.println("✅ MainMenuController inicializado");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar MainMenuController:");
            e.printStackTrace();
            mostrarError("Error al inicializar: " + e.getMessage());
        }
    }

    public void setUsuarioLogueado(String nombre, RolUsuarios rol) {
        this.nombreUsuario = nombre;
        this.rolUsuario = rol;

        lblUsuario.setText("Usuario: " + nombre);
        lblRol.setText("Rol: " + rol.toString());

        // Obtener ID del usuario
        if (rol == RolUsuarios.NORMAL) {
            UsuarioNormal usuario = gestorArchivoUsuario.buscarUsuarioNormal(nombre);
            if (usuario != null) {
                this.idUsuario = usuario.getIdUsuario();
            }
        }

        System.out.println("👤 Usuario configurado: " + nombre + " (ID: " + idUsuario + ", Rol: " + rol + ")");

        if (rol == RolUsuarios.ADMIN) {
            btnCrearDibujo.setVisible(true);
            btnCrearDibujo.setManaged(true);
            btnGestionarUsuarios.setVisible(true);
            btnGestionarUsuarios.setManaged(true);
        }

        cargarDibujos();
    }

    private void cargarDibujos() {
        try {
            vboxDibujos.getChildren().clear();
            dibujosActuales.clear();

            lblEstado.setText("Cargando dibujos...");

            System.out.println("\n🔄 Cargando dibujos desde archivo...");

            ArrayList<Dibujo> dibujos = obtenerDibujosDisponibles();

            System.out.println("✓ Dibujos encontrados: " + dibujos.size());

            if (dibujos.isEmpty()) {
                mostrarEstadoVacio();
            } else {
                mostrarDibujos(dibujos);
            }

            actualizarContador(dibujos.size());
            lblEstado.setText("Listo");

        } catch (Exception e) {
            System.err.println("❌ Error al cargar dibujos:");
            e.printStackTrace();
            mostrarError("Error al cargar dibujos: " + e.getMessage());
        }
    }

    private ArrayList<Dibujo> obtenerDibujosDisponibles() {
        ArrayList<Dibujo> dibujos = new ArrayList<>();

        System.out.println("🔍 Buscando dibujos (IDs 1-100)...");

        for (int i = 1; i <= 100; i++) {
            Dibujo d = gestorArchivoDibujo.buscarDibujoEnLista(i);
            if (d != null) {
                System.out.println("   ✓ Encontrado: ID=" + d.getIdDibujo() + ", Nombre=" + d.getNombreDibujo() + ", Activo=" + d.isActivo() + ", Cuadrículas=" + d.getCuadriculas().size());
                if (d.isActivo()) {
                    dibujos.add(d);
                } else {
                    System.out.println("     (Dibujo inactivo, no se muestra)");
                }
            }
        }

        System.out.println("📊 Total dibujos activos: " + dibujos.size());

        return dibujos;
    }

    private void mostrarDibujos(ArrayList<Dibujo> dibujos) {
        vboxSinDibujos.setVisible(false);
        scrollPane.setVisible(true);

        dibujosActuales = dibujos;

        for (Dibujo dibujo : dibujos) {
            VBox card = crearCardDibujo(dibujo);
            vboxDibujos.getChildren().add(card);
        }
    }

    private VBox crearCardDibujo(Dibujo dibujo) {
        VBox card = new VBox(15);
        card.getStyleClass().add("dibujo-card");
        card.setPadding(new Insets(20));
        card.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #e0e0e0; " +
                "-fx-border-radius: 10; " +
                "-fx-border-width: 1; " +
                "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.1), 10, 0, 0, 2);");

        // Header del card
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);

        Label lblNombre = new Label(dibujo.getNombreDibujo());
        lblNombre.setStyle("-fx-font-size: 18px; -fx-font-weight: bold; -fx-text-fill: #2c3e50;");

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // ← NUEVO: Badge de completado
        boolean completado = dibujoCompletadoPorUsuario(dibujo);
        if (completado) {
            Label lblCompletado = new Label("✓ COMPLETADO");
            lblCompletado.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: white; " +
                    "-fx-background-color: #2e7d32; -fx-padding: 5 10; -fx-background-radius: 5;");
            header.getChildren().add(lblCompletado);
        }

        Label lblTamanio = new Label(dibujo.getAnchoCuadricula() + "x" + dibujo.getAnchoCuadricula());
        lblTamanio.setStyle("-fx-font-size: 12px; -fx-text-fill: #7f8c8d; " +
                "-fx-background-color: #ecf0f1; -fx-padding: 5 10; -fx-background-radius: 5;");

        header.getChildren().addAll(lblNombre, spacer, lblTamanio);

        GridPane preview = crearPreviewDibujo(dibujo);

        HBox info = new HBox(20);
        info.setAlignment(Pos.CENTER_LEFT);

        Label lblColores = new Label("🎨 " + dibujo.getClavesColores().size() + " colores");
        lblColores.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        Label lblPixeles = new Label("📐 " + dibujo.getCuadriculas().size() + " píxeles pintados");
        lblPixeles.setStyle("-fx-font-size: 12px; -fx-text-fill: #34495e;");

        info.getChildren().addAll(lblColores, lblPixeles);

        HBox botones = new HBox(10);
        botones.setAlignment(Pos.CENTER_RIGHT);

        Button btnColorear = new Button(completado ? "🖌️ Volver a Colorear" : "🖌️ Colorear");
        btnColorear.setStyle("-fx-background-color: #3498db; -fx-text-fill: white; " +
                "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; " +
                "-fx-cursor: hand;");
        btnColorear.setOnAction(e -> abrirDibujo(dibujo));

        botones.getChildren().add(btnColorear);

        if (rolUsuario == RolUsuarios.ADMIN) {
            Button btnEditar = new Button("✏️ Editar");
            btnEditar.setStyle("-fx-background-color: #f39c12; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; " +
                    "-fx-cursor: hand;");
            btnEditar.setOnAction(e -> editarDibujo(dibujo));

            Button btnEliminar = new Button("🗑️ Eliminar");
            btnEliminar.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white; " +
                    "-fx-font-weight: bold; -fx-padding: 10 20; -fx-background-radius: 5; " +
                    "-fx-cursor: hand;");
            btnEliminar.setOnAction(e -> eliminarDibujo(dibujo));

            botones.getChildren().addAll(btnEditar, btnEliminar);
        }

        Separator separator = new Separator();

        card.getChildren().addAll(header, preview, separator, info, botones);

        return card;
    }

    // ← NUEVO MÉTODO
    private boolean dibujoCompletadoPorUsuario(Dibujo dibujo) {
        if (rolUsuario != RolUsuarios.NORMAL) {
            return false;
        }

        try {
            UsuarioNormal usuario = gestorArchivoUsuario.buscarUsuarioNormal(idUsuario);
            if (usuario != null) {
                return usuario.buscarDibujoPintado(dibujo.getIdDibujo());
            }
        } catch (Exception e) {
            System.err.println("Error al verificar si dibujo completado: " + e.getMessage());
        }

        return false;
    }
    /**
     * Crea un preview visual del dibujo
     */
    private GridPane crearPreviewDibujo(Dibujo dibujo) {
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(1);
        grid.setVgap(1);
        grid.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10;");

        int tamanio = dibujo.getAnchoCuadricula();
        double pixelSize = Math.min(400.0 / tamanio, 15); // Max 400px de ancho, o 15px por píxel

        Set<Cuadricula> cuadriculas = dibujo.getCuadriculas();

        System.out.println("🎨 Generando preview para: " + dibujo.getNombreDibujo());
        System.out.println("   - Tamaño: " + tamanio + "x" + tamanio);
        System.out.println("   - Cuadrículas: " + cuadriculas.size());

        for (int y = 0; y < tamanio; y++) {
            for (int x = 0; x < tamanio; x++) {
                Rectangle rect = new Rectangle(pixelSize, pixelSize);

                // Buscar si hay una cuadrícula pintada en esta posición
                String color = dibujo.colorCuadricula(x, y);

                if (color != null && !color.equals("#FFFFFF")) {
                    rect.setFill(Color.web(color));
                    rect.setStroke(Color.web("#34495e"));
                    rect.setStrokeWidth(0.3);
                } else {
                    rect.setFill(Color.WHITE);
                    rect.setStroke(Color.web("#dcdde1"));
                    rect.setStrokeWidth(0.5);
                }

                grid.add(rect, x, y);
            }
        }

        return grid;
    }

    /**
     * Muestra el estado vacío
     */
    private void mostrarEstadoVacio() {
        scrollPane.setVisible(false);
        vboxSinDibujos.setVisible(true);
    }

    /**
     * Actualiza el contador de dibujos
     */
    private void actualizarContador(int cantidad) {
        lblCantidadDibujos.setText(cantidad + " dibujo" + (cantidad != 1 ? "s" : "") + " disponible" + (cantidad != 1 ? "s" : ""));
    }

    // EVENTOS DE BOTONES

    @FXML
    private void handleBuscar(ActionEvent event) {
        String busqueda = txtBuscar.getText().trim().toLowerCase();

        if (busqueda.isEmpty()) {
            cargarDibujos();
            return;
        }

        ArrayList<Dibujo> filtrados = new ArrayList<>();
        for (Dibujo d : dibujosActuales) {
            if (d.getNombreDibujo().toLowerCase().contains(busqueda)) {
                filtrados.add(d);
            }
        }

        vboxDibujos.getChildren().clear();
        if (filtrados.isEmpty()) {
            mostrarEstadoVacio();
        } else {
            mostrarDibujos(filtrados);
        }

        actualizarContador(filtrados.size());
    }

    @FXML
    private void handleMostrarTodos(ActionEvent event) {
        txtBuscar.clear();
        cargarDibujos();
    }

    @FXML
    private void handleCrearDibujo(ActionEvent event) {
        try {
            System.out.println("🎨 Abriendo ventana de creación...");

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/CreateDibujoView.fxml"));
            Parent root = loader.load();

            // Pasar el ID del usuario al controlador
            CreateDibujoController controller = loader.getController();
            // TODO: Obtener el ID real del usuario desde GestorArchivoUsuario
            controller.setUsuarioCreador(1); // Por ahora ID hardcodeado

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(getClass().getResource("/resources/styles/create.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("⚠ CSS no encontrado");
            }

            stage.setScene(scene);
            stage.setTitle("Crear Nuevo Dibujo");
            stage.setResizable(false);

            // Actualizar la lista cuando se cierre la ventana
            stage.setOnHidden(e -> {
                System.out.println("🔄 Ventana de creación cerrada, actualizando lista...");

                // Forzar la actualización del gestor
                gestorArchivoDibujo = new GestorArchivoDibujo(); // Recrear para recargar desde archivo

                cargarDibujos();
            });

            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Error al abrir ventana de creación:");
            e.printStackTrace();
            mostrarError("Error al abrir ventana de creación: " + e.getMessage());
        }
    }

    @FXML
    private void handleGestionarUsuarios(ActionEvent event) {
        System.out.println("👥 Gestionar usuarios...");
        // TODO: Abrir ventana de gestión de usuarios
        mostrarInfo("Función en desarrollo", "La gestión de usuarios se implementará próximamente");
    }

    @FXML
    private void handleCerrarSesion(ActionEvent event) {
        try {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Cerrar Sesión");
            confirmacion.setHeaderText("¿Está seguro que desea cerrar sesión?");
            confirmacion.setContentText("Usuario: " + nombreUsuario);

            if (confirmacion.showAndWait().get() == ButtonType.OK) {
                System.out.println("👋 Cerrando sesión de: " + nombreUsuario);

                FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/LoginView.fxml"));
                Parent root = loader.load();

                Stage stage = (Stage) btnCerrarSesion.getScene().getWindow();
                Scene scene = new Scene(root);

                try {
                    scene.getStylesheets().add(getClass().getResource("/resources/styles/login.css").toExternalForm());
                } catch (Exception e) {
                    System.out.println("⚠ CSS no encontrado");
                }

                stage.setScene(scene);
                stage.setTitle("Login - Sistema de Dibujo");
            }

        } catch (IOException e) {
            System.err.println("❌ Error al cerrar sesión:");
            e.printStackTrace();
            mostrarError("Error al cerrar sesión: " + e.getMessage());
        }
    }

    // ACCIONES DE DIBUJOS

    private void abrirDibujo(Dibujo dibujo) {
        // ✅ Validar que solo usuarios NORMALES pueden colorear
        if (rolUsuario == RolUsuarios.ADMIN) {
            mostrarAdvertencia("Acceso Denegado",
                    "Los administradores no pueden colorear dibujos.\n" +
                            "Solo los usuarios normales tienen acceso a esta funcionalidad.");
            return;
        }

        try {
            System.out.println("🖌️ Abriendo dibujo para colorear: " + dibujo.getNombreDibujo());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/ColorearDibujoView.fxml"));
            Parent root = loader.load();

            // Pasar el dibujo y el usuario al controlador
            ColorearDibujoController controller = loader.getController();
            // TODO: Obtener el ID real del usuario logueado
            controller.setDibujoYUsuario(dibujo, 1); // Por ahora ID hardcodeado

            Stage stage = new Stage();
            Scene scene = new Scene(root);

            try {
                scene.getStylesheets().add(getClass().getResource("/resources/styles/main.css").toExternalForm());
            } catch (Exception e) {
                System.out.println("⚠ CSS no encontrado");
            }

            stage.setScene(scene);
            stage.setTitle("Colorear: " + dibujo.getNombreDibujo());
            stage.setResizable(false);
            stage.show();

        } catch (IOException e) {
            System.err.println("❌ Error al abrir dibujo:");
            e.printStackTrace();
            mostrarError("Error al abrir dibujo: " + e.getMessage());
        }
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void editarDibujo(Dibujo dibujo) {
        System.out.println("✏️ Editando dibujo: " + dibujo.getNombreDibujo());
        // TODO: Abrir ventana de edición
        mostrarInfo("Editar dibujo", "Función en desarrollo");
    }

    private void eliminarDibujo(Dibujo dibujo) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Eliminar Dibujo");
        confirmacion.setHeaderText("¿Está seguro que desea eliminar este dibujo?");
        confirmacion.setContentText("Dibujo: " + dibujo.getNombreDibujo() + "\n\nEsta acción no se puede deshacer.");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            boolean exito = gestorArchivoDibujo.eliminarDibujo(dibujo.getIdDibujo());

            if (exito) {
                System.out.println("✅ Dibujo eliminado: " + dibujo.getNombreDibujo());
                cargarDibujos();
                mostrarInfo("Éxito", "Dibujo eliminado correctamente");
            } else {
                System.err.println("❌ Error al eliminar dibujo");
                mostrarError("No se pudo eliminar el dibujo");
            }
        }
    }

    // UTILIDADES

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarInfo(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}