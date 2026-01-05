package com.bamboosession.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class BambooAPI {

    private static final String PROFILE_URL = "https://api.minecraftservices.com/minecraft/profile";
    private static final String SKIN_URL = "https://api.minecraftservices.com/minecraft/profile/skins";
    private static final String NAME_URL = "https://api.minecraftservices.com/minecraft/profile/name/";

    public static class ProfileInfo {
        public final String username;
        public final String uuid;

        public ProfileInfo(String username, String uuid) {
            this.username = username;
            this.uuid = uuid;
        }
    }

    public static ProfileInfo getProfileInfo(String accessToken) throws IOException {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet request = new HttpGet(PROFILE_URL);
            request.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = client.execute(request)) {
                int statusCode = response.getStatusLine().getStatusCode();

                if (statusCode != 200) {
                    throw new IOException("Failed to get profile: HTTP " + statusCode);
                }

                String jsonString = EntityUtils.toString(response.getEntity(), StandardCharsets.UTF_8);
                JsonObject jsonObject = JsonParser.parseString(jsonString).getAsJsonObject();

                String username = jsonObject.get("name").getAsString();
                String uuid = jsonObject.get("id").getAsString();

                return new ProfileInfo(username, uuid);
            }
        }
    }

    public static boolean validateSession(String accessToken, String expectedUsername, String expectedUuid) {
        try {
            ProfileInfo info = getProfileInfo(accessToken);
            String normalizedExpected = expectedUuid.replaceAll("-", "");
            String normalizedActual = info.uuid.replaceAll("-", "");

            return info.username.equals(expectedUsername) &&
                    normalizedActual.equals(normalizedExpected);
        } catch (Exception e) {
            return false;
        }
    }

    public static int changeSkin(String url, String accessToken) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost request = new HttpPost(SKIN_URL);
            request.setHeader("Authorization", "Bearer " + accessToken);
            request.setHeader("Content-Type", "application/json");

            String jsonPayload = String.format("{\"variant\":\"classic\",\"url\":\"%s\"}", url);
            request.setEntity(new StringEntity(jsonPayload));

            try (CloseableHttpResponse response = client.execute(request)) {
                return response.getStatusLine().getStatusCode();
            }
        } catch (Exception e) {
            return -1;
        }
    }

    public static int changeName(String newName, String accessToken) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPut request = new HttpPut(NAME_URL + newName);
            request.setHeader("Authorization", "Bearer " + accessToken);

            try (CloseableHttpResponse response = client.execute(request)) {
                return response.getStatusLine().getStatusCode();
            }
        } catch (Exception e) {
            return -1;
        }
    }
}