package com.smartpark.estacionamiento.model.dao;
import com.smartpark.estacionamiento.model.domain.ParkingSlot;
import com.smartpark.estacionamiento.patrones.comportamiento.state.*;
import com.smartpark.estacionamiento.patrones.creacional.singleton.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class ParkingSlotDAO implements IDAO<ParkingSlot, Long> {
    private Connection connection = DBConnection.getInstance().getConnection();
    @Override
    public ParkingSlot get(Long id) {
        String sql = "SELECT * FROM parking_slots WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractSlotFromResultSet(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    @Override
    public List<ParkingSlot> getAll() {
        List<ParkingSlot> slots = new ArrayList<>();
        String sql = "SELECT * FROM parking_slots";
        try (Statement stmt = connection.createStatement()) {
            ResultSet rs = stmt.executeQuery(sql);
            while (rs.next()) slots.add(extractSlotFromResultSet(rs));
        } catch (SQLException e) { e.printStackTrace(); }
        return slots;
    }
    @Override
    public void save(ParkingSlot slot) { /* ... implement... */ }
    @Override
    public void update(ParkingSlot slot) {
        String sql = "UPDATE parking_slots SET estado = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, slot.getCurrentState().getEstado());
            stmt.setLong(2, slot.getId());
            stmt.executeUpdate();
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public void delete(ParkingSlot slot) { /* ... implement... */ }
    private ParkingSlot extractSlotFromResultSet(ResultSet rs) throws SQLException {
        ParkingSlot slot = new ParkingSlot();
        slot.setId(rs.getLong("id"));
        slot.setNumeroSlot(rs.getString("numeroSlot"));
        slot.setTipo(rs.getString("tipo"));
        String estado = rs.getString("estado");
        if ("Ocupado".equals(estado)) {
            slot.setCurrentState(new OccupiedState());
        } else {
            slot.setCurrentState(new AvailableState());
        }
        return slot;
    }
}