package lease.Approval.Exception;

public class LeaseNotFoundException extends RuntimeException {
    public LeaseNotFoundException(String message) {
        super(message);
    }
}
