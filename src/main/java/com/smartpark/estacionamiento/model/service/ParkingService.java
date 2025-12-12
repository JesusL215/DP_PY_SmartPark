package com.smartpark.estacionamiento.model.service;

import com.smartpark.estacionamiento.model.dao.*;
import com.smartpark.estacionamiento.model.domain.*;
import java.time.LocalDateTime;
import com.smartpark.estacionamiento.patrones.creacional.factory.VehiculoFactory;
import com.smartpark.estacionamiento.patrones.estructural.decorator.*;

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

        slot.ocupar();
        parkingSlotDAO.update(slot);

        Ticket ticket = new Ticket();
        ticket.setVehiculo(vehiculo);
        ticket.setParkingSlot(slot);
        ticket.setHoraEntrada(LocalDateTime.now());
        ticket.setEstado("ACTIVO");
        ticketDAO.save(ticket);

        System.out.println("Entrada registrada para " + placa);
        return ticket;
    }

    /**
     * Metodo para compatibilidad.
     * Simplemente llama al metodo nuevo asumiendo que NO hay lavado.
     */
    public Ticket registrarSalida(Long ticketId) throws Exception {
        return registrarSalida(ticketId, false);
    }

    /**
     * Metodo principal de salida usando el Patrón Decorator.
     */
    public Ticket registrarSalida(Long ticketId, boolean conLavado) throws Exception {
        Ticket ticket = ticketDAO.get(ticketId);
        if (ticket == null || ticket.getEstado().equals("PAGADO")) {
            throw new Exception("Ticket no válido o ya pagado.");
        }

        ticket.setHoraSalida(LocalDateTime.now());
        ticket.setIncluyeLavado(conLavado);

        // --- APLICACIÓN DEL PATRÓN DECORATOR ---
        // 1. Empezamos con el costo base
        IParkingCost costoFinal = new BaseParkingCost(ticket);

        // 2. Si seleccionó lavado, "decoramos" (envolvemos) el objeto
        if (conLavado) {
            costoFinal = new CarWashDecorator(costoFinal);
        }

        // 3. Obtenemos el costo total calculado por la cadena de decoradores
        double monto = costoFinal.getCosto();
        // ----------------------------------------

        ticket.setMontoPagado(monto);
        ticket.setEstado("PAGADO");

        ParkingSlot slot = ticket.getParkingSlot();
        slot.liberar();
        parkingSlotDAO.update(slot);
        ticketDAO.update(ticket);

        System.out.println("Salida registrada. " + costoFinal.getDescripcion() + ". Total: " + monto);
        return ticket;
    }
}