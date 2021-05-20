package sjcomputers.com.qualitycooling.Global;

import android.os.AsyncTask;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by RabbitJang on 5/27/2018.
 */

public class APIManager {
    public static final int HTTP_POST = 1;
    public static final int HTTP_GET = 2;
    public static final int HTTP_PUT = 3;
    public static final int HTTP_DELETE = 4;

    public static String SERVER_ADDR = "https://ductorder.com/services/service.svc";
    private static APIManager instance = null;
    private APIManagerCallback callback = null;

    public static APIManager getInstance() {
        if (instance == null) {
            instance = new APIManager();
        }
        return instance;
    }

    // Set callback function after finished api request..
    public void setCallback(APIManagerCallback callback) {
        this.callback = callback;
    }

    public void authenticateUser(String userName, String password) {
        String API_URL = String.format("/Authenticate?username=%s&password=%s", userName, password);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    //----- Normal user apis
    public void getOrders(String status, int startIndex, int readCount) {
        String API_URL = String.format("/AllOrders?authtoken=%s&status=%s&startIndex=%d&count=%d", UserData.getInstance().authToken, status, startIndex, readCount);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void searchOrders(String orderNo, String status) {
        String API_URL = String.format("/Orders?authtoken=%s&orderno=%s&status=%s", UserData.getInstance().authToken, orderNo, status);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getOrderDetail(int orderId) {
        String API_URL = String.format("/Order?orderid=%s&authtoken=%s", orderId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getStatuses() {
        String API_URL = String.format("/Statuses?authtoken=%s", UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void changeOrderStatus(int orderId, int statusId) {
        String API_URL = String.format("/ChangeOrderStatus?orderid=%d&statusid=%d&userid=%d&authtoken=%s", orderId, statusId, UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    //----- Driver apis
    public void getDriverOrders(int status, int startIndex, int readCount, String orderNo) {
        String API_URL = String.format("/DriverOrders?userid=%s&authtoken=%s&status=%d&startIndex=%d&count=%d&orderno=%s", UserData.getInstance().userId, UserData.getInstance().authToken, status, startIndex, readCount, orderNo);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void orderItems(int orderId) {
        String API_URL = String.format("/OrderItems?orderId=%d&authtoken=%s", orderId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getOrderDocuments(int orderId) {
        String API_URL = String.format("/OrderDocuments?orderId=%d&authtoken=%s", orderId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getOrderDocument(int documentId) {
        String API_URL = String.format("/OrderDocument?DocumentId=%d&authtoken=%s", documentId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void deliverOrder(int orderId, int driverId) {
        String API_URL = String.format("/DeliverOrder?orderid=%d&driverid=%d&authtoken=%s", orderId, driverId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void sendDriverLocation(int orderId, int driverId, double latitude, double longitude) {
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("orderid", orderId);
            jsonObject.accumulate("driverid", driverId);
            jsonObject.accumulate("latitude", latitude);
            jsonObject.accumulate("longitude", longitude);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String API_URL = String.format("/DriverLocation?authtoken=%s", UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, jsonObject, HTTP_POST);
        task.execute((Void) null);
    }

    public void getDriverOrderDocuments(int orderId) {
        String API_URL = String.format("/DriverOrderDocuments?orderId=%d&authtoken=%s", orderId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }


    public void updateOrderItems(int orderId, JSONObject itemArrayJSONObj) {
        String API_URL = String.format("/UpdateOrderItems?orderid=%d&userid=%d&authtoken=%s", orderId, UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, itemArrayJSONObj, HTTP_POST);
        task.execute((Void) null);
    }

    public void updateDriverOrderItemsDriver(int orderId, JSONObject itemArrayJSONObj) {
        String API_URL = String.format("/UpdateOrderItemsDriver?orderid=%d&userid=%d&authtoken=%s", orderId, UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, itemArrayJSONObj, HTTP_POST);
        task.execute((Void) null);
    }

    public void updateSignature(JSONObject object) {
        String API_URL = String.format("/UpdateSignature");
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void saveSignature(int orderId, String signature, String receiverName) {
        String API_URL = "/SaveSignature";
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("Signature", signature);
            jsonObject.accumulate("OrderId", orderId);
            jsonObject.accumulate("SignOffPerson", receiverName);
            jsonObject.accumulate("SignOffLatitude", UserData.getInstance().lat);
            jsonObject.accumulate("SignOffLongitude", UserData.getInstance().lng);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        APITask task = new APITask(SERVER_ADDR, API_URL, jsonObject, HTTP_POST);
        task.execute((Void) null);
    }

    public void uploadPicture(int orderId, int userId, String bitmapStr, String name) {
        String API_URL = String.format("/UploadPicture?orderId=%d&userId=%d&authtoken=%s", orderId, userId, UserData.getInstance().authToken);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("Name", name);
            jsonObject.accumulate("Content", bitmapStr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        APITask task = new APITask(SERVER_ADDR, API_URL, jsonObject, HTTP_POST);
        task.execute((Void) null);
    }

    public void uploadDocument(int orderId, int userId, String documentStr, String name) {
        String API_URL = String.format("/UploadDocument?orderId=%d&userId=%d&authtoken=%s", orderId, userId, UserData.getInstance().authToken);
        JSONObject jsonObject = new JSONObject();
        try {
            jsonObject.accumulate("Name", name);
            jsonObject.accumulate("Content", documentStr);

        } catch (JSONException e) {
            e.printStackTrace();
        }
        APITask task = new APITask(SERVER_ADDR, API_URL, jsonObject, HTTP_POST);
        task.execute((Void) null);
    }

    public void getCustomerOrders() {
        String API_URL = String.format("/customerorders?customerid=%d&orderno=0", UserData.getInstance().customerId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getNewRefInfo() {
        String API_URL = "/getnewrefno";
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getCustomerJobSites() {
        String API_URL = String.format("/getcustomerjobsites?customerid=%d", UserData.getInstance().customerId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void saveCustomerOrder(JSONObject object) {
        String API_URL = "/savecustomerorder";
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void saveJobSite(JSONObject object) {
        String API_URL = "/savejobsite";
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void getCustomerOrderDocuments(int orderId) {
        String API_URL = String.format("/getorderdocuments?orderid=%d", orderId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getCustomerInvoices(int customerId) {
        String API_URL = String.format("/getcustomerinvoices?customerid=%d", customerId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getCustomerTickets(int customerId) {
        String API_URL = String.format("/getcustomertickets?customerid=%d", customerId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void deleteCustomerTicket(int ticketId) {
        String API_URL = String.format("/deletecustomerticket?ticketid=%d", ticketId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void getCustomerTicketDetail(int ticketId) {
        String API_URL = String.format("/getticketdetail?ticketid=%d", ticketId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void addTicketNote(JSONObject object) {
        String API_URL = "/saveticketnote";
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void saveCustomerTicket(JSONObject object) {
        String API_URL = "/savecustomerticket";
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void changePassword(JSONObject object) {
        String API_URL = "/changepassword";
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void searchValue(String searchValue) {
        String API_URL = String.format("/SearchItemList?SearchValue=%s", searchValue);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void markItemComplete(int orderId) {
        String API_URL = String.format("/MarkItemComplete?orderId=%d", orderId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void markItemDelivered(int orderId) {
        String API_URL = String.format("/MarkItemDelivered?orderId=%d", orderId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void markItemLoadedInTruck(int orderId) {
        String API_URL = String.format("/MarkItemLoadedInTruck?orderId=%d", orderId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void createOrderItem(JSONObject object) {
        String API_URL = String.format("/CreateOrderItem?userId=%d&authtoken=%s", UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void updateOrderItem(JSONObject object) {
        String API_URL = String.format("/UpdateOrderItem?userId=%d&authtoken=%s", UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, object, HTTP_POST);
        task.execute((Void) null);
    }

    public void checkOrderLastAction(int orderId) {
        String API_URL = String.format("/CheckOrderLastAction?orderid=%d&authtoken=%s", orderId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void cutting(String scannedValue) {
        String API_URL = String.format("/Cutting?scannedValue=%s&authtoken=%s&userId=%d", scannedValue, UserData.getInstance().authToken, UserData.getInstance().userId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void forming(String scannedValue) {
        String API_URL = String.format("/Forming?scannedValue=%s&authtoken=%s&userId=%d", scannedValue, UserData.getInstance().authToken, UserData.getInstance().userId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    //*********************************************************************************
    public void loading(String scannedValue) {
        //String API_URL = String.format("/Loading?scannedValue=%s&authtoken=%s&userId=%d", scannedValue, UserData.getInstance().authToken, UserData.getInstance().userId);
        String API_URL = String.format("/LoadingV2?scannedValue=%s&authtoken=%s&userid=%d", scannedValue, UserData.getInstance().authToken, UserData.getInstance().userId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void checkOrUncheck(int orderItemId, int loaded) {
        String API_URL = String.format("/LoadingCheckOrUncheck?orderItemId=%d&loaded=%d&authtoken=%s&userId=%d", orderItemId, loaded, UserData.getInstance().authToken, UserData.getInstance().userId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    /***************************************** Methods By Asad ********************************************************/

    public void knockedTogether(String scanResult) {
        String API_URL = String.format("/KnockedTogether?scannedValue=%s&authtoken=%s&userid=%d", scanResult, UserData.getInstance().authToken, UserData.getInstance().userId);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void checkOrUncheckKnocked(String orderItemId, String completed) {
        String API_URL = String.format("/KnockedTogetherCheckOrUncheck?orderItemId=%s&completed=%s&userid=%d&authtoken=%s", orderItemId, completed, UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void loadingChjeckOrUncheck(String orderItemId, String loaded) {
        String API_URL = String.format("/LoadingCheckOrUncheck?orderItemId=%s&loaded=%s&userid=%d&authtoken=%s", orderItemId, loaded, UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void showPopup(String inNumber, String buttonText) {
        String API_URL = String.format("/MarkOrderReadyFor?innumber=%s&buttonText=%s&userid=%d&authtoken=%s", inNumber, buttonText, UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void checkUncheckDelivered(String orderItemId, String delivered) {
        String API_URL = String.format("/KnockedTogetherCheckOrUncheckDelivered?orderItemId=%s&delivered=%s&userId=%d&authtoken=%s", orderItemId, delivered, UserData.getInstance().userId, UserData.getInstance().authToken);
        APITask task = new APITask(SERVER_ADDR, API_URL, null, HTTP_POST);
        task.execute((Void) null);
    }

    public void itemInfo(String scannedValue) {
        String API_URL2 = String.format("/ItemInfo?scannedValue=%s&authtoken=%s&userid=%d", scannedValue, UserData.getInstance().authToken, UserData.getInstance().userId);
        APITask task = new APITask(SERVER_ADDR, API_URL2, null, HTTP_POST);
        task.execute((Void) null);
    }/* Methods By Asad Ends Here */

    // API Task..
    private class APITask extends AsyncTask<Void, Void, Boolean> {
        /* ---------------- API Task Variables ---------------- */
        // For calling api request..
        private String serverAddr = "";
        private String apiURL = "";
        private JSONObject reqObject = null;
        private InputStream inputStream;
        private int method = 0;

        // Result of api request..
        private JSONObject result = null;

        /* ---------------- API Task Functions ---------------- */
        APITask(String serverAddr, String apiURL, JSONObject reqParams, int method) {
            this.serverAddr = serverAddr;
            this.apiURL = apiURL;
            this.reqObject = reqParams;
            this.method = method;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            result = requestAPI();
            return true;
        }

        @Override
        protected void onPostExecute(final Boolean success) {
            if (callback != null) {
                callback.APICallback(result);
            }
        }

        @Override
        protected void onCancelled() {
            if (callback != null) {
                callback.APICallback(null);
            }
        }

        private JSONObject requestAPI() {
            JSONObject result = null;

            try {
                String apiRequestURL = serverAddr + apiURL;

                HttpClient httpclient = new DefaultHttpClient();
                StringEntity se = null;
                if (reqObject != null) {
                    se = new StringEntity(reqObject.toString(), HTTP.UTF_8);
                }
                HttpResponse httpResponse = null;

                switch (method) {
                    case HTTP_GET:
                        HttpGet httpGet = new HttpGet(apiRequestURL);
                        httpResponse = httpclient.execute(httpGet);
                        break;

                    case HTTP_DELETE:
                        HttpDelete httpDelete = new HttpDelete(apiRequestURL);
                        httpResponse = httpclient.execute(httpDelete);
                        break;

                    case HTTP_PUT:
                        HttpPut httpPut = new HttpPut(apiRequestURL);
                        httpPut.setEntity(se);
                        httpResponse = httpclient.execute(httpPut);
                        break;

                    default:
                        HttpPost httpPost = new HttpPost(apiRequestURL);
                        httpPost.setEntity(se);
                        httpPost.setHeader("Accept", "application/json");
                        httpPost.setHeader("Content-type", "application/json");
                        httpResponse = httpclient.execute(httpPost);
                        break;
                }

                inputStream = httpResponse.getEntity().getContent();
                if (inputStream != null) {
                    String iData = convertInputStreamToString(inputStream);
                    result = new JSONObject(iData);
                } else {
                    Log.d("[APITask] requestAPI", "No input stream prepared!");
                }

            } catch (Exception e) {
                Log.e("[APITask] requestAPI", e.getLocalizedMessage());
            }

            return result;
        }

        private String convertInputStreamToString(InputStream inputStream) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            String result = "";
            try {
                while ((line = bufferedReader.readLine()) != null) {
                    result += line;
                }

                inputStream.close();
            } catch (Exception e) {
                Log.e("[APITask] ConvertInput", e.getLocalizedMessage());
            }
            return result;
        }
    }
}
