package com.smartpark.estacionamiento.model.service;

import com.smartpark.estacionamiento.model.dao.TicketDAO;
import com.smartpark.estacionamiento.model.domain.Ticket;
import java.util.List;

public class RealReportService implements IReportService {
    private TicketDAO ticketDAO = new TicketDAO();

    @Override
    public String generarReporteDiario() {
        List<Ticket> tickets = ticketDAO.getAll(); // Asumiendo que implementas getAll o usas una query
        // Para simplificar, simularemos el cálculo si getAll retorna vacío por ahora
        double total = tickets.stream().mapToDouble(Ticket::getMontoPagado).sum();
        long cantidad = tickets.stream().filter(t -> t.getEstado().equals("PAGADO")).count();

        return "=== REPORTE FINANCIERO ===\n" +
                "Tickets Pagados: " + cantidad + "\n" +
                "Ingresos Totales: S/ " + String.format("%.2f", total) + "\n" +
                "==========================";
    }
}