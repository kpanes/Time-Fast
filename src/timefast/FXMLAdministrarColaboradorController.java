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
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;

public class FXMLAdministrarColaboradorController implements Initializable {

    @FXML
    private TextField txtId;

    @FXML
    private TextField txtNombre;

    @FXML
    private TextField txtEmail;

    @FXML
    private Button btnAgregar;

    @FXML
    private Button btnEditar;

    @FXML
    private Button btnEliminar;

    @FXML
    private TableView<Colaborador> tablaColaboradores;

    @FXML
    private TableColumn<Colaborador, Integer> colId;

    @FXML
    private TableColumn<Colaborador, String> colNombre;

    @FXML
    private TableColumn<Colaborador, String> colEmail;

    private ObservableList<Colaborador> listaColaboradores;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaColaboradores = FXCollections.observableArrayList();
        inicializarTabla();
        cargarDatos();

        btnAgregar.setOnAction(event -> agregarColaborador());
        btnEditar.setOnAction(event -> editarColaborador());
        btnEliminar.setOnAction(event -> eliminarColaborador());
    }

    private void inicializarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colNombre.setCellValueFactory(new PropertyValueFactory<>("nombre"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));

        tablaColaboradores.setItems(listaColaboradores);
    }

    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos"; // Cambia esto
        String usuario = "tu_usuario"; // Cambia esto
        String contraseña = "tu_contraseña"; // Cambia esto
        return DriverManager.getConnection(url, usuario, contraseña);
    }

    private void cargarDatos() {
        listaColaboradores.clear();
        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM colaboradores");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                listaColaboradores.add(new Colaborador(
                        resultSet.getInt("id"),
                        resultSet.getString("nombre"),
                        resultSet.getString("email")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void agregarColaborador() {
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();

        if (nombre.isEmpty() || email.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(
                     "INSERT INTO colaboradores (nombre, email) VALUES (?, ?)")) {

            statement.setString(1, nombre);
            statement.setString(2, email);

            statement.executeUpdate();
            mostrarAlerta("Éxito", "Colaborador agregado correctamente.");
            cargarDatos();

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo agregar el colaborador: " + e.getMessage());
        }
    }

    private void editarColaborador() {
        String idTexto = txtId.getText();
        String nombre = txtNombre.getText();
        String email = txtEmail.getText();

        if (idTexto.isEmpty() || nombre.isEmpty() || email.isEmpty()) {
            mostrarAlerta("Error", "Todos los campos son obligatorios.");
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);

            try (Connection connection = obtenerConexion();
                 PreparedStatement statement = connection.prepareStatement(
                         "UPDATE colaboradores SET nombre = ?, email = ? WHERE id = ?")) {

                statement.setString(1, nombre);
                statement.setString(2, email);
                statement.setInt(3, id);

                int filasActualizadas = statement.executeUpdate();
                if (filasActualizadas > 0) {
                    mostrarAlerta("Éxito", "Colaborador actualizado correctamente.");
                    cargarDatos();
                } else {
                    mostrarAlerta("Error", "No se encontró el colaborador.");
                }

            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID inválido.");
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo editar el colaborador: " + e.getMessage());
        }
    }

    private void eliminarColaborador() {
        String idTexto = txtId.getText();

        if (idTexto.isEmpty()) {
            mostrarAlerta("Error", "El ID es obligatorio.");
            return;
        }

        try {
            int id = Integer.parseInt(idTexto);

            try (Connection connection = obtenerConexion();
                 PreparedStatement statement = connection.prepareStatement(
                         "DELETE FROM colaboradores WHERE id = ?")) {

                statement.setInt(1, id);

                int filasEliminadas = statement.executeUpdate();
                if (filasEliminadas > 0) {
                    mostrarAlerta("Éxito", "Colaborador eliminado correctamente.");
                    cargarDatos();
                } else {
                    mostrarAlerta("Error", "No se encontró el colaborador.");
                }
            }

        } catch (NumberFormatException e) {
            mostrarAlerta("Error", "ID inválido.");
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo eliminar el colaborador: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}
