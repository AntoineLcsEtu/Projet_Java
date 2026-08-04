package be.lucas.Model;

import java.util.ArrayList;
import java.util.List;
import be.lucas.DAO.MemberDAO;
import be.lucas.DAO.RideDAO;

public class Treasurer extends Person {
	
    public Treasurer(String name, String firstName, String phone, int id, String password) {
        super(name, firstName, phone, id, password);
    }

    public List<Member> getUnpaidMembers() throws Exception {
        MemberDAO memberDAO = new MemberDAO();
        List<Member> members = memberDAO.getAllMembersWithCategories();

        List<Member> unpaidMembers = new ArrayList<>();
        for (Member m : members) {
            if (!m.isMembershipPaid()) {
                unpaidMembers.add(m);
            }
        }
        return unpaidMembers;
    }

    public List<Member> getDriversToPay() throws Exception {
        RideDAO rideDAO = new RideDAO();
        return rideDAO.getDriversFromCompletedRides();
    }

    public Object[] payDriver() throws Exception {
        RideDAO rideDAO = new RideDAO();

        List<Member> drivers = rideDAO.getDriversFromCompletedRides();
        StringBuilder log = new StringBuilder();

        if (drivers.isEmpty()) {
            log.append("Aucun conducteur à payer (aucun ride terminé).\n");
            return new Object[]{ log.toString(), 0.0 };
        }

        double totalPaid = 0.0;
        int count = 0;

        for (Member driver : drivers) {
        	double fee = driver.getDrivenVehicle().calculateDriverFee();
        	
        	boolean success = driver.creditBalance(fee);
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

    public void claimFee() {}

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

            boolean isPaid = m.isMembershipPaid();

            if (isPaid) paidCount++;
            else unpaidCount++;

            sb.append(String.format("%3d. %s %s\n", i + 1, m.getFirstName(), m.getName()));
            sb.append(String.format("     Solde actuel   : %.2f €\n", m.getBalance()));
            sb.append(String.format("     Statut         : %s\n",
                    isPaid ? "PAYÉE " : "NON PAYÉE"));
            sb.append("     " + "-".repeat(60) + "\n");
        }

        sb.append(String.format("\nRÉSUMÉ : %d membre(s) à jour | %d membre(s) non à jour\n", paidCount, unpaidCount));

        return new Object[]{ sb.toString(), paidCount, unpaidCount };
    }
}