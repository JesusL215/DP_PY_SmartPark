package com.smartpark.estacionamiento.controller;

import com.smartpark.estacionamiento.model.dao.ParkingSlotDAO;
import com.smartpark.estacionamiento.model.dao.TicketDAO;
import com.smartpark.estacionamiento.model.dao.VehiculoDAO;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
import com.smartpark.estacionamiento.model.service.ParkingService;
import com.smartpark.estacionamiento.model.domain.Ticket;
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
    @FXML private TextField placaSalidaTextField;

    private ParkingService parkingService;
    private ParkingSlotDAO parkingSlotDAO;
    private TicketDAO ticketDAO;

    @FXML
    public void initialize() {
        // --- Inyección de Dependencias (Manual) ---
        VehiculoDAO vehiculoDAO = new VehiculoDAO();
        this.ticketDAO = new TicketDAO();
        this.parkingSlotDAO = new ParkingSlotDAO();
        this.parkingService = new ParkingService(vehiculoDAO, ticketDAO, parkingSlotDAO);

        statusLabel.setText("Bienvenido. Listo para operar.");
        tipoVehiculoComboBox.getItems().addAll("Auto", "Moto");

        // --- ¡AQUÍ ESTÁ LA CORRECCIÓN! ---

        // 1. Llama al método al inicio para deshabilitar el slotComboBox
        //    ya que todavía no se ha seleccionado ningún tipo de vehículo.
        cargarSlotsDisponibles(null);

        // 2. Añade un listener para reaccionar a los cambios de tipo de vehículo
        tipoVehiculoComboBox.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // 'newValue' es el string "Auto" o "Moto" que el usuario acaba de seleccionar.
                    // Llama al método de carga de slots con el nuevo tipo.
                    cargarSlotsDisponibles(newValue);
                }
        );
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
            tipoVehiculoComboBox.getSelectionModel().clearSelection();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en el Registro", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Maneja el clic en el botón "Registrar Salida".
     */
    @FXML
    private void handleRegistrarSalida() {
        String placa = placaSalidaTextField.getText();
        if (placa.isEmpty()) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error", "Debe ingresar una placa.");
            return;
        }

        try {
            // 1. Buscamos el ticket activo usando el nuevo método del DAO
            Ticket ticketActivo = ticketDAO.findActiveTicketByPlaca(placa);

            if (ticketActivo == null) {
                mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se encontró un ticket activo para la placa " + placa);
                return;
            }

            // 2. Usamos el ParkingService (Facade) para registrar la salida
            Ticket ticketPagado = parkingService.registrarSalida(ticketActivo.getId());

            // 3. Mostramos el resultado
            mostrarAlerta(Alert.AlertType.INFORMATION, "Salida Registrada",
                    "Salida exitosa para " + placa + ".\n" +
                            "Monto a pagar: S/ " + String.format("%.2f", ticketPagado.getMontoPagado()));

            // 4. Limpiamos y recargamos
            placaSalidaTextField.clear();
            String tipoSeleccionado = tipoVehiculoComboBox.getValue();
            cargarSlotsDisponibles(tipoSeleccionado);

        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error en el Registro", e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Carga los espacios (slots) en el ComboBox, filtrando por el tipo de vehículo
     * y el estado "Disponible".
     *
     * @param tipoVehiculo El tipo de vehículo ("Auto", "Moto") o null.
     */
    private void cargarSlotsDisponibles(String tipoVehiculo) {
        // 1. Limpia los items y deshabilita el ComboBox
        slotComboBox.getItems().clear();
        slotComboBox.setDisable(true); // Deshabilitado por defecto

        // 2. Si no se ha seleccionado un tipo de vehículo, no mostramos nada.
        if (tipoVehiculo == null) {
            slotComboBox.setPromptText("Seleccione un tipo de vehículo");
            return; // No sigas
        }

        // 3. Filtra la lista de TODOS los slots por DOS condiciones:
        //    - El tipo de slot debe coincidir (ej. "Auto" == "Auto")
        //    - El estado debe ser "Disponible"
        List<String> slotsDisponibles = parkingSlotDAO.getAll().stream()
                .filter(slot -> slot.getTipo().equalsIgnoreCase(tipoVehiculo) &&
                        slot.getCurrentState().getEstado().equals("Disponible"))
                .map(ParkingSlot::getNumeroSlot)
                .collect(Collectors.toList());

        // 4. Activa el ComboBox y muestra los slots si se encontraron
        if (slotsDisponibles.isEmpty()) {
            slotComboBox.setPromptText("No hay slots libres");
        } else {
            slotComboBox.getItems().addAll(slotsDisponibles);
            slotComboBox.setPromptText("Seleccionar espacio");
            slotComboBox.setDisable(false); // ¡Habilítalo!
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String contenido) {
        Alert alerta = new Alert(tipo);
        alerta.setTitle(titulo);
        alerta.setHeaderText(null);
        alerta.setContentText(contenido);
        alerta.showAndWait();
    }
}