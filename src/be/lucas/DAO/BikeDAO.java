package be.lucas.DAO;

import be.lucas.Model.Bike;
import be.lucas.Model.CategoryType;
import be.lucas.Model.Member;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class BikeDAO extends DAO<Bike> {

    @Override
    public boolean create(Bike obj) {
        if (obj.getMember() == null) {
            return false;
        }

        String maxSql = "SELECT COALESCE(MAX(BikeID), 0) AS MaxID FROM Bike";
        int newId = 1;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement maxPs = conn.prepareStatement(maxSql);
             ResultSet rs = maxPs.executeQuery()) {
            if (rs.next()) {
                newId = rs.getInt("MaxID") + 1;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

        String sql = "INSERT INTO Bike (BikeID, Weight, Type, Length, MemberID) VALUES (?, ?, ?, ?, ?)";

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, newId);
            ps.setDouble(2, obj.getWeight());
            ps.setString(3, obj.getType().name());
            ps.setDouble(4, obj.getLength());
            ps.setInt(5, obj.getMember().getId());

            if (ps.executeUpdate() > 0) {
                obj.setId(newId);
                return true;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delete(Bike obj) {
        return false;
    }

    @Override
    public boolean update(Bike obj) {
        return false;
    }

    @Override
    public Bike find(int id) throws SQLException {
        String sql = "SELECT BikeID, Weight, Type, Length, MemberID FROM Bike WHERE BikeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Member owner = new MemberDAO().getMemberByPersonId(rs.getInt("MemberID"));

                CategoryType type = null;
                String typeStr = rs.getString("Type");
                try {
                    type = CategoryType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Type de vélo inconnu en base pour BikeID " + id + " : " + typeStr);
                }

                return new Bike(
                    rs.getInt("BikeID"),
                    rs.getDouble("Weight"),
                    type,
                    rs.getDouble("Length"),
                    owner
                );
            }
        }
        return null;
    }

    public List<Bike> getBikesByMemberId(int memberId) throws SQLException {
        List<Bike> bikes = new ArrayList<>();
        String sql = "SELECT BikeID, Weight, Type, Length FROM Bike WHERE MemberID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, memberId);
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                CategoryType type = null;
                String typeStr = rs.getString("Type");
                try {
                    type = CategoryType.valueOf(typeStr);
                } catch (IllegalArgumentException e) {
                    System.err.println("Type de vélo inconnu en base pour BikeID " + rs.getInt("BikeID") + " : " + typeStr);
                }
                bikes.add(new Bike(rs.getInt("BikeID"), rs.getDouble("Weight"), type, rs.getDouble("Length"), null));
            }
        }
        return bikes;
    }
}