package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import be.lucas.DAO.InscriptionDAO;
import be.lucas.DAO.RideDAO;

import java.util.Date;

public class Ride {
    private int id;
    private String startPlace;  
    private Date startDate;
    private double fee;
    private List<Inscription> inscriptions;
    private List<Vehicle> vehicles;
    private Calendar calendar;  

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

    public int getTotalSeatNumber() {
        return vehicles.stream().mapToInt(Vehicle::getSeatNumber).sum();
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

    public int getMissingDriversCount() {
        long driversAssigned = vehicles.stream().filter(v -> v.getDriver() != null).count();
        return (int) Math.max(0, vehicles.size() - driversAssigned);
    }
    
    public boolean isDriver(Member member) {
        return vehicles.stream().anyMatch(v -> v.getDriver() != null && v.getDriver().getId() == member.getId());
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

    public boolean registerMember(Member member, boolean isPassenger, boolean isBike, Vehicle selectedVehicle, Bike selectedBike) throws Exception {
        InscriptionDAO inscriptionDAO = new InscriptionDAO();

        if (inscriptionDAO.isAlreadyRegistered(member.getId(), this.id)) {
            throw new Exception("Vous êtes déjà inscrit à ce ride.");
        }

        RideDAO rideDAO = new RideDAO();
        Ride currentState = rideDAO.getRideWithDetails(this.id);
        if (currentState == null) {
            throw new Exception("Ride introuvable.");
        }

        Vehicle memberVehicle = null;
        boolean isDriver = !isPassenger;

        if (isDriver) {
            List<Vehicle> ownedVehicles = member.getOwnedVehicles();
            if (ownedVehicles.isEmpty()) {
                throw new Exception("Vous devez avoir un véhicule enregistré pour être conducteur.");
            }
            boolean belongsToMember = selectedVehicle != null &&
                ownedVehicles.stream().anyMatch(v -> v.getId() == selectedVehicle.getId());
            memberVehicle = belongsToMember ? selectedVehicle : ownedVehicles.get(0);
        }

        boolean needsPassengerSeat = isPassenger;
        boolean needsBikeSpot = isBike;

        if (isDriver && memberVehicle != null) {
            needsPassengerSeat = false;

            if (isBike) {
                int usedBikeSpotsInOwnVehicle = currentState.getUsedBikeSpotsInVehicle(memberVehicle);
                if (usedBikeSpotsInOwnVehicle + 1 > memberVehicle.getBikeSpotNumber()) {
                    throw new Exception("Votre véhicule n'a plus de place pour un vélo.");
                }
                needsBikeSpot = false;
            }
        }

        if (needsPassengerSeat && !currentState.hasAvailableSeat()) {
            throw new Exception("Plus de places passager disponibles pour ce ride.");
        }
        if (needsBikeSpot && !currentState.hasAvailableBikeSpot()) {
            throw new Exception("Plus de places vélo disponibles pour ce ride.");
        }

        double rideFee = currentState.getFee();

        if (!member.canAfford(rideFee)) {
            throw new Exception(InscriptionDAO.MSG_SOLDE_INSUFFISANT +
                              " Solde actuel : " + String.format("%.2f", member.getBalance()) +
                              " €, Frais du ride : " + String.format("%.2f", rideFee) + " €");
        }

        Bike bikeToUse = null;
        if (isBike && selectedBike != null) {
            List<Bike> ownedBikes = member.getOwnedBikes();
            boolean belongsToMember = ownedBikes.stream().anyMatch(b -> b.getId() == selectedBike.getId());
            bikeToUse = belongsToMember ? selectedBike : null;
        }

        return inscriptionDAO.saveRegistration(member, this.id, isPassenger, isBike, rideFee, bikeToUse);
    }

    public boolean assignMemberVehicle(Member member, Vehicle vehicle) throws Exception {
        if (vehicle == null) return false;
        return new RideDAO().assignVehicleToRide(vehicle.getId(), this.id);
    }
    
    public Date getStartDate() { return startDate; }
    public void setStartDate(Date startDate) { this.startDate = startDate; }

    public double getFee() { return fee; }
    public void setFee(double fee) { this.fee = fee; }

    public List<Inscription> getRegistrations() { return inscriptions; }
    public List<Vehicle> getVehicles() { return vehicles; }
    
    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
    
    public Category getCategory() {
        return calendar != null ? calendar.getCategory() : null;
    }
    
    public int getCategoryId() {
        Category category = getCategory();
        return category != null ? category.getId() : 0;
    }
    
    public int getUsedBikeSpotsInVehicle(Vehicle vehicle) {
        return (int) inscriptions.stream()
            .filter(insc -> insc.isBike())
            .filter(insc -> {
                Member member = insc.getMember();
                for (Vehicle v : vehicles) {
                    if (v.equals(vehicle) && v.getPassengers().contains(member)) {
                        return true;
                    }
                }
                return false;
            })
            .count();
    }
    
    public void calculateFee() {
        this.fee = getNeededSeatNumber() * Vehicle.SEAT_FEE + getNeededBikeSpotNumber() * Vehicle.BIKE_FEE;
    }
    
    public boolean hasAvailableSeat() {
        return getAvailableSeatNumber() > 0;
    }

    public boolean hasAvailableBikeSpot() {
        return getAvailableBikeSpotNumber() > 0;
    }
    
    public double getPassengerFeeTotal() {
        return getNeededSeatNumber() * Vehicle.SEAT_FEE;
    }

    public double getBikeFeeTotal() {
        return getNeededBikeSpotNumber() * Vehicle.BIKE_FEE;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Ride ride = (Ride) o;
        return id == ride.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}