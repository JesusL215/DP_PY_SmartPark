package com.smartpark.estacionamiento.patrones.estructural.proxy;

import com.smartpark.estacionamiento.model.service.IReportService;
import com.smartpark.estacionamiento.model.service.RealReportService;

public class ProxyReportService implements IReportService {
    private RealReportService realService;
    private String password;

    public ProxyReportService(String password) {
        this.password = password;
    }

    @Override
    public String generarReporteDiario() {
        if ("admin123".equals(password)) { // Contraseña hardcodeada por simplicidad
            if (realService == null) {
                realService = new RealReportService();
            }
            return realService.generarReporteDiario();
        } else {
            return "ACCESO DENEGADO: Contraseña incorrecta.";
        }
    }
}