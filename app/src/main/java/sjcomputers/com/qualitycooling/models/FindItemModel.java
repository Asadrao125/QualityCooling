package sjcomputers.com.qualitycooling.models;

public class FindItemModel {
    public String ItemName;
    public String Piece;
    public String Quantity;
    public String Width;
    public String Height;
    public String Length;
    public String Depth;
    public String Loaded;
    public String Completed;
    public String CompletedBy;
    public String Delivered;
    public String Location;

    public FindItemModel(String itemName, String piece, String quantity, String width, String height, String length, String depth, String loaded, String completed, String completedBy, String delivered, String location) {
        ItemName = itemName;
        Piece = piece;
        Quantity = quantity;
        Width = width;
        Height = height;
        Length = length;
        Depth = depth;
        Loaded = loaded;
        Completed = completed;
        CompletedBy = completedBy;
        Delivered = delivered;
        Location = location;
    }
}