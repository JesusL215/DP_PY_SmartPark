package com.smartpark.estacionamiento.model.dao;

import com.smartpark.estacionamiento.model.domain.*;
import com.smartpark.estacionamiento.patrones.creacional.singleton.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketDAO implements IDAO<Ticket, Long> {
    private Connection connection = DBConnection.getInstance().getConnection();

    // Instancias para reconstruir relaciones
    private VehiculoDAO vehiculoDAO = new VehiculoDAO();
    private ParkingSlotDAO parkingSlotDAO = new ParkingSlotDAO();

    @Override
    public Ticket get(Long id) {
        String sql = "SELECT * FROM tickets WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractTicketFromResultSet(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }

    @Override
    public List<Ticket> getAll() {
        List<Ticket> historial = new ArrayList<>();
        // Ordenamos por hora de entrada descendente (lo más reciente primero)
        String sql = "SELECT * FROM tickets ORDER BY horaEntrada DESC";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) {
                historial.add(extractTicketFromResultSet(rs));
            }
        } catch (SQLException e) { e.printStackTrace(); }
        return historial;
    }

    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO tickets (horaEntrada, estado, vehiculo_id, parkingslot_id, incluye_lavado) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(ticket.getHoraEntrada()));
            stmt.setString(2, ticket.getEstado());
            stmt.setLong(3, ticket.getVehiculo().getId());
            stmt.setLong(4, ticket.getParkingSlot().getId());
            stmt.setBoolean(5, ticket.isIncluyeLavado());

            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) ticket.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void update(Ticket ticket) {
        String sql = "UPDATE tickets SET horaSalida = ?, montoPagado = ?, estado = ?, incluye_lavado = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            // Lógica para manejar fecha nula (Requerido para el Deshacer/Memento)
            if (ticket.getHoraSalida() != null) {
                stmt.setTimestamp(1, Timestamp.valueOf(ticket.getHoraSalida()));
            } else {
                stmt.setNull(1, Types.TIMESTAMP);
            }
            stmt.setDouble(2, ticket.getMontoPagado());
            stmt.setString(3, ticket.getEstado());
            stmt.setBoolean(4, ticket.isIncluyeLavado());
            stmt.setLong(5, ticket.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }

    @Override
    public void delete(Ticket ticket) { /* No implementado */ }

    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getLong("id"));
        t.setHoraEntrada(rs.getTimestamp("horaEntrada").toLocalDateTime());
        Timestamp tsSalida = rs.getTimestamp("horaSalida");
        if(tsSalida != null) t.setHoraSalida(tsSalida.toLocalDateTime());
        t.setMontoPagado(rs.getDouble("montoPagado"));
        t.setEstado(rs.getString("estado"));
        t.setIncluyeLavado(rs.getBoolean("incluye_lavado"));

        t.setVehiculo(vehiculoDAO.get(rs.getLong("vehiculo_id")));
        t.setParkingSlot(parkingSlotDAO.get(rs.getLong("parkingslot_id")));
        return t;
    }

    public Ticket findActiveTicketByPlaca(String placa) {
        String sql = "SELECT t.* FROM tickets t " +
                "JOIN vehiculos v ON t.vehiculo_id = v.id " +
                "WHERE v.placa = ? AND t.estado = 'ACTIVO'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return extractTicketFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}