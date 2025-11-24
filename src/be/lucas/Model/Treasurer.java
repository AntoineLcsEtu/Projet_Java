package be.lucas.Model;

import java.util.List;

import be.lucas.DAO.MemberDAO;
import be.lucas.DAO.RideDAO;

public class Treasurer extends Person {
    public Treasurer(String name, String firstName, String phone, int id, String password) {
        super(name, firstName, phone, id, password);
    }

    public void sendReminderLetter() 
    {
    	
    }

    public Object[] payDriver() throws Exception {
        RideDAO rideDAO = new RideDAO();
        MemberDAO memberDAO = new MemberDAO();

        List<Member> drivers = rideDAO.getDriversFromCompletedRides();
        StringBuilder log = new StringBuilder();

        if (drivers.isEmpty()) {
            log.append("Aucun conducteur à payer (aucun ride terminé).\n");
            return new Object[]{ log.toString(), 0.0 };
        }

        double totalPaid = 0.0;
        int count = 0;

        for (Member driver : drivers) {
            double fee = driver.getDrivenVehicle().getSeatNumber() * 5.0 +
                         driver.getDrivenVehicle().getBikeSpotNumber() * 2.5;

            boolean success = memberDAO.creditBalance(driver.getId(), fee);
            if (success) {
                log.append(String.format("✓ %s %s : +%.2f € (sièges: %d, vélo: %d)\n",
                        driver.getFirstName(), driver.getName(), fee,
                        driver.getDrivenVehicle().getSeatNumber(),
                        driver.getDrivenVehicle().getBikeSpotNumber()));
                totalPaid += fee;
                count++;
            } else {
                log.append(String.format("✗ ÉCHEC pour %s %s\n",
                        driver.getFirstName(), driver.getName()));
            }
        }

        log.append("\n").append("═".repeat(50)).append("\n");
        log.append(String.format("RÉSUMÉ : %d conducteur(s) payé(s) | Total : %.2f €\n", count, totalPaid));

        return new Object[]{ log.toString(), totalPaid };
    }


    public void claimFee() {
        
    }

    
    public Object[] verifyMembershipFees() throws Exception {
        MemberDAO memberDAO = new MemberDAO();
        List<Member> members = memberDAO.getAllMembersWithCategories();

        StringBuilder sb = new StringBuilder();
        sb.append("═══════════════════════════════════════════════════════════════════════\n");
        sb.append("           RAPPORT DE VÉRIFICATION DES COTISATIONS\n");
        sb.append("═══════════════════════════════════════════════════════════════════════\n\n");

        if (members.isEmpty()) {
            sb.append("Aucun membre trouvé.\n");
            return new Object[]{ sb.toString(), 0, 0 };
        }

        int paidCount = 0, unpaidCount = 0;

        for (int i = 0; i < members.size(); i++) {
            Member m = members.get(i);

            double requiredFee = 20.0 + (m.getCategories().size() * 5.0);
            boolean isPaid = m.getBalance() >= requiredFee;
            m.setMembershipPaid(isPaid);               // mise à jour du modèle

            if (isPaid) paidCount++; else unpaidCount++;

            sb.append(String.format("%3d. %s %s\n", i + 1, m.getFirstName(), m.getName()));
            sb.append(String.format("     Solde : %.2f €\n", m.getBalance()));
            sb.append(String.format("     Catégories : %d\n", m.getCategories().size()));
            sb.append(String.format("     Cotisation : %s\n", isPaid ? "PAYÉE" : "NON PAYÉE"));
            sb.append("     " + "-".repeat(60) + "\n");
        }

        sb.append(String.format("\nRÉSUMÉ : %d payé(s) | %d non payé(s)\n", paidCount, unpaidCount));

        return new Object[]{ sb.toString(), paidCount, unpaidCount };
    }
    
    
}