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
    public Category find(int id) throws SQLException {
        String sql = """
            SELECT c.CategoryID, c.Type
            FROM Category c
            WHERE c.CategoryID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int categoryId = rs.getInt("CategoryID");
                String typeStr = rs.getString("Type");

                CategoryType type;
                try {
                    type = CategoryType.valueOf(typeStr.toUpperCase());
                } catch (IllegalArgumentException e) {
                    throw new SQLException("Type de catégorie inconnu : " + typeStr);
                }

                Category category = new Category(categoryId, null, type);


                return category;
            }
        }
        return null;
    }
    
    
    public Manager findManagerByCategoryId(int categoryId) throws SQLException {
        String sql = """
            SELECT ma.ManagerID AS PersonID
            FROM Manager ma
            WHERE ma.CategoryID = ?
            """;
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, categoryId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                int personId = rs.getInt("PersonID");
                return new ManagerDAO().find(personId);
            }
        }
        return null;
    }
}