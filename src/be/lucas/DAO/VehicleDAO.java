package be.lucas.DAO;

import be.lucas.Model.Vehicle;
import be.lucas.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO extends DAO<Vehicle> {

	@Override
	public boolean create(Vehicle obj) {
	    if (obj.getDriver() == null) {
	        return false;
	    }

	    String maxSql = "SELECT COALESCE(MAX(VehicleID), 0) AS MaxID FROM Vehicle";
	    int newId = 1;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(maxSql);
	         ResultSet rs = ps.executeQuery()) {
	        if (rs.next()) {
	            newId = rs.getInt("MaxID") + 1;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	        return false;
	    }

	    String sql = """
	        INSERT INTO Vehicle (VehicleID, SeatNumber, BikeSpotNumber, DriverID)
	        VALUES (?, ?, ?, ?)
	        """;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql)) {

	        ps.setInt(1, newId);
	        ps.setInt(2, obj.getSeatNumber());
	        ps.setInt(3, obj.getBikeSpotNumber());
	        ps.setInt(4, obj.getDriver().getId());

	        if (ps.executeUpdate() > 0) {
	            obj.setId(newId);
	            return true;
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
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
    public Vehicle find(int id) throws SQLException {
        String sql = "SELECT VehicleID, SeatNumber, BikeSpotNumber FROM Vehicle WHERE VehicleID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
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

    public List<Vehicle> findVehiclesByDriverId(int personId) throws SQLException {
        List<Vehicle> vehicles = new ArrayList<>();
        String sql = "SELECT VehicleID, SeatNumber, BikeSpotNumber FROM Vehicle WHERE DriverID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                vehicles.add(new Vehicle(
                    rs.getInt("VehicleID"),
                    rs.getInt("SeatNumber"),
                    rs.getInt("BikeSpotNumber")
                ));
            }
        }
        return vehicles;
    }
}
