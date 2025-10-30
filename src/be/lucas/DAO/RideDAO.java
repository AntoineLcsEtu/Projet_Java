package be.lucas.DAO;

import be.lucas.Model.*;
import be.lucas.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideDAO {

    public List<Ride> getAllRides() throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
            SELECT r.RideID, r.StartPlace, r.StartDate, r.Fee, r.CategoryID,
                   v.VehicleID, v.SeatNumber, v.BikeSpotNumber, v.DriverID
            FROM Ride r
            LEFT JOIN Ride_Vehicle rv ON r.RideID = rv.RideID
            LEFT JOIN Vehicle v ON rv.VehicleID = v.VehicleID
            ORDER BY r.StartDate
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Ride currentRide = null;
            int currentRideId = -1;

            while (rs.next()) {
                int rideId = rs.getInt("RideID");

                if (rideId != currentRideId) {
                    currentRide = new Ride(
                        rideId,
                        rs.getString("StartPlace"),
                        rs.getTimestamp("StartDate"),
                        rs.getDouble("Fee")
                    );

                    rides.add(currentRide);
                    currentRideId = rideId;
                }

                int vehicleId = rs.getInt("VehicleID");
                if (!rs.wasNull()) {
                    Vehicle vehicle = new Vehicle(
                        vehicleId,
                        rs.getInt("SeatNumber"),
                        rs.getInt("BikeSpotNumber")
                    );
                    currentRide.addVehicle(vehicle);
                }
            }
        }
        return rides;
    }
    
    public Ride getRideWithDetails(int rideId) throws SQLException {
        String sql = """
            SELECT r.*, v.VehicleID, v.SeatNumber, v.BikeSpotNumber, v.DriverID
            FROM Ride r
            LEFT JOIN Ride_Vehicle rv ON r.RideID = rv.RideID
            LEFT JOIN Vehicle v ON rv.VehicleID = v.VehicleID
            WHERE r.RideID = ?
            """;

        Ride ride = null;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, rideId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                if (ride == null) {
                    ride = new Ride(
                        rs.getInt("RideID"),
                        rs.getString("StartPlace"),
                        rs.getTimestamp("StartDate"),
                        rs.getDouble("Fee")
                    );
                }

                int vehicleId = rs.getInt("VehicleID");
                if (!rs.wasNull()) {
                    Vehicle vehicle = new Vehicle(
                        vehicleId,
                        rs.getInt("SeatNumber"),
                        rs.getInt("BikeSpotNumber")
                    );
                    ride.addVehicle(vehicle);
                }
            }
        }
        return ride;
    }
    
    public List<Ride> getAvailableRidesForMember(int personId) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
            SELECT r.RideID, r.StartPlace, r.StartDate, r.Fee
            FROM Ride r
            WHERE r.RideID NOT IN (
                SELECT i.RideID FROM Inscription i
                JOIN Member m ON i.MemberID = m.MemberID
                WHERE m.PersonID = ?
            )
            ORDER BY r.StartDate
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ride ride = new Ride(
                    rs.getInt("RideID"),
                    rs.getString("StartPlace"),
                    rs.getTimestamp("StartDate"),
                    rs.getDouble("Fee")
                );
                rides.add(ride);
            }
        }
        return rides;
    }
    
    public List<Ride> getRidesForVehicleOffer(int personId) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
            SELECT DISTINCT r.RideID, r.StartPlace, r.StartDate, r.Fee
            FROM Ride r
            JOIN Inscription i ON r.RideID = i.RideID
            JOIN Member m ON i.MemberID = m.MemberID
            LEFT JOIN Ride_Vehicle rv ON r.RideID = rv.RideID
            LEFT JOIN Vehicle v ON rv.VehicleID = v.VehicleID
            WHERE m.PersonID = ?
              AND (v.DriverID IS NULL OR v.DriverID != ?)
              AND NOT EXISTS (
                  SELECT 1 FROM Ride_Vehicle rv2
                  JOIN Vehicle v2 ON rv2.VehicleID = v2.VehicleID
                  WHERE rv2.RideID = r.RideID AND v2.DriverID = ?
              )
            ORDER BY r.StartDate
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ps.setInt(2, personId);
            ps.setInt(3, personId);  
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ride ride = new Ride(
                    rs.getInt("RideID"),
                    rs.getString("StartPlace"),
                    rs.getTimestamp("StartDate"),
                    rs.getDouble("Fee")
                );
                rides.add(ride);
            }
        }
        return rides;
    }
    
    public boolean assignVehicleToRide(int vehicleId, int rideId) throws SQLException {
        String checkSql = "SELECT 1 FROM Ride_Vehicle WHERE RideID = ? AND VehicleID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, rideId);
            ps.setInt(2, vehicleId);
            if (ps.executeQuery().next()) return false;
        }

        String sql = "INSERT INTO Ride_Vehicle (RideID, VehicleID) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rideId);
            ps.setInt(2, vehicleId);
            return ps.executeUpdate() > 0;
        }
    }
}
