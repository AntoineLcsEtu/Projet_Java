package be.lucas.Class;

import java.util.ArrayList;
import java.util.List;

public abstract class Category {
    protected int number;
    private Manager manager; // Relation 1-1 with Manager
    private List<Member> members; // Relation 1*-0* with Member

    // Constructor
    public Category(int number) {
        this.number = number;
        this.members = new ArrayList<>();
    }

    // Getters and Setters
    public int getNumber() { return number; }
    public void setNumber(int number) { this.number = number; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public List<Member> getMembers() { return members; }
    public void addMember(Member member) { this.members.add(member); }
}