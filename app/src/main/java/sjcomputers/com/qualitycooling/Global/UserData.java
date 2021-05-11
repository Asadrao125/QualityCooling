package sjcomputers.com.qualitycooling.Global;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by RabbitJang on 5/27/2018.
 */

public class UserData {
    public static UserData instance = null;
    public int userId;
    public String authToken;
    public int customerId;
    public ArrayList<HashMap<String, Object>> statusArray;
    public String[] statuses;
    public double lat;
    public double lng;

    public int cuttingButtonShow;
    public String cuttingButtonText;
    public int formingButtonShow;
    public String formingButtonText;
    public int itemInfoButtonShow;
    public String itemInfoButtonText;
    public int jobsButtonShow;
    public String jobsButtonText;
    public int loadingButtonShow;
    public String loadingButtonText;
    public int knockedTogetherButtonShow;
    public String knockedTogetherButtonText;

    public static UserData getInstance() {
        if (instance == null) {
            instance = new UserData();
            instance.initInstance();
        }
        return instance;
    }

    private void initInstance() {
        userId = 0;
        authToken = null;
        customerId = 0;
        statusArray = new ArrayList<>();
        statuses = new String[0];
        lat = 0;
        lng = 0;

        cuttingButtonShow = 0;
        cuttingButtonText = "";
        formingButtonShow = 0;
        formingButtonText = "";
        itemInfoButtonShow = 0;
        itemInfoButtonText = "";
        jobsButtonShow = 0;
        jobsButtonText = "";
        loadingButtonShow = 0;
        loadingButtonText = "";
        knockedTogetherButtonShow = 0;
        knockedTogetherButtonText = "";
    }
}
