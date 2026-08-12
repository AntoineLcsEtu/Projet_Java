package be.lucas.DAO;

import be.lucas.Model.*;
import be.lucas.util.DBConnection;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RideDAO extends DAO<Ride> {

	@Override
	public boolean create(Ride obj) {
	    try {
	        String maxSql = "SELECT COALESCE(MAX(RideID), 0) AS MaxID FROM Ride";
	        int newId = 1;

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement maxPs = conn.prepareStatement(maxSql);
	             ResultSet rs = maxPs.executeQuery()) {
	            if (rs.next()) {
	                newId = rs.getInt("MaxID") + 1;
	            }
	        }

	        String insertSql = """
	            INSERT INTO Ride (RideID, StartPlace, StartDate, Fee, CalendarID)
	            VALUES (?, ?, ?, ?, ?)
	            """;

	        try (Connection conn = DBConnection.getConnection();
	             PreparedStatement ps = conn.prepareStatement(insertSql)) {

	            ps.setInt(1, newId);
	            ps.setString(2, obj.getStartPlace());
	            ps.setTimestamp(3, new java.sql.Timestamp(obj.getStartDate().getTime()));
	            ps.setDouble(4, obj.getFee());
	            ps.setInt(5, obj.getCategoryId());

	            if (ps.executeUpdate() > 0) {
	                obj.setId(newId);
	                return true;
	            }
	        }
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
	    return false;
	}

    @Override
    public boolean delete(Ride obj) {
        return false;
    }

    @Override
    public boolean update(Ride obj) {
        return false;
    }

    @Override
    public Ride find(int id) throws SQLException {
    	String sql = """
                SELECT r.*, v.VehicleID, v.SeatNumber, v.BikeSpotNumber, v.DriverID
                FROM Ride r
                LEFT JOIN Ride_Vehicle rv ON r.RideID = rv.RideID
                LEFT JOIN Vehicle v ON rv.VehicleID = v.VehicleID
                WHERE r.RideID = ?
                """;

            Ride ride = null;
            try (Connection conn = DBConnection.getConnection();
                 PreparedStatement ps = conn.prepareStatement(sql)) {

                ps.setInt(1, id);
                ResultSet rs = ps.executeQuery();

                while (rs.next()) {
                    if (ride == null) {
                        ride = new Ride(
                            rs.getInt("RideID"),
                            rs.getString("StartPlace"),
                            rs.getTimestamp("StartDate"),
                            rs.getDouble("Fee")
                        );
                        
                        int categoryId = rs.getInt("CalendarID");
                        if (!rs.wasNull()) {
                            CategoryDAO categoryDAO = new CategoryDAO();
                            Category category = categoryDAO.find(categoryId);
                            if (category != null) {
                                if (category.getCalendar() == null) {
                                    category.setCalendar(new Calendar(categoryId, category));
                                }
                                ride.setCalendar(category.getCalendar());
                            }
                        }
                        
                        new InscriptionDAO().loadInscriptionsForRide(ride);
                    }

                    int vehicleId = rs.getInt("VehicleID");
                    if (!rs.wasNull()) {
                        Vehicle vehicle = new Vehicle(
                            vehicleId,
                            rs.getInt("SeatNumber"),
                            rs.getInt("BikeSpotNumber")
                        );
                        
                        int driverId = rs.getInt("DriverID");
                        if (!rs.wasNull()) {
                            MemberDAO memberDAO = new MemberDAO();
                            Member driver = memberDAO.find(driverId);
                            vehicle.setDriver(driver);
                        }
                        
                        ride.addVehicle(vehicle);
                    }
                }
            }
            return ride;
    }

    public List<Ride> findAllRides() throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
        	    SELECT r.RideID, r.StartPlace, r.StartDate, r.Fee, r.CalendarID AS CategoryID,
        	           v.VehicleID, v.SeatNumber, v.BikeSpotNumber, v.DriverID
        	    FROM Ride r
        	    LEFT JOIN Ride_Vehicle rv ON r.RideID = rv.RideID
        	    LEFT JOIN Vehicle v ON rv.VehicleID = v.VehicleID
        	    ORDER BY r.StartDate
        	    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            Ride currentRide = null;
            int currentRideId = -1;

            while (rs.next()) {
                int rideId = rs.getInt("RideID");

                if (rideId != currentRideId) {
                    currentRide = new Ride(
                        rideId,
                        rs.getString("StartPlace"),
                        rs.getTimestamp("StartDate"),
                        rs.getDouble("Fee")
                    );
                    
                    int categoryId = rs.getInt("CategoryID");
                    if (!rs.wasNull()) {
                        CategoryDAO categoryDAO = new CategoryDAO();
                        Category category = categoryDAO.find(categoryId);
                        if (category != null) {
                            if (category.getCalendar() == null) {
                                category.setCalendar(new Calendar(categoryId, category));
                            }
                            currentRide.setCalendar(category.getCalendar());
                        }
                    }
                    
                    new InscriptionDAO().loadInscriptionsForRide(currentRide);                    
                    rides.add(currentRide);
                    currentRideId = rideId;
                }

                int vehicleId = rs.getInt("VehicleID");
                if (!rs.wasNull()) {
                    Vehicle vehicle = new Vehicle(
                        vehicleId,
                        rs.getInt("SeatNumber"),
                        rs.getInt("BikeSpotNumber")
                    );
                    
                    int driverId = rs.getInt("DriverID");
                    if (!rs.wasNull()) {
                        MemberDAO memberDAO = new MemberDAO();
                        Member driver = memberDAO.find(driverId);
                        vehicle.setDriver(driver);
                    }
                    
                    currentRide.addVehicle(vehicle);
                }
            }
        }
        return rides;
    }
    
    
    public List<Ride> findAvailableRidesForMember(int personId) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
            SELECT r.RideID, r.StartPlace, r.StartDate, r.Fee, r.CalendarID AS CategoryID
			FROM Ride r
			WHERE r.RideID NOT IN (
                SELECT i.RideID FROM Inscription i
                JOIN Member m ON i.MemberID = m.MemberID
                WHERE m.MemberID = ?
            )
            ORDER BY r.StartDate
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, personId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Ride ride = new Ride(
                    rs.getInt("RideID"),
                    rs.getString("StartPlace"),
                    rs.getTimestamp("StartDate"),
                    rs.getDouble("Fee")
                );
                
                int categoryId = rs.getInt("CategoryID");
                if (!rs.wasNull()) {
                    CategoryDAO categoryDAO = new CategoryDAO();
                    Category category = categoryDAO.find(categoryId);
                    if (category != null) {
                        if (category.getCalendar() == null) {
                            category.setCalendar(new Calendar(categoryId, category));
                        }
                        ride.setCalendar(category.getCalendar());
                    }
                }
                
                loadVehiclesForRide(ride);
                new InscriptionDAO().loadInscriptionsForRide(ride);
                
                rides.add(ride);
            }
        }
        return rides;
    }
    
    private void loadVehiclesForRide(Ride ride) throws SQLException {
        String sql = """
            SELECT v.VehicleID, v.SeatNumber, v.BikeSpotNumber, v.DriverID
            FROM Ride_Vehicle rv
            JOIN Vehicle v ON rv.VehicleID = v.VehicleID
            WHERE rv.RideID = ?
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, ride.getId());
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Vehicle vehicle = new Vehicle(
                    rs.getInt("VehicleID"),
                    rs.getInt("SeatNumber"),
                    rs.getInt("BikeSpotNumber")
                );
                
                int driverId = rs.getInt("DriverID");
                if (!rs.wasNull()) {
                    MemberDAO memberDAO = new MemberDAO();
                    Member driver = memberDAO.find(driverId);
                    vehicle.setDriver(driver);
                }
                
                ride.addVehicle(vehicle);
            }
        }
    }
    
    public List<Ride> findRidesForVehicleOffer(int personId) throws SQLException {
        List<Ride> rides = new ArrayList<>();
        String sql = """
    	    SELECT DISTINCT r.RideID, r.StartPlace, r.StartDate, r.Fee, r.CalendarID AS CategoryID
    	    FROM Ride r
    	    JOIN Inscription i ON r.RideID = i.RideID
            JOIN Member m ON i.MemberID = m.MemberID
            LEFT JOIN Ride_Vehicle rv ON r.RideID = rv.RideID
            LEFT JOIN Vehicle v ON rv.VehicleID = v.VehicleID
            WHERE m.MemberID = ?
        	  AND (v.DriverID IS NULL OR v.DriverID != ?)	
              AND NOT EXISTS (
                  SELECT 1 FROM Ride_Vehicle rv2
                  JOIN Vehicle v2 ON rv2.VehicleID = v2.VehicleID
                  WHERE rv2.RideID = r.RideID AND v2.DriverID = ?
              )
            ORDER BY r.StartDate
            """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, personId);
            ps.setInt(2, personId);
            ps.setInt(3, personId);  
            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                Ride ride = new Ride(
                    rs.getInt("RideID"),
                    rs.getString("StartPlace"),
                    rs.getTimestamp("StartDate"),
                    rs.getDouble("Fee")
                );
                
                int categoryId = rs.getInt("CategoryID");
                if (!rs.wasNull()) {
                    CategoryDAO categoryDAO = new CategoryDAO();
                    Category category = categoryDAO.find(categoryId);
                    if (category != null) {
                        if (category.getCalendar() == null) {
                            category.setCalendar(new Calendar(categoryId, category));
                        }
                        ride.setCalendar(category.getCalendar());
                    }
                }
                
                loadVehiclesForRide(ride);
                new InscriptionDAO().loadInscriptionsForRide(ride);
                
                rides.add(ride);
            }
        }
        return rides;
    }
    
    public boolean assignVehicleToRide(int vehicleId, int rideId) throws SQLException {
        String checkSql = "SELECT 1 FROM Ride_Vehicle WHERE RideID = ? AND VehicleID = ?";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(checkSql)) {
            ps.setInt(1, rideId);
            ps.setInt(2, vehicleId);
            if (ps.executeQuery().next()) return false;
        }

        String sql = "INSERT INTO Ride_Vehicle (RideID, VehicleID) VALUES (?, ?)";
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, rideId);
            ps.setInt(2, vehicleId);
            return ps.executeUpdate() > 0;
        }
    }
    
    public List<Member> findDriversFromCompletedRides() throws SQLException {
        List<Member> drivers = new ArrayList<>();
        String sql = """
        	    SELECT DISTINCT m.MemberID AS PersonID, p.Name, p.FirstName, p.Phone, p.Password,
        	           m.Balance, v.SeatNumber, v.BikeSpotNumber
        	    FROM Ride r
        	    JOIN Ride_Vehicle rv ON r.RideID = rv.RideID
        	    JOIN Vehicle v ON rv.VehicleID = v.VehicleID
        	    JOIN Member m ON v.DriverID = m.MemberID
        	    JOIN Person p ON m.MemberID = p.PersonID
        	    WHERE r.StartDate < ?
        	    """;

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, new java.sql.Timestamp(System.currentTimeMillis()));
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                Member driver = new Member(
                    rs.getString("Name"),
                    rs.getString("FirstName"),
                    rs.getString("Phone"),
                    rs.getInt("PersonID"),
                    rs.getString("Password"),
                    rs.getDouble("Balance")
                );

                Vehicle vehicle = new Vehicle(0, rs.getInt("SeatNumber"), rs.getInt("BikeSpotNumber"));
                vehicle.setDriver(driver);
                driver.setDrivenVehicle(vehicle);

                drivers.add(driver);
            }
        }
        return drivers;
    }
    
    
    
    
}