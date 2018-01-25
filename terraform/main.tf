variable "region" {
  default = "eu-west-1"
}

provider "aws" {
  region = "${var.region}"
}

resource "aws_api_gateway_rest_api" "movie_api" {
  name = "movie_api"
  description = "Sample movies REST API"
}

resource "aws_api_gateway_resource" "movies_resource" {
  rest_api_id = "${aws_api_gateway_rest_api.movie_api.id}"
  parent_id = "${aws_api_gateway_rest_api.movie_api.root_resource_id}"
  path_part = "{proxy+}"
}

resource "aws_api_gateway_method" "any_method" {
  rest_api_id = "${aws_api_gateway_rest_api.movie_api.id}"
  resource_id = "${aws_api_gateway_resource.movies_resource.id}"
  http_method = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "api_method_integration" {
  rest_api_id = "${aws_api_gateway_rest_api.movie_api.id}"
  resource_id = "${aws_api_gateway_resource.movies_resource.id}"
  http_method = "${aws_api_gateway_method.any_method.http_method}"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.movie_api_lambda.arn}/invocations"
  integration_http_method = "POST"
}

resource "aws_iam_role" "lambda_exec_role" {
  name = "lambda_exec_role"
  assume_role_policy = <<EOF
{
	"Version": "2012-10-17",
	"Statement": [
		{
			"Action": "sts:AssumeRole",
			"Principal": {
				"Service": "lambda.amazonaws.com"
			},
			"Effect": "Allow",
			"Sid": ""
		}
	]
}
EOF
}

resource "aws_lambda_function" "movie_api_lambda" {
  source_code_hash = "${base64sha256(file("../build/distributions/go-serverless-example-0.1.zip"))}"
  filename = "../build/distributions/go-serverless-example-0.1.zip"

  function_name = "movie_api_lambda"
  role = "${aws_iam_role.lambda_exec_role.arn}"
  handler = "be.houbrechtsit.goserverless.MovieAPILambda::handleRequest"
  runtime = "java8"
}

resource "aws_lambda_permission" "api_lambda_permissions" {
  statement_id = "AllowExecutionFromAPIGateway"
  action = "lambda:InvokeFunction"
  function_name = "movie_api_lambda"
  principal = "apigateway.amazonaws.com"
}

resource "aws_api_gateway_deployment" "apig_deployment" {
  rest_api_id = "${aws_api_gateway_rest_api.movie_api.id}"
  stage_name = "test"
  stage_description = "test"
  depends_on = ["aws_api_gateway_integration.api_method_integration"]
}

output "display_invoke_url" {
  value = "Invoke URL: ${aws_api_gateway_deployment.apig_deployment.invoke_url}"
}