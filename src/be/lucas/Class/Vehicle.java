package be.lucas.Class;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
    private int seatNumber;
    private int bikeSpotNumber;
    private Member driver; // Relation 1-0..1 with Member
    private List<Member> passengers; // Relation 0*-1* with Member
    private List<Bike> bikes; // Relation 0*-0..1 with Bike
    private List<Ride> rides; // Relation 0*-1* with Ride

    // Constructor
    public Vehicle(int seatNumber, int bikeSpotNumber) {
        this.seatNumber = seatNumber;
        this.bikeSpotNumber = bikeSpotNumber;
        this.passengers = new ArrayList<>();
        this.bikes = new ArrayList<>();
        this.rides = new ArrayList<>();
    }

    // Methods
    public void addPassenger(Member member) {
        this.passengers.add(member);
    }

    public void addBike(Bike bike) {
        this.bikes.add(bike);
    }

    // Getters and Setters
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
}