package kz.lab;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ToolsController {
    private final ObjectMapper mapper = new ObjectMapper();

    @GetMapping("/api/benchmark")
    public Map<String, Object> benchmark() throws Exception {
        return FormatBenchmark.measure(new Student(1, "Aruzhan", "aruzhan@example.kz", 20));
    }

    @PostMapping("/api/validate")
    public Map<String, Object> validate(@RequestBody JsonNode body) throws Exception {
        JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V202012);
        try (InputStream schemaStream = new ClassPathResource("student.schema.json").getInputStream()) {
            JsonSchema schema = factory.getSchema(schemaStream);
            List<String> errors = schema.validate(body).stream().map(Object::toString).collect(Collectors.toList());
            return Map.of("valid", errors.isEmpty(), "errors", errors);
        }
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public Map<String, String> badRequest(Exception e) {
        return Map.of("error", "Invalid request or processing error");
    }
}
