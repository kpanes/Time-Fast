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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLModuloEnviosController implements javafx.fxml.Initializable {

    @FXML
    private TableView<Envio> tablaEnvios;

    @FXML
    private TableColumn<Envio, Integer> colId;

    @FXML
    private TableColumn<Envio, String> colDestino;

    @FXML
    private TableColumn<Envio, String> colFecha;

    @FXML
    private TableColumn<Envio, String> colEstado;

    @FXML
    private TextField txtDestino;

    @FXML
    private TextField txtFecha;

    @FXML
    private TextField txtEstado;

    @FXML
    private TextField txtId;

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private Button btnRefrescar;

    private ObservableList<Envio> listaEnvios;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaEnvios = FXCollections.observableArrayList();
        inicializarTabla();
        cargarDatos();

        btnAgregar.setOnAction(event -> agregarEnvio());
        btnEditar.setOnAction(event -> editarEnvio());
        btnEliminar.setOnAction(event -> eliminarEnvio());
        btnRefrescar.setOnAction(event -> cargarDatos());
    }

    private void inicializarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colDestino.setCellValueFactory(new PropertyValueFactory<>("destino"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));

        tablaEnvios.setItems(listaEnvios);
    }

    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos"; // Cambia esto
        String usuario = "tu_usuario"; // Cambia esto
        String contrasena = "tu_contrasena"; // Cambia esto
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private void cargarDatos() {
        listaEnvios.clear();
        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM envios");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                listaEnvios.add(new Envio(
                        resultSet.getInt("id"),
                        resultSet.getString("destino"),
                        resultSet.getString("fecha"),
                        resultSet.getString("estado")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void agregarEnvio() {
        String destino = txtDestino.getText();
        String fecha = txtFecha.getText();
        String estado = txtEstado.getText();

        if (destino.isEmpty() || fecha.isEmpty() || estado.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO envios (destino, fecha, estado) VALUES (?, ?, ?)")) {

            statement.setString(1, destino);
            statement.setString(2, fecha);
            statement.setString(3, estado);

            statement.executeUpdate();
            mostrarAlerta("Éxito", "Envío agregado correctamente.");
            cargarDatos();

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo agregar el envío: " + e.getMessage());
        }
    }

    private void editarEnvio() {
        String idTexto = txtId.getText();
        String destino = txtDestino.getText();
        String fecha = txtFecha.getText();
        String estado = txtEstado.getText();

        if (idTexto.isEmpty() || destino.isEmpty() || fecha.isEmpty() || estado.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);

            try (Connection connection = obtenerConexion();
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE envios SET destino = ?, fecha = ?, estado = ? WHERE id = ?")) {

                statement.setString(1, destino);
                statement.setString(2, fecha);
                statement.setString(3, estado);
                statement.setInt(4, id);

                int filasActualizadas = statement.executeUpdate();
                if (filasActualizadas > 0) {
                    mostrarAlerta("Éxito", "Envío actualizado correctamente.");
                    cargarDatos();
                } else {
                    mostrarAlerta("Error", "No se encontró el envío.");
                }
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID inválido.");
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo actualizar el envío: " + e.getMessage());
        }
    }

    private void eliminarEnvio() {
        String idTexto = txtId.getText();

        if (idTexto.isEmpty()) {
            mostrarAlerta("Error", "El ID es obligatorio.");
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);

            try (Connection connection = obtenerConexion();
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM envios WHERE id = ?")) {

                statement.setInt(1, id);

                int filasEliminadas = statement.executeUpdate();
                if (filasEliminadas > 0) {
                    mostrarAlerta("Éxito", "Envío eliminado correctamente.");
                    cargarDatos();
                } else {
                    mostrarAlerta("Error", "No se encontró el envío.");
                }
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID inválido.");
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo eliminar el envío: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

class Envio {
    private int id;
    private String destino;
    private String fecha;
    private String estado;

    public Envio(int id, String destino, String fecha, String estado) {
        this.id = id;
        this.destino = destino;
        this.fecha = fecha;
        this.estado = estado;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDestino() {
        return destino;
    }

    public void setDestino(String destino) {
        this.destino = destino;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }
}
