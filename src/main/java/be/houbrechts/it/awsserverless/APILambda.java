package be.houbrechts.it.awsserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class APILambda {
    public static final String HTTP_METHOD = "httpMethod";
    public static final String BODY = "body";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String PATH_PARAMETERS = "pathParameters";

    public static final Gson gson = new Gson();

    private static final Logger log = LogManager.getLogger(APILambda.class);

    @SuppressWarnings("unchecked")
    public ProxyResponse handleRequest(Map<String, Object> request, Context context) {
        log.debug("request: {}", new Gson().toJson(request));
        log.debug("context: {}", new Gson().toJson(context));

        try {
            String resource = (String) request.get("resource");
            if (resource != null) {
                if (resource.startsWith("/v1/movies")) {
                    return MovieAPI.handleMovieRequest((String) request.get(HTTP_METHOD),
                            (String) request.get(BODY),
                            (Map<String, String>) request.get(PATH_PARAMETERS));
                }
                if (resource.startsWith("/v1/ratings")) {
                    return RatingAPI.handleRatingRequest((String) request.get(HTTP_METHOD),
                            (String) request.get(BODY),
                            (Map<String, String>) request.get(PATH_PARAMETERS));
                }
            }
            if ("/v1/info".equals(resource)) {
                return handleInfoRequest(request, context);
            }
        } catch (Exception e) {
            log.error("Exception handling request", e);
            return new ProxyResponse(500, null, "{\"error\":\"" + e.getMessage() + "\"}");
        }
        return new ProxyResponse(404, null, null);
    }


    public static ProxyResponse methodNotAllowed() {
        return new ProxyResponse(405, null, "{\"error\":\"method not allowed\"}");
    }


    private ProxyResponse handleInfoRequest(Map<String, Object> request, Context context) {
        Map<String, Object> info = new HashMap<>();
        info.put("applicationInfo", readApplicationProperties());
        info.put("request", request);
        info.put("request-class", request.getClass().getName());
        info.put("context", context);
        return new ProxyResponse(200, Collections.emptyMap(), new Gson().toJson(info));
    }

    private Properties readApplicationProperties() {
        Properties applicationProperties = new Properties();
        try {
            applicationProperties.load(getClass().getClassLoader().getResourceAsStream("application.properties"));
        } catch (IOException e) {
            log.error("could not read application.properties", e);
        }
        return applicationProperties;
    }
}
