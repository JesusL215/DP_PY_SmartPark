package com.smartpark.estacionamiento.patrones.comportamiento.state;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
public class AvailableState implements SlotState {
    @Override
    public void ocupar(ParkingSlot slot) {
        System.out.println("Slot " + slot.getNumeroSlot() + " ahora está ocupado.");
        slot.setCurrentState(new OccupiedState());
    }
    @Override
    public void liberar(ParkingSlot slot) {
        System.err.println("Error: El Slot " + slot.getNumeroSlot() + " ya está libre.");
    }
    @Override
    public String getEstado() { return "Disponible"; }
}