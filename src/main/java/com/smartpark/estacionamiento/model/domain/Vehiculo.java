package com.smartpark.estacionamiento.model.domain;

/**
 * Clase base (abstracta) para el Patrón Factory Method.
 * Cumple con el principio de Liskov (L) de SOLID.
 */
public abstract class Vehiculo {
    private long id;
    private String placa;
    private String tipoVehiculo; // "Auto", "Moto", etc.
    private String propietario;

    public Vehiculo(String placa, String tipoVehiculo, String propietario) {
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.propietario = propietario;
    }

    /**
     * Método abstracto. Cada subclase (Auto, Moto) deberá
     * implementar este método y definir su propia tarifa.
     */
    public abstract double getTarifaPorHora();

    // --- Getters y Setters (sin cambios) ---
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
    }
    public String getPlaca() {
        return placa;
    }
    public void setPlaca(String placa) {
        this.placa = placa;
    }
    public String getTipoVehiculo() {
        return tipoVehiculo;
    }
    public void setTipoVehiculo(String tipoVehiculo) {
        this.tipoVehiculo = tipoVehiculo;
    }
    public String getPropietario() {
        return propietario;
    }
    public void setPropietario(String propietario) {
        this.propietario = propietario;
    }
}