package be.lucas.Model;


import java.util.ArrayList;
import java.util.List;

public class Member extends Person {
    private double balance;
    private List<Bike> bikes;
    private Vehicle drivenVehicle;
    private List<Vehicle> passengerVehicles;
    private boolean isMembershipPaid;
    private List<Category> categories;

    public Member(String name, String firstName, String phone, int id, String password, double balance) {
        super(name, firstName, phone, id, password);
        this.balance = balance;
        this.bikes = new ArrayList<>();
        this.passengerVehicles = new ArrayList<>();
        this.categories = new ArrayList<>();
        this.isMembershipPaid = false;
    }

    public void calculateBalance() {
        double membershipFee = 20.0;
        int additionalCategories = categories.size() - 1;
        if (additionalCategories > 0) {
            membershipFee += additionalCategories * 5.0;
        }
        balance -= membershipFee;
    }

    public double checkBalance() {
        return balance;
    }

    public void addCategory(Category category) {
        if (!categories.contains(category)) {
            categories.add(category);
            category.addMember(this);
        }
    }

    public void validateMembership() {
        if (categories.isEmpty()) {
            throw new IllegalStateException("Member must belong to at least one category");
        }
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
}