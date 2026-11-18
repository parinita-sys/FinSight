
package finsight;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class CurrencyConverter {
    private static String cachedCurrency = "";
    private static double cachedRate = 1.0;

    public static double convert(
            double amount,
            String fromCurrency,
            String toCurrency
    ) throws Exception {

        if (fromCurrency.equals(toCurrency)) {
            return amount;
        }
        if (toCurrency.equals(cachedCurrency)) {
            return amount * cachedRate;
        }

        String url =
                "https://api.frankfurter.dev/v2/rate/"
                        + fromCurrency + "/" + toCurrency;

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();

        HttpResponse<String> response =
                client.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        if (response.statusCode() != 200) {
            throw new Exception("Could not fetch exchange rate.");
        }

        java.util.regex.Matcher matcher =
                java.util.regex.Pattern
                        .compile("\"rate\"\\s*:\\s*([0-9]+(?:\\.[0-9]+)?)")
                        .matcher(response.body());

        if (!matcher.find()) {
            throw new Exception("Exchange rate was not found.");
        }

        double rate = Double.parseDouble(matcher.group(1));
        cachedCurrency = toCurrency;
        cachedRate = rate;
        return amount * rate;
    }
}
