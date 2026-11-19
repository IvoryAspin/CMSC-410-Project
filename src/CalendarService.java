import java.net.URI;
import java.net.URLEncoder;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

public class CalendarService {

    private static final HttpClient client =
            HttpClient.newHttpClient();

    public static String getEvents(String date) throws Exception {

        String apiKey = Main.config.getProperty("GOOGLE_API_KEY");

        String calendarId =
                Main.config.getProperty("GOOGLE_CALENDAR_ID");

        String timezone =
                Main.config.getProperty("TIME_ZONE");

        ZoneId zone = ZoneId.of(timezone);

        LocalDate selectedDate = LocalDate.parse(date);

        ZonedDateTime start =
                selectedDate.atStartOfDay(zone);

        ZonedDateTime end =
                selectedDate.plusDays(1).atStartOfDay(zone);

        String timeMin = start.format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String timeMax = end.format(
                DateTimeFormatter.ISO_OFFSET_DATE_TIME);

        String encodedCalendar = URLEncoder.encode(
                calendarId, StandardCharsets.UTF_8);

        String url =
                "https://www.googleapis.com/calendar/v3/calendars/"
                + encodedCalendar
                + "/events?key=" + apiKey
                + "&timeMin=" + URLEncoder.encode(
                        timeMin, StandardCharsets.UTF_8)
                + "&timeMax=" + URLEncoder.encode(
                        timeMax, StandardCharsets.UTF_8)
                + "&singleEvents=true"
                + "&orderBy=startTime"
                + "&maxResults=250";

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response = client.send(
                request,
                HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() != 200) {
            throw new RuntimeException(
                    "Google returned HTTP " + response.statusCode());
        }

        return response.body();
    }
}