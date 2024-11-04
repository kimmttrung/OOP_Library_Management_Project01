package API;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class GoogleBooksAPI {
    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_RETRY_DELAY = 1000; // 1 second


    public String fetchBooksData(String query) throws IOException {
        // Mã hóa truy vấn
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String fullURL = API_URL + encodedQuery;
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            HttpURLConnection connection = null;
            try {
                URL url = new URL(fullURL);
                connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return readResponse(connection);
                } else if (responseCode == 429) {
                    attempt++;
                    if (attempt == MAX_RETRIES) {
                        throw new IOException("Failed after maximum retries due to HTTP 429 error.");
                    }
                    int delay = INITIAL_RETRY_DELAY * (int) Math.pow(2, attempt - 1);
                    System.out.println("HTTP 429 error. Retrying in " + delay + "ms...");
                    Thread.sleep(delay);
                } else {
                    throw new IOException("Failed : HTTP error code : " + responseCode);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new IOException("Request interrupted during retry wait.", e);
            } finally {
                if (connection != null) {
                    connection.disconnect();
                }
            }
        }
        throw new IOException("Unable to fetch data after retries.");
    }

    private String readResponse(HttpURLConnection connection) throws IOException {
        try (InputStream inputStream = connection.getInputStream();
             InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {

            StringBuilder response = new StringBuilder();
            int data = inputStreamReader.read();
            while (data != -1) {
                response.append((char) data);
                data = inputStreamReader.read();
            }
            return response.toString();
        }
    }
}
