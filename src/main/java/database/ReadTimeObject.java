package database;

import org.json.simple.JSONObject;
import org.json.simple.JSONValue;

import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

public class ReadTimeObject {
    public HashMap<String, String> readJsonFromUrl(String link) throws IOException {
        InputStream input = new URL(link).openStream();
        // Input Stream Object To Start Streaming.
        try {                                 // try catch for checked exception
            BufferedReader re = new BufferedReader(new InputStreamReader(input, Charset.forName("UTF-8")));
            // Buffer Reading In UTF-8
            String Text = Read(re);         // Handy Method To Read Data From BufferReader
            HashMap<String, String> json = (HashMap<String, String>) JSONValue.parse(Text);
            return json;    // Returning JSON
        } catch (Exception e) {
            return null;
        } finally {
            input.close();
        }
    }

    public String Read(Reader re) throws IOException {     // class Declaration
        StringBuilder str = new StringBuilder();     // To Store Url Data In String.
        int temp;
        do {
            temp = re.read();       //reading Charcter By Chracter.
            str.append((char) temp);

        } while (temp != -1);
        //  re.read() return -1 when there is end of buffer , data or end of file.

        return str.toString();

    }
}
