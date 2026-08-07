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

    public List<Member> getMembershipReport() throws Exception {
        MemberDAO memberDAO = new MemberDAO();
        return memberDAO.getAllMembersWithCategories();
    }
}