package com.smartpark.estacionamiento.model.service;
import com.smartpark.estacionamiento.model.dao.TicketDAO;
import com.smartpark.estacionamiento.model.domain.Ticket;
import java.util.List;

public class RealHistoryService implements IHistoryService {
    private TicketDAO ticketDAO = new TicketDAO();

    @Override
    public List<Ticket> obtenerHistorialCompleto() {
        System.out.println("--- REAL SERVICE: Consultando base de datos (Lento)... ---");
        return ticketDAO.getAll();
    }
}