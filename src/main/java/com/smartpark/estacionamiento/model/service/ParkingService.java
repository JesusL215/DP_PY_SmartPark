package com.smartpark.estacionamiento.model.service;

import com.smartpark.estacionamiento.model.dao.*;
import com.smartpark.estacionamiento.model.domain.*;
import java.time.LocalDateTime;
import com.smartpark.estacionamiento.patrones.creacional.factory.VehiculoFactory;

public class ParkingService {
    private VehiculoDAO vehiculoDAO;
    private TicketDAO ticketDAO;
    private ParkingSlotDAO parkingSlotDAO;

    public ParkingService(VehiculoDAO vehiculoDAO, TicketDAO ticketDAO, ParkingSlotDAO parkingSlotDAO) {
        this.vehiculoDAO = vehiculoDAO;
        this.ticketDAO = ticketDAO;
        this.parkingSlotDAO = parkingSlotDAO;
    }

    public Ticket registrarEntrada(String placa, String tipoVehiculo, Long slotId) throws Exception {
        Vehiculo vehiculo = vehiculoDAO.findByPlaca(placa);
        if (vehiculo == null) {
            vehiculo = VehiculoFactory.createVehiculo(tipoVehiculo, placa, "N/A");
            vehiculoDAO.save(vehiculo);
        }
        ParkingSlot slot = parkingSlotDAO.get(slotId);
        if (slot == null) throw new Exception("El slot no existe.");

        slot.ocupar(); // Patrón State en acción
        parkingSlotDAO.update(slot); // Persiste el nuevo estado

        Ticket ticket = new Ticket();
        ticket.setVehiculo(vehiculo);
        ticket.setParkingSlot(slot);
        ticket.setHoraEntrada(LocalDateTime.now());
        ticket.setEstado("ACTIVO");
        ticketDAO.save(ticket);

        System.out.println("Entrada registrada para " + placa);
        return ticket;
    }

    public Ticket registrarSalida(Long ticketId) throws Exception {
        Ticket ticket = ticketDAO.get(ticketId);
        if (ticket == null || ticket.getEstado().equals("PAGADO")) {
            throw new Exception("Ticket no válido.");
        }
        ticket.setHoraSalida(LocalDateTime.now());
        // Ahora, el monto se calcula basado en la tarifa del Vehiculo
        double monto = calcularTarifa(ticket.getHoraEntrada(), ticket.getHoraSalida(), ticket.getVehiculo());
        ticket.setMontoPagado(monto);
        ticket.setEstado("PAGADO");

        ParkingSlot slot = ticket.getParkingSlot();
        slot.liberar(); // Patrón State en acción
        parkingSlotDAO.update(slot); // Persiste el nuevo estado

        ticketDAO.update(ticket);
        System.out.println("Salida registrada. Monto: " + monto);
        return ticket;
    }

    /**
     * El método de tarifa ahora usa el objeto Vehiculo
     * para obtener la tarifa correcta (polimorfismo).
     */
    private double calcularTarifa(LocalDateTime entrada, LocalDateTime salida, Vehiculo vehiculo) {
        long horas = java.time.Duration.between(entrada, salida).toHours();
        if (horas < 1) horas = 1; // Cobro mínimo de 1 hora

        // Obtenemos la tarifa del objeto (5.0 para Auto, 2.5 para Moto)
        double tarifaPorHora = vehiculo.getTarifaPorHora();

        return horas * tarifaPorHora;
    }
}