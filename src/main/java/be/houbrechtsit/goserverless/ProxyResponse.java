package be.houbrechtsit.goserverless;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;

/**
 * @author Ivo Houbrechts
 */
@Data
@AllArgsConstructor
public class ProxyResponse {
    private int statusCode;
    private Map<String, Object> headers;
    private String body;
}
