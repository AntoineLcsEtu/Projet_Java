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
        return null;
    }
    
    public List<Ride> getRidesByMemberId(int personId) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
            SELECT DISTINCT r.RideID, r.StartPlace, r.StartDate, r.Fee
            FROM Inscription i
            JOIN Ride r ON i.RideID = r.RideID
            WHERE i.MemberID = (
                SELECT MemberID FROM Member WHERE PersonID = ?
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
                   m.PersonID, p.Name, p.FirstName, p.Phone, p.Password, m.Balance
            FROM Inscription i
            JOIN Member m ON i.MemberID = m.MemberID
            JOIN Person p ON m.PersonID = p.PersonID
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
    
    public boolean registerMember(Member member, int rideId, boolean isPassenger, boolean isBike) throws Exception {
        if (member.getBalance() < 0) {
            throw new Exception("Solde insuffisant pour participer à ce ride. Solde actuel : " + 
                              String.format("%.2f", member.getBalance()) + " €");
        }

        String maxSql = "SELECT MAX(InscriptionID) AS MaxID FROM Inscription";
        int nextId = 1;  

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(maxSql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next() && rs.getObject("MaxID") != null) {
                nextId = rs.getInt("MaxID") + 1;
            }
        }

        String checkSql = "SELECT 1 FROM Inscription WHERE MemberID = ? AND RideID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, member.getId());
            ps.setInt(2, rideId);
            if (ps.executeQuery().next()) {
                throw new Exception("Vous êtes déjà inscrit à ce ride.");
            }
        }

        Ride ride = new RideDAO().getRideWithDetails(rideId);
        if (ride == null) {
            throw new Exception("Ride introuvable.");
        }

        Vehicle memberVehicle = null;
        boolean isDriver = !isPassenger;

        if (isDriver) {
            try {
                memberVehicle = new VehicleDAO().getVehicleByDriverId(member.getId());
                if (memberVehicle == null) {
                    throw new Exception("Vous devez avoir un véhicule enregistré pour être conducteur.");
                }
            } catch (Exception e) {
                throw new Exception("Erreur lors de la récupération du véhicule : " + e.getMessage());
            }
        }

        boolean needsPassengerSeat = isPassenger;
        boolean needsBikeSpot = isBike;

        if (isDriver && memberVehicle != null) {
            needsPassengerSeat = false; 

            if (isBike) {
                int usedBikeSpotsInOwnVehicle = ride.getUsedBikeSpotsInVehicle(memberVehicle);
                if (usedBikeSpotsInOwnVehicle + 1 > memberVehicle.getBikeSpotNumber()) {
                    throw new Exception("Votre véhicule n'a plus de place pour un vélo.");
                }
                needsBikeSpot = false; 
            }
        }

        if (needsPassengerSeat && ride.getAvailableSeatNumber() <= 0) {
            throw new Exception("Plus de places passager disponibles pour ce ride.");
        }
        if (needsBikeSpot && ride.getAvailableBikeSpotNumber() <= 0) {
            throw new Exception("Plus de places vélo disponibles pour ce ride.");
        }

        double rideFee = ride.getFee();

        if (member.getBalance() < rideFee) {
            throw new Exception("Solde insuffisant pour participer à ce ride. " +
                              "Solde actuel : " + String.format("%.2f", member.getBalance()) + 
                              " €, Frais du ride : " + String.format("%.2f", rideFee) + " €");
        }

        String addCategorySql = """
                INSERT INTO Member_Category (MemberID, CategoryID)
                SELECT m.MemberID, r.CategoryID
                FROM Member m
                JOIN Ride r ON r.RideID = ?
                WHERE m.PersonID = ?
                  AND r.CategoryID IS NOT NULL
                  AND NOT EXISTS (
                      SELECT 1 FROM Member_Category mc
                      WHERE mc.MemberID = m.MemberID
                        AND mc.CategoryID = r.CategoryID
                  )
                """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(addCategorySql)) {
            ps.setInt(1, rideId);
            ps.setInt(2, member.getId());
            ps.executeUpdate();
        }

        double newBalance = member.getBalance() - rideFee;
        MemberDAO memberDAO = new MemberDAO();
        boolean balanceUpdated = memberDAO.creditBalance(member.getId(), -rideFee); // Montant négatif pour déduire
        
        if (!balanceUpdated) {
            throw new Exception("Erreur lors de la mise à jour du solde.");
        }

        member.setBalance(newBalance);

        String sql = """
            INSERT INTO Inscription (InscriptionID, MemberID, RideID, IsPassenger, IsBike)
            VALUES (?, ?, ?, ?, ?)
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, nextId);
            ps.setInt(2, member.getId());
            ps.setInt(3, rideId);
            ps.setBoolean(4, isPassenger);
            ps.setBoolean(5, isBike);
            boolean inscriptionAdded = ps.executeUpdate() > 0;
            
            if (inscriptionAdded) {
                System.out.println("Inscription réussie pour membre " + member.getId() + 
                                 " au ride " + rideId + ". Balance mise à jour : " + newBalance);
                return true;
            } else {
                memberDAO.creditBalance(member.getId(), rideFee);
                member.setBalance(member.getBalance() + rideFee);
                throw new Exception("Erreur lors de l'enregistrement de l'inscription.");
            }
        }
    }
}