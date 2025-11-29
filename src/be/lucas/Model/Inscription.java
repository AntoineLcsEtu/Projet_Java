package be.lucas.Model;

public class Inscription {
    private int id;                    
    private Member member;            
    private Ride ride;  
    private boolean isPassenger;
    private boolean isBike;

    public Inscription(Member member, Ride ride, boolean isPassenger, boolean isBike) {
        this.member = member;
        this.ride = ride;
        this.isPassenger = isPassenger;
        this.isBike = isBike;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public Ride getRide() { return ride; }
    public void setRide(Ride ride) { this.ride = ride; }
    
    public int getRideId() { 
        return ride != null ? ride.getId() : 0; 
    }

    public boolean isPassenger() { return isPassenger; }
    public void setPassenger(boolean passenger) { this.isPassenger = passenger; }

    public boolean isBike() { return isBike; }
    public void setBike(boolean bike) { this.isBike = bike; }
}