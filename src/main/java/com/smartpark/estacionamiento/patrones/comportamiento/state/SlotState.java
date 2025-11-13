package com.smartpark.estacionamiento.patrones.comportamiento.state;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
public interface SlotState {
    void ocupar(ParkingSlot slot);
    void liberar(ParkingSlot slot);
    String getEstado();
}