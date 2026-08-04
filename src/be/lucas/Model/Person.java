package be.lucas.Model;

import be.lucas.DAO.PersonDAO;
import be.lucas.DAO.MemberDAO;
import be.lucas.DAO.ManagerDAO;
import be.lucas.DAO.TreasurerDAO;

import java.util.Objects;


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

    public static Person authenticate(int id, String password) throws Exception {
        PersonDAO personDAO = new PersonDAO();
        String role = personDAO.login(id, password);

        if (role != null) {
            return switch (role) {
                case "MEMBER"   -> new MemberDAO().getMemberByPersonId(id);
                case "MANAGER"  -> new ManagerDAO().getManagerByPersonId(id);
                case "TREASURER"-> new TreasurerDAO().getTreasurerByPersonId(id);
                default         -> null;
            };
        }
        return null;
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
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return id == person.id;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
