package be.lucas.DAO;

import be.lucas.Model.Vehicle;
import be.lucas.util.DBConnection;

import java.sql.*;

public class VehicleDAO extends DAO<Vehicle> {

	@Override
	public boolean create(Vehicle obj) {
	    if (obj.getDriver() == null) {
	        return false;
	    }

	    String sql = """
	        INSERT INTO Vehicle (SeatNumber, BikeSpotNumber, DriverID)
	        VALUES (?, ?, ?)
	        """;

	    try (Connection conn = DBConnection.getConnection();
	         PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

	        ps.setInt(1, obj.getSeatNumber());
	        ps.setInt(2, obj.getBikeSpotNumber());
	        ps.setInt(3, obj.getDriver().getId());

	        if (ps.executeUpdate() > 0) {
	            ResultSet rs = ps.getGeneratedKeys();
	            if (rs.next()) {
	                obj.setId(rs.getInt(1));
	            }
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
