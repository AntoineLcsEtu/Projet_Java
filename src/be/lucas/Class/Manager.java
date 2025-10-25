package be.lucas.Class;

public class Manager extends Person {
    private Category category;

    // Constructor
    public Manager(String name, String firstName, String phone, String id, String password, Category category) {
        super(name, firstName, phone, id, password);
        this.category = category;
    }

    // Getter and Setter for category
    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }
}
