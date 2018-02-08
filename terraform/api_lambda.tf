resource "aws_lambda_function" "movie_api_lambda" {
  source_code_hash = "${base64sha256(file("../build/distributions/aws-serverless-movies-${local.application_version}.zip"))}"
  filename = "../build/distributions/aws-serverless-movies-${local.application_version}.zip"

  function_name = "movie_api_lambda"
  role = "${aws_iam_role.lambda_exec_role.arn}"
  handler = "be.houbrechts.it.awsserverless.MovieAPILambda::handleRequest"
  runtime = "java8"

  environment {
    variables = {
      AWS_TABLE_REGION = "${var.region}"
    }
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

resource "aws_iam_policy_attachment" "allow_dynamodb_from_lambda" {
  name = "policy_atchmt"
  roles = [
    "${aws_iam_role.lambda_exec_role.name}"]
  policy_arn = "arn:aws:iam::aws:policy/service-role/AWSLambdaDynamoDBExecutionRole"
}
