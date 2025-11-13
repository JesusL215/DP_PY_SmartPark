package com.smartpark.estacionamiento.patrones.comportamiento.state;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
public class OccupiedState implements SlotState {
    @Override
    public void ocupar(ParkingSlot slot) {
        System.err.println("Error: El Slot " + slot.getNumeroSlot() + " ya está ocupado.");
    }
    @Override
    public void liberar(ParkingSlot slot) {
        System.out.println("Slot " + slot.getNumeroSlot() + " ahora está disponible.");
        slot.setCurrentState(new AvailableState());
    }
    @Override
    public String getEstado() { return "Ocupado"; }
}