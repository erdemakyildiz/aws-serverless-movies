package be.houbrechts.it.awsserverless

import com.google.gson.Gson
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

import static be.houbrechts.it.awsserverless.MovieAPILambda.*

/**
 * @author Ivo Houbrechts
 */
class MovieAPILambdaSpec extends Specification {
    @Shared
    def localDynamoUtils = LocalDynamoUtils.instance
    @Shared
    def movieRepository = MovieRepository.instance

    def shawshank = new Movie(id: 1, title: "The Shawshank Redemption", director: "Frank Darabont", year: 1994)
    def godfather = new Movie(id: 2, title: "The Godfather", director: "Francis Ford Coppola", year: 1972)


    def lambda = new MovieAPILambda()

    void setupSpec() {
        localDynamoUtils.startDynamo()
        localDynamoUtils.createTable(Movie)
    }

    void cleanup() {
        localDynamoUtils.clearTable(Movie)
    }

    def "test list operation"() {
        given:
        movieRepository.saveOrUpdate(shawshank)
        movieRepository.saveOrUpdate(godfather)
        def request = [httpMethod: GET, resource: '/v1/movies']
        def (statusCode, movies) = executeRequest(request)

        expect:
        statusCode == 200
        movies.toSorted { a, b -> a.id <=> b.id } == [shawshank, godfather]
    }

    def "test get operation"() {
        given:
        movieRepository.saveOrUpdate(shawshank)
        movieRepository.saveOrUpdate(godfather)
        def request = [httpMethod    : GET,
                       resource      : '/v1/movies/{movieId}',
                       pathParameters: [movieId: "1"]]
        def (statusCode, movies) = executeRequest(request)

        expect:
        statusCode == 200
        movies == [shawshank]
    }

    def "test create operation"() {
        given:
        def newMovie
        def request = [httpMethod: POST,
                       resource  : '/v1/movies',
                       body      : new Gson().toJson(shawshank)]

        when:
        def (statusCode, movies) = executeRequest(request)
        newMovie = movies[0]

        then:
        statusCode == 201
        newMovie == new Movie(newMovie.id, shawshank.title, shawshank.director, shawshank.year)
        UUID.fromString(newMovie.id)

        when:
        request = [httpMethod    : GET,
                   resource      : '/v1/movies/{movieId}',
                   pathParameters: [movieId: newMovie.id]]
        (statusCode, movies) = executeRequest(request)

        then:
        statusCode == 200
        movies == [newMovie]
    }

    def "test update operation"() {
        given:
        def updatedMovie
        movieRepository.saveOrUpdate(shawshank)
        def request = [httpMethod    : PUT,
                       resource      : '/v1/movies/{movieId}',
                       body          : new Gson().toJson(new Movie(title: 'newTitle', director: 'newDirector', year: 2000)),
                       pathParameters: [movieId: shawshank.id]]

        when:
        def (statusCode, movies) = executeRequest(request)
        updatedMovie = movies[0]

        then:
        statusCode == 200
        updatedMovie == new Movie(id: shawshank.id, title: 'newTitle', director: 'newDirector', year: 2000)

        when:
        request = [httpMethod    : GET,
                   resource      : '/v1/movies/{movieId}',
                   pathParameters: [movieId: shawshank.id]]
        (statusCode, movies) = executeRequest(request)

        then:
        statusCode == 200
        movies == [updatedMovie]
    }

    def "test delete operation"() {
        given:
        movieRepository.saveOrUpdate(shawshank)
        def request = [httpMethod    : DELETE,
                       resource      : '/v1/movies/{movieId}',
                       pathParameters: [movieId: shawshank.id]]

        when:
        def (statusCode, movies) = executeRequest(request)

        then:
        statusCode == 204

        when:
        request = [httpMethod    : GET,
                   resource      : '/v1/movies/{movieId}',
                   pathParameters: [movieId: shawshank.id]]
        (statusCode, movies) = executeRequest(request)

        then:
        statusCode == 404
    }

    def "test movie not found"() {
        given:
        def request = [httpMethod    : httpMethod,
                       resource      : '/v1/movies/{movieId}',
                       pathParameters: [movieId: 'no existing']]
        def (statusCode, _) = executeRequest(request)

        expect:
        statusCode == 404

        where:
        httpMethod << [GET, DELETE, PUT]
    }

    private def executeRequest(Map request) {
        def proxyResponse = lambda.handleRequest(request, null)
        def body = proxyResponse.body
        def movies = body ? [new JsonSlurper().parseText(body)].flatten() : null

        [proxyResponse.statusCode, movies.collect { new Movie(it) }]
    }
}
