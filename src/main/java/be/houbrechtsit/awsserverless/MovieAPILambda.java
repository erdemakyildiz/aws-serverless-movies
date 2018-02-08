package be.houbrechtsit.awsserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.LambdaLogger;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class MovieAPILambda {
    public static final String HTTP_METHOD = "httpMethod";
    public static final String BODY = "body";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";

    private static final Logger log = LogManager.getLogger(MovieAPILambda.class);
    private static final Gson gson = new Gson();
    public static final String MOVIE_ID_PATH_PARAMETER = "movieId";
    public static final String PATH_PARAMETERS = "pathParameters";


    public ProxyResponse handleRequest(Map<String, Object> request, Context context) {
        LambdaLogger logger = context.getLogger();
        logger.log("request: " + new Gson().toJson(request));
        logger.log("context: " + new Gson().toJson(context));

        return handleInfoRequest(request, context);
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
