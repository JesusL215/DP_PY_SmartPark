package com.smartpark.estacionamiento.patrones.estructural.decorator;

public abstract class ServiceDecorator implements IParkingCost {
    protected IParkingCost parkingCost;

    public ServiceDecorator(IParkingCost parkingCost) {
        this.parkingCost = parkingCost;
    }

    @Override
    public double getCosto() {
        return parkingCost.getCosto();
    }

    @Override
    public String getDescripcion() {
        return parkingCost.getDescripcion();
    }
}