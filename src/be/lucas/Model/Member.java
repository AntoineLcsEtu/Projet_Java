package be.lucas.Model;

import java.util.Date;

import java.util.ArrayList;
import java.util.List;

import be.lucas.DAO.InscriptionDAO;
import be.lucas.DAO.MemberDAO;
import be.lucas.DAO.RideDAO;
import be.lucas.DAO.VehicleDAO;

public class Member extends Person {
    
	private double balance;
    private List<Bike> bikes;
    private Vehicle drivenVehicle;
    private List<Vehicle> passengerVehicles;
    private boolean isMembershipPaid;
    private List<Category> categories;
    private List<Inscription> inscriptions;

    public Member(String name, String firstName, String phone, int id, String password, double balance) {
        super(name, firstName, phone, id, password);
        this.balance = balance;
        this.bikes = new ArrayList<>();
        this.passengerVehicles = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.isMembershipPaid = false;
        this.inscriptions = new ArrayList<>();
    }

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
            category.addMember(this);
        }
    }

    public void validateMembership() throws Exception {
        if (getCategoryCount() == 0) {
            throw new IllegalStateException("Un membre doit appartenir à au moins une catégorie avant de pouvoir payer sa cotisation.");
        }
    }
    
    public List<Ride> getReservedRides() throws Exception {
        InscriptionDAO dao = new InscriptionDAO();
        return dao.getRidesByMemberId(this.getId());
    }
    
    public List<Ride> getEligibleRidesForVehicleOffer() throws Exception {
        RideDAO dao = new RideDAO();
        return dao.getRidesForVehicleOffer(this.getId());
    }


    public List<Vehicle> getOwnedVehicles() throws Exception {
        VehicleDAO dao = new VehicleDAO();
        List<Vehicle> vehicles = dao.getVehiclesByDriverId(this.getId());
        for (Vehicle v : vehicles) {
            v.setDriver(this);
        }
        return vehicles;
    }

    public boolean addVehicle(int seatNumber, int bikeSpotNumber) throws Exception {
        Vehicle vehicle = new Vehicle(0, seatNumber, bikeSpotNumber);
        vehicle.setDriver(this);

        VehicleDAO dao = new VehicleDAO();
        return dao.create(vehicle);
    }

    public boolean assignVehicleToRide(Vehicle vehicle, Ride ride) throws Exception {
        RideDAO dao = new RideDAO();
        boolean success = dao.assignVehicleToRide(vehicle.getId(), ride.getId());
        if (success) {
            ride.addVehicle(vehicle);
            vehicle.addRide(ride);
        }
        return success;
    }


    public boolean canPayMembership() throws Exception {
        double totalFee = calculateMembershipFee();
        return getBalance() >= totalFee;
    }

    public boolean payMembership() throws Exception {
        validateMembership();

        double totalFee = calculateMembershipFee();
        if (getBalance() < totalFee) return false;

        double newBalance = getBalance() - totalFee;

        MemberDAO dao = new MemberDAO();
        boolean success = dao.updateMembershipPaid(getId(), newBalance, true); 
        if (success) {
            setBalance(newBalance);
            setMembershipPaid(true);
        }
        return success;
    }
    
    
    public double calculateMembershipFee() throws Exception {
        int categoryCount = getCategoryCount(); 
        return 20.0 + categoryCount * 5.0;
    }

    public int getCategoryCount() throws Exception {
        MemberDAO dao = new MemberDAO();
        return dao.getCategoryCountForMember(getId());
    }
    

    public List<Ride> getAvailableRides() throws Exception {
        RideDAO dao = new RideDAO();
        List<Ride> rides = dao.getAvailableRidesForMember(getId());
        rides.removeIf(ride -> !ride.getStartDate().after(new Date()));
        return rides;
    }


    public boolean reserveRide(Ride ride, boolean isPassenger, boolean isBike) throws Exception {
        return ride.registerMember(this, isPassenger, isBike);
    }


    public boolean offerVehicleForRide(Ride ride) throws Exception {
        return ride.assignMemberVehicle(this);
    }
    
    public boolean creditBalance(double amount) throws Exception {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        return applyBalanceChange(amount);
    }
    
    public boolean debitBalance(double amount) throws Exception {
        if (amount <= 0) {
            throw new IllegalArgumentException("Le montant doit être positif");
        }
        return applyBalanceChange(-amount);
    }

    private boolean applyBalanceChange(double amount) throws Exception {
        double newBalance = this.balance + amount;
        if (newBalance < 0) {
            amount = -this.balance;
            newBalance = 0;
        }

        MemberDAO dao = new MemberDAO();
        boolean success = dao.creditBalance(this.getId(), amount);
        if (success) {
            this.balance = newBalance;
        }
        return success;
    }
    
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public List<Bike> getBikes() { return bikes; }
    public void addBike(Bike bike) { this.bikes.add(bike); }
    public Vehicle getDrivenVehicle() { return drivenVehicle; }
    public void setDrivenVehicle(Vehicle drivenVehicle) { this.drivenVehicle = drivenVehicle; }
    public List<Vehicle> getPassengerVehicles() { return passengerVehicles; }
    public void addPassengerVehicle(Vehicle vehicle) { this.passengerVehicles.add(vehicle); }
    public boolean isMembershipPaid() { return isMembershipPaid; }
    public void setMembershipPaid(boolean isMembershipPaid) { this.isMembershipPaid = isMembershipPaid; }
    public List<Category> getCategories() { return categories; }
    public List<Inscription> getInscriptions() {
		return inscriptions;
	}

	public void setInscriptions(List<Inscription> inscriptions) {
		this.inscriptions = inscriptions;
	}
	public void addInscritions(Inscription i)
	{
		inscriptions.add(i);
	}
	
	public boolean canAfford(double amount) {
	    return balance >= amount;
	}
	
}