package be.lucas.Model;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import be.lucas.DAO.RideDAO;
import java.util.stream.Collectors;
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

    public int publishRideWithDB(Ride ride) throws SQLException {
        if (category != null) {
            if (category.getCalendar() == null) {
                category.setCalendar(new Calendar(category.getId(), category));
            }
            ride.setCalendar(category.getCalendar());
        }
        RideDAO rideDAO = new RideDAO();
        boolean success = rideDAO.create(ride);
        return success ? ride.getId() : -1;
    }
    
    public void calculateRideFee(Ride ride) {
        ride.calculateFee();
    }
    
    public List<Ride> getAllRides() throws Exception {
        RideDAO dao = new RideDAO();
        return dao.findAllRides();
    }
    
    public Ride getRideById(int rideId) throws Exception {
        RideDAO dao = new RideDAO();
        return dao.find(rideId); 
    }
    
    public List<Ride> getRidesInMyCategory() throws Exception {
        if (category == null) {
            return new ArrayList<>();
        }
        List<Ride> allRides = getAllRides();
        return allRides.stream()
            .filter(r -> r.getCategoryId() == category.getId())
            .collect(Collectors.toList());
    } 
    
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}