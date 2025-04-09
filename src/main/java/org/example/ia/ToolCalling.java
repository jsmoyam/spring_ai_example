package org.example.ia;

import org.json.JSONObject;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ToolCalling {

    /**
     * Obtiene la temperatura actual de una ciudad usando una API en Internet.
     *
     * @param cityName Nombre de la ciudad (por ejemplo, "Madrid").
     * @return La temperatura actual en grados Celsius (como String).
     */
    @Tool(description = "temperatura de una ciudad")
    public String getCurrentTemperature(String cityName) {
        System.out.println("Llamando a la API de OpenWeatherMap para obtener la temperatura de " + cityName);

        // URL base de la API de OpenWeatherMap
        String apiKey = "XXXXX";
        String url = "http://api.openweathermap.org/data/2.5/weather?q=" + cityName + "&APPID=" + apiKey + "&units=metric";

        try {
            // Crear el cliente HTTP
            RestTemplate restTemplate = new RestTemplate();

            // Realizar la solicitud GET y capturar la respuesta
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Verificar que la respuesta sea exitosa
            if (response.getStatusCode().is2xxSuccessful()) {
                // Parsear el JSON recibido
                JSONObject jsonResponse = new JSONObject(response.getBody());

                // Extraer el valor de la temperatura
                double temperature = jsonResponse.getJSONObject("main").getDouble("temp");

                // Retornar la temperatura en un formato adecuado
                return "La temperatura actual en " + cityName + " es: " + temperature + "°C";
            } else {
                return "No se pudo obtener la temperatura. Código de respuesta: " + response.getStatusCode();
            }
        } catch (Exception e) {
            // Capturar cualquier excepción que ocurra durante la solicitud
            return "Error al obtener la temperatura: " + e.getMessage();
        }
    }

    /**
     * Hace una llamada a la API de Chuck Norris y obtiene un chiste aleatorio.
     * Extrae y devuelve el valor del atributo "value" del JSON.
     *
     * @return El texto del chiste (atributo "value") si la solicitud es exitosa,
     *         o un mensaje de error en caso de fallo.
     */
    @Tool(description = "Broma de chuck norris", returnDirect = true)
    public String getChuckNorrisJoke() {
        System.out.println("Llamando a la API de Chuck Norris para obtener un chiste");

        // URL del API de Chuck Norris
        String url = "https://api.chucknorris.io/jokes/random";

        try {
            // Crear el cliente HTTP
            RestTemplate restTemplate = new RestTemplate();

            // Realizar la solicitud GET y capturar la respuesta
            ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

            // Registrar el cuerpo de la respuesta (para depuración)
            return response.getBody();
        } catch (Exception e) {
            // Capturar cualquier excepción que ocurra durante la solicitud
            return "Error al obtener el chiste: " + e.getMessage();
        }
    }


    public static void main(String[] args) {
        String temperature = new ToolCalling().getCurrentTemperature("Malaga");
        System.out.println(temperature);

        String joke = new ToolCalling().getChuckNorrisJoke();
        System.out.println(joke);
    }


}
