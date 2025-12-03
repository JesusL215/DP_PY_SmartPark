package com.smartpark.estacionamiento.patrones.comportamiento.observer;

import java.util.ArrayList;
import java.util.List;

public class ParkingNotifier {
    private static ParkingNotifier instance;
    private List<Observer> observers = new ArrayList<>();

    private ParkingNotifier() {}

    public static ParkingNotifier getInstance() {
        if (instance == null) instance = new ParkingNotifier();
        return instance;
    }

    public void attach(Observer observer) {
        observers.add(observer);
    }

    public void notifyObservers() {
        for (Observer observer : observers) {
            observer.update();
        }
    }
}