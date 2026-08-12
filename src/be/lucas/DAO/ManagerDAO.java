package be.lucas.DAO;

import be.lucas.Model.Manager;
import be.lucas.Model.Category;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ManagerDAO extends DAO<Manager> {

    @Override
    public boolean create(Manager obj) {
        return false;
    }

    @Override
    public boolean delete(Manager obj) {
        return false;
    }

    @Override
    public boolean update(Manager obj) {
        return false;
    }

    @Override
    public Manager find(int id) throws SQLException {
    	String sql = """
                SELECT p.*, ma.ManagerID, ma.CategoryID
                FROM Person p
                JOIN Manager ma ON p.PersonID = ma.ManagerID
                WHERE p.PersonID = ?
                """;

            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                if (rs.next()) {
                	Category category = null;
                	int categoryId = rs.getInt("CategoryID");
                	if (!rs.wasNull()) {
                	    category = new CategoryDAO().find(categoryId);
                	}

                	return new Manager(
                	    rs.getString("Name"),
                	    rs.getString("FirstName"),
                	    rs.getString("Phone"),
                	    rs.getInt("PersonID"),
                	    rs.getString("Password"),
                	    category
                	);
                }
            }
            return null;    }
}
