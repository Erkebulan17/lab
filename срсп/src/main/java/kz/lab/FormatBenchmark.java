package kz.lab;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/** Small local comparison; results depend on machine, JVM warm-up and library versions. */
public final class FormatBenchmark {
    private static final ObjectMapper JSON = new ObjectMapper();
    private static final XmlMapper XML = new XmlMapper();
    private static final YAMLMapper YAML = new YAMLMapper();
    private FormatBenchmark() { }

    public static Map<String, Object> measure(Student student) throws Exception {
        Map<String, Object> result = new LinkedHashMap<>();
        measureOne(result, "JSON", () -> JSON.writeValueAsBytes(student));
        measureOne(result, "XML", () -> XML.writeValueAsBytes(student));
        measureOne(result, "YAML", () -> YAML.writeValueAsBytes(student));
        measureOne(result, "Protobuf", () -> toProto(student));
        return result;
    }

    private static void measureOne(Map<String, Object> result, String name, Encoder encoder) throws Exception {
        for (int i = 0; i < 500; i++) encoder.encode(); // warm-up
        int iterations = 5000;
        long start = System.nanoTime();
        byte[] last = null;
        for (int i = 0; i < iterations; i++) last = encoder.encode();
        long elapsed = System.nanoTime() - start;
        result.put(name, Map.of("bytes", last.length, "iterations", iterations,
                "totalMillis", elapsed / 1_000_000.0, "averageMicros", elapsed / 1000.0 / iterations));
    }

    @FunctionalInterface private interface Encoder { byte[] encode() throws Exception; }

    // Protobuf wire format: fields 1=id(varint), 2=name(string), 3=email(string), 4=age(varint).
    private static byte[] toProto(Student s) throws Exception {
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        writeVarint(out, (1 << 3) | 0); writeVarint(out, s.id());
        writeString(out, 2, s.name()); writeString(out, 3, s.email());
        writeVarint(out, (4 << 3) | 0); writeVarint(out, s.age());
        return out.toByteArray();
    }
    private static void writeString(ByteArrayOutputStream out, int field, String value) throws Exception {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        writeVarint(out, (field << 3) | 2); writeVarint(out, bytes.length); out.write(bytes);
    }
    private static void writeVarint(ByteArrayOutputStream out, int value) {
        while ((value & ~0x7F) != 0) { out.write((value & 0x7F) | 0x80); value >>>= 7; }
        out.write(value);
    }
}
