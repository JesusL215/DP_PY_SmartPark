package com.smartpark.estacionamiento.model.domain;
import java.time.LocalDateTime;
public class Ticket {
    private long id;
    private LocalDateTime horaEntrada;
    private LocalDateTime horaSalida;
    private double montoPagado;
    private String estado;
    private Vehiculo vehiculo;
    private ParkingSlot parkingSlot;
    // --- Getters y Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public LocalDateTime getHoraEntrada() { return horaEntrada; }
    public void setHoraEntrada(LocalDateTime horaEntrada) { this.horaEntrada = horaEntrada; }
    public LocalDateTime getHoraSalida() { return horaSalida; }
    public void setHoraSalida(LocalDateTime horaSalida) { this.horaSalida = horaSalida; }
    public double getMontoPagado() { return montoPagado; }
    public void setMontoPagado(double montoPagado) { this.montoPagado = montoPagado; }
    public String getEstado() { return estado; }
    public void setEstado(String estado) { this.estado = estado; }
    public Vehiculo getVehiculo() { return vehiculo; }
    public void setVehiculo(Vehiculo vehiculo) { this.vehiculo = vehiculo; }
    public ParkingSlot getParkingSlot() { return parkingSlot; }
    public void setParkingSlot(ParkingSlot parkingSlot) { this.parkingSlot = parkingSlot; }
}