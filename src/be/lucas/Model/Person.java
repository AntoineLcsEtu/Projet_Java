package be.lucas.Model;

public abstract class Person {
    protected String name;
    protected String firstName;
    protected String phone;
    protected int id;
    protected String password;

    public Person(String name, String firstName, String phone, int id, String password) {
        this.name = name;
        this.firstName = firstName;
        this.phone = phone;
        this.id = id;
        this.password = password;
    }

    public boolean login(int id, String password) {
        return this.id == id && this.password.equals(password);
    }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}
