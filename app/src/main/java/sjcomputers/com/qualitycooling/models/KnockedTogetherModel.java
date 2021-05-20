package sjcomputers.com.qualitycooling.models;

public class KnockedTogetherModel {
    public String Completed;
    public String Customer;
    public String INNumber;
    public String ItemName;
    public String JobSite;
    public String JobSiteAddress;
    public String OrderItemId;
    public String PieceNo;
    public String Delivered;

    public KnockedTogetherModel(String completed, String customer, String INNumber, String itemName, String jobSite, String jobSiteAddress, String orderItemId, String pieceNo, String delivered) {
        Completed = completed;
        Customer = customer;
        this.INNumber = INNumber;
        ItemName = itemName;
        JobSite = jobSite;
        JobSiteAddress = jobSiteAddress;
        OrderItemId = orderItemId;
        PieceNo = pieceNo;
        Delivered = delivered;
    }
}
