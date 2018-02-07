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

    private MovieRepository movieRepository = MovieRepository.geInstance();

    public ProxyResponse handleRequest(Map<String, Object> request, Context context) {
        LambdaLogger logger= context.getLogger();
        logger.log("request: " +  new Gson().toJson(request));
        logger.log("context: " +  new Gson().toJson(context));

        String resource = (String) request.get("resource");
        if ("/v1/movies".equals(resource)) {
            return handleMovieRequest(request, context);
        }
        if ("/v1/info".equals(resource)) {
            return handleInfoRequest(request, context);
        }
        return new ProxyResponse(404, null, null);
    }

    private ProxyResponse handleMovieRequest(Map<String, Object> request, Context context) {
        String movieId = getMovieId(request);
        String httpMethod = (String) request.get(HTTP_METHOD);
        String body = (String) request.get(BODY);

        if (movieId == null) {
            if (GET.equals(httpMethod)) {
                return getMovies();
            } else if (POST.equals(httpMethod)) {
                return createMovie(gson.fromJson(body, Movie.class));
            }
            return methodNotAllowed();
        } else {
            if (GET.equals(httpMethod)) {
                return getMovie(movieId);
            } else if (PUT.equals(httpMethod)) {
                return updateMovie(movieId, gson.fromJson(body, Movie.class));
            } else if (DELETE.equals(httpMethod)) {
                return deleteMovie(movieId);
            }
            return methodNotAllowed();
        }
    }

    private ProxyResponse getMovies() {
        return new ProxyResponse(200, null, gson.toJson(movieRepository.list()));
    }

    private ProxyResponse createMovie(Movie movie) {
        movieRepository.saveOrUpdate(movie);
        return new ProxyResponse(200, null, gson.toJson(movie));
    }

    private ProxyResponse getMovie(String resourceId) {
        return new ProxyResponse(200, null, gson.toJson(movieRepository.load(resourceId)));
    }

    private ProxyResponse updateMovie(String resourceId, Movie movie) {
        movie.setTitle(resourceId);
        movieRepository.saveOrUpdate(movie);
        return new ProxyResponse(200, null, gson.toJson(movie));
    }

    private ProxyResponse deleteMovie(String resourceId) {
        movieRepository.delete(new Movie(resourceId, null, 0));
        return new ProxyResponse(204, null, null);
    }

    private ProxyResponse methodNotAllowed() {
        return new ProxyResponse(405, null, "{\"error\":\"method not allowed\"}");
    }

    private String getMovieId(Map<String, Object> request) {
        Map<String, String> pathParameters = (Map<String, String>) request.get(PATH_PARAMETERS);
        if (pathParameters != null) {
            return pathParameters.get(MOVIE_ID_PATH_PARAMETER);
        }
        return null;
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
