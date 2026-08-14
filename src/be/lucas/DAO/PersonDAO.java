package be.lucas.DAO;

import be.lucas.Model.Person;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class PersonDAO extends DAO<Person> {

    public static class AuthInfo {
        public final String password;
        public final String role;

        public AuthInfo(String password, String role) {
            this.password = password;
            this.role = role;
        }
    }

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
        AuthInfo info = findAuthInfo(id);
        if (info == null || info.role == null) return null;

        return switch (info.role) {
            case "MEMBER"    -> new MemberDAO().find(id);
            case "MANAGER"   -> new ManagerDAO().find(id);
            case "TREASURER" -> new TreasurerDAO().find(id);
            default          -> null;
        };
    }

    public AuthInfo findAuthInfo(int personId) throws SQLException {
        String sql = """
            SELECT p.Password, m.MemberID, ma.ManagerID, t.TreasurerID
            FROM Person p
            LEFT JOIN Member m ON p.PersonID = m.MemberID
            LEFT JOIN Manager ma ON p.PersonID = ma.ManagerID
            LEFT JOIN Treasurer t ON p.PersonID = t.TreasurerID
            WHERE p.PersonID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                String password = rs.getString("Password");
                String role = null;
                if (rs.getObject("MemberID") != null) role = "MEMBER";
                else if (rs.getObject("ManagerID") != null) role = "MANAGER";
                else if (rs.getObject("TreasurerID") != null) role = "TREASURER";
                return new AuthInfo(password, role);
            }
        }
        return null;
    }
}