package sjcomputers.com.qualitycooling.models;

public class DeliveredModel {
    public String Completed;
    public String Customer;
    public String INNumber;
    public String ItemName;
    public String JobSite;
    public String JobSiteAddress;
    public String OrderItemId;
    public String PieceNo;
    public String Delivered;
    public String ShowNotificationPopup;
    public String ShowPopup;
    public String Button1Text;
    public String Button2Text;
    public String OrderId;

    public DeliveredModel(String completed, String customer, String INNumber, String itemName, String jobSite, String jobSiteAddress, String orderItemId, String pieceNo, String delivered, String showNotificationPopup, String showPopup, String button1Text, String button2Text, String orderId) {
        Completed = completed;
        Customer = customer;
        this.INNumber = INNumber;
        ItemName = itemName;
        JobSite = jobSite;
        JobSiteAddress = jobSiteAddress;
        OrderItemId = orderItemId;
        PieceNo = pieceNo;
        Delivered = delivered;
        ShowNotificationPopup = showNotificationPopup;
        ShowPopup = showPopup;
        Button1Text = button1Text;
        Button2Text = button2Text;
        OrderId = orderId;
    }
}
