package be.houbrechtsit.goserverless;

import com.amazonaws.services.lambda.runtime.Context;
import com.google.gson.Gson;

public class MovieAPILambda {

    private MovieRepository movieRepository = MovieRepository.geInstance();

    protected String handleApiGatewayRequest(Object request, Context context) {
        return new Gson().toJson(new Movie("Starwars", "George Lucas", 1978));
    }
}