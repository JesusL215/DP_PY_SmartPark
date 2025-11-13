package com.smartpark.estacionamiento.model.domain;
import com.smartpark.estacionamiento.patrones.comportamiento.state.AvailableState;
import com.smartpark.estacionamiento.patrones.comportamiento.state.SlotState;
public class ParkingSlot {
    private long id;
    private String numeroSlot;
    private String tipo;
    private SlotState currentState;
    public ParkingSlot() { this.currentState = new AvailableState(); }
    public void ocupar() { this.currentState.ocupar(this); }
    public void liberar() { this.currentState.liberar(this); }
    // --- Getters y Setters ---
    public long getId() { return id; }
    public void setId(long id) { this.id = id; }
    public String getNumeroSlot() { return numeroSlot; }
    public void setNumeroSlot(String numeroSlot) { this.numeroSlot = numeroSlot; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public SlotState getCurrentState() { return currentState; }
    public void setCurrentState(SlotState currentState) { this.currentState = currentState; }
}