package be.lucas.Model;

public class Manager extends Person {
    private Category category;

    public Manager(String name, String firstName, String phone, int id, String password, Category category) {
        super(name, firstName, phone, id, password);
        this.category = category;
    }

    public void publishCalendar(Ride ride) {
        if (category != null && category.getCalendar() != null) {
            category.getCalendar().addRide(ride);
        }
    }

    public void calculateRideFee(Ride ride) {
        int totalSeats = ride.getVehicles().stream().mapToInt(Vehicle::getSeatNumber).sum();
        int totalBikeSpots = ride.getVehicles().stream().mapToInt(Vehicle::getBikeSpotNumber).sum();
        ride.setFee(totalSeats * 10.0 + totalBikeSpots * 5.0);
    }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
