package com.smartpark.estacionamiento.patrones.estructural.decorator;

public class CarWashDecorator extends ServiceDecorator {
    private static final double COSTO_LAVADO = 20.0;

    public CarWashDecorator(IParkingCost parkingCost) {
        super(parkingCost);
    }

    @Override
    public double getCosto() {
        return super.getCosto() + COSTO_LAVADO;
    }

    @Override
    public String getDescripcion() {
        return super.getDescripcion() + " + Lavado de Auto";
    }
}