package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Date;

public class Ride {
    private int id;
    private String startPlace;  
    private Date startDate;
    private double fee;
    private List<Inscription> inscriptions;
    private List<Vehicle> vehicles;

    public Ride(int id, String startPlace, Date startDate, double fee) {
        this.id = id;
        this.startPlace = startPlace;  
        this.startDate = startDate;
        this.fee = fee;
        this.inscriptions = new ArrayList<>();
        this.vehicles = new ArrayList<>();
    }

    public void addRegistration(Inscription inscription) {
        boolean exists = inscriptions.stream()
            .anyMatch(i -> i.getMember().getId() == inscription.getMember().getId()
                        && i.getRideId() == inscription.getRideId());
        if (!exists) {
            inscriptions.add(inscription);
        }
    }

    public int getTotalRegistrationNumber() {
        return inscriptions.size();
    }

    public int getAvailableSeatNumber() {
        int total = vehicles.stream().mapToInt(Vehicle::getSeatNumber).sum();
        int used = inscriptions.stream().filter(i -> i.isPassenger()).mapToInt(i -> 1).sum();
        return Math.max(0, total - used);
    }

    public int getTotalBikeSpotNumber() {
        return vehicles.stream().mapToInt(Vehicle::getBikeSpotNumber).sum();
    }

    public int getAvailableBikeSpotNumber() {
        int total = vehicles.stream().mapToInt(Vehicle::getBikeSpotNumber).sum();
        int used = inscriptions.stream().filter(i -> i.isBike()).mapToInt(i -> 1).sum();
        return Math.max(0, total - used);
    }

    public int getNeededSeatNumber() {
        return inscriptions.stream().filter(Inscription::isPassenger).mapToInt(r -> 1).sum();
    }

    public int getNeededBikeSpotNumber() {
        return inscriptions.stream().filter(Inscription::isBike).mapToInt(r -> 1).sum();
    }

    public String checkDriverNeeds() {
        long drivers = vehicles.stream().filter(v -> v.getDriver() != null).count();
        return drivers < vehicles.size() 
            ? "Besoin de " + (vehicles.size() - drivers) + " conducteur(s)" 
            : "Tous les véhicules ont un conducteur";
    }

    public void addVehicle(Vehicle vehicle) {
        this.vehicles.add(vehicle);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getStartPlace() { return startPlace; }
    public void setStartPlace(String startPlace) { 
        this.startPlace = startPlace;  
    }

    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    public List<Inscription> getRegistrations() { return inscriptions; }
    public List<Vehicle> getVehicles() { return vehicles; }
}