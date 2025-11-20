package controllers;

import javafx.collections.FXCollections;
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
import models.Dibujo;
import models.Cuadricula;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class CreateDibujoController {

    @FXML private Label lblTitulo;
    @FXML private TextField txtNombreDibujo;
    @FXML private ComboBox<String> cmbTamanio;
    @FXML private Label lblInfoTamanio;
    @FXML private ToggleButton btnHerramientaPintar;
    @FXML private ToggleButton btnHerramientaBorrar;
    @FXML private Region regionColorActual;
    @FXML private Label lblColorActual;
    @FXML private GridPane gridPaletaColores;
    @FXML private Label lblCantidadColores;
    @FXML private Button btnAgregarColor;
    @FXML private Label lblPixelesMarcados;
    @FXML private Button btnGuardar;
    @FXML private Button btnLimpiar;
    @FXML private CheckBox chkMostrarGrid;
    @FXML private GridPane gridCanvas;
    @FXML private Button btnVolver;

    private GestorArchivoDibujo gestorArchivoDibujo;
    private int tamanioActual = 16;
    private String colorActual = "#000000";
    private boolean modoPintar = true;
    private Map<String, String> pixelesMarcados;
    private ArrayList<String> paletaPersonalizada;
    private int idUsuarioCreador;

    private boolean modoEdicion = false;
    private int idDibujoEditando = -1;

    @FXML
    public void initialize() {
        gestorArchivoDibujo = new GestorArchivoDibujo();
        pixelesMarcados = new HashMap<>();
        paletaPersonalizada = new ArrayList<>();

        paletaPersonalizada.add("#000000");
        paletaPersonalizada.add("#FFFFFF");

        inicializarControles();
        configurarHerramientas();
        generarPaletaColores();
        cambiarTamanioCanvas(16);
    }

    public void setUsuarioCreador(int idUsuario) {
        this.idUsuarioCreador = idUsuario;
    }

    public void setDibujoParaEditar(Dibujo dibujo) {
        try {
            System.out.println("Cargando dibujo para editar: " + dibujo.getNombreDibujo());

            this.modoEdicion = true;
            this.idDibujoEditando = dibujo.getIdDibujo();

            lblTitulo.setText("Editar: " + dibujo.getNombreDibujo());
            txtNombreDibujo.setText(dibujo.getNombreDibujo());

            int tamanio = dibujo.getAnchoCuadricula();
            System.out.println("Tamaño del dibujo: " + tamanio);

            // Desactivar listener temporalmente
            cmbTamanio.setOnAction(null);
            cmbTamanio.setValue(tamanio + " x " + tamanio);
            tamanioActual = tamanio;
            lblInfoTamanio.setText(tamanio * tamanio + " pixeles");

            // Reactivar listener
            cmbTamanio.setOnAction(this::handleCambiarTamanio);

            // Cargar paleta
            paletaPersonalizada.clear();
            paletaPersonalizada.addAll(dibujo.getClavesColores().values());
            System.out.println("Colores cargados: " + paletaPersonalizada.size());
            generarPaletaColores();

            // Cargar píxeles
            pixelesMarcados.clear();
            for (Cuadricula c : dibujo.getCuadriculas()) {
                String key = c.getIndiceX() + "," + c.getIndiceY();
                pixelesMarcados.put(key, c.getColor());
            }
            System.out.println("Pixeles cargados: " + pixelesMarcados.size());

            // Generar canvas con los píxeles cargados
            generarCanvas();
            actualizarEstadisticas();

            System.out.println("Dibujo cargado correctamente para edicion");

        } catch (Exception e) {
            System.err.println("Error al cargar dibujo para editar: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al cargar dibujo: " + e.getMessage());
        }
    }

    private void inicializarControles() {
        int[] tamanios = GestorLienzo.getTamaniosDisponibles();
        String[] opciones = new String[tamanios.length];
        for (int i = 0; i < tamanios.length; i++) {
            opciones[i] = tamanios[i] + " x " + tamanios[i];
        }
        cmbTamanio.setItems(FXCollections.observableArrayList(opciones));
        cmbTamanio.getSelectionModel().select(1);
        actualizarVisualizacionColor();
    }

    private void configurarHerramientas() {
        ToggleGroup herramientas = new ToggleGroup();
        btnHerramientaPintar.setToggleGroup(herramientas);
        btnHerramientaBorrar.setToggleGroup(herramientas);
        btnHerramientaPintar.setSelected(true);
        modoPintar = true;

        btnHerramientaPintar.selectedProperty().addListener((obs, old, val) -> {
            if (val) modoPintar = true;
        });

        btnHerramientaBorrar.selectedProperty().addListener((obs, old, val) -> {
            if (val) modoPintar = false;
        });
    }

    private void generarPaletaColores() {
        gridPaletaColores.getChildren().clear();

        int col = 0;
        int row = 0;
        int columnas = 5;

        for (int i = 0; i < paletaPersonalizada.size(); i++) {
            String colorHex = paletaPersonalizada.get(i);
            StackPane container = crearColorConBoton(colorHex, i);
            gridPaletaColores.add(container, col, row);

            col++;
            if (col >= columnas) {
                col = 0;
                row++;
            }
        }

        lblCantidadColores.setText(paletaPersonalizada.size() + " colores");
    }

    private StackPane crearColorConBoton(String colorHex, int index) {
        StackPane container = new StackPane();
        container.setPrefSize(45, 45);

        Rectangle rect = new Rectangle(45, 45);
        rect.setFill(Color.web(colorHex));
        rect.setStroke(colorHex.equals(colorActual) ? Color.web("#3498db") : Color.web("#bdc3c7"));
        rect.setStrokeWidth(colorHex.equals(colorActual) ? 3 : 2);
        rect.setStyle("-fx-cursor: hand;");

        rect.setOnMouseClicked(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                seleccionarColor(colorHex);
            } else if (e.getButton() == MouseButton.SECONDARY) {
                cambiarColorDePaleta(index);
            }
        });

        Button btnEliminar = new Button("×");
        btnEliminar.setStyle("-fx-background-color: rgba(231, 76, 60, 0.9); -fx-text-fill: white; " +
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-padding: 0; -fx-min-width: 18px; " +
                "-fx-min-height: 18px; -fx-max-width: 18px; -fx-max-height: 18px; " +
                "-fx-background-radius: 9; -fx-cursor: hand;");
        btnEliminar.setOnAction(e -> eliminarColorDePaleta(index));
        btnEliminar.setVisible(paletaPersonalizada.size() > 2);

        StackPane.setAlignment(btnEliminar, Pos.TOP_RIGHT);
        StackPane.setMargin(btnEliminar, new javafx.geometry.Insets(2, 2, 0, 0));

        container.getChildren().addAll(rect, btnEliminar);
        return container;
    }

    private void seleccionarColor(String colorHex) {
        colorActual = colorHex;
        actualizarVisualizacionColor();
        generarPaletaColores();
    }

    @FXML
    private void handleAgregarColor(ActionEvent event) {
        ColorPicker colorPicker = new ColorPicker(Color.web(colorActual));
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Agregar Color");
        dialog.setHeaderText("Selecciona un color");
        dialog.getDialogPane().setContent(colorPicker);

        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            Color selectedColor = colorPicker.getValue();
            String hexColor = String.format("#%02X%02X%02X",
                    (int)(selectedColor.getRed() * 255),
                    (int)(selectedColor.getGreen() * 255),
                    (int)(selectedColor.getBlue() * 255));

            if (paletaPersonalizada.contains(hexColor)) {
                mostrarAdvertencia("Color duplicado", "Ya existe");
                return;
            }

            paletaPersonalizada.add(hexColor);
            generarPaletaColores();
        }
    }

    private void cambiarColorDePaleta(int index) {
        String colorActualPaleta = paletaPersonalizada.get(index);
        ColorPicker colorPicker = new ColorPicker(Color.web(colorActualPaleta));
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        dialog.setTitle("Cambiar Color");
        dialog.setHeaderText("Cambiar " + colorActualPaleta);
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

            for (Map.Entry<String, String> entry : pixelesMarcados.entrySet()) {
                if (entry.getValue().equals(colorAnterior)) {
                    entry.setValue(hexColor);
                }
            }

            if (colorActual.equals(colorAnterior)) {
                colorActual = hexColor;
                actualizarVisualizacionColor();
            }

            generarPaletaColores();
            generarCanvas();
        }
    }

    private void eliminarColorDePaleta(int index) {
        if (paletaPersonalizada.size() <= 2) {
            mostrarAdvertencia("Error", "Minimo 2 colores");
            return;
        }

        String colorAEliminar = paletaPersonalizada.get(index);
        boolean colorEnUso = pixelesMarcados.values().stream()
                .anyMatch(c -> c.equals(colorAEliminar));

        if (colorEnUso) {
            Alert confirmacion = new Alert(Alert.AlertType.WARNING);
            confirmacion.setTitle("Color en uso");
            confirmacion.setContentText("Los pixeles se cambiaran al primer color. Continuar?");

            if (confirmacion.showAndWait().get() != ButtonType.OK) return;

            String colorReemplazo = paletaPersonalizada.get(0);
            for (Map.Entry<String, String> entry : pixelesMarcados.entrySet()) {
                if (entry.getValue().equals(colorAEliminar)) {
                    entry.setValue(colorReemplazo);
                }
            }
        }

        paletaPersonalizada.remove(index);

        if (colorActual.equals(colorAEliminar)) {
            colorActual = paletaPersonalizada.get(0);
            actualizarVisualizacionColor();
        }

        generarPaletaColores();
        generarCanvas();
    }

    private void actualizarVisualizacionColor() {
        regionColorActual.setStyle("-fx-background-color: " + colorActual + "; -fx-border-color: #999; -fx-border-width: 2;");
        lblColorActual.setText(colorActual);
    }

    @FXML
    private void handleCambiarTamanio(ActionEvent event) {
        // No permitir cambio de tamaño en modo edición
        if (modoEdicion) {
            Alert alerta = new Alert(Alert.AlertType.WARNING);
            alerta.setTitle("Accion no permitida");
            alerta.setContentText("No se puede cambiar el tamaño en modo edicion");
            alerta.showAndWait();

            // Restaurar el tamaño original
            cmbTamanio.setValue(tamanioActual + " x " + tamanioActual);
            return;
        }

        String seleccion = cmbTamanio.getValue();
        if (seleccion != null) {
            int tamanio = Integer.parseInt(seleccion.split(" ")[0]);
            cambiarTamanioCanvas(tamanio);
        }
    }

    private void cambiarTamanioCanvas(int tamanio) {
        tamanioActual = tamanio;
        pixelesMarcados.clear();
        lblInfoTamanio.setText(tamanio * tamanio + " pixeles");
        generarCanvas();
        actualizarEstadisticas();
    }

    private void generarCanvas() {
        gridCanvas.getChildren().clear();
        gridCanvas.setHgap(1);
        gridCanvas.setVgap(1);
        gridCanvas.setStyle("-fx-background-color: #ccc;");

        double pixelSize = Math.min(500.0 / tamanioActual, 25);

        for (int y = 0; y < tamanioActual; y++) {
            for (int x = 0; x < tamanioActual; x++) {
                Rectangle pixel = crearPixel(x, y, pixelSize);
                gridCanvas.add(pixel, x, y);
            }
        }
    }

    private Rectangle crearPixel(int x, int y, double size) {
        Rectangle pixel = new Rectangle(size, size);

        String key = x + "," + y;
        if (pixelesMarcados.containsKey(key)) {
            pixel.setFill(Color.web(pixelesMarcados.get(key)));
        } else {
            pixel.setFill(Color.WHITE);
        }

        pixel.setStroke(Color.web("#ddd"));
        pixel.setStrokeWidth(chkMostrarGrid.isSelected() ? 1 : 0);
        pixel.setStyle("-fx-cursor: hand;");

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
                pixel.setFill(Color.web(colorActual));
                pixelesMarcados.put(key, colorActual);
            } else {
                pixel.setFill(Color.WHITE);
                pixelesMarcados.remove(key);
            }
        } else if (e.getButton() == MouseButton.SECONDARY) {
            pixel.setFill(Color.WHITE);
            pixelesMarcados.remove(key);
        }

        actualizarEstadisticas();
    }

    private void actualizarEstadisticas() {
        int total = tamanioActual * tamanioActual;
        int marcados = pixelesMarcados.size();
        lblPixelesMarcados.setText(marcados + " / " + total + " pixeles marcados");
    }

    @FXML
    private void handleToggleGrid(ActionEvent event) {
        boolean mostrar = chkMostrarGrid.isSelected();
        for (var node : gridCanvas.getChildren()) {
            if (node instanceof Rectangle) {
                ((Rectangle) node).setStrokeWidth(mostrar ? 1 : 0);
            }
        }
    }

    @FXML
    private void handleGuardar(ActionEvent event) {
        String nombreDibujo = txtNombreDibujo.getText().trim();

        if (nombreDibujo.isEmpty()) {
            mostrarAdvertencia("Error", "Ingresa un nombre");
            return;
        }

        if (pixelesMarcados.isEmpty()) {
            mostrarAdvertencia("Error", "Canvas vacio");
            return;
        }

        try {
            if (modoEdicion) {
                System.out.println("Guardando cambios en modo edicion...");

                // ELIMINAR el dibujo viejo
                gestorArchivoDibujo.eliminarDibujo(idDibujoEditando);
                Thread.sleep(100);

                // CREAR uno nuevo con los mismos datos
                gestorArchivoDibujo.crearDibujo(idUsuarioCreador, nombreDibujo, true, tamanioActual);
                Thread.sleep(100);

                // Recargar gestor
                gestorArchivoDibujo = new GestorArchivoDibujo();
                Dibujo dibujoNuevo = buscarDibujoPorNombre(nombreDibujo);

                if (dibujoNuevo == null) {
                    mostrarError("Error al actualizar el dibujo");
                    return;
                }

                System.out.println("Nuevo dibujo creado con ID: " + dibujoNuevo.getIdDibujo());

                // Agregar paleta
                System.out.println("Agregando " + paletaPersonalizada.size() + " colores...");
                for (String color : paletaPersonalizada) {
                    try {
                        dibujoNuevo.insertarColor(color);
                    } catch (Exception e) {
                        System.err.println("Error insertando color: " + e.getMessage());
                    }
                }

                // Agregar cuadriculas
                System.out.println("Agregando " + pixelesMarcados.size() + " cuadriculas...");
                for (Map.Entry<String, String> entry : pixelesMarcados.entrySet()) {
                    String[] coords = entry.getKey().split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    dibujoNuevo.ingresarCuadricula(new Cuadricula(x, y, entry.getValue()));
                }

                gestorArchivoDibujo.modificarDibujo(dibujoNuevo);
                System.out.println("Dibujo actualizado correctamente");

                mostrarExito("Exito", "Dibujo actualizado correctamente");
                cerrarVentana();

            } else {
                System.out.println("Creando nuevo dibujo...");

                gestorArchivoDibujo.crearDibujo(idUsuarioCreador, nombreDibujo, true, tamanioActual);
                Thread.sleep(100);

                gestorArchivoDibujo = new GestorArchivoDibujo();
                Dibujo dibujoCreado = buscarDibujoPorNombre(nombreDibujo);

                if (dibujoCreado == null) {
                    mostrarError("No se pudo crear el dibujo");
                    return;
                }

                System.out.println("Dibujo creado con ID: " + dibujoCreado.getIdDibujo());

                for (String color : paletaPersonalizada) {
                    try {
                        dibujoCreado.insertarColor(color);
                    } catch (Exception e) {
                        System.err.println("Error insertando color: " + e.getMessage());
                    }
                }

                for (Map.Entry<String, String> entry : pixelesMarcados.entrySet()) {
                    String[] coords = entry.getKey().split(",");
                    int x = Integer.parseInt(coords[0]);
                    int y = Integer.parseInt(coords[1]);
                    dibujoCreado.ingresarCuadricula(new Cuadricula(x, y, entry.getValue()));
                }

                gestorArchivoDibujo.modificarDibujo(dibujoCreado);
                mostrarExito("Exito", "Dibujo creado");
                limpiarTodo();
            }

        } catch (Exception e) {
            System.err.println("Error al guardar: " + e.getMessage());
            e.printStackTrace();
            mostrarError("Error al guardar: " + e.getMessage());
        }
    }

    private Dibujo buscarDibujoPorNombre(String nombre) {
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
        confirmacion.setContentText("Limpiar canvas?");

        if (confirmacion.showAndWait().get() == ButtonType.OK) {
            pixelesMarcados.clear();
            generarCanvas();
            actualizarEstadisticas();
        }
    }

    private void limpiarTodo() {
        txtNombreDibujo.clear();
        pixelesMarcados.clear();
        generarCanvas();
        actualizarEstadisticas();
    }

    @FXML
    private void handleVolver(ActionEvent event) {
        if (!pixelesMarcados.isEmpty()) {
            Alert confirmacion = new Alert(Alert.AlertType.CONFIRMATION);
            confirmacion.setContentText("Salir sin guardar?");

            if (confirmacion.showAndWait().get() != ButtonType.OK) {
                return;
            }
        }

        cerrarVentana();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) btnVolver.getScene().getWindow();
        stage.close();
    }

    private void mostrarError(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarAdvertencia(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void mostrarExito(String titulo, String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(titulo);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}