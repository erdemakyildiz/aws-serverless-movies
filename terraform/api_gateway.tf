data "template_file" "swagger" {
  template = "${file("../swagger.yaml")}"

  vars {
    api_integration = "{ uri: 'arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.movie_api_lambda.arn}/invocations', passthroughBehavior: when_no_match, httpMethod: POST, type: aws_proxy }"
  }
}

resource "aws_api_gateway_rest_api" "movie_api" {
  name = "movie_api"
  description = "Sample movies REST API"
  body = "${data.template_file.swagger.rendered}"
}

resource "aws_lambda_permission" "api_movie_lambda_permissions" {
  statement_id = "AllowExecutionFromAPIGateway"
  action = "lambda:InvokeFunction"
  function_name = "${aws_lambda_function.movie_api_lambda.function_name}"
  principal = "apigateway.amazonaws.com"
}

resource "aws_api_gateway_deployment" "apig_deployment" {
  rest_api_id = "${aws_api_gateway_rest_api.movie_api.id}"
  stage_name = "test"
  stage_description = "test"
}

output "api_invoke_url" {
  value = "${aws_api_gateway_deployment.apig_deployment.invoke_url}"
}
