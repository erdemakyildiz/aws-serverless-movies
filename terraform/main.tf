variable "region" {
  default = "eu-west-1"
}

provider "aws" {
  region = "${var.region}"
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
  role = "arn:aws:iam::069272765570:role/carrierint-lambda-systemtesting"
  handler = "be.houbrechtsit.goserverless.MovieAPILambda:handleApiGatewayRequest"
  runtime = "java8"
  publish = true
  memory_size = "1024"
  timeout = "20"

  environment {
    variables = {
      TABLE_REGION = "${var.region}"
      TABLE_NAME_PREFIX = "test-"
      requestType = "json"
    }
  }
}
resource "aws_lambda_permission" "api_lambda_permissions" {
  statement_id = "AllowExecutionFromAPIGateway"
  action = "lambda:InvokeFunction"
  function_name = "movie_api_lambda"
  principal = "apigateway.amazonaws.com"
  source_arn = "arn:aws:execute-api:${var.region}:${data.aws_caller_identity.current.account_id}:${var.gateway_api_id}/${var.gateway_stage_name}/*/*"
}