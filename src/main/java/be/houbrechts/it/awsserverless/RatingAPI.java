package be.houbrechts.it.awsserverless;

import java.util.Map;

import static be.houbrechts.it.awsserverless.APILambda.*;
import static be.houbrechts.it.awsserverless.MovieAPI.MOVIE_ID_PATH_PARAMETER;

/**
 * @author Ivo Houbrechts
 */
public class RatingAPI {
    private static final String EMAIL_PATH_PARAMETER = "email";

    public static ProxyResponse handleRatingRequest(String httpMethod, String body, Map<String, String> pathParameters) {
        String movieId = getMovieId(pathParameters);

        if (movieId == null) {
            if (GET.equals(httpMethod)) {
                return getRatings();
            }
            return methodNotAllowed();
        }

        String email = getEmail(pathParameters);
        if (email == null) {
            if (GET.equals(httpMethod)) {
                return getRatings(movieId);
            }
            if (POST.equals(httpMethod)) {
                return createRating(movieId, gson.fromJson(body, Rating.class));
            }
        }
        if (GET.equals(httpMethod)) {
            return getRating(movieId, email);
        }
        if (PUT.equals(httpMethod)) {
            return updateRating(movieId, email, gson.fromJson(body, Rating.class));
        }
        if (DELETE.equals(httpMethod)) {
            return deleteRating(movieId, email);
        }
        return methodNotAllowed();

    }

    private static ProxyResponse getRatings() {
        return new ProxyResponse(200, null, gson.toJson(RatingRepository.getInstance().list()));
    }

    private static ProxyResponse getRatings(String movieId) {
        return new ProxyResponse(200, null, gson.toJson(RatingRepository.getInstance().listByMovieId(movieId)));
    }

    private static ProxyResponse createRating(String movieId, Rating rating) {
        rating.setMovieId(movieId);
        RatingRepository.getInstance().saveOrUpdate(rating);
        return new ProxyResponse(201, null, gson.toJson(rating));
    }

    private static ProxyResponse getRating(String movieId, String email) {
        final Rating rating = RatingRepository.getInstance().load(movieId, email);
        return rating == null ?
                new ProxyResponse(404, null, null) :
                new ProxyResponse(200, null, gson.toJson(rating));
    }

    private static ProxyResponse updateRating(String movieId, String email, Rating rating) {
        final Rating existing = RatingRepository.getInstance().load(movieId, email);
        if (existing == null) {
            return new ProxyResponse(404, null, null);
        }
        rating.setMovieId(movieId);
        rating.setEmail(email);
        RatingRepository.getInstance().saveOrUpdate(rating);
        return new ProxyResponse(200, null, gson.toJson(rating));
    }

    private static ProxyResponse deleteRating(String movieId, String email) {
        final Rating existing = RatingRepository.getInstance().load(movieId, email);
        if (existing == null) {
            return new ProxyResponse(404, null, null);
        }
        RatingRepository.getInstance().delete(existing);
        return new ProxyResponse(204, null, null);
    }

    private static String getMovieId(Map<String, String> pathParameters) {
        if (pathParameters != null) {
            return pathParameters.get(MOVIE_ID_PATH_PARAMETER);
        }
        return null;
    }

    private static String getEmail(Map<String, String> pathParameters) {
        if (pathParameters != null) {
            return pathParameters.get(EMAIL_PATH_PARAMETER);
        }
        return null;
    }
}
