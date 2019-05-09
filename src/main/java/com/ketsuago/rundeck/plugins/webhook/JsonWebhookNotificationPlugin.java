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

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.plugins.descriptions.PluginDescription;
import com.dtolabs.rundeck.plugins.descriptions.PluginProperty;
import com.dtolabs.rundeck.plugins.notification.NotificationPlugin;
import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Map;

@Plugin( service="Notification",name="JsonWebhookNotification" )
@PluginDescription( title="JSON Webhook", description="POST Webhook in the JSON format" )
public class JsonWebhookNotificationPlugin implements NotificationPlugin {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private final int CONNECTION_TIMEOUT = 5 * 1000; //5 seconds

    @PluginProperty( name = "webhookURL",
            title = "URL(s)",
            description = "Comma-separated URLs",
            required = true )
    private String STR_WEBHOOK_URL; // from GUI input

    public JsonWebhookNotificationPlugin() {
        // Do not remove constructor
    }

    public boolean postNotification(String trigger, Map executionData, Map config) {
        String[] strUrls = STR_WEBHOOK_URL.split(",");
        if ( strUrls.length > 0 ) {

            // convert map to json
            Gson gson = new Gson();
            String requestPayload = gson.toJson(ImmutableMap.of("trigger", trigger,
                    "execution", executionData,
                    "config", config));

            for (String strUrl : strUrls) {
                if (strUrl.length() == 0 || strUrl.trim().length() == 0)
                    continue;

                if (logger.isDebugEnabled())
                    logger.debug("Sending notification to [{}]", strUrl);

                // post json webhook
                postWebhook(strUrl, requestPayload);
            }
        }

        return true;
    }

    private void postWebhook(String strUrl, String requestPayload) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(strUrl);
            connection = openConnection(url);
            if (connection != null) {
                appendBody(connection, requestPayload);
                connection.connect();

                if (connection.getResponseCode() != 200)
                    logger.warn("Error {}, failed to send notification to [{}], response: {}", connection.getResponseCode(),
                            strUrl, connection.getResponseMessage());
            }
        } catch ( SocketTimeoutException ex ) {
            logger.warn("Failed to establish connection to [" + strUrl + "]", ex);
        } catch ( Exception ex ) {
            logger.warn("Unhandled exception while sending notification", ex);
        } finally {
            if (connection != null)
                connection.disconnect();
        }
    }

    private HttpURLConnection openConnection(URL requestUrl) {
        try {
            HttpURLConnection connection = (HttpURLConnection) requestUrl.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(CONNECTION_TIMEOUT);

            return connection;
        } catch (SocketTimeoutException ex) {
            logger.warn( "Failed to establish connection to [" + requestUrl + "]", ex );
            return null;
        } catch (Exception ex) {
            logger.warn( "Unhandled exception while establishing connection", ex );
            return null;
        }
    }

    private void appendBody(HttpURLConnection connection, String payload) {
        try (final DataOutputStream writer = new DataOutputStream(connection.getOutputStream())) {
            writer.writeBytes( payload );
            writer.flush();
        } catch (IOException ex) {
            logger.warn( "Unable to write request payload", ex );
        }
    }
}
