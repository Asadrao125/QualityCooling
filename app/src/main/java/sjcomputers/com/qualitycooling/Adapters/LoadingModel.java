package sjcomputers.com.qualitycooling.Adapters;

public class LoadingModel {
    public String A_R_ENERGY;
    public String INNumber;
    public String ItemName;
    public String JobSite;
    public String JobSiteAddress;
    public String Loaded;
    public String OrderItemId;
    public String PieceNo;

    public LoadingModel(String a_R_ENERGY, String INNumber, String itemName, String jobSite, String jobSiteAddress, String loaded, String orderItemId, String pieceNo) {
        A_R_ENERGY = a_R_ENERGY;
        this.INNumber = INNumber;
        ItemName = itemName;
        JobSite = jobSite;
        JobSiteAddress = jobSiteAddress;
        Loaded = loaded;
        OrderItemId = orderItemId;
        PieceNo = pieceNo;
    }
}