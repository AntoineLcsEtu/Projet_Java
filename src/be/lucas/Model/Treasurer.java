package be.lucas.Model;

public class Treasurer extends Person {
    public Treasurer(String name, String firstName, String phone, int id, String password) {
        super(name, firstName, phone, id, password);
    }

    public void sendReminderLetter() {
        // Implémentation à compléter (par exemple, envoi d'un email)
    }

    public void payDriver() {
        // Implémentation à compléter (par exemple, gestion des paiements)
    }

    public void claimFee() {
        // Implémentation à compléter (par exemple, collecte des frais)
    }

    public void verifyMembershipFees(Member member) {
        if (!member.isMembershipPaid()) {
            sendReminderLetter();
        }
    }
}