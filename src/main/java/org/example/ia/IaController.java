package org.example.ia;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.ai.ollama.api.OllamaModel;
import org.springframework.ai.ollama.api.OllamaOptions;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.reader.tika.TikaDocumentReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.FileSystemResourceLoader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class IaController {

    private final OllamaChatModel chatModel;
    private final ChatClient chatClient;
    private final ChatClient askClient;
    private final VectorStore vectorStore;

    //@Value("${pdf.folder.path:./pdf_folder}")
    String pdfFolder = "./pdf_folder";

    @Autowired
    public IaController(OllamaChatModel chatModel, VectorStore vectorStore) {
        this.chatModel = chatModel;
        this.chatClient = ChatClient.create(this.chatModel);
        this.askClient = ChatClient.create(this.chatModel);
        this.vectorStore = vectorStore;

        processPdf();
    }

    private void processPdf() {
        File folder = new File(pdfFolder);
        File[] files = folder.listFiles();
        List<Document> documents = new ArrayList<>();

        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().toLowerCase().endsWith(".pdf")) {
                    FileSystemResource resource = new FileSystemResource(file);
                    TikaDocumentReader reader = new TikaDocumentReader(resource);
                    try {
                        documents.addAll(reader.get());
                    } catch (RuntimeException e) {
                        System.err.println("Error processing file: " + file.getName() + " - " + e.getMessage());
                    }
                }
            }
        }

        vectorStore.add(documents);
    }


    @PostMapping("/ai/loadPdfs")
    public ResponseEntity<Void> loadPdfs() {
        processPdf();
        return ResponseEntity.ok().build();
    }

    @GetMapping("/ai/ask")
    public Map<String,String> ask(@RequestParam(value = "question")  String question) {
        Prompt prompt = new Prompt(
                question,
                OllamaOptions.builder()
                        //.model("granite3-dense:2b")

                        .temperature(0.2)
                        .build()
        );

        String response = this.askClient
                .prompt(prompt)
                .advisors(new QuestionAnswerAdvisor(vectorStore))
                .call()
                .content();
        return Map.of("generation", response);
    }


    @GetMapping("/ai/generate")
    public Map<String,String> generate(@RequestParam(value = "message") String message) {
        String response = this.chatClient
                .prompt(message)
                .tools(new ToolCalling())
                .call()
                .content();

        return Map.of("generation", response);
    }

    @GetMapping("/ai/generateStream")
    public Flux<ChatResponse> generateStream(@RequestParam(value = "message") String message) {
        Prompt prompt = new Prompt(new UserMessage(message));
        return this.chatModel.stream(prompt);
    }

    /**
     * Endpoint para traducir texto de un idioma a otro.
     *
     * @param sourceLanguage      Idioma de origen (por ejemplo, "en").
     * @param destinationLanguage Idioma de destino (por ejemplo, "es").
     * @param text                Texto a traducir.
     * @return Mapa con el texto traducido.
     */
    @GetMapping("/ai/translate")
    public Map<String, String> translate(
            @RequestParam("sourceLanguage") String sourceLanguage,
            @RequestParam("destinationLanguage") String destinationLanguage,
            @RequestParam("text") String text) {

        // Construir el prompt para traducción
        String promptMessage = "Actua como un traductor experto entre dos lenguajes que te voy a pasar. " +
                "No quiero que me des explicaciones, ni ejemplos, ni nada, solo el texto traducido. " +
                "Traduce el siguiente texto desde "
                + sourceLanguage + " a "
                + destinationLanguage + ": \""
                + text + "\"";

        // En funcion del idioma origen escogemos un modelo u otro
        String model = OllamaModel.MISTRAL.getName();
        if (sourceLanguage.equals("español")) {
            model = "granite3-dense:2b";
        }

        // Procesar la traducción mediante el modelo
        ChatResponse chatResponse = chatModel.call(
                new Prompt(
                        promptMessage,
                        OllamaOptions.builder()
                                .model(model)
                                .temperature(0.2)
                                .build()
                ));

        String response = chatResponse.getResults().stream()
                .map(Generation::getOutput)
                .filter(AssistantMessage.class::isInstance) // Filtrar solo AssistantMessage
                .map(AssistantMessage.class::cast) // Castear a AssistantMessage
                .map(AssistantMessage::getText) // Obtener el contenido
                .collect(Collectors.joining("\n"));

        // Retornar la traducción
        //System.out.println("Prompt: " + promptMessage);
        return Map.of("translation", response);
    }

}
