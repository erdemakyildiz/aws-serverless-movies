package be.houbrechtsit.goserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Map;

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
        log.debug("request: {}", request);
        log.debug("context: {}", context);

        String resourceId = getResourceId(request);
        String httpMethod = (String) request.get(HTTP_METHOD);
        String body = (String) request.get(BODY);

        if (resourceId == null) {
            if (GET.equals(httpMethod)) {
                return getMovies();
            } else if (POST.equals(httpMethod)) {
                return createMovie(gson.fromJson(body, Movie.class));
            }
            return methodNotAllowed();
        } else {
            if (GET.equals(httpMethod)) {
                return getMovie(resourceId);
            } else if (PUT.equals(httpMethod)) {
                return updateMovie(resourceId, gson.fromJson(body, Movie.class));
            } else if (DELETE.equals(httpMethod)) {
                return deleteMovie(resourceId);
            }
        }
        return new ProxyResponse()
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

    private String getResourceId(Map<String, Object> request) {
        Map<String, String> pathParameters = (Map<String, String>) request.get(PATH_PARAMETERS);
        if (pathParameters != null) {
            return pathParameters.get(MOVIE_ID_PATH_PARAMETER);
        }
        return null;
    }
}
