package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.model.dao.ParkingSlotDAO;
import com.smartpark.estacionamiento.model.dao.TicketDAO;
import com.smartpark.estacionamiento.model.dao.VehiculoDAO;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
import com.smartpark.estacionamiento.model.service.ParkingService;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import java.util.stream.Collectors;
import java.util.List;

public class MainDashboardController {
    @FXML private TextField placaTextField;
    @FXML private ComboBox<String> tipoVehiculoComboBox;
    @FXML private ComboBox<String> slotComboBox;
    @FXML private Label statusLabel;

    private ParkingService parkingService;
    private ParkingSlotDAO parkingSlotDAO;

    @FXML
    public void initialize() {
        // --- Inyección de Dependencias (Manual) ---
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        TicketDAO ticketDAO = new TicketDAO();
        this.parkingSlotDAO = new ParkingSlotDAO();
        this.parkingService = new ParkingService(vehiculoDAO, ticketDAO, parkingSlotDAO);

        statusLabel.setText("Bienvenido. Listo para operar.");
        tipoVehiculoComboBox.getItems().addAll("Auto", "Moto");

        cargarSlotsDisponibles();
    }

    @FXML
    private void handleRegistrarEntrada() {
        try {
            String placa = placaTextField.getText();
            String tipoVehiculo = tipoVehiculoComboBox.getValue();
            String slotNumero = slotComboBox.getValue();
            if (placa.isEmpty() || tipoVehiculo == null || slotNumero == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "Todos los campos son obligatorios.");
                return;
            }
            // Obtenemos el ID del slot a partir del número
            long slotId = parkingSlotDAO.getAll().stream()
                    .filter(s -> s.getNumeroSlot().equals(slotNumero))
                    .findFirst().get().getId();

            parkingService.registrarEntrada(placa, tipoVehiculo, slotId);
            mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Vehículo " + placa + " registrado.");

            placaTextField.clear();
            cargarSlotsDisponibles();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en el Registro", e.getMessage());
            e.printStackTrace();
        }
    }

    private void cargarSlotsDisponibles() {
        List<String> slotsDisponibles = parkingSlotDAO.getAll().stream()
                .filter(slot -> slot.getCurrentState().getEstado().equals("Disponible"))
                .map(ParkingSlot::getNumeroSlot)
                .collect(Collectors.toList());
        slotComboBox.getItems().clear();
        slotComboBox.getItems().addAll(slotsDisponibles);
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}