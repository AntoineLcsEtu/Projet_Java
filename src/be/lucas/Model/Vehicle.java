package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Vehicle {
	public static final double SEAT_FEE = 5.0;
	public static final double BIKE_FEE = 2.5;

    private int id;
    private int seatNumber;
    private int bikeSpotNumber;
    private Member driver;
    private List<Member> passengers;
    private List<Bike> bikes;
    private List<Ride> rides;

    public Vehicle(int id, int seatNumber, int bikeSpotNumber) {
        this.id = id;
        this.seatNumber = seatNumber;
        this.bikeSpotNumber = bikeSpotNumber;
        this.passengers = new ArrayList<>();
        this.bikes = new ArrayList<>();
        this.rides = new ArrayList<>();
    }

    public void addPassenger(Member member) {
        this.passengers.add(member);
    }

    public void addBike(Bike bike) {
        this.bikes.add(bike);
    }

    public double calculateDriverFee() {
	    return seatNumber * SEAT_FEE + bikeSpotNumber * BIKE_FEE;
	}
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    public int getBikeSpotNumber() { return bikeSpotNumber; }
    public void setBikeSpotNumber(int bikeSpotNumber) { this.bikeSpotNumber = bikeSpotNumber; }
    public Member getDriver() { return driver; }
    public void setDriver(Member driver) { this.driver = driver; }
    public List<Member> getPassengers() { return passengers; }
    public List<Bike> getBikes() { return bikes; }
    public List<Ride> getRides() { return rides; }
    public void addRide(Ride ride) { this.rides.add(ride); }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Vehicle vehicle = (Vehicle) o;
        return id == vehicle.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}