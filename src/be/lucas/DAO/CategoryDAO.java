package be.lucas.DAO;


import be.lucas.Model.Category;
import be.lucas.Model.CategoryType;
import be.lucas.Model.Manager;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CategoryDAO extends DAO<Category> {

    @Override
    public boolean create(Category obj) {
        return false;
    }

    @Override
    public boolean delete(Category obj) {
        return false;
    }

    @Override
    public boolean update(Category obj) {
        return false;
    }

    @Override
    public Category find(int id) {
        try {
            return getCategoryById(id);
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
    public Category getCategoryById(int categoryId) throws SQLException {
        String sql = """
            SELECT c.CategoryID, c.Type
            FROM Category c
            WHERE c.CategoryID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int id = rs.getInt("CategoryID");
                String typeStr = rs.getString("Type");

                CategoryType type;
                try {
                    type = CategoryType.valueOf(typeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Type de catégorie inconnu : " + typeStr);
                }

                Category category = new Category(id, null, type);


                return category;
            }
        }
        return null;
    }
    public Manager getManagerByCategoryId(int categoryId) throws SQLException {
        String sql = """
            SELECT ma.PersonID
            FROM Manager ma
            WHERE ma.CategoryID = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int personId = rs.getInt("PersonID");
                return new ManagerDAO().getManagerByPersonId(personId);
            }
        }
        return null;
    }
}