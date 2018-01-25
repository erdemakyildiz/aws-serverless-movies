package be.houbrechtsit.goserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MovieAPILambda {

//    private MovieRepository movieRepository = MovieRepository.geInstance();

    public ProxyResponse handleRequest(Object request, Context context) {
        Map<String, Object> dump = new HashMap<>();
        dump.put("request", request);
        dump.put("request-class", request.getClass().getName());
        dump.put("context", context);
        return new ProxyResponse(200, Collections.emptyMap(), new Gson().toJson(dump));
    }


    @Data
    @AllArgsConstructor
    public class ProxyResponse {
        private int statusCode;
        private Map<String, Object> headers;
        private String body;
    }
}
