package be.lucas.Model;

import java.util.Objects;

public class Bike {
    private int id;
    private double weight;
    private double length;
    private Vehicle vehicle;
    private Member member;
    private CategoryType type;

    public Bike(int id, double weight, CategoryType type, double length, Member member) {
        this.id = id;
        setWeight(weight);
        setLength(length);
        this.type = type;
        this.member = member;
    }
    
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public double getWeight() { return weight; }
    public void setWeight(double weight) {
        if (weight <= 0) {
            throw new IllegalArgumentException("Le poids du vélo doit être positif.");
        }
        this.weight = weight;
    }
    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }
    public double getLength() { return length; }
    public void setLength(double length) {
        if (length <= 0) {
            throw new IllegalArgumentException("La longueur du vélo doit être positive.");
        }
        this.length = length;
    }
    public Vehicle getVehicle() { return vehicle; }
    public void setVehicle(Vehicle vehicle) { this.vehicle = vehicle; }
    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Bike bike = (Bike) o;
        return id == bike.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}