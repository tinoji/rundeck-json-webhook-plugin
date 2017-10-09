package com.ketsuago.rundeck.plugins.webhook;

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
import java.util.*;
import com.google.gson.Gson;

@Plugin(service="Notification",name="JsonWebhookNotification")
@PluginDescription(title="JSON Webhook", description="POST JSON data to a webhook URL")
public class JsonWebhookNotificationPlugin implements NotificationPlugin {

    @PluginProperty(name = "webhookURL", title = "webhook URL", description = "")
    private String webhookURL;

    public JsonWebhookNotificationPlugin() {
        // Do not remove constructor
    }

    public boolean postNotification(String trigger, Map executionData, Map config) {
        // merge trigger, executionData and config
        Map<String, Object> allData = new HashMap<>();
        allData.put("trigger", trigger);
        allData.put("execution", executionData);
        allData.put("config", config);

        // convert map to json
        Gson gson = new Gson();
        String executionJson = gson.toJson(allData);

        HttpURLConnection con = null;
        StringBuffer result = new StringBuffer();

        try {
            URL url = new URL(webhookURL);
            con = (HttpURLConnection) url.openConnection();
            con.setDoOutput(true);
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(executionJson);
            out.close();
            con.connect();

            // HTTP response code
            final int status = con.getResponseCode();
            if (status == HttpURLConnection.HTTP_OK) {
                // get response
                final InputStream in = con.getInputStream();
                String encoding = con.getContentEncoding();

                if (null == encoding) {
                    encoding = "UTF-8";
                }
                final InputStreamReader inReader = new InputStreamReader(in, encoding);
                final BufferedReader bufReader = new BufferedReader(inReader);
                String line = null;

                while ((line = bufReader.readLine()) != null) {
                    result.append(line);
                }
                bufReader.close();
                inReader.close();
                in.close();
            } else {
                System.err.printf("ERROR! HTTP code: %d\n", status);
                return false;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
        System.err.printf("POST result: %s\n", result.toString());
        return true;
    }
}
