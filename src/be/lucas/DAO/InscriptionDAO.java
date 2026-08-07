package be.lucas.DAO;

import be.lucas.Model.Member;
import be.lucas.Model.Ride;
import be.lucas.Model.Vehicle;
import be.lucas.Model.Inscription;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscriptionDAO extends DAO<Inscription> {

    public static final String MSG_SOLDE_INSUFFISANT = "Solde insuffisant pour participer à ce ride.";

    @Override
    public boolean create(Inscription obj) {
        return false;
    }

    @Override
    public boolean delete(Inscription obj) {
        return false;
    }

    @Override
    public boolean update(Inscription obj) {
        return false;
    }

    @Override
    public Inscription find(int id) {
        String sql = """
            SELECT i.InscriptionID, i.RideID, i.IsPassenger, i.IsBike,
                   p.PersonID, p.Name, p.FirstName, p.Phone, p.Password, m.Balance
            FROM Inscription i
            JOIN Member m ON i.MemberID = m.MemberID
            JOIN Person p ON m.PersonID = p.PersonID
            WHERE i.InscriptionID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Member member = new Member(
                    rs.getString("Name"),
                    rs.getString("FirstName"),
                    rs.getString("Phone"),
                    rs.getInt("PersonID"),
                    rs.getString("Password"),
                    rs.getDouble("Balance")
                );

                Ride ride = new RideDAO().getRideWithDetails(rs.getInt("RideID"));
                if (ride == null) {
                    return null;
                }

                Inscription inscription = new Inscription(
                    member,
                    ride,
                    rs.getBoolean("IsPassenger"),
                    rs.getBoolean("IsBike")
                );
                inscription.setId(id);
                return inscription;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
    
    public List<Ride> getRidesByMemberId(int personId) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
            SELECT DISTINCT r.RideID, r.StartPlace, r.StartDate, r.Fee
            FROM Inscription i
            JOIN Ride r ON i.RideID = r.RideID
            WHERE i.MemberID = (
			    SELECT MemberID FROM Member WHERE MemberID = ?
			)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                int rideId = rs.getInt("RideID");
                Ride ride = new Ride(
                    rideId,
                    rs.getString("StartPlace"),
                    rs.getTimestamp("StartDate"),
                    rs.getDouble("Fee")
                );
                
                loadVehiclesForRide(ride);
                loadInscriptionsForRide(ride);
                
                rides.add(ride);
            }
        }
        return rides;
    }
    
    private void loadVehiclesForRide(Ride ride) throws SQLException {
        String sql = """
            SELECT v.VehicleID, v.SeatNumber, v.BikeSpotNumber, v.DriverID
            FROM Ride_Vehicle rv
            JOIN Vehicle v ON rv.VehicleID = v.VehicleID
            WHERE rv.RideID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ride.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                    rs.getInt("VehicleID"),
                    rs.getInt("SeatNumber"),
                    rs.getInt("BikeSpotNumber")
                );
                
                int driverId = rs.getInt("DriverID");
                if (!rs.wasNull()) {
                    MemberDAO memberDAO = new MemberDAO();
                    Member driver = memberDAO.getMemberByPersonId(driverId);
                    vehicle.setDriver(driver);
                }
                
                ride.addVehicle(vehicle);
            }
        }
    }
    
    private void loadInscriptionsForRide(Ride ride) throws SQLException {
    	String sql = """
    		    SELECT i.InscriptionID, i.MemberID, i.IsPassenger, i.IsBike,
    		           m.MemberID AS PersonID, p.Name, p.FirstName, p.Phone, p.Password, m.Balance
    		    FROM Inscription i
    		    JOIN Member m ON i.MemberID = m.MemberID
    		    JOIN Person p ON m.MemberID = p.PersonID
    		    WHERE i.RideID = ?
    		    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ride.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Member member = new Member(
                    rs.getString("Name"),
                    rs.getString("FirstName"),
                    rs.getString("Phone"),
                    rs.getInt("PersonID"),
                    rs.getString("Password"),
                    rs.getDouble("Balance")
                );

                Inscription inscription = new Inscription(
                    member,
                    ride,  
                    rs.getBoolean("IsPassenger"),
                    rs.getBoolean("IsBike")
                );
                inscription.setId(rs.getInt("InscriptionID"));

                ride.addRegistration(inscription);
            }
        }
    }

    public boolean isAlreadyRegistered(int memberId, int rideId) throws SQLException {
        String sql = "SELECT 1 FROM Inscription WHERE MemberID = ? AND RideID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ps.setInt(2, rideId);
            return ps.executeQuery().next();
        }
    }

    public boolean saveRegistration(Member member, int rideId, boolean isPassenger, boolean isBike, double fee) throws Exception {
        String maxSql = "SELECT MAX(InscriptionID) AS MaxID FROM Inscription";
        int nextId = 1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(maxSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getObject("MaxID") != null) {
                nextId = rs.getInt("MaxID") + 1;
            }
        }

        String addCategorySql = """
                INSERT INTO Member_Category (MemberID, CategoryID)
                SELECT m.MemberID, r.CalendarID
                FROM Member m
                JOIN Ride r ON r.RideID = ?
                WHERE m.MemberID = ?
                  AND r.CalendarID IS NOT NULL
                  AND NOT EXISTS (
                      SELECT 1 FROM Member_Category mc
                      WHERE mc.MemberID = m.MemberID
                        AND mc.CategoryID = r.CalendarID
                  )
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(addCategorySql)) {
            ps.setInt(1, rideId);
            ps.setInt(2, member.getId());
            ps.executeUpdate();
        }

        boolean balanceUpdated = member.debitBalance(fee);
        if (!balanceUpdated) {
            throw new Exception("Erreur lors de la mise à jour du solde.");
        }

        String findBikeSql = "SELECT MIN(BikeID) AS AnyBikeID FROM Bike";
        int placeholderBikeId = 0;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(findBikeSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                placeholderBikeId = rs.getInt("AnyBikeID");
            }
        }

        String sql = """
            INSERT INTO Inscription (InscriptionID, MemberID, RideID, IsPassenger, IsBike, BikeID)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nextId);
            ps.setInt(2, member.getId());
            ps.setInt(3, rideId);
            ps.setBoolean(4, isPassenger);
            ps.setBoolean(5, isBike);
            ps.setInt(6, placeholderBikeId);
            boolean inscriptionAdded = ps.executeUpdate() > 0;

            if (!inscriptionAdded) {
                member.creditBalance(fee);
                throw new Exception("Erreur lors de l'enregistrement de l'inscription.");
            }
        }

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement("UPDATE Inscription SET BikeID = NULL WHERE InscriptionID = ?")) {
            ps.setInt(1, nextId);
            ps.executeUpdate();
        }

        return true;
        
    }
}