package be.lucas.DAO;

import be.lucas.Model.Vehicle;
import be.lucas.util.DBConnection;

import java.sql.*;

public class VehicleDAO extends DAO<Vehicle> {

    @Override
    public boolean create(Vehicle obj) {
        return false;
    }

    @Override
    public boolean delete(Vehicle obj) {
        return false;
    }

    @Override
    public boolean update(Vehicle obj) {
        return false;
    }

    @Override
    public Vehicle find(int id) {
        return null;
    }

    public boolean insertVehicle(Vehicle vehicle, int personId) throws SQLException {
        String sql = """
            INSERT INTO Vehicle (SeatNumber, BikeSpotNumber, DriverID)
            VALUES (?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, vehicle.getSeatNumber());
            ps.setInt(2, vehicle.getBikeSpotNumber());
            ps.setInt(3, personId);

            if (ps.executeUpdate() > 0) {
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    vehicle.setId(rs.getInt(1));
                }
                return true;
            }
        }
        return false;
    }

    public Vehicle getVehicleByDriverId(int personId) throws SQLException {
        String sql = "SELECT * FROM Vehicle WHERE DriverID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return new Vehicle(
                    rs.getInt("VehicleID"),
                    rs.getInt("SeatNumber"),
                    rs.getInt("BikeSpotNumber")
                );
            }
        }
        return null;
    }
}
