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

    public String fetchBooksData(String query) throws IOException {
        // Mã hóa truy vấn
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String fullURL = API_URL + encodedQuery;
        URL url = new URL(fullURL);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        // Kiểm tra mã phản hồi
        int responseCode = connection.getResponseCode();
        if (responseCode != HttpURLConnection.HTTP_OK) {
            throw new IOException("Failed : HTTP error code : " + responseCode);
        }

        InputStream inputStream = connection.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream, StandardCharsets.UTF_8);
        StringBuilder response = new StringBuilder();

        int data = inputStreamReader.read();
        while (data != -1) {
            response.append((char) data);
            data = inputStreamReader.read();
        }
        inputStreamReader.close();
        return response.toString();
    }
}
