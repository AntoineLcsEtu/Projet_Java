package be.lucas.Class;

public abstract class Person {
    protected String name;
    protected String firstName;
    protected String phone;
    protected String id;
    protected String password;

    // Constructor
    public Person(String name, String firstName, String phone, String id, String password) {
        this.name = name;
        this.firstName = firstName;
        this.phone = phone;
        this.id = id;
        this.password = password;
    }

    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
