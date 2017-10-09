package com.ketsuago.rundeck.plugins.huginn;

import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.rmi.server.ExportException;
import java.util.*;
import com.google.gson.Gson;

@Plugin(service="Notification",name="example")
@PluginDescription(title="Example Plugin", description="An example Plugin for Rundeck Notifications.")
public class HuginnNotificationPlugin implements NotificationPlugin{

//    @PluginProperty(name = "example",title = "Example String",description = "Example description")
//    private String example;

    @PluginProperty(name = "webhook",title = "huginn webhook URL",description = "Set URL of a webhook agent of huginn")
    private String webhookURL;

    public HuginnNotificationPlugin(){

    }

    public boolean postNotification(String trigger, Map executionData, Map config) {
        System.err.printf("Trigger %s fired for %s, configuration: %s\n",trigger,executionData,config);
        System.err.printf("Local field example is: %s\n",webhookURL);
        return true;
    }

    // test code for parse map to json and POST json
    public static void main(String args[]) {

        Map<String, String> map = new HashMap<>();
        map.put("id", "1");
        map.put("href", "hogehoge");
        System.out.println(map);

        // map -> json
        Gson gson = new Gson();
        String json = gson.toJson(map);
        System.out.println(json);


        HttpURLConnection con = null;
        String testHTTPServer = "http://posttestserver.com/post.php";
        StringBuffer result = new StringBuffer();

        try {
            URL url = new URL(testHTTPServer);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(json);
            out.close();
            con.connect();

            // HTTPレスポンスコード
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // 通信に成功した
                // テキストを取得する
                final InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();

                if (null == encoding) {
                    encoding = "UTF-8";
                }
                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;
                // 1行ずつテキストを読み込む
                while ((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                bufReader.close();
                inReader.close();
                in.close();
            } else {
                System.out.println(status);
            }

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        System.out.println("result=" + result.toString());
    }
}
