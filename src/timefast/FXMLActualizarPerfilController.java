package timefast;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;

public class FXMLActualizarPerfilController implements Initializable {

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnActualizar;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Asignar funcionalidad al botón
        btnActualizar.setOnAction(event -> actualizarPerfil());
    }

    private void actualizarPerfil() {
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();

        if (nombre.isEmpty() || email.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE usuarios SET nombre = ?, email = ? WHERE id = ?")) {

            // Supongamos que "id" se obtiene de algún lugar en tu aplicación.
            int userId = 1; // Cambiar según corresponda

            statement.setString(1, nombre);
            statement.setString(2, email);
            statement.setInt(3, userId);

            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas > 0) {
                mostrarAlerta("Éxito", "Perfil actualizado correctamente.");
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el perfil.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
            mostrarAlerta("Error", "Hubo un problema al conectar con la base de datos.");
        }
    }

    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos";
        String usuario = "tu_usuario";
        String contraseña = "tu_contraseña";
        return DriverManager.getConnection(url, usuario, contraseña);
    }

    private void mostrarAlerta(String titulo, String contenido) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}
