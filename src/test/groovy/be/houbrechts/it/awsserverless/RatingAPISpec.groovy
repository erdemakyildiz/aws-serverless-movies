package be.houbrechts.it.awsserverless

import com.google.gson.Gson
import groovy.json.JsonSlurper
import spock.lang.Shared
import spock.lang.Specification

import static be.houbrechts.it.awsserverless.APILambda.*

/**
 * @author Ivo Houbrechts
 */
class RatingAPISpec extends Specification {
    @Shared
    def localDynamoUtils = LocalDynamoUtils.instance
    @Shared
    def movieRepository = MovieRepository.instance
    @Shared
    def ratingRepository = RatingRepository.instance

    def shawshank = new Movie(id: 1, title: "The Shawshank Redemption", director: "Frank Darabont", year: 1994)
    def godfather = new Movie(id: 2, title: "The Godfather", director: "Francis Ford Coppola", year: 1972)

    def homer =  new Rating(null, 'homer@simpson', 8)
    def bart =  new Rating(null, 'bart@simpson', 7)
    def lisa =  new Rating(null, 'lisa@simpson', 10)

    def lambda = new APILambda()

    void setupSpec() {
        localDynamoUtils.startDynamo()
        localDynamoUtils.createTable(Movie)
        localDynamoUtils.createTable(Rating)
        shawshank = movieRepository.saveOrUpdate(shawshank)
        godfather = movieRepository.saveOrUpdate(godfather)
    }

    void cleanup() {
        localDynamoUtils.clearTable(Rating)
        localDynamoUtils.clearTable(Movie)
    }

    def "test list operation"() {
        given:
        ratingRepository.saveOrUpdate(bart)
        ratingRepository.saveOrUpdate(godfather)
        def request = [httpMethod: GET, resource: '/v1/ratings']
        def (statusCode, ratings) = executeRequest(request)

        expect:
        statusCode == 200
        ratings.toSorted { a, b -> a.id <=> b.id } == [shawshank, godfather]
    }

    def "test get operation"() {
        given:
        ratingRepository.saveOrUpdate(shawshank)
        ratingRepository.saveOrUpdate(godfather)
        def request = [httpMethod    : GET,
                       resource      : '/v1/ratings/{ratingId}',
                       pathParameters: [ratingId: "1"]]
        def (statusCode, ratings) = executeRequest(request)

        expect:
        statusCode == 200
        ratings == [shawshank]
    }

    def "test create operation"() {
        given:
        def newRating
        def request = [httpMethod: POST,
                       resource  : '/v1/ratings',
                       body      : new Gson().toJson(shawshank)]

        when:
        def (statusCode, ratings) = executeRequest(request)
        newRating = ratings[0]

        then:
        statusCode == 201
        newRating == new Rating(newRating.id, shawshank.title, shawshank.director, shawshank.year)
        UUID.fromString(newRating.id)

        when:
        request = [httpMethod    : GET,
                   resource      : '/v1/ratings/{ratingId}',
                   pathParameters: [ratingId: newRating.id]]
        (statusCode, ratings) = executeRequest(request)

        then:
        statusCode == 200
        ratings == [newRating]
    }

    def "test update operation"() {
        given:
        def updatedRating
        ratingRepository.saveOrUpdate(shawshank)
        def request = [httpMethod    : PUT,
                       resource      : '/v1/ratings/{ratingId}',
                       body          : new Gson().toJson(new Rating(title: 'newTitle', director: 'newDirector', year: 2000)),
                       pathParameters: [ratingId: shawshank.id]]

        when:
        def (statusCode, ratings) = executeRequest(request)
        updatedRating = ratings[0]

        then:
        statusCode == 200
        updatedRating == new Rating(id: shawshank.id, title: 'newTitle', director: 'newDirector', year: 2000)

        when:
        request = [httpMethod    : GET,
                   resource      : '/v1/ratings/{ratingId}',
                   pathParameters: [ratingId: shawshank.id]]
        (statusCode, ratings) = executeRequest(request)

        then:
        statusCode == 200
        ratings == [updatedRating]
    }

    def "test delete operation"() {
        given:
        ratingRepository.saveOrUpdate(shawshank)
        def request = [httpMethod    : DELETE,
                       resource      : '/v1/ratings/{ratingId}',
                       pathParameters: [ratingId: shawshank.id]]

        when:
        def (statusCode, _) = executeRequest(request)

        then:
        statusCode == 204

        when:
        request = [httpMethod    : GET,
                   resource      : '/v1/ratings/{ratingId}',
                   pathParameters: [ratingId: shawshank.id]]
        (statusCode, _) = executeRequest(request)

        then:
        statusCode == 404
    }

    def "test rating not found"() {
        given:
        def request = [httpMethod    : httpMethod,
                       resource      : '/v1/ratings/{ratingId}',
                       pathParameters: [ratingId: 'no existing']]
        def (statusCode, _) = executeRequest(request)

        expect:
        statusCode == 404

        where:
        httpMethod << [GET, DELETE, PUT]
    }

    private def executeRequest(Map request) {
        def proxyResponse = lambda.handleRequest(request, null)
        def body = proxyResponse.body
        def ratings = body ? [new JsonSlurper().parseText(body)].flatten() : null

        [proxyResponse.statusCode, ratings.collect { new Rating(it) }]
    }
}
