package be.houbrechtsit.goserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MovieAPILambda {
    private MovieRepository movieRepository = MovieRepository.geInstance();

    public ProxyResponse handleRequest(Map<String, Object> request, Context context) {
        Map<String, Object> applicationInfo = new HashMap<>();
        applicationInfo.put("name", "Movie API");
        applicationInfo.put("version", "Movie API");

        Map<String, Object> info = new HashMap<>();
        info.put("applicationInfo", applicationInfo);
        info.put("request", request);
        info.put("request-class", request.getClass().getName());
        info.put("context", context);
        return new ProxyResponse(200, Collections.emptyMap(), new Gson().toJson(info));
    }
}
