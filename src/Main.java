import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        try {
            // Cargar el archivo FXML de Login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/vistas/LoginView.fxml"));
            Parent root = loader.load();

            // Crear la escena
            Scene scene = new Scene(root, 500, 600);

            // Cargar CSS
            try {
                scene.getStylesheets().add(getClass().getResource("/vistas/recources/styles/login.css").toExternalForm());
                System.out.println("CSS cargado correctamente");
            } catch (Exception e) {
                System.out.println("Advertencia: No se pudo cargar el CSS - " + e.getMessage());
                // Continuar sin CSS
            }

            // Configurar el Stage
            primaryStage.setTitle("Sistema de Dibujo - Login");
            primaryStage.setScene(scene);
            primaryStage.setResizable(false); // Evitar redimensionar
            primaryStage.centerOnScreen();

            // Mostrar la ventana
            primaryStage.show();

            System.out.println("Aplicación iniciada correctamente");

        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación:");
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        System.out.println("Aplicación cerrada");
    }

    public static void main(String[] args) {
        System.out.println("Iniciando Sistema de Dibujo...");
        launch(args);
    }
}