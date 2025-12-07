package com.smartpark.estacionamiento.model.service;
import com.smartpark.estacionamiento.model.domain.Ticket;
import java.util.List;

public interface IHistoryService {
    List<Ticket> obtenerHistorialCompleto();
}