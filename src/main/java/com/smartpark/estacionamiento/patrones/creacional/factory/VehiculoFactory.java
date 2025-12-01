package com.smartpark.estacionamiento.patrones.creacional.factory;

import com.smartpark.estacionamiento.model.domain.Auto;
import com.smartpark.estacionamiento.model.domain.Moto;
import com.smartpark.estacionamiento.model.domain.Vehiculo;

public class VehiculoFactory {

    public static Vehiculo createVehiculo(String tipoVehiculo, String placa, String propietario) {
        if (tipoVehiculo == null || tipoVehiculo.isEmpty()) {
            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo.");
        }
        switch (tipoVehiculo) {
            case "Auto":
                return new Auto(placa, propietario);
            case "Moto":
                return new Moto(placa, propietario);
            default:

                throw new IllegalArgumentException("Tipo de vehículo desconocido: " + tipoVehiculo);
        }
    }
}