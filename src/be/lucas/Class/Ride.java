package be.lucas.Class;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Ride {
    private int number;
    private String startPlace;
    private Date startDate;
    private double fee;
    private List<Inscription> registrations; // Relation 1-0* with Registration
    private List<Vehicle> vehicles; // Relation 1*-0* with Vehicle

    // Constructor
    public Ride(int number, String startPlace, Date startDate, double fee) {
        this.number = number;
        this.startPlace = startPlace;
        this.startDate = startDate;
        this.fee = fee;
        this.registrations = new ArrayList<>();
        this.vehicles = new ArrayList<>();
    }

    // Methods
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

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    // Getters and Setters
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public String getStartPlace() { return startPlace; }
    public void setStartPlace(String startPlace) { this.startPlace = startPlace; }
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }
    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }
    public List<Inscription> getRegistrations() { return registrations; }
    public List<Vehicle> getVehicles() { return vehicles; }
}