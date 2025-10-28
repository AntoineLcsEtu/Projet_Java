package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;

public class Vehicle {
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
}