package com.smartpark.estacionamiento.model.domain;

public class Auto extends Vehiculo {

    private static final double TARIFA_AUTO_POR_HORA = 5.0;

    public Auto(String placa, String propietario) {
        // Llama al constructor padre y le asigna el tipo "Auto"
        super(placa, "Auto", propietario);
    }

    @Override
    public double getTarifaPorHora() {
        return TARIFA_AUTO_POR_HORA;
    }
}