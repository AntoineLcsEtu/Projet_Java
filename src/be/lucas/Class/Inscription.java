package be.lucas.Class;

public class Inscription {
    private boolean passenger;
    private boolean bike;
    private Ride ride; // Relation 0*-1 with Ride

    // Constructor
    public Inscription(boolean passenger, boolean bike, Ride ride) {
        this.passenger = passenger;
        this.bike = bike;
        this.ride = ride;
    }

    // Getters and Setters
    public boolean isPassenger() { return passenger; }
    public void setPassenger(boolean passenger) { this.passenger = passenger; }
    public boolean isBike() { return bike; }
    public void setBike(boolean bike) { this.bike = bike; }
    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
}
