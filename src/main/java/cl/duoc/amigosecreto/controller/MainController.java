package cl.duoc.amigosecreto.controller;

import cl.duoc.amigosecreto.dao.MySQLParticipanteDAO;
import cl.duoc.amigosecreto.dao.ParticipanteDAO;
import cl.duoc.amigosecreto.dao.PersistenciaException;
import cl.duoc.amigosecreto.model.Adulto;
import cl.duoc.amigosecreto.model.Nino;
import cl.duoc.amigosecreto.model.Participante;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

/**
 * Controlador de la ventana principal: formulario, tabla, sorteo y área de mensajes.
 * Accede a los datos únicamente a través de la interfaz ParticipanteDAO.
 */
public class MainController {

    // =====================================================================
    // EA3: asigna aquí tu implementación de ParticipanteDAO
    //      (por ejemplo: = new MiImplementacionDAO();).
    //      No es necesario modificar ninguna otra parte de esta clase.
    // =====================================================================
    private final ParticipanteDAO dao = new MySQLParticipanteDAO();

    private static final Locale LOCALE_CL = Locale.forLanguageTag("es-CL");
    private static final int MINIMO_SORTEO = 3;

    @FXML private ComboBox<String> cbTipo;
    @FXML private TextField txtNombre;
    @FXML private TextField txtEdad;
    @FXML private TextField txtSugerencia;
    @FXML private CheckBox chkTrabajadorActivo;
    @FXML private TextField txtNombreTutor;
    @FXML private Button btnRegistrar;
    @FXML private Button btnCancelar;

    @FXML private TableView<Participante> tabla;
    @FXML private TableColumn<Participante, Integer> colId;
    @FXML private TableColumn<Participante, String> colTipo;
    @FXML private TableColumn<Participante, String> colNombre;
    @FXML private TableColumn<Participante, Integer> colEdad;
    @FXML private TableColumn<Participante, String> colDetalle;
    @FXML private TableColumn<Participante, String> colPresupuesto;
    @FXML private TableColumn<Participante, String> colSorteo;

    @FXML private Label lblMensaje;

    /** Participante que se está editando; null cuando el formulario registra uno nuevo. */
    private Participante enEdicion;

    /** true mientras el área de mensajes muestra un amigo secreto (se borra al cambiar de fila). */
    private boolean mostrandoSecreto;

    @FXML
    private void initialize() {
        cbTipo.setItems(FXCollections.observableArrayList(Adulto.TIPO, Nino.TIPO));
        cbTipo.valueProperty().addListener((obs, anterior, actual) -> actualizarCamposPorTipo());

        colId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colTipo.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getTipo()));
        colNombre.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getNombre()));
        colEdad.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getEdad()));
        colDetalle.setCellValueFactory(c -> new SimpleStringProperty(detalle(c.getValue())));
        colPresupuesto.setCellValueFactory(c ->
                new SimpleStringProperty(formatearMonto(c.getValue().calcularPresupuesto())));
        colSorteo.setCellValueFactory(c ->
                new SimpleStringProperty(c.getValue().tieneAmigoSecreto() ? "Asignado" : "Pendiente"));
        tabla.setPlaceholder(new Label("No hay participantes para mostrar."));
        tabla.getSelectionModel().selectedItemProperty().addListener((obs, anterior, actual) -> {
            if (mostrandoSecreto) {
                mostrarInfo("Amigo secreto oculto. Selecciona tu nombre y presiona Ver amigo secreto.");
            }
        });

        limpiarFormulario();
        cargarTabla();
    }

    // ---------------------------------------------------------------- acciones

    @FXML
    private void onRegistrar() {
        if (!hayDao()) {
            return;
        }
        Participante participante;
        try {
            participante = crearDesdeFormulario();
        } catch (IllegalArgumentException e) {
            mostrarError(e.getMessage());
            return;
        }

        try {
            if (enEdicion == null) {
                dao.insertar(participante);
                cargarTabla();
                limpiarFormulario();
                mostrarExito(describir(participante) + " registrado correctamente.");
            } else {
                participante.setId(enEdicion.getId());
                participante.setIdAmigoSecreto(enEdicion.getIdAmigoSecreto());
                dao.actualizar(participante);
                cargarTabla();
                limpiarFormulario();
                mostrarExito(describir(participante) + " actualizado correctamente.");
            }
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onEditar() {
        Participante seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un participante de la tabla para editar.");
            return;
        }
        enEdicion = seleccionado;
        cbTipo.setValue(seleccionado.getTipo());
        cbTipo.setDisable(true);
        txtNombre.setText(seleccionado.getNombre());
        txtEdad.setText(String.valueOf(seleccionado.getEdad()));
        txtSugerencia.setText(seleccionado.getSugerenciaRegalo());
        if (seleccionado instanceof Adulto adulto) {
            chkTrabajadorActivo.setSelected(adulto.isTrabajadorActivo());
        } else if (seleccionado instanceof Nino nino) {
            txtNombreTutor.setText(nino.getNombreTutor());
        }
        btnRegistrar.setText("Guardar cambios");
        btnCancelar.setDisable(false);
        txtNombre.requestFocus();
        mostrarInfo("Editando " + describir(seleccionado)
                + ". Modifica los datos y presiona Guardar cambios.");
    }

    @FXML
    private void onCancelar() {
        limpiarFormulario();
        mostrarInfo("Edición cancelada.");
    }

    @FXML
    private void onEliminar() {
        if (!hayDao()) {
            return;
        }
        Participante seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona un participante de la tabla para eliminar.");
            return;
        }
        try {
            dao.eliminar(seleccionado.getId());
            if (enEdicion != null && enEdicion.getId() == seleccionado.getId()) {
                limpiarFormulario();
            }
            cargarTabla();
            if (seleccionado.tieneAmigoSecreto()) {
                mostrarInfo(describir(seleccionado) + " eliminado. El sorteo quedó incompleto: "
                        + "vuelve a presionar Sortear.");
            } else {
                mostrarExito(describir(seleccionado) + " eliminado correctamente.");
            }
        } catch (PersistenciaException e) {
            mostrarError(e.getMessage());
        }
    }

    /**
     * Asigna a cada participante un amigo secreto distinto de sí mismo (todos regalan
     * y todos reciben un regalo) y guarda cada asignación con dao.actualizar.
     */
    @FXML
    private void onSortear() {
        if (!hayDao()) {
            return;
        }
        try {
            List<Participante> participantes = new ArrayList<>(dao.obtenerTodos());
            if (participantes.size() < MINIMO_SORTEO) {
                mostrarError("Se necesitan al menos " + MINIMO_SORTEO + " participantes para sortear.");
                return;
            }
            // Se ordena al azar y cada uno le regala al siguiente; el último, al primero.
            Collections.shuffle(participantes);
            for (int i = 0; i < participantes.size(); i++) {
                Participante quienRegala = participantes.get(i);
                Participante quienRecibe = participantes.get((i + 1) % participantes.size());
                quienRegala.setIdAmigoSecreto(quienRecibe.getId());
                dao.actualizar(quienRegala);
            }
            limpiarFormulario();
            cargarTabla();
            mostrarExito("Sorteo realizado para " + participantes.size() + " participantes. "
                    + "Cada persona selecciona su nombre y presiona Ver amigo secreto.");
        } catch (PersistenciaException e) {
            cargarTabla();
            mostrarError(e.getMessage());
        }
    }

    @FXML
    private void onVerAmigoSecreto() {
        Participante seleccionado = tabla.getSelectionModel().getSelectedItem();
        if (seleccionado == null) {
            mostrarError("Selecciona tu nombre en la tabla para ver a quién le regalas.");
            return;
        }
        if (!seleccionado.tieneAmigoSecreto()) {
            mostrarError(seleccionado.getNombre() + " aún no tiene amigo secreto asignado. Presiona Sortear.");
            return;
        }
        Participante amigo = buscarEnTabla(seleccionado.getIdAmigoSecreto());
        if (amigo == null) {
            mostrarError("No se encontró el amigo secreto de " + seleccionado.getNombre()
                    + ". Vuelve a presionar Sortear.");
            return;
        }
        String sugerencia = amigo.getSugerenciaRegalo().isEmpty()
                ? "sin sugerencia" : "sugerencia: " + amigo.getSugerenciaRegalo();
        mostrarInfo(seleccionado.getNombre() + ", te toca regalarle a " + amigo.getNombre()
                + " (" + amigo.getTipo().toLowerCase(LOCALE_CL) + ", " + amigo.getEdad() + " años; "
                + sugerencia + "). Tu presupuesto es de "
                + formatearMonto(seleccionado.calcularPresupuesto()) + ".");
        mostrandoSecreto = true;
    }

    // ---------------------------------------------------------------- apoyo

    /** Vuelve a leer todos los participantes desde el almacenamiento y los muestra en la tabla. */
    private void cargarTabla() {
        if (!hayDao()) {
            tabla.getItems().clear();
            return;
        }
        try {
            tabla.setItems(FXCollections.observableArrayList(dao.obtenerTodos()));
        } catch (PersistenciaException e) {
            tabla.getItems().clear();
            mostrarError("Error al leer la información: " + e.getMessage());
        }
    }

    private Participante buscarEnTabla(int id) {
        for (Participante p : tabla.getItems()) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }

    private boolean hayDao() {
        if (dao == null) {
            mostrarError("Aún no hay una implementación de ParticipanteDAO asignada en MainController.");
            return false;
        }
        return true;
    }

    private Participante crearDesdeFormulario() {
        String tipo = cbTipo.getValue();
        if (tipo == null) {
            throw new IllegalArgumentException("Selecciona el tipo.");
        }
        String nombre = txtNombre.getText();
        int edad = leerEntero(txtEdad.getText(), "Edad: debe ser un número entero.");
        String sugerencia = txtSugerencia.getText();

        if (Adulto.TIPO.equals(tipo)) {
            return new Adulto(nombre, edad, sugerencia, chkTrabajadorActivo.isSelected());
        }
        return new Nino(nombre, edad, sugerencia, txtNombreTutor.getText());
    }

    private int leerEntero(String texto, String mensajeError) {
        try {
            return Integer.parseInt(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException(mensajeError);
        }
    }

    private void actualizarCamposPorTipo() {
        boolean esAdulto = Adulto.TIPO.equals(cbTipo.getValue());
        boolean esNino = Nino.TIPO.equals(cbTipo.getValue());
        chkTrabajadorActivo.setDisable(!esAdulto);
        txtNombreTutor.setDisable(!esNino);
    }

    private void limpiarFormulario() {
        enEdicion = null;
        cbTipo.setDisable(false);
        cbTipo.setValue(null);
        txtNombre.clear();
        txtEdad.clear();
        txtSugerencia.clear();
        chkTrabajadorActivo.setSelected(false);
        txtNombreTutor.clear();
        btnRegistrar.setText("Registrar");
        btnCancelar.setDisable(true);
        actualizarCamposPorTipo();
        tabla.getSelectionModel().clearSelection();
    }

    private String detalle(Participante participante) {
        if (participante instanceof Adulto adulto) {
            return "Trabajador activo: " + (adulto.isTrabajadorActivo() ? "sí" : "no");
        }
        if (participante instanceof Nino nino) {
            return "Responsable: " + nino.getNombreTutor();
        }
        return "";
    }

    private String describir(Participante participante) {
        return participante.getNombre() + " (" + participante.getTipo() + ")";
    }

    private String formatearMonto(double monto) {
        return String.format(LOCALE_CL, "$%,.0f", monto);
    }

    private void mostrarExito(String mensaje) {
        mostrarMensaje(mensaje, "#eef5ec", "#a9cfa1", "#1e4620");
    }

    private void mostrarInfo(String mensaje) {
        mostrarMensaje(mensaje, "#eef3f8", "#a8c1d9", "#1f3a56");
    }

    private void mostrarError(String mensaje) {
        mostrarMensaje(mensaje, "#fbeeee", "#e0a3a3", "#8a1f1f");
    }

    private void mostrarMensaje(String mensaje, String fondo, String borde, String texto) {
        mostrandoSecreto = false;
        lblMensaje.setStyle("-fx-padding: 8 10 8 10; -fx-background-color: " + fondo
                + "; -fx-border-color: " + borde + "; -fx-text-fill: " + texto + ";");
        lblMensaje.setText(mensaje);
    }
}
