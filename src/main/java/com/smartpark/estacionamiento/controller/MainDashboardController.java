package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.model.dao.*;
import com.smartpark.estacionamiento.model.domain.*;
import com.smartpark.estacionamiento.model.service.ParkingService;
import com.smartpark.estacionamiento.patrones.comportamiento.memento.Caretaker;
import com.smartpark.estacionamiento.patrones.comportamiento.memento.TicketMemento;
import com.smartpark.estacionamiento.patrones.comportamiento.observer.Observer;
import com.smartpark.estacionamiento.patrones.comportamiento.observer.ParkingNotifier;
import com.smartpark.estacionamiento.patrones.estructural.proxy.ProxyReportService;
import com.smartpark.estacionamiento.patrones.estructural.proxy.HistoryCacheProxy;
import com.smartpark.estacionamiento.model.service.RealReportService;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

// Implementamos Observer para escuchar cambios
public class MainDashboardController implements Observer {

    @FXML private TextField placaTextField, placaSalidaTextField;
    @FXML private ComboBox<String> tipoVehiculoComboBox, slotComboBox;
    @FXML private CheckBox lavadoCheckBox;
    @FXML private Label statusLabel;
    @FXML private GridPane parkingGrid; // El mapa visual

    private ParkingService parkingService;
    private ParkingSlotDAO parkingSlotDAO;
    private TicketDAO ticketDAO;
    private Caretaker caretaker = new Caretaker(); // Memento
    private HistoryCacheProxy historyProxy = new HistoryCacheProxy();

    @FXML
    public void initialize() {
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        this.ticketDAO = new TicketDAO();
        this.parkingSlotDAO = new ParkingSlotDAO();
        this.parkingService = new ParkingService(vehiculoDAO, ticketDAO, parkingSlotDAO);

        // Configuración inicial
        tipoVehiculoComboBox.getItems().addAll("Auto", "Moto");

        // Listener para ComboBox (Lógica existente)
        cargarSlotsDisponibles(null);
        tipoVehiculoComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> cargarSlotsDisponibles(newVal)
        );

        // PATRÓN OBSERVER: Nos suscribimos a las notificaciones
        ParkingNotifier.getInstance().attach(this);

        // Cargar el mapa visual inicial
        actualizarMapaVisual();
    }

    // --- PATRÓN OBSERVER: Metodo que se llama cuando algo cambia ---
    @Override
    public void update() {
        // Ejecutar en el hilo de UI de JavaFX
        Platform.runLater(() -> {
            actualizarMapaVisual();
            cargarSlotsDisponibles(tipoVehiculoComboBox.getValue());
            statusLabel.setText("Mapa actualizado en tiempo real.");
        });
    }

    private void actualizarMapaVisual() {
        parkingGrid.getChildren().clear();
        List<ParkingSlot> slots = parkingSlotDAO.getAll();

        int col = 0;
        int row = 0;

        for (ParkingSlot slot : slots) {
            Button btn = new Button(slot.getNumeroSlot() + "\n" + slot.getTipo());
            btn.setPrefSize(80, 60);
            btn.getStyleClass().add("slot-button");

            if ("Disponible".equals(slot.getCurrentState().getEstado())) {
                btn.getStyleClass().add("slot-free");
            } else {
                btn.getStyleClass().add("slot-occupied");
            }

            // Al hacer clic en un cuadrito, se selecciona automáticamente en el formulario
            btn.setOnAction(e -> {
                if ("Disponible".equals(slot.getCurrentState().getEstado())) {
                    tipoVehiculoComboBox.setValue(slot.getTipo());
                    slotComboBox.setValue(slot.getNumeroSlot());
                }
            });

            parkingGrid.add(btn, col, row);
            col++;
            if (col > 3) { col = 0; row++; } // 4 columnas
        }
    }

    @FXML
    private void handleRegistrarEntrada() {
        try {
            // ... (Tu lógica de validación existente) ...
            String placa = placaTextField.getText();
            String tipo = tipoVehiculoComboBox.getValue();
            String slotNum = slotComboBox.getValue();

            if(placa.isEmpty() || tipo == null || slotNum == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Complete todos los campos."); return;
            }

            long slotId = parkingSlotDAO.getAll().stream()
                    .filter(s -> s.getNumeroSlot().equals(slotNum))
                    .findFirst().get().getId();

            parkingService.registrarEntrada(placa, tipo, slotId);

            // NOTIFICAR CAMBIO (OBSERVER)
            ParkingNotifier.getInstance().notifyObservers();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Entrada registrada.");
            placaTextField.clear();
            tipoVehiculoComboBox.getSelectionModel().clearSelection();

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleRegistrarSalida() {
        try {
            String placa = placaSalidaTextField.getText();
            if(placa.isEmpty()) return;

            Ticket ticket = ticketDAO.findActiveTicketByPlaca(placa);
            if (ticket == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No encontrado."); return;
            }

            // PATRÓN MEMENTO: Guardar estado antes de salir
            caretaker.guardar(new TicketMemento(ticket.getId(), "ACTIVO", 0.0));

            Ticket pagado = parkingService.registrarSalida(ticket.getId(), lavadoCheckBox.isSelected());

            // NOTIFICAR CAMBIO (OBSERVER)
            ParkingNotifier.getInstance().notifyObservers();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Salida", "Total: S/" + pagado.getMontoPagado());
            placaSalidaTextField.clear();
            lavadoCheckBox.setSelected(false);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    // --- PATRÓN MEMENTO: Deshacer ---
    @FXML
    private void handleDeshacer() {
        TicketMemento memento = caretaker.deshacer();
        if (memento != null) {
            // Aquí iría la lógica para revertir en BD (update ticket set state=ACTIVO...)
            // Por simplicidad en este demo, solo mostramos que el patrón funciona:
            mostrarAlerta(Alert.AlertType.INFORMATION, "Memento", "Se restauraría el ticket ID: " + memento.getTicketId());
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Memento", "No hay acciones para deshacer.");
        }
    }

    // --- PATRÓN PROXY: Reportes ---
    @FXML
    private void handleVerReporte() {
        RealReportService service = new RealReportService();
        String reporte = service.generarReporteDiario();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Reporte Financiero", reporte);
    }

    // Metodo para ver la tabla de historial (Usa Proxy de Caché)
    @FXML
    private void handleVerHistorial() {
        // Obtenemos los datos a través del Proxy
        List<Ticket> historial = historyProxy.obtenerHistorialCompleto();

        // Creamos una ventana (Stage) para mostrar la tabla
        TableView<Ticket> tabla = new TableView<>();

        // Columna Placa (Accedemos a través del objeto Vehiculo)
        TableColumn<Ticket, String> colPlaca = new TableColumn<>("Placa");
        colPlaca.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getVehiculo().getPlaca()));

        // Columna Entrada
        TableColumn<Ticket, String> colEntrada = new TableColumn<>("Entrada");
        colEntrada.setCellValueFactory(new PropertyValueFactory<>("horaEntrada"));

        // Columna Salida
        TableColumn<Ticket, String> colSalida = new TableColumn<>("Salida");
        colSalida.setCellValueFactory(new PropertyValueFactory<>("horaSalida"));

        // Columna Lavado (El nuevo campo)
        TableColumn<Ticket, String> colLavado = new TableColumn<>("Lavado");
        colLavado.setCellValueFactory(new PropertyValueFactory<>("lavadoTexto")); // Llama a getLavadoTexto()

        // Columna Monto
        TableColumn<Ticket, Double> colMonto = new TableColumn<>("Monto (S/)");
        colMonto.setCellValueFactory(new PropertyValueFactory<>("montoPagado"));

        tabla.getColumns().addAll(colPlaca, colEntrada, colSalida, colLavado, colMonto);
        tabla.getItems().addAll(historial);

        Scene scene = new Scene(tabla, 600, 400);
        Stage stage = new Stage();
        stage.setTitle("Historial de Vehículos (Cache Proxy)");
        stage.setScene(scene);
        stage.show();
    }

    // ACTUALIZAR: Invalida la caché cuando hay cambios
    @Override
    public void update() {
        Platform.runLater(() -> {
            actualizarMapaVisual();
            cargarSlotsDisponibles(tipoVehiculoComboBox.getValue());

            // ¡IMPORTANTE! Si entra o sale un auto, la caché vieja ya no sirve.
            historyProxy.invalidarCache();

            statusLabel.setText("Datos actualizados.");
        });
    }

    private void cargarSlotsDisponibles(String tipo) {
        // ... (Tu lógica existente se mantiene igual) ...
        // Asegúrate de copiar tu método cargarSlotsDisponibles aquí
        if (tipo == null) {
            slotComboBox.getItems().clear();
            slotComboBox.setDisable(true);
            return;
        }
        List<String> slots = parkingSlotDAO.getAll().stream()
                .filter(s -> s.getTipo().equalsIgnoreCase(tipo) && s.getCurrentState().getEstado().equals("Disponible"))
                .map(ParkingSlot::getNumeroSlot).collect(Collectors.toList());
        slotComboBox.getItems().setAll(slots);
        slotComboBox.setDisable(false);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}