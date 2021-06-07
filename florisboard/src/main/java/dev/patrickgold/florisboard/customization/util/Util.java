package dev.patrickgold.florisboard.customization.util;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Shimona on 01-06-2018.
 */

public class Util {

    public static String getDataFromServer(String content,
                                           String requestMethod, String serverUrl, String contentType) {
        String response = "", responseMessage = "";
        Boolean success = false;
        DataOutputStream outputStream = null;
        try {

            URL new_url = new URL(serverUrl);
            HttpURLConnection connection = (HttpURLConnection) new_url
                    .openConnection();

            // Allow Inputs & Outputs
            connection.setDoInput(true);
            connection.setDoOutput(true);
            connection.setUseCaches(true);
            // Enable PUT method
            connection.setRequestMethod(requestMethod);
            connection.setRequestProperty("Connection", "Keep-Alive");

            connection.setRequestProperty("Content-Type", contentType);

            outputStream = new DataOutputStream(connection.getOutputStream());

            byte[] BytesToBeSent = content.getBytes();
            if (BytesToBeSent != null) {
                outputStream.write(BytesToBeSent, 0, BytesToBeSent.length);
            }
            int responseCode = connection.getResponseCode();

            responseMessage = connection.getResponseMessage();

            if (responseCode == 200 || responseCode == 202) {
                success = true;

            }

            InputStreamReader inputStreamReader = null;
            BufferedReader bufferedReader = null;
            try {
                inputStreamReader = new InputStreamReader(
                        connection.getInputStream());
                bufferedReader = new BufferedReader(inputStreamReader);

                StringBuilder responseContent = new StringBuilder();

                String temp = null;

                boolean isFirst = true;

                while ((temp = bufferedReader.readLine()) != null) {
                    if (!isFirst)
                        responseContent.append("\n");
                    responseContent.append(temp);
                    isFirst = false;
                }

                response = responseContent.toString();

            } catch (Exception e) {
            } finally {
                try {
                    inputStreamReader.close();
                } catch (Exception e) {
                }
                try {
                    bufferedReader.close();
                } catch (Exception e) {
                }

            }

        } catch (Exception ex) {
            success = false;
        } finally {
            try {
                outputStream.flush();
                outputStream.close();
            } catch (Exception e) {
            }
        }

        return response;
    }
}
