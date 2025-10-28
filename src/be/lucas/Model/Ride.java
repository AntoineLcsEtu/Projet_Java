package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Ride {
    private int id;
    private String startPlace;
    private Date startDate;
    private double fee;
    private List<Inscription> registrations;
    private List<Vehicle> vehicles;
    private static final String CLUB_ADDRESS = "Club Address";

    public Ride(int id, String startPlace, Date startDate, double fee) {
        this.id = id;
        this.startPlace = CLUB_ADDRESS;
        this.startDate = startDate;
        this.fee = fee;
        this.registrations = new ArrayList<>();
        this.vehicles = new ArrayList<>();
    }

    public void addRegistration(Inscription registration) {
        this.registrations.add(registration);
    }

    public int getTotalRegistrationNumber() {
        return registrations.size();
    }

    public int getAvailableSeatNumber() {
        int totalSeats = vehicles.stream().mapToInt(Vehicle::getSeatNumber).sum();
        int occupiedSeats = registrations.stream().filter(Inscription::isPassenger).mapToInt(r -> 1).sum();
        return totalSeats - occupiedSeats;
    }

    public int getTotalBikeSpotNumber() {
        return vehicles.stream().mapToInt(Vehicle::getBikeSpotNumber).sum();
    }

    public int getAvailableBikeSpotNumber() {
        int totalSpots = getTotalBikeSpotNumber();
        int occupiedSpots = registrations.stream().filter(Inscription::isBike).mapToInt(r -> 1).sum();
        return totalSpots - occupiedSpots;
    }

    public int getNeededSeatNumber() {
        return registrations.stream().filter(Inscription::isPassenger).mapToInt(r -> 1).sum();
    }

    public int getNeededBikeSpotNumber() {
        return registrations.stream().filter(Inscription::isBike).mapToInt(r -> 1).sum();
    }

    public String checkDriverNeeds() {
        int neededSeats = getNeededSeatNumber();
        int availableSeats = getAvailableSeatNumber();
        if (neededSeats > availableSeats) {
            return "Missing " + (neededSeats - availableSeats) + " seats";
        } else if (neededSeats < availableSeats) {
            return "Surplus of " + (availableSeats - neededSeats) + " seats";
        }
        return "Sufficient drivers";
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getStartPlace() { return startPlace; }
    public void setStartPlace(String startPlace) { this.startPlace = CLUB_ADDRESS; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
    public List<Inscription> getRegistrations() { return registrations; }
    public List<Vehicle> getVehicles() { return vehicles; }
}