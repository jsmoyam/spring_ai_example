package org.example.ia;

import java.util.Map;

public class MainTest {
    public static void main(String[] args) {
        String baseUrl = "http://localhost:8080";
        IaApiClientTest client = new IaApiClientTest(baseUrl);

        /*
        String message = "Cuentame en palabras sencillas la transformada de Fourier";
        Map<String, String> response = client.generate(message);
        System.out.println("Respuesta: " + response.get("generation"));
        */

/*
        String messageToolCalling1 = "dime la temperatura que hace en Madrid";
        Map<String, String> responseToolCalling1 = client.generate(messageToolCalling1);
        System.out.println("Respuesta1: " + responseToolCalling1.get("generation"));


        String messageToolCalling2 = "Cuentame una broma de chuck norris";
        Map<String, String> responseToolCalling2 = client.generate(messageToolCalling2);
        System.out.println("Respuesta2: " + responseToolCalling2.get("generation"));



        String messageToolCalling3 = "Dime la temperatura en Malaga";
        Map<String, String> responseToolCalling3 = client.generate(messageToolCalling3);
        System.out.println("Respuesta3: " + responseToolCalling3.get("generation"));
*/
/*
        String translateMessage = "El sol se eleva sobre las montañas, pintando el cielo con tonos de naranja y rosa. " +
                "El aire fresco de la mañana trae consigo el aroma de los pinos y la tierra húmeda. " +
                "Un nuevo día comienza, lleno de promesas y posibilidades.";
        String translateMessageGerman = "Die Sonne geht über den Bergen auf und malt den Himmel mit Farbtönen von Orange und Rosa. Die kühle Morgenluft bringt den Duft von Kiefern und feuchter Erde mit sich. Ein neuer Tag beginnt, voller Versprechen und Möglichkeiten.";
        String translateResponse = client.translate("español", "catalan", translateMessage);
        System.out.println("Respuesta: " + translateResponse);
*/

        String question = "En que consiste un documento PDF? Quiero que me respondas en español y que me digas de que documento " +
                "has sacado la informacion. Quiero que seas muy conciso y sin irte por las ramas";
        question="Quiero que me hagas un resumen en 10 lineas de todos los documentos pdf que tienes, con su nombre";
        Map<String, String> answer = client.ask(question);
        System.out.println("Respuesta: " + answer);


    }
}
