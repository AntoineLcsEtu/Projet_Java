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
        List<Member> members = memberDAO.findAllMembersWithCategories();

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
        return rideDAO.findDriversFromCompletedRides();
    }

    public Object[] payDriver() throws Exception {
        RideDAO rideDAO = new RideDAO();
        List<Member> drivers = rideDAO.findDriversFromCompletedRides();

        List<Member> paidDrivers = new ArrayList<>();
        List<Member> failedDrivers = new ArrayList<>();
        double totalPaid = 0.0;

        for (Member driver : drivers) {
            double fee = driver.getDrivenVehicle().calculateDriverFee();
            boolean success = driver.creditBalance(fee);
            if (success) {
                paidDrivers.add(driver);
                totalPaid += fee;
            } else {
                failedDrivers.add(driver);
            }
        }

        return new Object[]{ paidDrivers, failedDrivers, totalPaid };
    }

    public void claimFee() {}

    public List<Member> getMembershipReport() throws Exception {
        MemberDAO memberDAO = new MemberDAO();
        return memberDAO.findAllMembersWithCategories();
    }
}