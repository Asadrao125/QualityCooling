package sjcomputers.com.qualitycooling.Adapters;

public class LoadingModel2 {
    public String Button1Text;
    public String Button2Text;
    public String Customer;
    public String INNumber;
    public String ItemName;
    public String JobSite;
    public String JobSiteAddress;
    public String Loaded;
    public String OrderId;
    public String OrderItemId;
    public String PieceNo;
    public String ShowNotificationPopup;
    public String ShowPopup;

    public LoadingModel2(String button1Text, String button2Text, String customer, String INNumber, String itemName, String jobSite, String jobSiteAddress, String loaded, String orderId, String orderItemId, String pieceNo, String showNotificationPopup, String showPopup) {
        Button1Text = button1Text;
        Button2Text = button2Text;
        Customer = customer;
        this.INNumber = INNumber;
        ItemName = itemName;
        JobSite = jobSite;
        JobSiteAddress = jobSiteAddress;
        Loaded = loaded;
        OrderId = orderId;
        OrderItemId = orderItemId;
        PieceNo = pieceNo;
        ShowNotificationPopup = showNotificationPopup;
        ShowPopup = showPopup;
    }
}