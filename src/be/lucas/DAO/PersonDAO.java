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
    public Person find(int id) {
        return null;
    }
	
    public String login(int personId, String password) throws SQLException {
        String sql = """
            SELECT p.Password,
                   m.MemberID, ma.ManagerID, t.TreasurerID
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

            if (rs.next() && password.equals(rs.getString("Password"))) {
                if (rs.getObject("MemberID") != null) return "MEMBER";
                if (rs.getObject("ManagerID") != null) return "MANAGER";
                if (rs.getObject("TreasurerID") != null) return "TREASURER";
            }
        }
        return null;
    }
    
}
