package com.smartpark.estacionamiento.patrones.estructural.proxy;

import com.smartpark.estacionamiento.model.domain.Ticket;
import com.smartpark.estacionamiento.model.service.IHistoryService;
import com.smartpark.estacionamiento.model.service.RealHistoryService;
import java.util.List;

public class HistoryCacheProxy implements IHistoryService {
    private RealHistoryService realService;
    private List<Ticket> cacheHistorial;

    public HistoryCacheProxy() {
        this.realService = new RealHistoryService();
    }

    @Override
    public List<Ticket> obtenerHistorialCompleto() {
        if (cacheHistorial == null) {
            cacheHistorial = realService.obtenerHistorialCompleto();
        } else {
            System.out.println("--- PROXY: Retornando historial desde CACHÉ (Rápido) ---");
        }
        return cacheHistorial;
    }

    // Método para invalidar la caché cuando hay nuevos movimientos
    public void invalidarCache() {
        this.cacheHistorial = null;
        System.out.println("--- PROXY: Caché invalidada por nueva operación ---");
    }
}