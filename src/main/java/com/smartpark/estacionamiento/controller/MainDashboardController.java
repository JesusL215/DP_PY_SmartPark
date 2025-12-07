package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.model.dao.*;
import com.smartpark.estacionamiento.model.domain.*;
import com.smartpark.estacionamiento.model.service.ParkingService;
import com.smartpark.estacionamiento.patrones.comportamiento.memento.Caretaker;
import com.smartpark.estacionamiento.patrones.comportamiento.memento.TicketMemento;
import com.smartpark.estacionamiento.patrones.comportamiento.observer.Observer;
import com.smartpark.estacionamiento.patrones.comportamiento.observer.ParkingNotifier;
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
import java.util.stream.Collectors;

public class MainDashboardController implements Observer {

    @FXML private TextField placaTextField, placaSalidaTextField;
    @FXML private ComboBox<String> tipoVehiculoComboBox, slotComboBox;
    @FXML private CheckBox lavadoCheckBox;
    @FXML private Label statusLabel;
    @FXML private GridPane parkingGrid;

    private ParkingService parkingService;
    private ParkingSlotDAO parkingSlotDAO;
    private TicketDAO ticketDAO;
    private Caretaker caretaker = new Caretaker();
    private HistoryCacheProxy historyProxy = new HistoryCacheProxy();

    @FXML
    public void initialize() {
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        this.ticketDAO = new TicketDAO();
        this.parkingSlotDAO = new ParkingSlotDAO();
        this.parkingService = new ParkingService(vehiculoDAO, ticketDAO, parkingSlotDAO);

        tipoVehiculoComboBox.getItems().addAll("Auto", "Moto");

        cargarSlotsDisponibles(null);
        tipoVehiculoComboBox.getSelectionModel().selectedItemProperty().addListener(
                (obs, old, newVal) -> cargarSlotsDisponibles(newVal)
        );

        ParkingNotifier.getInstance().attach(this);
        actualizarMapaVisual();
    }

    // --- PATRÓN OBSERVER: ÚNICA IMPLEMENTACION ---
    @Override
    public void update() {
        Platform.runLater(() -> {
            actualizarMapaVisual();
            cargarSlotsDisponibles(tipoVehiculoComboBox.getValue());

            historyProxy.invalidarCache();

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

            btn.setOnAction(e -> {
                if ("Disponible".equals(slot.getCurrentState().getEstado())) {
                    tipoVehiculoComboBox.setValue(slot.getTipo());
                    slotComboBox.setValue(slot.getNumeroSlot());
                }
            });

            parkingGrid.add(btn, col, row);
            col++;
            if (col > 3) { col = 0; row++; }
        }
    }

    @FXML
    private void handleRegistrarEntrada() {
        try {
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

            caretaker.guardar(new TicketMemento(ticket.getId(), "ACTIVO", 0.0));
            Ticket pagado = parkingService.registrarSalida(ticket.getId(), lavadoCheckBox.isSelected());
            ParkingNotifier.getInstance().notifyObservers();

            mostrarAlerta(Alert.AlertType.INFORMATION, "Salida", "Total: S/" + pagado.getMontoPagado());
            placaSalidaTextField.clear();
            lavadoCheckBox.setSelected(false);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", e.getMessage());
        }
    }

    @FXML
    private void handleDeshacer() {
        // 1. Recuperar el recuerdo (Memento)
        TicketMemento memento = caretaker.deshacer();

        if (memento != null) {
            try {
                // 2. Obtener el ticket actual de la BD
                Ticket ticket = ticketDAO.get(memento.getTicketId());

                // 3. Restaurar los valores anteriores (Revertir salida)
                ticket.setEstado(memento.getEstadoAnterior()); // Vuelve a "ACTIVO"
                ticket.setMontoPagado(memento.getMontoAnterior()); // Vuelve a 0.0
                ticket.setHoraSalida(null); // Borra la hora de salida

                // 4. Guardar cambios del Ticket en BD
                ticketDAO.update(ticket);

                // 5. Revertir también el espacio (Volver a ocuparlo)
                ParkingSlot slot = ticket.getParkingSlot();
                slot.ocupar(); // Cambia estado en memoria
                parkingSlotDAO.update(slot); // Cambia estado en BD

                // 6. Actualizar la vista
                ParkingNotifier.getInstance().notifyObservers();
                mostrarAlerta(Alert.AlertType.INFORMATION, "Deshacer", "Se restauró el Ticket #" + ticket.getId());

            } catch (Exception e) {
                e.printStackTrace();
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo deshacer la acción.");
            }
        } else {
            mostrarAlerta(Alert.AlertType.WARNING, "Memento", "No hay acciones para deshacer.");
        }
    }

    @FXML
    private void handleVerReporte() {
        RealReportService service = new RealReportService();
        String reporte = service.generarReporteDiario();
        mostrarAlerta(Alert.AlertType.INFORMATION, "Reporte Financiero", reporte);
    }

    @FXML
    private void handleVerHistorial() {
        List<Ticket> historial = historyProxy.obtenerHistorialCompleto();

        TableView<Ticket> tabla = new TableView<>();

        TableColumn<Ticket, String> colPlaca = new TableColumn<>("Placa");
        colPlaca.setCellValueFactory(cell -> new javafx.beans.property.SimpleStringProperty(
                cell.getValue().getVehiculo().getPlaca()));

        TableColumn<Ticket, String> colEntrada = new TableColumn<>("Entrada");
        colEntrada.setCellValueFactory(new PropertyValueFactory<>("horaEntrada"));

        TableColumn<Ticket, String> colSalida = new TableColumn<>("Salida");
        colSalida.setCellValueFactory(new PropertyValueFactory<>("horaSalida"));

        TableColumn<Ticket, String> colLavado = new TableColumn<>("Lavado");
        colLavado.setCellValueFactory(new PropertyValueFactory<>("lavadoTexto"));

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

    private void cargarSlotsDisponibles(String tipo) {
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