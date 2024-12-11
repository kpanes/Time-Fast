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

public class FXMLEnviosAsignadosController implements Initializable {

    @FXML
    private TableView<EnvioAsignado> tablaEnviosAsignados;

    @FXML
    private TableColumn<EnvioAsignado, Integer> colId;

    @FXML
    private TableColumn<EnvioAsignado, String> colUsuario;

    @FXML
    private TableColumn<EnvioAsignado, String> colEstado;

    @FXML
    private TableColumn<EnvioAsignado, String> colFecha;

    @FXML
    private TextField txtUsuarioFiltro;

    @FXML
    private TextField txtEstadoFiltro;

    @FXML
    private Button btnBuscar;

    @FXML
    private Button btnActualizarEstado;

    @FXML
    private Button btnRefrescar;

    private ObservableList<EnvioAsignado> listaEnviosAsignados;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        listaEnviosAsignados = FXCollections.observableArrayList();
        inicializarTabla();
        cargarDatos();

        btnBuscar.setOnAction(event -> buscarEnvios());
        btnActualizarEstado.setOnAction(event -> actualizarEstadoEnvio());
        btnRefrescar.setOnAction(event -> cargarDatos());
    }

    private void inicializarTabla() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colUsuario.setCellValueFactory(new PropertyValueFactory<>("usuario"));
        colEstado.setCellValueFactory(new PropertyValueFactory<>("estado"));
        colFecha.setCellValueFactory(new PropertyValueFactory<>("fecha"));

        tablaEnviosAsignados.setItems(listaEnviosAsignados);
    }

    private Connection obtenerConexion() throws SQLException {
        String url = "jdbc:mysql://localhost:3306/tu_base_de_datos"; // Cambia esto
        String usuario = "tu_usuario"; // Cambia esto
        String contrasena = "tu_contrasena"; // Cambia esto
        return DriverManager.getConnection(url, usuario, contrasena);
    }

    private void cargarDatos() {
        listaEnviosAsignados.clear();
        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement("SELECT * FROM envios_asignados");
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                listaEnviosAsignados.add(new EnvioAsignado(
                        resultSet.getInt("id"),
                        resultSet.getString("usuario"),
                        resultSet.getString("estado"),
                        resultSet.getString("fecha")
                ));
            }
        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron cargar los datos: " + e.getMessage());
        }
    }

    private void buscarEnvios() {
        String usuarioFiltro = txtUsuarioFiltro.getText();
        String estadoFiltro = txtEstadoFiltro.getText();

        listaEnviosAsignados.clear();

        String consulta = "SELECT * FROM envios_asignados WHERE 1=1";
        if (!usuarioFiltro.isEmpty()) {
            consulta += " AND usuario LIKE ?";
        }
        if (!estadoFiltro.isEmpty()) {
            consulta += " AND estado LIKE ?";
        }

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(consulta)) {

            int indice = 1;
            if (!usuarioFiltro.isEmpty()) {
                statement.setString(indice++, "%" + usuarioFiltro + "%");
            }
            if (!estadoFiltro.isEmpty()) {
                statement.setString(indice++, "%" + estadoFiltro + "%");
            }

            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                listaEnviosAsignados.add(new EnvioAsignado(
                        resultSet.getInt("id"),
                        resultSet.getString("usuario"),
                        resultSet.getString("estado"),
                        resultSet.getString("fecha")
                ));
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudieron buscar los envíos: " + e.getMessage());
        }
    }

    private void actualizarEstadoEnvio() {
        EnvioAsignado envioSeleccionado = tablaEnviosAsignados.getSelectionModel().getSelectedItem();
        if (envioSeleccionado == null) {
            mostrarAlerta("Advertencia", "Debe seleccionar un envío para actualizar su estado.");
            return;
        }

        try (Connection connection = obtenerConexion();
             PreparedStatement statement = connection.prepareStatement(
                     "UPDATE envios_asignados SET estado = ? WHERE id = ?")) {

            String nuevoEstado = "En Progreso"; // Cambia según tu lógica
            statement.setString(1, nuevoEstado);
            statement.setInt(2, envioSeleccionado.getId());

            int filasActualizadas = statement.executeUpdate();
            if (filasActualizadas > 0) {
                mostrarAlerta("Éxito", "Estado del envío actualizado.");
                cargarDatos();
            } else {
                mostrarAlerta("Error", "No se pudo actualizar el estado del envío.");
            }

        } catch (SQLException e) {
            mostrarAlerta("Error", "No se pudo actualizar el estado del envío: " + e.getMessage());
        }
    }

    private void mostrarAlerta(String titulo, String mensaje) {
        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
        alerta.setTitle(titulo);
        alerta.setContentText(mensaje);
        alerta.showAndWait();
    }
}

class EnvioAsignado {
    private int id;
    private String usuario;
    private String estado;
    private String fecha;

    public EnvioAsignado(int id, String usuario, String estado, String fecha) {
        this.id = id;
        this.usuario = usuario;
        this.estado = estado;
        this.fecha = fecha;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getFecha() {
        return fecha;
    }

    public void setFecha(String fecha) {
        this.fecha = fecha;
    }
}
