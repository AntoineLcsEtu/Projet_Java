package be.lucas.Model;

public class Inscription {
    private int id;
    private boolean passenger;
    private boolean bike;
    private Ride ride;

    public Inscription(int id, boolean passenger, boolean bike, Ride ride) {
        this.id = id;
        this.passenger = passenger;
        this.bike = bike;
        this.ride = ride;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public boolean isPassenger() { return passenger; }
    public void setPassenger(boolean passenger) { this.passenger = passenger; }
    public boolean isBike() { return bike; }
    public void setBike(boolean bike) { this.bike = bike; }
    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
}
