package com.smartpark.estacionamiento.patrones.estructural.decorator;

import com.smartpark.estacionamiento.model.domain.Ticket;
import com.smartpark.estacionamiento.model.domain.Vehiculo;
import java.time.Duration;

public class BaseParkingCost implements IParkingCost {
    private Ticket ticket;
    private double costoCalculado;

    public BaseParkingCost(Ticket ticket) {
        this.ticket = ticket;
        this.costoCalculado = calcularCostoBase();
    }

    private double calcularCostoBase() {
        Vehiculo vehiculo = ticket.getVehiculo();
        long horas = Duration.between(ticket.getHoraEntrada(), ticket.getHoraSalida()).toHours();
        if (horas < 1) horas = 1; // Mínimo 1 hora
        return horas * vehiculo.getTarifaPorHora();
    }

    @Override
    public double getCosto() {
        return this.costoCalculado;
    }

    @Override
    public String getDescripcion() {
        return "Estacionamiento (" + ticket.getVehiculo().getTipoVehiculo() + ")";
    }
}