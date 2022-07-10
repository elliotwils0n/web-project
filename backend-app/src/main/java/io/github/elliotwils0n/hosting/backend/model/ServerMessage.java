package io.github.elliotwils0n.hosting.backend.model;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import java.util.LinkedHashMap;
import java.util.Map;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class ServerMessage {

    private int status;
    private String message;

    @SneakyThrows
    @Override
    public String toString() {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("status", status);
        map.put("message", message);

        return new ObjectMapper()
                //.enable(SerializationFeature.INDENT_OUTPUT)
                .writeValueAsString(map);
    }

}
