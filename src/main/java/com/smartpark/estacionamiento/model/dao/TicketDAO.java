package com.smartpark.estacionamiento.model.dao;
import com.smartpark.estacionamiento.model.domain.*;
import com.smartpark.estacionamiento.patrones.creacional.singleton.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class TicketDAO implements IDAO<Ticket, Long> {
    private Connection connection = DBConnection.getInstance().getConnection();
    // Ojo: Esta implementación simple asume que ya tienes los DAO inyectados
    // pero para simplicidad los instanciamos aquí.
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
    public List<Ticket> getAll() { /* ... implement... */ return new ArrayList<>(); }
    @Override
    public void save(Ticket ticket) {
        String sql = "INSERT INTO tickets (horaEntrada, estado, vehiculo_id, parkingslot_id) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setTimestamp(1, Timestamp.valueOf(ticket.getHoraEntrada()));
            stmt.setString(2, ticket.getEstado());
            stmt.setLong(3, ticket.getVehiculo().getId());
            stmt.setLong(4, ticket.getParkingSlot().getId());
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) ticket.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public void update(Ticket ticket) {
        String sql = "UPDATE tickets SET horaSalida = ?, montoPagado = ?, estado = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setTimestamp(1, Timestamp.valueOf(ticket.getHoraSalida()));
            stmt.setDouble(2, ticket.getMontoPagado());
            stmt.setString(3, ticket.getEstado());
            stmt.setLong(4, ticket.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public void delete(Ticket ticket) { /* ... implement... */ }
    private Ticket extractTicketFromResultSet(ResultSet rs) throws SQLException {
        Ticket t = new Ticket();
        t.setId(rs.getLong("id"));
        t.setHoraEntrada(rs.getTimestamp("horaEntrada").toLocalDateTime());
        Timestamp tsSalida = rs.getTimestamp("horaSalida");
        if(tsSalida != null) t.setHoraSalida(tsSalida.toLocalDateTime());
        t.setMontoPagado(rs.getDouble("montoPagado"));
        t.setEstado(rs.getString("estado"));
        t.setVehiculo(vehiculoDAO.get(rs.getLong("vehiculo_id"))); // Reconstruye el objeto
        t.setParkingSlot(parkingSlotDAO.get(rs.getLong("parkingslot_id"))); // Reconstruye el objeto
        return t;
    }
    /**
     * Busca un ticket activo (no pagado) usando la placa del vehículo.
     * @param placa La placa del vehículo.
     * @return El Ticket activo, o null si no se encuentra.
     */
    public Ticket findActiveTicketByPlaca(String placa) {
        // Esta consulta SQL une las tablas tickets y vehiculos
        String sql = "SELECT t.* FROM tickets t " +
                "JOIN vehiculos v ON t.vehiculo_id = v.id " +
                "WHERE v.placa = ? AND t.estado = 'ACTIVO'";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Usamos el helper que ya tenías para crear el objeto Ticket
                return extractTicketFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // No se encontró un ticket activo para esa placa
    }
}