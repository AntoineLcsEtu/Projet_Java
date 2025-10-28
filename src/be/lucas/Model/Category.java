package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;

public class Category {
    private int id;
    private Calendar calendar;
    private Manager manager;
    private List<Member> members;
    private CategoryType type;

    public Category(int id, Calendar calendar, CategoryType type) {
        this.id = id;
        this.calendar = calendar;
        this.type = type;
        this.members = new ArrayList<>();
    }

    public void addMember(Member member) {
        this.members.add(member);
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public Calendar getCalendar() { return calendar; }
    public void setCalendar(Calendar calendar) { this.calendar = calendar; }
    public Manager getManager() { return manager; }
    public void setManager(Manager manager) { this.manager = manager; }
    public List<Member> getMembers() { return members; }
    public CategoryType getType() { return type; }
    public void setType(CategoryType type) { this.type = type; }
}