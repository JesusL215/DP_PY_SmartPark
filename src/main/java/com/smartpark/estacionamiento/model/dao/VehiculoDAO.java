package com.smartpark.estacionamiento.model.dao;
import com.smartpark.estacionamiento.model.domain.Vehiculo;
import com.smartpark.estacionamiento.patrones.creacional.singleton.DBConnection;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
public class VehiculoDAO implements IDAO<Vehiculo, Long> {
    private Connection connection = DBConnection.getInstance().getConnection();
    public Vehiculo findByPlaca(String placa) {
        String sql = "SELECT * FROM vehiculos WHERE placa = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, placa);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) return extractVehiculoFromResultSet(rs);
        } catch (SQLException e) { e.printStackTrace(); }
        return null;
    }
    @Override
    public Vehiculo get(Long id) { /* ... implement... */ return null; }
    @Override
    public List<Vehiculo> getAll() { /* ... implement... */ return new ArrayList<>(); }
    @Override
    public void save(Vehiculo vehiculo) {
        String sql = "INSERT INTO vehiculos (placa, tipoVehiculo, propietario) VALUES (?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setString(1, vehiculo.getPlaca());
            stmt.setString(2, vehiculo.getTipoVehiculo());
            stmt.setString(3, vehiculo.getPropietario());
            if (stmt.executeUpdate() > 0) {
                try (ResultSet rs = stmt.getGeneratedKeys()) {
                    if (rs.next()) vehiculo.setId(rs.getLong(1));
                }
            }
        } catch (SQLException e) { e.printStackTrace(); }
    }
    @Override
    public void update(Vehiculo vehiculo) { /* ... implement... */ }
    @Override
    public void delete(Vehiculo vehiculo) { /* ... implement... */ }
    private Vehiculo extractVehiculoFromResultSet(ResultSet rs) throws SQLException {
        Vehiculo v = new Vehiculo();
        v.setId(rs.getLong("id"));
        v.setPlaca(rs.getString("placa"));
        v.setTipoVehiculo(rs.getString("tipoVehiculo"));
        v.setPropietario(rs.getString("propietario"));
        return v;
    }
}