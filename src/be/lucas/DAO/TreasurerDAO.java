package be.lucas.DAO;


import be.lucas.Model.Treasurer;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class TreasurerDAO extends PersonDAO {

    /**
     * Charge un Treasurer
     */
    public Treasurer getTreasurerByPersonId(int personId) throws SQLException {
        String sql = """
            SELECT p.*, t.TreasurerID
            FROM Person p
            JOIN Treasurer t ON p.PersonID = t.PersonID
            WHERE p.PersonID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return new Treasurer(
                    rs.getString("Name"),
                    rs.getString("FirstName"),
                    rs.getString("Phone"),
                    rs.getInt("PersonID"),
                    rs.getString("Password")
                );
            }
        }
        return null;
    }
}
