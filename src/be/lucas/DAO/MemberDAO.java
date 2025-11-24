package be.lucas.DAO;

import be.lucas.Model.Category;
import be.lucas.Model.Member;
import be.lucas.Model.Ride;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MemberDAO extends PersonDAO {

    public Member getMemberByPersonId(int personId) throws SQLException {
        String sql = """
            SELECT p.*, m.MemberID, m.Balance, m.MembershipPaid
            FROM Person p
            JOIN Member m ON p.PersonID = m.PersonID
            WHERE p.PersonID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
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

                member.setMembershipPaid(rs.getBoolean("MembershipPaid"));
                return member;
            }
        }
        return null;
    }
    
    public List<Ride> getRidesByMember(Member member) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
            SELECT r.RideID, r.StartPlace, r.StartDate, r.Fee
            FROM Inscription i
            JOIN Ride r ON i.RideID = r.RideID
            WHERE i.MemberID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, member.getId());
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
    
    public boolean updateMembershipPaid(int personId, double newBalance, boolean paid) throws SQLException {
        String sql = """
            UPDATE Member 
            SET Balance = ?, MembershipPaid = ? 
            WHERE PersonID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDouble(1, newBalance);
            ps.setBoolean(2, paid);
            ps.setInt(3, personId);

            return ps.executeUpdate() > 0;
        }
    }
    
    public int getCategoryCountForMember(int personId) throws SQLException {
        String sql = """
            SELECT COUNT(*) 
            FROM Member_Category mc
            JOIN Member m ON mc.MemberID = m.MemberID
            WHERE m.PersonID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<Member> getAllMembersWithCategories() throws SQLException {
        List<Member> members = new ArrayList<>();
        String sql = """
            SELECT p.PersonID, p.Name, p.FirstName, p.Phone, p.Password,
                   m.Balance, m.MembershipPaid,
                   c.CategoryID
            FROM Person p
            JOIN Member m ON p.PersonID = m.PersonID
            LEFT JOIN Member_Category mc ON m.MemberID = mc.MemberID
            LEFT JOIN Category c ON mc.CategoryID = c.CategoryID
            ORDER BY p.Name, p.FirstName
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Member current = null;
            int currentId = -1;

            while (rs.next()) {
                int personId = rs.getInt("PersonID");
                if (personId != currentId) {
                    current = new Member(
                        rs.getString("Name"),
                        rs.getString("FirstName"),
                        rs.getString("Phone"),  
                        personId,
                        rs.getString("Password"),
                        rs.getDouble("Balance")
                    );
                    current.setMembershipPaid(rs.getBoolean("MembershipPaid"));
                    members.add(current);
                    currentId = personId;
                }

                int categoryId = rs.getInt("CategoryID");
                if (!rs.wasNull()) {
                    Category cat = new Category(categoryId, null, null);
                    current.getCategories().add(cat);
                }
            }
        }
        return members;
    }
    
    public boolean creditBalance(int personId, double amount) throws SQLException {
        String sql = "UPDATE Member SET Balance = Balance + ? WHERE PersonID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, amount);
            ps.setInt(2, personId);
            return ps.executeUpdate() > 0;
        }
    }
}