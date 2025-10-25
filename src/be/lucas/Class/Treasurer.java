package be.lucas.Class;

public class Treasurer extends Person
{
    // Constructor
    public Treasurer(String name, String firstName, String phone, String id, String password) {
        super(name, firstName, phone, id, password);
    }

    // Methods
    public void sendReminderLetter() {
        // Logic to send a reminder letter
    }

    public void payDriver() {
        // Logic to pay a driver
    }

    public void claimFee() {
        // Logic to claim a fee
    }
}