import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class NasaService {

    private static final HttpClient client =
            HttpClient.newHttpClient();

    public static String getPicture(String date) throws Exception {

        String apiKey = Main.config.getProperty("NASA_API_KEY");

        String url =
                "https://api.nasa.gov/planetary/apod"
                + "?api_key=" + apiKey
                + "&date=" + date;

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "NASA returned HTTP " + response.statusCode());
        }

        return response.body();
    }
}