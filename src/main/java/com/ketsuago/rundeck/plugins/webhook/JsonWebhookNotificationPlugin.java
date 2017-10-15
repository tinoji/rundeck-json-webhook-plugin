/*
 * Copyright 2017 Hiroaki Kikuchi
 * exception handling is based on rundeck-slack-incoming-webhook-plugin from Andrew Karpow
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.ketsuago.rundeck.plugins.webhook;

import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;

import java.io.*;
import java.net.*;
import java.util.*;
import com.google.gson.Gson;

@Plugin(service="Notification",name="JsonWebhookNotification")
@PluginDescription(title="JSON Webhook", description="")
public class JsonWebhookNotificationPlugin implements NotificationPlugin {

    @PluginProperty(name = "webhookURL", title = "webhook URL", description = "Enter comma-separated URLs")
    private String strWebhookURL;

    public JsonWebhookNotificationPlugin() {
        // Do not remove constructor
    }

    public boolean postNotification(String trigger, Map executionData, Map config) {
        // get URL list and trim extra blanks
        List<String> webhookURLs = Arrays.asList(strWebhookURL.split(","));
        for (int i = 0; i < webhookURLs.size(); i++) {
            webhookURLs.set(i, webhookURLs.get(i).trim());
        }

        // merge trigger, executionData and config
        Map<String, Object> allData = new HashMap<>();
        allData.put("trigger", trigger);
        allData.put("execution", executionData);
        allData.put("config", config);

        // convert map to json
        Gson gson = new Gson();
        String executionJson = gson.toJson(allData);

        // post json to URLs
        for (String webhookURL: webhookURLs) {
            HttpResponse response = postWebhook(webhookURL, executionJson);

            if (response.getCode() != HttpURLConnection.HTTP_OK) {
                throw new JsonWebhookNotificationPluginException("URL " + webhookURL + ": Unable to POST notification: " +
                        "server response: " + response.getCode() + " " + response.getMessage());
            }
        }
        return true;
    }


    private HttpResponse postWebhook(String URL, String message) {
        HttpURLConnection con = null;
        try {
            URL url = toURL(URL);
            con = openConnection(url);
            con.setDoOutput(true);
            putRequestStream(con, message);
            con.connect();

            return new HttpResponse(con.getResponseCode(), con.getResponseMessage());
        } catch (IOException e) {
            throw new JsonWebhookNotificationPluginException("Error opening connection: [" + e.getMessage() + "]", e);
        } finally {
            if (con != null) {
                con.disconnect();
            }
        }
    }

    private URL toURL(String str) {
        try {
            return new URL(str);
        } catch (MalformedURLException e) {
            throw new JsonWebhookNotificationPluginException("Webhook URL is malformed: [" + e.getMessage() +"]", e);
        }
    }

    private HttpURLConnection openConnection(URL url) {
        try {
            return (HttpURLConnection) url.openConnection();
        } catch (IOException e) {
            throw new JsonWebhookNotificationPluginException("Error opening connection: [" + e.getMessage() + "]", e);
        }
    }

    private void putRequestStream(HttpURLConnection con, String message) {
        try {
            con.setRequestMethod("POST");
            con.setRequestProperty("Content-Type", "application/json");
            OutputStreamWriter out = new OutputStreamWriter(con.getOutputStream());
            out.write(message);
        } catch (ProtocolException e) {
            throw new JsonWebhookNotificationPluginException("Error in the underlying protocol: [" + e.getMessage() + "]", e);
        } catch (IOException e) {
            throw new JsonWebhookNotificationPluginException("Error putting data to Webhook URL: [" + e.getMessage() + "]", e);
        }
    }
}
