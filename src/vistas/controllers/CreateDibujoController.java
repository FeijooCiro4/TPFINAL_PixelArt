package vistas.controllers;

import controllers.GestorArchivoDibujo;
import controllers.GestorLienzo;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import models.Dibujo;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CreateDibujoController {

    // FXML Elements - Left Panel
    @FXML private TextField txtNombreDibujo;
    @FXML private ComboBox<String> cmbTamanio;
    @FXML private Label lblInfoTamanio;
    @FXML private ToggleButton btnHerramientaPintar;
    @FXML private ToggleButton btnHerramientaBorrar;
    @FXML private Region regionColorActual;
    @FXML private Label lblColorActual;
    @FXML private HBox hboxColorActual;
    @FXML private GridPane gridPaletaColores;
    @FXML private Label lblCantidadColores;
    @FXML private Button btnAgregarColor;
    @FXML private Label lblPixelesMarcados;
    @FXML private Label lblPorcentajeLleno;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;

    // FXML Elements - Center Panel
    @FXML private CheckBox chkMostrarGrid;
    @FXML private ScrollPane scrollCanvas;
    @FXML private StackPane stackCanvas;
    @FXML private GridPane gridCanvas;

    // FXML Elements - Right Panel
    @FXML private GridPane gridPreview;

    // FXML Elements - Other
    @FXML private Button btnVolver;
    @FXML private Label lblEstado;
    @FXML private Label lblUltimaAccion;

    // Variables de estado
    private GestorArchivoDibujo gestorArchivoDibujo;
    private GestorLienzo gestorLienzo;
    private int tamanioActual = 16;
    private String colorActual = "#000000";
    private boolean modoPintar = true;
    private Map<String, String> pixelesMarcados; // key: "x,y" -> value: color hexadecimal
    private ArrayList<String> paletaPersonalizada; // Paleta personalizable del dibujo
    private int idUsuarioCreador; // ID del admin que está creando

    @FXML
    public void initialize() {
        try {
            gestorArchivoDibujo = new GestorArchivoDibujo();
            gestorLienzo = new GestorLienzo();
            pixelesMarcados = new HashMap<>();
            paletaPersonalizada = new ArrayList<>();

            // Inicializar con colores predefinidos del sistema
            String[] coloresPredefinidos = GestorLienzo.getColoresPermitidos();
            for (String color : coloresPredefinidos) {
                paletaPersonalizada.add(color);
            }

            inicializarControles();
            configurarHerramientas();
            generarPaletaColores();

            // Tamaño inicial
            cambiarTamanioCanvas(16);

            System.out.println("✅ CreateDibujoController inicializado");
            System.out.println("   - Paleta inicial: " + paletaPersonalizada.size() + " colores");
            actualizarEstado("Listo para crear un nuevo dibujo");

        } catch (Exception e) {
            System.err.println("❌ Error al inicializar CreateDibujoController:");
            e.printStackTrace();
            mostrarError("Error al inicializar: " + e.getMessage());
        }
    }

    /**
     * Configura el ID del usuario creador
     */
    public void setUsuarioCreador(int idUsuario) {
        this.idUsuarioCreador = idUsuario;
        System.out.println("👤 Usuario creador configurado: " + idUsuario);
    }

    private void inicializarControles() {
        // Configurar ComboBox de tamaños
        int[] tamanios = GestorLienzo.getTamaniosDisponibles();
        String[] opciones = new String[tamanios.length];
        for (int i = 0; i < tamanios.length; i++) {
            opciones[i] = tamanios[i] + " x " + tamanios[i];
        }
        cmbTamanio.setItems(FXCollections.observableArrayList(opciones));
        cmbTamanio.getSelectionModel().select(1); // 16x16 por defecto

        // Configurar color actual
        actualizarVisualizacionColor();
    }

    private void configurarHerramientas() {
        // Grupo de toggle buttons (solo uno activo a la vez)
        ToggleGroup herramientas = new ToggleGroup();
        btnHerramientaPintar.setToggleGroup(herramientas);
        btnHerramientaBorrar.setToggleGroup(herramientas);

        btnHerramientaPintar.setSelected(true);
        modoPintar = true;

        // Listeners
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

    private void generarPaletaColores() {
        gridPaletaColores.getChildren().clear();

        int col = 0;
        int row = 0;
        int columnas = 5;

        for (int i = 0; i < paletaPersonalizada.size(); i++) {
            String colorHex = paletaPersonalizada.get(i);

            // Crear un StackPane que contenga el color y el botón X
            StackPane colorContainer = crearColorConBoton(colorHex, i);

            gridPaletaColores.add(colorContainer, col, row);

            col++;
            if (col >= columnas) {
                col = 0;
                row++;
            }
        }

        lblCantidadColores.setText(paletaPersonalizada.size() + " colores en la paleta");
    }

    private StackPane crearColorConBoton(String colorHex, int index) {
        StackPane container = new StackPane();
        container.setPrefSize(45, 45);

        // Rectángulo de color
        Rectangle rect = new Rectangle(45, 45);
        rect.setFill(Color.web(colorHex));
        rect.setStroke(colorHex.equals(colorActual) ? Color.web("#3498db") : Color.web("#bdc3c7"));
        rect.setStrokeWidth(colorHex.equals(colorActual) ? 3 : 2);
        rect.setStyle("-fx-cursor: hand;");

        // Eventos del rectángulo
        rect.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                // Click izquierdo: Seleccionar color
                seleccionarColor(colorHex);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                // Click derecho: Cambiar color
                cambiarColorDePaleta(index);
            }
        });

        // Botón X para eliminar (solo si hay más de 2 colores)
        Button btnEliminar = new Button("×");
        btnEliminar.setStyle(
                "-fx-background-color: rgba(231, 76, 60, 0.9); " +
                        "-fx-text-fill: white; " +
                        "-fx-font-size: 14px; " +
                        "-fx-font-weight: bold; " +
                        "-fx-padding: 0; " +
                        "-fx-min-width: 18px; " +
                        "-fx-min-height: 18px; " +
                        "-fx-max-width: 18px; " +
                        "-fx-max-height: 18px; " +
                        "-fx-background-radius: 9; " +
                        "-fx-cursor: hand;"
        );
        btnEliminar.setOnAction(e -> eliminarColorDePaleta(index));
        btnEliminar.setVisible(paletaPersonalizada.size() > 2); // Mínimo 2 colores

        StackPane.setAlignment(btnEliminar, Pos.TOP_RIGHT);
        StackPane.setMargin(btnEliminar, new javafx.geometry.Insets(2, 2, 0, 0));

        container.getChildren().addAll(rect, btnEliminar);

        return container;
    }

    private void seleccionarColor(String colorHex) {
        colorActual = colorHex;
        actualizarVisualizacionColor();
        generarPaletaColores(); // Regenerar para actualizar bordes
        actualizarEstado("Color seleccionado: " + colorHex);
    }

    @FXML
    private void handleAgregarColor(ActionEvent event) {
        ColorPicker colorPicker = new ColorPicker(Color.web(colorActual));

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Agregar Color");
        dialog.setHeaderText("Selecciona un nuevo color para la paleta");
        dialog.getDialogPane().setContent(colorPicker);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Color selectedColor = colorPicker.getValue();
            String hexColor = String.format("#%02X%02X%02X",
                    (int)(selectedColor.getRed() * 255),
                    (int)(selectedColor.getGreen() * 255),
                    (int)(selectedColor.getBlue() * 255));

            // Verificar si el color ya existe
            if (paletaPersonalizada.contains(hexColor)) {
                mostrarAdvertencia("Color duplicado", "Este color ya está en la paleta");
                return;
            }

            paletaPersonalizada.add(hexColor);
            generarPaletaColores();

            System.out.println("✓ Color agregado a la paleta: " + hexColor);
            actualizarEstado("Color " + hexColor + " agregado a la paleta");
        }
    }

    private void cambiarColorDePaleta(int index) {
        String colorActualPaleta = paletaPersonalizada.get(index);
        ColorPicker colorPicker = new ColorPicker(Color.web(colorActualPaleta));

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Cambiar Color");
        dialog.setHeaderText("Cambia el color " + colorActualPaleta);
        dialog.getDialogPane().setContent(colorPicker);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Color selectedColor = colorPicker.getValue();
            String hexColor = String.format("#%02X%02X%02X",
                    (int)(selectedColor.getRed() * 255),
                    (int)(selectedColor.getGreen() * 255),
                    (int)(selectedColor.getBlue() * 255));

            String colorAnterior = paletaPersonalizada.get(index);
            paletaPersonalizada.set(index, hexColor);

            // Actualizar píxeles que tenían el color anterior
            for (Map.Entry<String, String> entry : pixelesMarcados.entrySet()) {
                if (entry.getValue().equals(colorAnterior)) {
                    entry.setValue(hexColor);
                }
            }

            // Si el color actual era el que se cambió, actualizar
            if (colorActual.equals(colorAnterior)) {
                colorActual = hexColor;
                actualizarVisualizacionColor();
            }

            generarPaletaColores();
            generarCanvas();
            actualizarPreview();

            System.out.println("✓ Color cambiado: " + colorAnterior + " → " + hexColor);
            actualizarEstado("Color actualizado en la paleta");
        }
    }

    private void eliminarColorDePaleta(int index) {
        if (paletaPersonalizada.size() <= 2) {
            mostrarAdvertencia("No se puede eliminar", "Debe haber al menos 2 colores en la paleta");
            return;
        }

        String colorAEliminar = paletaPersonalizada.get(index);

        // Verificar si hay píxeles pintados con este color
        boolean colorEnUso = pixelesMarcados.values().stream()
                .anyMatch(c -> c.equals(colorAEliminar));

        if (colorEnUso) {
            Alert confirmacion = new Alert(Alert.AlertType.WARNING);
            confirmacion.setTitle("Color en uso");
            confirmacion.setHeaderText("Hay píxeles pintados con este color");
            confirmacion.setContentText("Si eliminas este color, los píxeles pintados se convertirán al primer color de la paleta.\n\n¿Continuar?");

            Optional<ButtonType> result = confirmacion.showAndWait();
            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return;
            }

            // Cambiar todos los píxeles de este color al primer color
            String colorReemplazo = paletaPersonalizada.get(0);
            for (Map.Entry<String, String> entry : pixelesMarcados.entrySet()) {
                if (entry.getValue().equals(colorAEliminar)) {
                    entry.setValue(colorReemplazo);
                }
            }
        }

        paletaPersonalizada.remove(index);

        // Si el color eliminado era el actual, seleccionar el primero
        if (colorActual.equals(colorAEliminar)) {
            colorActual = paletaPersonalizada.get(0);
            actualizarVisualizacionColor();
        }

        generarPaletaColores();
        generarCanvas();
        actualizarPreview();

        System.out.println("✓ Color eliminado de la paleta: " + colorAEliminar);
        actualizarEstado("Color eliminado de la paleta");
    }

    private void actualizarVisualizacionColor() {
        regionColorActual.setStyle("-fx-background-color: " + colorActual + "; " +
                "-fx-border-color: #bdc3c7; -fx-border-width: 2; " +
                "-fx-background-radius: 5; -fx-border-radius: 5;");
        lblColorActual.setText(colorActual);
    }

    @FXML
    private void handleCambiarTamanio(ActionEvent event) {
        String seleccion = cmbTamanio.getValue();
        if (seleccion != null) {
            int tamanio = Integer.parseInt(seleccion.split(" ")[0]);
            cambiarTamanioCanvas(tamanio);
        }
    }

    private void cambiarTamanioCanvas(int tamanio) {
        tamanioActual = tamanio;
        pixelesMarcados.clear();

        lblInfoTamanio.setText("Píxeles totales: " + (tamanio * tamanio));

        generarCanvas();
        generarPreview();
        actualizarEstadisticas();
        actualizarEstado("Canvas redimensionado a " + tamanio + "x" + tamanio);
    }

    private void generarCanvas() {
        gridCanvas.getChildren().clear();
        gridCanvas.setHgap(1);
        gridCanvas.setVgap(1);
        gridCanvas.setStyle("-fx-background-color: #ecf0f1; -fx-padding: 10;");

        double pixelSize = calcularTamanioPixel();

        for (int y = 0; y < tamanioActual; y++) {
            for (int x = 0; x < tamanioActual; x++) {
                Rectangle pixel = crearPixel(x, y, pixelSize);
                gridCanvas.add(pixel, x, y);
            }
        }
    }

    private Rectangle crearPixel(int x, int y, double size) {
        Rectangle pixel = new Rectangle(size, size);
        pixel.setFill(Color.WHITE);
        pixel.setStroke(Color.web("#dcdde1"));
        pixel.setStrokeWidth(chkMostrarGrid.isSelected() ? 1 : 0);
        pixel.setStyle("-fx-cursor: hand;");

        String key = x + "," + y;

        // Eventos de mouse
        pixel.setOnMouseClicked(e -> handleClickPixel(x, y, pixel, e));
        pixel.setOnMouseEntered(e -> {
            if (e.isPrimaryButtonDown()) {
                handleClickPixel(x, y, pixel, e);
            }
        });

        return pixel;
    }

    private void handleClickPixel(int x, int y, Rectangle pixel, MouseEvent e) {
        String key = x + "," + y;

        if (e.getButton() == MouseButton.PRIMARY || e.isPrimaryButtonDown()) {
            if (modoPintar) {
                // Pintar pixel con el color actual
                pixel.setFill(Color.web(colorActual));
                pixelesMarcados.put(key, colorActual); // Guardar el color específico
            } else {
                // Borrar pixel
                pixel.setFill(Color.WHITE);
                pixelesMarcados.remove(key);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            // Clic derecho siempre borra
            pixel.setFill(Color.WHITE);
            pixelesMarcados.remove(key);
        }

        actualizarEstadisticas();
        actualizarPreview();
    }

    private void generarPreview() {
        gridPreview.getChildren().clear();
        gridPreview.setHgap(1);
        gridPreview.setVgap(1);
        gridPreview.setAlignment(Pos.CENTER);

        double previewPixelSize = Math.min(200.0 / tamanioActual, 10);

        for (int y = 0; y < tamanioActual; y++) {
            for (int x = 0; x < tamanioActual; x++) {
                Rectangle pixel = new Rectangle(previewPixelSize, previewPixelSize);

                String key = x + "," + y;
                if (pixelesMarcados.containsKey(key)) {
                    // Usar el color guardado para este píxel específico
                    String colorGuardado = pixelesMarcados.get(key);
                    pixel.setFill(Color.web(colorGuardado));
                } else {
                    pixel.setFill(Color.WHITE);
                }

                pixel.setStroke(Color.web("#dcdde1"));
                pixel.setStrokeWidth(0.5);

                gridPreview.add(pixel, x, y);
            }
        }
    }

    private void actualizarPreview() {
        // Actualizar solo los píxeles cambiados
        for (var node : gridPreview.getChildren()) {
            if (node instanceof Rectangle) {
                Integer colIndex = GridPane.getColumnIndex(node);
                Integer rowIndex = GridPane.getRowIndex(node);

                if (colIndex != null && rowIndex != null) {
                    String key = colIndex + "," + rowIndex;
                    Rectangle pixel = (Rectangle) node;

                    if (pixelesMarcados.containsKey(key)) {
                        // Usar el color guardado para este píxel
                        String colorGuardado = pixelesMarcados.get(key);
                        pixel.setFill(Color.web(colorGuardado));
                    } else {
                        pixel.setFill(Color.WHITE);
                    }
                }
            }
        }
    }

    private double calcularTamanioPixel() {
        // Calcular tamaño óptimo del pixel según el tamaño del canvas
        double maxSize = 600.0; // Tamaño máximo del canvas en píxeles
        return Math.min(maxSize / tamanioActual, 30); // Máximo 30px por cuadrícula
    }

    private void actualizarEstadisticas() {
        int total = tamanioActual * tamanioActual;
        int marcados = pixelesMarcados.size();
        double porcentaje = (marcados * 100.0) / total;

        lblPixelesMarcados.setText("Píxeles marcados: " + marcados + " / " + total);
        lblPorcentajeLleno.setText(String.format("Completado: %.1f%%", porcentaje));
    }

    @FXML
    private void handleToggleGrid(ActionEvent event) {
        boolean mostrar = chkMostrarGrid.isSelected();

        for (var node : gridCanvas.getChildren()) {
            if (node instanceof Rectangle) {
                Rectangle pixel = (Rectangle) node;
                pixel.setStrokeWidth(mostrar ? 1 : 0);
            }
        }

        actualizarEstado(mostrar ? "Grid visible" : "Grid oculto");
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        String nombreDibujo = txtNombreDibujo.getText().trim();

        // Validaciones
        if (nombreDibujo.isEmpty()) {
            mostrarAdvertencia("Nombre requerido", "Por favor ingresa un nombre para el dibujo");
            return;
        }

        if (pixelesMarcados.isEmpty()) {
            mostrarAdvertencia("Canvas vacío", "Debes marcar al menos un píxel en el canvas");
            return;
        }

        try {
            System.out.println("💾 Guardando dibujo: " + nombreDibujo);
            System.out.println("   - Tamaño: " + tamanioActual + "x" + tamanioActual);
            System.out.println("   - Píxeles pintados: " + pixelesMarcados.size());

            // Crear el dibujo básico con GestorArchivoDibujo
            gestorArchivoDibujo.crearDibujo(
                    idUsuarioCreador,
                    nombreDibujo,
                    true,
                    tamanioActual
            );

            // IMPORTANTE: Obtener el dibujo recién creado para agregarle las cuadrículas
            // Como no tenemos un método que retorne el ID generado, buscamos el último dibujo
            // TODO: Mejorar esto agregando un método que retorne el Dibujo creado

            // Por ahora, buscamos el dibujo por nombre (asumiendo que es único)
            Dibujo dibujoCreado = buscarDibujoPorNombre(nombreDibujo);

            if (dibujoCreado != null) {
                System.out.println("✓ Dibujo base creado con ID: " + dibujoCreado.getIdDibujo());

                // Primero, agregar los colores personalizados a la paleta del dibujo
                System.out.println("📝 Agregando paleta personalizada...");
                for (String color : paletaPersonalizada) {
                    if (!dibujoCreado.estaColorEnMap(color)) {
                        try {
                            dibujoCreado.insertarColor(color);
                        } catch (Exception e) {
                            System.err.println("⚠ Error al insertar color " + color + ": " + e.getMessage());
                        }
                    }
                }
                System.out.println("✓ Paleta guardada: " + paletaPersonalizada.size() + " colores");

                // Luego, agregar las cuadrículas pintadas
                int cuadriculasAgregadas = 0;
                for (Map.Entry<String, String> entry : pixelesMarcados.entrySet()) {
                    String[] coords = entry.getKey().split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    String color = entry.getValue();

                    // Crear la cuadrícula y agregarla al dibujo
                    models.Cuadricula cuadricula = new models.Cuadricula(x, y, color);
                    dibujoCreado.ingresarCuadricula(cuadricula);
                    cuadriculasAgregadas++;
                }

                System.out.println("✓ Agregadas " + cuadriculasAgregadas + " cuadrículas");

                // Guardar el dibujo con las cuadrículas y la paleta
                gestorArchivoDibujo.modificarDibujo(dibujoCreado);

                System.out.println("✅ Dibujo guardado completamente: " + nombreDibujo);
                actualizarEstado("Dibujo guardado exitosamente");
                lblUltimaAccion.setText("Último guardado: " + nombreDibujo);

                mostrarExito("Dibujo Creado",
                        "El dibujo '" + nombreDibujo + "' fue creado exitosamente.\n\n" +
                                "Tamaño: " + tamanioActual + "x" + tamanioActual + "\n" +
                                "Píxeles: " + pixelesMarcados.size() + "\n" +
                                "Colores en paleta: " + paletaPersonalizada.size() + "\n" +
                                "ID: " + dibujoCreado.getIdDibujo());

                // Limpiar después de guardar
                limpiarTodo();
            } else {
                System.err.println("❌ No se pudo encontrar el dibujo recién creado");
                mostrarError("Error: No se pudo recuperar el dibujo creado");
            }

        } catch (Exception e) {
            System.err.println("❌ Error al guardar dibujo:");
            e.printStackTrace();
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    /**
     * Busca un dibujo por nombre (workaround temporal)
     * TODO: Mejorar GestorArchivoDibujo para que crearDibujo() retorne el Dibujo creado
     */
    private Dibujo buscarDibujoPorNombre(String nombre) {
        // Intentar buscar entre los IDs recientes
        for (int i = 1; i <= 1000; i++) {
            Dibujo d = gestorArchivoDibujo.buscarDibujoEnLista(i);
            if (d != null && d.getNombreDibujo().equals(nombre)) {
                return d;
            }
        }
        return null;
    }

    @FXML
    private void handleLimpiar(ActionEvent event) {
        Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
        confirmacion.setTitle("Limpiar Canvas");
        confirmacion.setHeaderText("¿Estás seguro?");
        confirmacion.setContentText("Se perderán todos los píxeles marcados en el canvas");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            pixelesMarcados.clear();
            generarCanvas();
            generarPreview();
            actualizarEstadisticas();
            actualizarEstado("Canvas limpiado");
            System.out.println("🗑️ Canvas limpiado");
        }
    }

    private void limpiarTodo() {
        txtNombreDibujo.clear();
        pixelesMarcados.clear();
        generarCanvas();
        generarPreview();
        actualizarEstadisticas();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        if (!pixelesMarcados.isEmpty()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setTitle("Volver");
            confirmacion.setHeaderText("¿Deseas salir?");
            confirmacion.setContentText("Se perderán los cambios no guardados");

            if (confirmacion.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        try {
            // TODO: Volver al MainMenu con los datos del usuario
            System.out.println("← Volviendo al menú principal");
            Stage stage = (Stage) btnVolver.getScene().getWindow();
            stage.close();

        } catch (Exception e) {
            System.err.println("❌ Error al volver:");
            e.printStackTrace();
        }
    }

    private void actualizarEstado(String mensaje) {
        lblEstado.setText("✓ " + mensaje);
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

    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setHeaderText("✓ Operación Exitosa");
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}