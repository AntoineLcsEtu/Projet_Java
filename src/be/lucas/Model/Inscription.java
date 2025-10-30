package be.lucas.Model;

public class Inscription {
	private int id;                    
    private Member member;            
    private int rideId;                
    private boolean isPassenger;
    private boolean isBike;

    public Inscription(Member member, int rideId, boolean isPassenger, boolean isBike) {
        this.member = member;
        this.rideId = rideId;
        this.isPassenger = isPassenger;
        this.isBike = isBike;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public Member getMember() { return member; }
    public void setMember(Member member) { this.member = member; }

    public int getRideId() { return rideId; }
    public void setRideId(int rideId) { this.rideId = rideId; }

    public boolean isPassenger() { return isPassenger; }
    public void setPassenger(boolean passenger) { this.isPassenger = passenger; }

    public boolean isBike() { return isBike; }
    public void setBike(boolean bike) { this.isBike = bike; }
}
