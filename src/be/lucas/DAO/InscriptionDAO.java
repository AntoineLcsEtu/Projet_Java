package be.lucas.DAO;

import be.lucas.Model.Member;
import be.lucas.Model.Ride;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class InscriptionDAO {


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
    
    public boolean registerMember(Member member, int rideId, boolean isPassenger, boolean isBike) throws SQLException {
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
            if (ps.executeQuery().next()) return false;
        }

        Ride ride = new RideDAO().getRideWithDetails(rideId);
        if (ride == null) return false;
        if (isPassenger && ride.getAvailableSeatNumber() <= 0) return false;
        if (isBike && ride.getAvailableBikeSpotNumber() <= 0) return false;

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
            return ps.executeUpdate() > 0;
        }
    }
}
