package database;

import java.io.IOException;
import java.util.HashMap;

public class TimeObject {

    public static String getTime() {
        String url = "https://worldtimeapi.org/api/timezone/Asia/Ho_Chi_Minh";  // example url which return json data
        ReadTimeObject readJson = new ReadTimeObject();
        HashMap<String, String> json = null;  // calling method in order to read.
        try {
            json = readJson.readJsonFromUrl(url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assert json != null;
        String data = json.get("datetime");
        String date = data.split("T")[0];
        String time = data.split("T")[1].substring(0,14);
        return date + " " + time;
    }
}

