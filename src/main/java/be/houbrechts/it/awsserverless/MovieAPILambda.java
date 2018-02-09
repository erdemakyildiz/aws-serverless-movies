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

import static java.util.UUID.randomUUID;

public class MovieAPILambda {
    public static final String HTTP_METHOD = "httpMethod";
    public static final String BODY = "body";
    public static final String GET = "GET";
    public static final String POST = "POST";
    public static final String PUT = "PUT";
    public static final String DELETE = "DELETE";
    public static final String MOVIE_ID_PATH_PARAMETER = "movieId";
    public static final String PATH_PARAMETERS = "pathParameters";

    private static final Logger log = LogManager.getLogger(MovieAPILambda.class);
    private static final Gson gson = new Gson();

    private MovieRepository movieRepository = MovieRepository.geInstance();

    public ProxyResponse handleRequest(Map<String, Object> request, Context context) {
        log.debug("request: {}", new Gson().toJson(request));
        log.debug("context: {}", new Gson().toJson(context));

        try {
            String resource = (String) request.get("resource");
            if (resource != null && resource.startsWith("/v1/movies")) {
                return handleMovieRequest(request);
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

    private ProxyResponse handleMovieRequest(Map<String, Object> request) {
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
        movie.setId(randomUUID().toString());
        movieRepository.saveOrUpdate(movie);
        return new ProxyResponse(201, null, gson.toJson(movie));
    }

    private ProxyResponse getMovie(String movieId) {
        final Movie movie = movieRepository.load(movieId);
        return movie == null ?
                new ProxyResponse(404, null, null) :
                new ProxyResponse(200, null, gson.toJson(movie));
    }

    private ProxyResponse updateMovie(String movieId, Movie movie) {
        final Movie existing = movieRepository.load(movieId);
        if (existing == null) {
            return new ProxyResponse(404, null, null);
        }
        movie.setId(movieId);
        movieRepository.saveOrUpdate(movie);
        return new ProxyResponse(200, null, gson.toJson(movie));
    }

    private ProxyResponse deleteMovie(String movieId) {
        final Movie existing = movieRepository.load(movieId);
        if (existing == null) {
            return new ProxyResponse(404, null, null);
        }
        movieRepository.delete(existing);
        return new ProxyResponse(204, null, null);
    }

    private ProxyResponse methodNotAllowed() {
        return new ProxyResponse(405, null, "{\"error\":\"method not allowed\"}");
    }

    private String getMovieId(Map<String, Object> request) {
        @SuppressWarnings("unchecked")
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
