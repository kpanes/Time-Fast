package timefast;

import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLConsultarEnviosController implements Initializable {

    @FXML
    private TableView<Envio> tablaEnvios;

    @FXML
    private TableColumn<Envio, Integer> colId;

    @FXML
    private TableColumn<Envio, String> colCliente;

    @FXML
    private TableColumn<Envio, String> colFecha;

    @FXML
    private TableColumn<Envio, String> colEstado;

    @FXML
    private Button btnRefrescar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnDetalles;

    private ObservableList<Envio> listaEnvios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaEnvios = FXCollections.observableArrayList();
        inicializarTabla();
        cargarDatos();

        btnRefrescar.setOnAction(event -> cargarDatos());
        btnEliminar.setOnAction(event -> eliminarEnvio());
        btnDetalles.setOnAction(event -> verDetallesEnvio());
    }

    private void inicializarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colCliente.setCellValueFactory(new PropertyValueFactory<>("cliente"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaEnvios.setItems(listaEnvios);
    }

    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos"; // Cambia esto
        String usuario = "tu_usuario"; // Cambia esto
        String contraseña = "tu_contraseña"; // Cambia esto
        return DriverManager.getConnection(url, usuario, contraseña);
    }

    private void cargarDatos() {
        listaEnvios.clear();
        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM envios");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                listaEnvios.add(new Envio(
                        resultSet.getInt("id"),
                        resultSet.getString("cliente"),
                        resultSet.getString("fecha"),
                        resultSet.getString("estado")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void eliminarEnvio() {
        Envio envioSeleccionado = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un envío para eliminar.");
            return;
        }

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement("DELETE FROM envios WHERE id = ?")) {

            statement.setInt(1, envioSeleccionado.getId());
            int filasEliminadas = statement.executeUpdate();

            if (filasEliminadas > 0) {
                mostrarAlerta("Éxito", "Envío eliminado correctamente.");
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo eliminar el envío.");
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo eliminar el envío: " + e.getMessage());
        }
    }

    private void verDetallesEnvio() {
        Envio envioSeleccionado = tablaEnvios.getSelectionModel().getSelectedItem();
        if (envioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un envío para ver los detalles.");
            return;
        }

        // Aquí puedes abrir una nueva pantalla o mostrar un diálogo con los detalles
        mostrarAlerta("Detalles del Envío",
                "ID: " + envioSeleccionado.getId() +
                "\nCliente: " + envioSeleccionado.getCliente() +
                "\nFecha: " + envioSeleccionado.getFecha() +
                "\nEstado: " + envioSeleccionado.getEstado());
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}    