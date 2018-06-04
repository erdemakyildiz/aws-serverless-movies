package be.houbrechts.it.awsserverless;

import java.util.Map;

import static be.houbrechts.it.awsserverless.APILambda.*;
import static java.util.UUID.randomUUID;

/**
 * @author Ivo Houbrechts
 */
public class MovieAPI {
    static final String MOVIE_ID_PATH_PARAMETER = "movieId";

    public static ProxyResponse handleMovieRequest(String httpMethod, String body, Map<String, String> pathParameters) {
        String movieId = getMovieId(pathParameters);

        if (movieId == null) {
            if (GET.equals(httpMethod)) {
                return getMovies();
            }
            if (POST.equals(httpMethod)) {
                return createMovie(gson.fromJson(body, Movie.class));
            }
            return methodNotAllowed();
        }
        if (GET.equals(httpMethod)) {
            return getMovie(movieId);
        }
        if (PUT.equals(httpMethod)) {
            return updateMovie(movieId, gson.fromJson(body, Movie.class));
        }
        if (DELETE.equals(httpMethod)) {
            return deleteMovie(movieId);
        }
        return methodNotAllowed();

    }

    private static ProxyResponse getMovies() {
        return new ProxyResponse(200, null, gson.toJson(MovieRepository.getInstance().list()));
    }

    private static ProxyResponse createMovie(Movie movie) {
        movie.setId(randomUUID().toString());
        MovieRepository.getInstance().saveOrUpdate(movie);
        return new ProxyResponse(201, null, gson.toJson(movie));
    }

    private static ProxyResponse getMovie(String movieId) {
        final Movie movie = MovieRepository.getInstance().load(movieId);
        return movie == null ?
                new ProxyResponse(404, null, null) :
                new ProxyResponse(200, null, gson.toJson(movie));
    }

    private static ProxyResponse updateMovie(String movieId, Movie movie) {
        final Movie existing = MovieRepository.getInstance().load(movieId);
        if (existing == null) {
            return new ProxyResponse(404, null, null);
        }
        movie.setId(movieId);
        MovieRepository.getInstance().saveOrUpdate(movie);
        return new ProxyResponse(200, null, gson.toJson(movie));
    }

    private static ProxyResponse deleteMovie(String movieId) {
        final Movie existing = MovieRepository.getInstance().load(movieId);
        if (existing == null) {
            return new ProxyResponse(404, null, null);
        }
        MovieRepository.getInstance().delete(existing);
        return new ProxyResponse(204, null, null);
    }

    private static String getMovieId(Map<String, String> pathParameters) {
        if (pathParameters != null) {
            return pathParameters.get(MOVIE_ID_PATH_PARAMETER);
        }
        return null;
    }

}
