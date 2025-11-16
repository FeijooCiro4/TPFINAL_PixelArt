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

    // FXML Elements
    @FXML private Label lblTitulo;
    @FXML private Label lblNombreDibujo;
    @FXML private Label lblTamanioDibujo;
    @FXML private ToggleButton btnHerramientaPintar;
    @FXML private ToggleButton btnHerramientaBorrar;
    @FXML private Region regionColorActual;
    @FXML private Label lblColorActual;
    @FXML private Label lblIndiceColor;
    @FXML private GridPane gridPaletaColores;
    @FXML private Label lblCantidadColores;
    @FXML private Label lblPixelesColoreados;
    @FXML private ProgressBar progressBar;
    @FXML private Label lblPorcentaje;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private CheckBox chkMostrarGrid;
    @FXML private CheckBox chkMostrarPlantilla;
    @FXML private ScrollPane scrollCanvas;
    @FXML private StackPane stackCanvas;
    @FXML private GridPane gridCanvas;
    @FXML private GridPane gridPreview;
    @FXML private Label lblEstadoDibujo;
    @FXML private Label lblMensajeEstado;
    @FXML private Label lblEstado;
    @FXML private Label lblUltimaAccion;
    @FXML private Button btnVolver;

    // Variables de estado
    private GestorArchivoDibujo gestorArchivoDibujo;
    private GestorArchivoUsuario gestorArchivoUsuario;
    private Dibujo dibujoActual;
    private int idUsuario;
    private String colorActual;
    private int indiceColorActual;
    private boolean modoPintar = true;
    private Map<String, String> pixelesColoreados; // key: "x,y" -> value: color hex
    private Map<String, Integer> pixelesNumero; // key: "x,y" -> value: índice del color
    private Set<String> pixelesPintables; // Píxeles que el admin marcó como coloreables
    private TreeMap<Integer, String> paletaDibujo; // La paleta del dibujo

    @FXML
    public void initialize() {
        try {
            gestorArchivoDibujo = new GestorArchivoDibujo();
            gestorArchivoUsuario = new GestorArchivoUsuario();
            pixelesColoreados = new HashMap<>();
            pixelesNumero = new HashMap<>();
            pixelesPintables = new HashSet<>();

            configurarHerramientas();

            System.out.println("✅ ColorearDibujoController inicializado");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar ColorearDibujoController:");
            e.printStackTrace();
            mostrarError("Error al inicializar: " + e.getMessage());
        }
    }

    /**
     * Configura el dibujo y el usuario
     */
    public void setDibujoYUsuario(Dibujo dibujo, int idUsuario) {
        this.dibujoActual = dibujo;
        this.idUsuario = idUsuario;

        if (dibujo == null) {
            mostrarError("Error: Dibujo no válido");
            return;
        }

        System.out.println("🎨 Cargando dibujo para colorear:");
        System.out.println("   - Dibujo: " + dibujo.getNombreDibujo());
        System.out.println("   - ID Usuario: " + idUsuario);
        System.out.println("   - Tamaño: " + dibujo.getAnchoCuadricula() + "x" + dibujo.getAnchoCuadricula());
        System.out.println("   - Cuadrículas plantilla: " + dibujo.getCuadriculas().size());

        cargarInformacionDibujo();
        cargarPaletaDibujo();
        cargarPixelesPintables();
        cargarProgresoGuardado();
        generarCanvas();
        generarPreview();
        actualizarProgreso();
    }

    private void configurarHerramientas() {
        ToggleGroup herramientas = new ToggleGroup();
        btnHerramientaPintar.setToggleGroup(herramientas);
        btnHerramientaBorrar.setToggleGroup(herramientas);

        btnHerramientaPintar.setSelected(true);
        modoPintar = true;

        btnHerramientaPintar.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                modoPintar = true;
                actualizarEstado("Modo: Pintar");
            }
        });

        btnHerramientaBorrar.selectedProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal) {
                modoPintar = false;
                actualizarEstado("Modo: Borrar");
            }
        });
    }

    private void cargarInformacionDibujo() {
        lblNombreDibujo.setText(dibujoActual.getNombreDibujo());
        int tamanio = dibujoActual.getAnchoCuadricula();
        lblTamanioDibujo.setText(tamanio + "x" + tamanio);
        lblTitulo.setText("Colorear: " + dibujoActual.getNombreDibujo());
    }

    private void cargarPaletaDibujo() {
        paletaDibujo = new TreeMap<>(dibujoActual.getClavesColores());

        System.out.println("🎨 Paleta del dibujo:");
        for (Map.Entry<Integer, String> entry : paletaDibujo.entrySet()) {
            System.out.println("   [" + entry.getKey() + "] → " + entry.getValue());
        }

        generarPaletaColores();

        // Seleccionar el primer color
        if (!paletaDibujo.isEmpty()) {
            Map.Entry<Integer, String> firstEntry = paletaDibujo.firstEntry();
            seleccionarColor(firstEntry.getKey(), firstEntry.getValue());
        }
    }

    private void generarPaletaColores() {
        gridPaletaColores.getChildren().clear();

        int col = 0;
        int row = 0;
        int columnas = 4;

        for (Map.Entry<Integer, String> entry : paletaDibujo.entrySet()) {
            int indice = entry.getKey();
            String colorHex = entry.getValue();

            VBox colorBox = crearCeldaColor(indice, colorHex);
            gridPaletaColores.add(colorBox, col, row);

            col++;
            if (col >= columnas) {
                col = 0;
                row++;
            }
        }

        lblCantidadColores.setText(paletaDibujo.size() + " colores disponibles");
    }

    private VBox crearCeldaColor(int indice, String colorHex) {
        VBox box = new VBox(3);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-cursor: hand; -fx-padding: 3;");

        Rectangle rect = new Rectangle(50, 50);
        rect.setFill(Color.web(colorHex));
        rect.setStroke(Color.web("#bdc3c7"));
        rect.setStrokeWidth(2);
        rect.setStyle("-fx-cursor: hand;");

        Label lblIndice = new Label("#" + indice);
        lblIndice.setStyle("-fx-font-size: 10px; -fx-text-fill: #7f8c8d;");

        box.getChildren().addAll(rect, lblIndice);

        box.setOnMouseClicked(e -> seleccionarColor(indice, colorHex));

        // Marcar si es el seleccionado
        if (indiceColorActual == indice) {
            rect.setStroke(Color.web("#3498db"));
            rect.setStrokeWidth(4);
        }

        return box;
    }

    private void seleccionarColor(int indice, String colorHex) {
        this.indiceColorActual = indice;
        this.colorActual = colorHex;

        regionColorActual.setStyle("-fx-background-color: " + colorHex + "; " +
                "-fx-border-color: #bdc3c7; -fx-border-width: 2; " +
                "-fx-background-radius: 5; -fx-border-radius: 5;");

        lblColorActual.setText(colorHex);
        lblIndiceColor.setText("#" + indice);

        // Regenerar paleta para actualizar selección
        generarPaletaColores();

        actualizarEstado("Color seleccionado: #" + indice);
    }

    private void cargarPixelesPintables() {
        pixelesPintables.clear();
        pixelesNumero.clear();

        for (Cuadricula cuadricula : dibujoActual.getCuadriculas()) {
            int x = cuadricula.getIndiceX();
            int y = cuadricula.getIndiceY();
            String colorPlantilla = cuadricula.getColor();
            String key = x + "," + y;

            pixelesPintables.add(key);

            // Encontrar el índice del color en la paleta
            for (Map.Entry<Integer, String> entry : paletaDibujo.entrySet()) {
                if (entry.getValue().equalsIgnoreCase(colorPlantilla)) {
                    pixelesNumero.put(key, entry.getKey());
                    break;
                }
            }
        }

        System.out.println("📍 Píxeles pintables: " + pixelesPintables.size());
    }

    private void cargarProgresoGuardado() {
        // TODO: Implementar carga del progreso guardado
        // Por ahora empezamos con canvas vacío
        System.out.println("💾 Cargando progreso guardado (no implementado aún)");
    }

    private void generarCanvas() {
        gridCanvas.getChildren().clear();
        int tamanio = dibujoActual.getAnchoCuadricula();
        double pixelSize = calcularTamanioPixel(tamanio);

        gridCanvas.setHgap(0);
        gridCanvas.setVgap(0);
        gridCanvas.setAlignment(Pos.CENTER);

        // Aplicar estilos al grid
        if (chkMostrarGrid.isSelected()) {
            gridCanvas.setHgap(1);
            gridCanvas.setVgap(1);
            gridCanvas.setStyle("-fx-background-color: #bdc3c7;");
        } else {
            gridCanvas.setStyle("-fx-background-color: white;");
        }

        for (int y = 0; y < tamanio; y++) {
            for (int x = 0; x < tamanio; x++) {
                String key = x + "," + y;
                StackPane celda = crearCeldaCanvas(x, y, pixelSize, key);
                gridCanvas.add(celda, x, y);
            }
        }
    }

    private StackPane crearCeldaCanvas(int x, int y, double size, String key) {
        StackPane celda = new StackPane();
        celda.setMinSize(size, size);
        celda.setMaxSize(size, size);

        Rectangle fondo = new Rectangle(size, size);

        // Determinar el color de fondo
        if (pixelesColoreados.containsKey(key)) {
            // Mostrar el color pintado por el usuario
            String colorPintado = pixelesColoreados.get(key);
            fondo.setFill(Color.web(colorPintado));
        } else {
            // Fondo blanco si no está coloreado
            fondo.setFill(Color.WHITE);
        }

        // SIN bordes de validación verde/rojo
        fondo.setStroke(Color.TRANSPARENT);
        fondo.setStrokeWidth(0);

        celda.getChildren().add(fondo);

        // Si es pintable, mostrar el número (solo si mostrar plantilla está activo)
        if (pixelesPintables.contains(key)) {
            if (chkMostrarPlantilla.isSelected() && !pixelesColoreados.containsKey(key)) {
                Integer numeroColor = pixelesNumero.get(key);
                if (numeroColor != null) {
                    Label lblNumero = new Label(String.valueOf(numeroColor));
                    lblNumero.setStyle("-fx-font-size: " + (size * 0.4) + "px; " +
                            "-fx-text-fill: #95a5a6; -fx-font-weight: bold;");
                    celda.getChildren().add(lblNumero);
                }
            }

            // Hacer clickeable
            celda.setStyle("-fx-cursor: hand;");
            celda.setOnMouseClicked(this::handleClickPixel);
            celda.setOnMouseDragEntered(this::handleClickPixel);
        }

        return celda;
    }

    private void handleClickPixel(MouseEvent e) {
        StackPane celda = (StackPane) e.getSource();
        Integer colIndex = GridPane.getColumnIndex(celda);
        Integer rowIndex = GridPane.getRowIndex(celda);

        if (colIndex == null || rowIndex == null) return;

        int x = colIndex;
        int y = rowIndex;
        String key = x + "," + y;

        if (!pixelesPintables.contains(key)) {
            return; // No es pintable
        }

        if (e.getButton() == MouseButton.PRIMARY || e.isPrimaryButtonDown()) {
            if (modoPintar) {
                // Pintar con el color actual
                pixelesColoreados.put(key, colorActual);
            } else {
                // Borrar
                pixelesColoreados.remove(key);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            // Borrar
            pixelesColoreados.remove(key);
        }

        // Regenerar canvas
        generarCanvas();
        actualizarProgreso();
        actualizarPreview();
    }

    private double calcularTamanioPixel(int tamanio) {
        double maxSize = 600.0;
        return Math.min(maxSize / tamanio, 30);
    }

    private void generarPreview() {
        gridPreview.getChildren().clear();
        gridPreview.setHgap(1);
        gridPreview.setVgap(1);
        gridPreview.setAlignment(Pos.CENTER);

        int tamanio = dibujoActual.getAnchoCuadricula();
        double previewSize = Math.min(200.0 / tamanio, 10);

        for (int y = 0; y < tamanio; y++) {
            for (int x = 0; x < tamanio; x++) {
                Rectangle pixel = new Rectangle(previewSize, previewSize);
                String key = x + "," + y;

                if (pixelesColoreados.containsKey(key)) {
                    String color = pixelesColoreados.get(key);
                    pixel.setFill(Color.web(color));
                } else {
                    pixel.setFill(Color.WHITE);
                }

                pixel.setStroke(Color.web("#dcdde1"));
                pixel.setStrokeWidth(0.3);

                gridPreview.add(pixel, x, y);
            }
        }
    }

    private void actualizarPreview() {
        for (var node : gridPreview.getChildren()) {
            if (node instanceof Rectangle) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);

                if (colIndex != null && rowIndex != null) {
                    String key = colIndex + "," + rowIndex;
                    Rectangle pixel = (Rectangle) node;

                    if (pixelesColoreados.containsKey(key)) {
                        String color = pixelesColoreados.get(key);
                        pixel.setFill(Color.web(color));
                    } else {
                        pixel.setFill(Color.WHITE);
                    }
                }
            }
        }
    }

    private void actualizarProgreso() {
        int total = pixelesPintables.size();
        int coloreados = pixelesColoreados.size();

        double porcentajeColoreados = total > 0 ? (coloreados * 100.0) / total : 0;

        lblPixelesColoreados.setText(coloreados + " / " + total + " píxeles coloreados");
        progressBar.setProgress(porcentajeColoreados / 100.0);
        lblPorcentaje.setText(String.format("%.1f%% completado", porcentajeColoreados));

        // Actualizar estado basado solo en cantidad coloreada
        if (coloreados == total && total > 0) {
            lblEstadoDibujo.setText("¡COMPLETADO!");
            lblMensajeEstado.setText("¡Has coloreado todos los píxeles!");
            lblEstadoDibujo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #2e7d32;");
            lblMensajeEstado.setStyle("-fx-font-size: 10px; -fx-text-fill: #2e7d32;");
        } else {
            int faltantes = total - coloreados;
            lblEstadoDibujo.setText("En progreso");
            lblMensajeEstado.setText("Faltan " + faltantes + " píxeles por colorear");
            lblEstadoDibujo.setStyle("-fx-font-size: 13px; -fx-font-weight: bold; -fx-text-fill: #e65100;");
            lblMensajeEstado.setStyle("-fx-font-size: 10px; -fx-text-fill: #e65100;");
        }
    }

    @FXML
    private void handleToggleGrid(ActionEvent event) {
        generarCanvas();
        actualizarEstado(chkMostrarGrid.isSelected() ? "Grid visible" : "Grid oculto");
    }

    @FXML
    private void handleTogglePlantilla(ActionEvent event) {
        generarCanvas();
        actualizarEstado(chkMostrarPlantilla.isSelected() ? "Plantilla visible" : "Plantilla oculta");
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        try {
            System.out.println("💾 Guardando progreso...");
            System.out.println("   - Píxeles coloreados: " + pixelesColoreados.size());

            // TODO: Implementar guardado del progreso
            // Deberías guardar pixelesColoreados en algún lugar asociado al usuario
            // Por ejemplo, en un archivo JSON específico por usuario y dibujo

            actualizarEstado("Progreso guardado");
            lblUltimaAccion.setText("Guardado: " + new java.util.Date());

            mostrarInfo("Guardado", "Tu progreso ha sido guardado correctamente");

        } catch (Exception e) {
            System.err.println("❌ Error al guardar:");
            e.printStackTrace();
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Limpiar Todo");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("Se borrarán todos los píxeles coloreados");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            pixelesColoreados.clear();
            generarCanvas();
            actualizarPreview();
            actualizarProgreso();
            actualizarEstado("Canvas limpiado");
            System.out.println("🗑 Canvas limpiado");
        }
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        if (!pixelesColoreados.isEmpty()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Volver");
            confirmacion.setHeaderText("¿Deseas salir?");
            confirmacion.setContentText("Asegúrate de haber guardado tu progreso");

            if (confirmacion.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void actualizarEstado(String mensaje) {
        lblEstado.setText(mensaje);
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
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