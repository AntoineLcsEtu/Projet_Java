package be.lucas.Model;

import java.sql.SQLException;
import java.util.List;

import be.lucas.DAO.RideDAO;

public class Manager extends Person {
    private Category category;

    public Manager(String name, String firstName, String phone, int id, String password, Category category) {
        super(name, firstName, phone, id, password);
        this.category = category;
    }

    public void publishCalendar(Ride ride) {
        if (category != null && category.getCalendar() != null) {
            Calendar calendar = category.getCalendar();
            calendar.addRide(ride);
            ride.setCalendar(calendar);  
        }
    }

    public int publishRideWithDB(Ride ride, int categoryId) throws SQLException {
        RideDAO rideDAO = new RideDAO();
        return rideDAO.insertRideManualId(ride, categoryId);
    }
    
    public void calculateRideFee(Ride ride) {
        ride.calculateFee();
    }
    
    public List<Ride> getAllRides() throws Exception {
        RideDAO dao = new RideDAO();
        return dao.getAllRides();
    }
    
    public Ride getRideById(int rideId) throws Exception {
        RideDAO dao = new RideDAO();
        return dao.find(rideId); 
    }
    
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}