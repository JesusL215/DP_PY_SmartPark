package com.smartpark.estacionamiento.model.domain;
public class Vehiculo {
    private long id;
    private String placa;
    private String tipoVehiculo;
    private String propietario;
    public Vehiculo() {}
    public Vehiculo(String placa, String tipoVehiculo, String propietario) {
        this.placa = placa;
        this.tipoVehiculo = tipoVehiculo;
        this.propietario = propietario;
    }
    // --- Getters y Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getPlaca() { return placa; }
    public void setPlaca(String placa) { this.placa = placa; }
    public String getTipoVehiculo() { return tipoVehiculo; }
    public void setTipoVehiculo(String tipoVehiculo) { this.tipoVehiculo = tipoVehiculo; }
    public String getPropietario() { return propietario; }
    public void setPropietario(String propietario) { this.propietario = propietario; }
}