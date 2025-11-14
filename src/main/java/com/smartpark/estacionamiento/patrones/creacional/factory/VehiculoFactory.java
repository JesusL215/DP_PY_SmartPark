package com.smartpark.estacionamiento.patrones.creacional.factory;

import com.smartpark.estacionamiento.model.domain.Auto;
import com.smartpark.estacionamiento.model.domain.Moto;
import com.smartpark.estacionamiento.model.domain.Vehiculo;

/**
 * Implementación del Patrón Factory Method.
 * Encapsula la lógica de creación de Vehículos.
 * [Patrón Creacional: Factory Method]
 */
public class VehiculoFactory {

    /**
     * Método de fábrica estático.
     * Crea un objeto Vehiculo (Auto o Moto) basado en el tipo.
     *
     * @param tipoVehiculo "Auto" o "Moto" (viene del ComboBox)
     * @param placa La placa del vehículo
     * @param propietario (Opcional) El propietario
     * @return Una instancia de Auto o Moto
     * @throws IllegalArgumentException si el tipo de vehículo no es válido
     */
    public static Vehiculo createVehiculo(String tipoVehiculo, String placa, String propietario) {
        if (tipoVehiculo == null || tipoVehiculo.isEmpty()) {
            throw new IllegalArgumentException("El tipo de vehículo no puede ser nulo.");
        }

        // El ComboBox en tu UI nos da "Auto" o "Moto"
        switch (tipoVehiculo) {
            case "Auto":
                return new Auto(placa, propietario);
            case "Moto":
                return new Moto(placa, propietario);
            default:
                // Si en el futuro añades "Camión", solo modificas esta fábrica.
                throw new IllegalArgumentException("Tipo de vehículo desconocido: " + tipoVehiculo);
        }
    }
}