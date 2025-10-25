package be.lucas.Class;

import java.util.*;

public class Member extends Person {
    private double balance;
    private List<Bike> bikes;
    private Vehicle drivenVehicle; // Relation 1-0..1 (driver)
    private List<Vehicle> passengerVehicles; // Relation 1*-0* (passenger)

    // Constructor
    public Member(String name, String firstName, String phone, String id, String password, double balance) {
        super(name, firstName, phone, id, password);
        this.balance = balance;
        this.bikes = new ArrayList<>();
        this.passengerVehicles = new ArrayList<>();
    }

    // Methods
    public void calculateBalance() {
        // Logic to calculate balance
    }

    public double checkBalance() {
        return balance;
    }

    // Getters and Setters
    public double getBalance() { return balance; }
    public void setBalance(double balance) { this.balance = balance; }
    public List<Bike> getBikes() { return bikes; }
    public void addBike(Bike bike) { this.bikes.add(bike); }
    public Vehicle getDrivenVehicle() { return drivenVehicle; }
    public void setDrivenVehicle(Vehicle drivenVehicle) { this.drivenVehicle = drivenVehicle; }
    public List<Vehicle> getPassengerVehicles() { return passengerVehicles; }
    public void addPassengerVehicle(Vehicle vehicle) { this.passengerVehicles.add(vehicle); }
}