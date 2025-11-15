package com.smartpark.estacionamiento.model.dao;
import com.smartpark.estacionamiento.model.domain.Vehiculo;
import com.smartpark.estacionamiento.patrones.creacional.singleton.DBConnection;
import com.smartpark.estacionamiento.patrones.creacional.factory.VehiculoFactory;
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
    public Vehiculo get(Long id) {
        String sql = "SELECT * FROM vehiculos WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setLong(1, id);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                // Usamos el helper que ya corregiste, que usa la Factory
                return extractVehiculoFromResultSet(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
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

        // 1. Obtenemos los datos de la BD
        long id = rs.getLong("id");
        String placa = rs.getString("placa");
        String tipoVehiculo = rs.getString("tipoVehiculo"); // Ej: "Auto" o "Moto"
        String propietario = rs.getString("propietario");

        // 2. Usamos la Fábrica para crear la instancia correcta
        Vehiculo vehiculo = VehiculoFactory.createVehiculo(tipoVehiculo, placa, propietario);

        // 3. Asignamos el ID de la base de datos
        vehiculo.setId(id);

        return vehiculo;
    }
}