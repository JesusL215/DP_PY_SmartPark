package com.smartpark.estacionamiento.patrones.comportamiento.memento;

public class TicketMemento {
    private final long ticketId;
    private final String estadoAnterior;
    private final double montoAnterior;

    public TicketMemento(long ticketId, String estadoAnterior, double montoAnterior) {
        this.ticketId = ticketId;
        this.estadoAnterior = estadoAnterior;
        this.montoAnterior = montoAnterior;
    }

    public long getTicketId() { return ticketId; }
    public String getEstadoAnterior() { return estadoAnterior; }
    public double getMontoAnterior() { return montoAnterior; }
}