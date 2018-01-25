variable "region" {
  default = "eu-west-1"
}

provider "aws" {
  region = "${var.region}"
}

resource "aws_api_gateway_rest_api" "movie_api" {
  name = "movie_api"
  description = "Sample movies REST API"
  body = "${data.template_file.swagger.rendered}"
}

data "template_file" "swagger" {
  template = "${file("../swagger.yaml")}"

  vars {
    invocation_uri = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.movie_api_lambda.arn}/invocations"
  }
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
  function_name = "${aws_lambda_function.movie_api_lambda.function_name}"
  principal = "apigateway.amazonaws.com"
}

resource "aws_api_gateway_deployment" "apig_deployment" {
  rest_api_id = "${aws_api_gateway_rest_api.movie_api.id}"
  stage_name = "test"
  stage_description = "test"
}

output "display_invoke_url" {
  value = "Invoke URL: ${aws_api_gateway_deployment.apig_deployment.invoke_url}"
}

output "invocation_uri" {
  value = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${aws_lambda_function.movie_api_lambda.arn}/invocations"
}

