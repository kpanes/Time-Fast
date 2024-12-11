package timefast;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;


public class FXMLInicioSesionController implements javafx.fxml.Initializable {

    @FXML
    private TextField txtUsuario;

    @FXML
    private PasswordField txtContrasena;

    @FXML
    private Button btnIniciarSesion;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        btnIniciarSesion.setOnAction(event -> iniciarSesion());
    }

    private void iniciarSesion() {
        String usuario = txtUsuario.getText();
        String contrasena = txtContrasena.getText();

        if (usuario.isEmpty() || contrasena.isEmpty()) {
            mostrarAlerta("Error", "Por favor, ingrese el usuario y la contraseña.");
            return;
        }

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(
                     "SELECT * FROM usuarios WHERE usuario = ? AND contrasena = ?")) {

            statement.setString(1, usuario);
            statement.setString(2, contrasena);

            ResultSet resultSet = statement.executeQuery();

            if (resultSet.next()) {
                mostrarAlerta("Éxito", "Inicio de sesión exitoso.");
                abrirMenuPrincipal();
            } else {
                mostrarAlerta("Error", "Usuario o contraseña incorrectos.");
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo conectar a la base de datos: " + e.getMessage());
        }
    }

    private void abrirMenuPrincipal() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("FXMLMenuPrincipal.fxml"));
            Stage stage = new Stage();
            stage.setScene(new Scene(loader.load()));
            stage.setTitle("Menú Principal");
            stage.show();

            // Cerrar la ventana de inicio de sesión
            Stage ventanaActual = (Stage) btnIniciarSesion.getScene().getWindow();
            ventanaActual.close();

        } catch (Exception e) {
            mostrarAlerta("Error", "No se pudo abrir el menú principal: " + e.getMessage());
        }
    }

    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos"; // Cambia esto
        String usuario = "tu_usuario"; // Cambia esto
        String contrasena = "tu_contrasena"; // Cambia esto
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
