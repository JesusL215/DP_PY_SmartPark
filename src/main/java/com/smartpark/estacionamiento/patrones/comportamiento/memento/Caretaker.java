package com.smartpark.estacionamiento.patrones.comportamiento.memento;

import java.util.Stack;

public class Caretaker {
    private Stack<TicketMemento> historia = new Stack<>();

    public void guardar(TicketMemento memento) {
        historia.push(memento);
    }

    public TicketMemento deshacer() {
        if (!historia.isEmpty()) {
            return historia.pop();
        }
        return null;
    }
}