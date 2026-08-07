package be.lucas.DAO;

import be.lucas.Model.Person;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonDAO extends DAO<Person> {

    @Override
    public boolean create(Person obj) {
        return false;
    }

    @Override
    public boolean delete(Person obj) {
        return false;
    }

    @Override
    public boolean update(Person obj) {
        return false;
    }

    @Override
    public Person find(int id) throws SQLException {
        String role = getRole(id);
        if (role == null) return null;

        return switch (role) {
            case "MEMBER"    -> new MemberDAO().getMemberByPersonId(id);
            case "MANAGER"   -> new ManagerDAO().getManagerByPersonId(id);
            case "TREASURER" -> new TreasurerDAO().getTreasurerByPersonId(id);
            default          -> null;
        };
    }
	
    public String getStoredPassword(int personId) throws SQLException {
        String sql = "SELECT Password FROM Person WHERE PersonID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                return rs.getString("Password");
            }
        }
        return null;
    }

    public String getRole(int personId) throws SQLException {
        String sql = """
            SELECT m.MemberID, ma.ManagerID, t.TreasurerID
            FROM Person p
            LEFT JOIN Member m ON p.PersonID = m.PersonID
            LEFT JOIN Manager ma ON p.PersonID = ma.PersonID
            LEFT JOIN Treasurer t ON p.PersonID = t.PersonID
            WHERE p.PersonID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                if (rs.getObject("MemberID") != null) return "MEMBER";
                if (rs.getObject("ManagerID") != null) return "MANAGER";
                if (rs.getObject("TreasurerID") != null) return "TREASURER";
            }
        }
        return null;
    }
    
}
