package be.lucas.DAO;

import be.lucas.Model.Member;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MemberDAO extends PersonDAO {

    /**
     * Charge un Member complet par PersonID
     */
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
                // MemberID n'est pas dans le constructeur → on le set après
                // Tu peux ajouter un setter si besoin
                member.setMembershipPaid(rs.getBoolean("MembershipPaid"));
                return member;
            }
        }
        return null;
    }
}