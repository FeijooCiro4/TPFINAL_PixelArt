package vistas.controllers;

import controllers.GestorArchivoDibujo;
import controllers.GestorArchivoUsuario;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Cuadricula;
import models.Dibujo;

import java.util.*;

public class ColorearDibujoController {

    @FXML private Label lblTitulo;
    @FXML private Label lblNombreDibujo;
    @FXML private Label lblTamanioDibujo;
    @FXML private ToggleButton btnHerramientaPintar;
    @FXML private ToggleButton btnHerramientaBorrar;
    @FXML private Region regionColorActual;
    @FXML private Label lblColorActual;
    @FXML private GridPane gridPaletaColores;
    @FXML private Label lblCantidadColores;
    @FXML private Label lblPixelesColoreados;
    @FXML private Button btnLimpiar;
    @FXML private CheckBox chkMostrarGrid;
    @FXML private CheckBox chkMostrarPlantilla;
    @FXML private GridPane gridCanvas;
    @FXML private Button btnVolver;

    // Variables
    private GestorArchivoUsuario gestorArchivoUsuario;
    private Dibujo dibujoActual;
    private int idUsuario;
    private String colorActual;
    private boolean modoPintar = true;
    private Map<String, String> pixelesColoreados;
    private Map<String, Integer> pixelesNumero;
    private Set<String> pixelesPintables;
    private TreeMap<Integer, String> paletaDibujo;
    private boolean yaGuardado = false;

    @FXML
    public void initialize() {
        gestorArchivoUsuario = new GestorArchivoUsuario();
        pixelesColoreados = new HashMap<>();
        pixelesNumero = new HashMap<>();
        pixelesPintables = new HashSet<>();
        configurarHerramientas();
    }

    public void setDibujoYUsuario(Dibujo dibujo, int idUsuario) {
        this.dibujoActual = dibujo;
        this.idUsuario = idUsuario;

        lblNombreDibujo.setText(dibujo.getNombreDibujo());
        lblTamanioDibujo.setText(dibujo.getAnchoCuadricula() + "x" + dibujo.getAnchoCuadricula());
        lblTitulo.setText("Colorear: " + dibujo.getNombreDibujo());

        cargarPaleta();
        cargarPixelesPintables();
        verificarCompletado();
        generarCanvas();
        actualizarProgreso();
    }

    private void configurarHerramientas() {
        ToggleGroup herramientas = new ToggleGroup();
        btnHerramientaPintar.setToggleGroup(herramientas);
        btnHerramientaBorrar.setToggleGroup(herramientas);
        btnHerramientaPintar.setSelected(true);

        btnHerramientaPintar.selectedProperty().addListener((obs, old, val) -> {
            if (val) modoPintar = true;
        });

        btnHerramientaBorrar.selectedProperty().addListener((obs, old, val) -> {
            if (val) modoPintar = false;
        });
    }

    private void cargarPaleta() {
        paletaDibujo = new TreeMap<>(dibujoActual.getClavesColores());
        gridPaletaColores.getChildren().clear();

        int col = 0;
        for (Map.Entry<Integer, String> entry : paletaDibujo.entrySet()) {
            VBox colorBox = new VBox(3);
            colorBox.setAlignment(Pos.CENTER);

            Rectangle rect = new Rectangle(40, 40);
            rect.setFill(Color.web(entry.getValue()));
            rect.setStroke(Color.web("#999"));
            rect.setStrokeWidth(2);
            rect.setStyle("-fx-cursor: hand;");
            rect.setOnMouseClicked(e -> seleccionarColor(entry.getValue()));

            Label lblNum = new Label(String.valueOf(entry.getKey()));
            lblNum.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #666;");

            colorBox.getChildren().addAll(rect, lblNum);
            colorBox.setStyle("-fx-cursor: hand;");
            colorBox.setOnMouseClicked(e -> seleccionarColor(entry.getValue()));

            gridPaletaColores.add(colorBox, col++, 0);
        }

        lblCantidadColores.setText(paletaDibujo.size() + " colores");

        if (!paletaDibujo.isEmpty()) {
            seleccionarColor(paletaDibujo.firstEntry().getValue());
        }
    }

    private void seleccionarColor(String colorHex) {
        this.colorActual = colorHex;
        regionColorActual.setStyle("-fx-background-color: " + colorHex + "; -fx-border-color: #999; -fx-border-width: 2;");
        lblColorActual.setText(colorHex);
    }

    private void cargarPixelesPintables() {
        for (Cuadricula cuadricula : dibujoActual.getCuadriculas()) {
            String key = cuadricula.getIndiceX() + "," + cuadricula.getIndiceY();
            pixelesPintables.add(key);

            for (Map.Entry<Integer, String> entry : paletaDibujo.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(cuadricula.getColor())) {
                    pixelesNumero.put(key, entry.getKey());
                    break;
                }
            }
        }
    }

    private void verificarCompletado() {
        try {
            models.UsuarioNormal usuario = gestorArchivoUsuario.buscarUsuarioNormal(idUsuario);
            if (usuario != null) {
                yaGuardado = usuario.buscarDibujoPintado(dibujoActual.getIdDibujo());
            }
        } catch (Exception e) {
            yaGuardado = false;
        }
    }

    private void generarCanvas() {
        gridCanvas.getChildren().clear();
        int tamanio = dibujoActual.getAnchoCuadricula();
        double pixelSize = Math.min(500.0 / tamanio, 25);

        if (chkMostrarGrid.isSelected()) {
            gridCanvas.setHgap(1);
            gridCanvas.setVgap(1);
            gridCanvas.setStyle("-fx-background-color: #ccc;");
        } else {
            gridCanvas.setHgap(0);
            gridCanvas.setVgap(0);
            gridCanvas.setStyle("-fx-background-color: white;");
        }

        gridCanvas.setAlignment(Pos.CENTER);

        for (int y = 0; y < tamanio; y++) {
            for (int x = 0; x < tamanio; x++) {
                String key = x + "," + y;
                StackPane celda = crearCelda(x, y, pixelSize, key);
                gridCanvas.add(celda, x, y);
            }
        }
    }

    private StackPane crearCelda(int x, int y, double size, String key) {
        StackPane celda = new StackPane();
        celda.setMinSize(size, size);
        celda.setMaxSize(size, size);

        Rectangle fondo = new Rectangle(size, size);

        if (pixelesColoreados.containsKey(key)) {
            fondo.setFill(Color.web(pixelesColoreados.get(key)));
        } else {
            fondo.setFill(Color.WHITE);
        }

        celda.getChildren().add(fondo);

        if (pixelesPintables.contains(key)) {
            if (chkMostrarPlantilla.isSelected() && !pixelesColoreados.containsKey(key)) {
                Integer num = pixelesNumero.get(key);
                if (num != null) {
                    Label lbl = new Label(String.valueOf(num));
                    lbl.setStyle("-fx-font-size: " + (size * 0.4) + "px; -fx-text-fill: #999;");
                    celda.getChildren().add(lbl);
                }
            }

            celda.setStyle("-fx-cursor: hand;");
            celda.setOnMouseClicked(this::handleClick);
            celda.setOnMouseDragEntered(this::handleClick);
        }

        return celda;
    }

    private void handleClick(MouseEvent e) {
        StackPane celda = (StackPane) e.getSource();
        Integer x = GridPane.getColumnIndex(celda);
        Integer y = GridPane.getRowIndex(celda);

        if (x == null || y == null) return;

        String key = x + "," + y;
        if (!pixelesPintables.contains(key)) return;

        if (e.getButton() == MouseButton.PRIMARY || e.isPrimaryButtonDown()) {
            if (modoPintar) {
                pixelesColoreados.put(key, colorActual);
            } else {
                pixelesColoreados.remove(key);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            pixelesColoreados.remove(key);
        }

        generarCanvas();
        actualizarProgreso();
    }

    private void actualizarProgreso() {
        int total = pixelesPintables.size();
        int coloreados = pixelesColoreados.size();

        lblPixelesColoreados.setText(coloreados + " / " + total);

        if (coloreados == total && total > 0 && !yaGuardado) {
            autoGuardar();
        }
    }

    private void autoGuardar() {
        try {
            gestorArchivoUsuario.agregarDibujoPintado(idUsuario, dibujoActual.getIdDibujo());
            yaGuardado = true;

            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("¡Completado!");
            alert.setHeaderText("🎉 ¡Dibujo Completado!");
            alert.setContentText("Has terminado de colorear:\n\"" + dibujoActual.getNombreDibujo() + "\"\n\nGuardado automáticamente.");
            alert.showAndWait();
        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleToggleGrid(ActionEvent event) {
        generarCanvas();
    }

    @FXML
    private void handleTogglePlantilla(ActionEvent event) {
        generarCanvas();
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
        confirm.setTitle("Limpiar");
        confirm.setContentText("¿Borrar todos los píxeles?");

        if (confirm.showAndWait().get() == ButtonType.OK) {
            pixelesColoreados.clear();
            generarCanvas();
            actualizarProgreso();
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        if (!pixelesColoreados.isEmpty() && !yaGuardado) {
            Alert confirm = new Alert(Alert.AlertType.CONFIRMATION);
            confirm.setTitle("Volver");
            confirm.setContentText("No has completado el dibujo.\nPerderás tu progreso.\n¿Salir?");

            if (confirm.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }
}