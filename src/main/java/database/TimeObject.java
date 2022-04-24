package database;

import org.json.JSONObject;

import java.io.IOException;

public class TimeObject {

    public static String getTime() {
        String url = "http://worldtimeapi.org/api/timezone/Asia/Ho_Chi_Minh";  // example url which return json data
        ReadTimeObject readJson = new ReadTimeObject();
        JSONObject json = null;  // calling method in order to read.
        try {
            json = readJson.readJsonFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        String data = (String) json.get("datetime");
        String date = data.split("T")[0];
        String time = data.split("T")[1].substring(0,14);
        return date + " " + time;
    }

   /* public static void main(String[] args) throws IOException, JSONException {
        for (int i = 0; i < 10; i ++) {
            System.out.println(TimeTest.getTime());
        }
    }*/
}

