package API;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * GoogleBooksAPI is a utility class for interacting with the Google Books API.
 * It sends HTTP GET requests to fetch book data based on a search query and handles retries for rate-limiting errors (HTTP 429).
 */
public class GoogleBooksAPI {

    private static final String API_URL = "https://www.googleapis.com/books/v1/volumes?q=";
    private static final int MAX_RETRIES = 3;
    private static final int INITIAL_RETRY_DELAY = 1000; // 1 second

    /**
     * Fetches book data from the Google Books API based on the provided query.
     * The method implements exponential backoff for retries in case of rate-limiting (HTTP 429).
     *
     * @param query The search query string to use for fetching book data. It can be a book title, author, or ISBN.
     * @return A JSON string containing the response from the Google Books API.
     * @throws IOException If there is a network error, an HTTP error, or the retries are exhausted.
     */
    public String fetchBooksData(String query) throws IOException {
        String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8.toString());
        String fullURL = API_URL + encodedQuery;
        int attempt = 0;

        while (attempt < MAX_RETRIES) {
            HttpURLConnection connection = null;
            try {
                connection = createConnection(fullURL);
                connection.setRequestMethod("GET");

                int responseCode = connection.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    return readResponse(connection);
                } else if (responseCode == 429) {
                    // Handle rate-limiting with exponential backoff
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
                // Handle interruptions during retry delays
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

    /**
     * Creates an HttpURLConnection for the given URL.
     * This method is protected to allow overriding during testing.
     *
     * @param fullURL The full URL to connect to.
     * @return An HttpURLConnection instance.
     * @throws IOException If an error occurs while opening the connection.
     */
    protected HttpURLConnection createConnection(String fullURL) throws IOException {
        URL url = new URL(fullURL);
        return (HttpURLConnection) url.openConnection();
    }

    /**
     * Reads the response from an HttpURLConnection.
     *
     * @param connection The HttpURLConnection to read the response from.
     * @return A string containing the API response.
     * @throws IOException If an error occurs while reading the response.
     */
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
