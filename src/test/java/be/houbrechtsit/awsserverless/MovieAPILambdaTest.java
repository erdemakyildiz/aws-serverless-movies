package be.houbrechtsit.awsserverless;

import com.google.gson.Gson;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * @author Ivo Houbrechts
 */
public class MovieAPILambdaTest {
    private static LocalDynamoUtils localDynamoUtils;
    private static MovieRepository movieRepository = MovieRepository.geInstance();

    private MovieAPILambda lambda = new MovieAPILambda();

    private Movie shawshank = new Movie("The Shawshank Redemption", "Frank Darabont", 1994);
    private Movie godfather = new Movie("The Godfather", "Francis Ford Coppola", 1972);

//    @BeforeClass
    public static void startLocalDynamo() {
        localDynamoUtils = LocalDynamoUtils.getInstance();
        localDynamoUtils.startDynamo();
        localDynamoUtils.createTable(Movie.class);
    }

//    @After
    public void tearDown(){
        localDynamoUtils.clearTable(Movie.class);
    }

//    @Test
    public void listMovies() {
        movieRepository.saveOrUpdate(shawshank);
        movieRepository.saveOrUpdate(godfather);

        Map<String, Object> request = new HashMap<>();
        request.put(HTTP_METHOD, GET);

        ProxyResponse proxyResponse = lambda.handleRequest(request, null);
        assertEquals(200, proxyResponse.getStatusCode());
        assertEquals(2, new Gson().fromJson(proxyResponse.getBody(), Movie[].class).length);
    }

//    @Test
    public void getMovie() {
        movieRepository.saveOrUpdate(shawshank);
        movieRepository.saveOrUpdate(godfather);

        Map<String, Object> request = new HashMap<>();
        request.put(HTTP_METHOD, GET);
        request.put(PATH_PARAMETERS, Collections.singletonMap(MOVIE_ID_PATH_PARAMETER, godfather.getTitle()));

        ProxyResponse proxyResponse = lambda.handleRequest(request, null);
        assertEquals(200, proxyResponse.getStatusCode());
        assertEquals(godfather, new Gson().fromJson(proxyResponse.getBody(), Movie.class));

        request.put(PATH_PARAMETERS, Collections.singletonMap(MOVIE_ID_PATH_PARAMETER, shawshank.getTitle()));
        proxyResponse = lambda.handleRequest(request, null);
        assertEquals(200, proxyResponse.getStatusCode());
        assertEquals(shawshank, new Gson().fromJson(proxyResponse.getBody(), Movie.class));
    }
}