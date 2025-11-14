package com.smartpark.estacionamiento.model.domain;

public class Moto extends Vehiculo {

    private static final double TARIFA_MOTO_POR_HORA = 2.5; // Las motos pagan menos

    public Moto(String placa, String propietario) {
        // Llama al constructor padre y le asigna el tipo "Moto"
        super(placa, "Moto", propietario);
    }

    @Override
    public double getTarifaPorHora() {
        return TARIFA_MOTO_POR_HORA;
    }
}