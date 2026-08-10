package be.lucas.DAO;

import be.lucas.Model.Bike;
import be.lucas.Model.Member;
import be.lucas.util.DBConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BikeDAO extends DAO<Bike> {

    @Override
    public boolean create(Bike obj) {
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
    public Bike find(int id) {
        String sql = "SELECT BikeID, Weight, Type, Length, MemberID FROM Bike WHERE BikeID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, id);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                Member owner = new MemberDAO().getMemberByPersonId(rs.getInt("MemberID"));
                return new Bike(
                    rs.getInt("BikeID"),
                    rs.getDouble("Weight"),
                    rs.getString("Type"),
                    rs.getDouble("Length"),
                    owner
                );
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }
}