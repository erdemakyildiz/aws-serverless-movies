resource "aws_lambda_function" "movie_api_lambda" {
  source_code_hash = "${base64sha256(file("../build/distributions/aws-serverless-movies-${local.application_version}.zip"))}"
  filename = "../build/distributions/aws-serverless-movies-${local.application_version}.zip"

  function_name = "movie_api_lambda"
  role = "${aws_iam_role.movie_api_lambda_role.arn}"
  handler = "be.houbrechts.it.awsserverless.MovieAPILambda::handleRequest"
  runtime = "java8"
  memory_size = 512
  timeout = 20
}

resource "aws_iam_role" "movie_api_lambda_role" {
  name = "movie_api_lambda_role"
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

resource "aws_iam_role_policy" "movie_api_lambda_policy" {
  name = "movie_api_lambda_policy"
  role = "${aws_iam_role.movie_api_lambda_role.id}"
  policy = <<EOF
{
    "Version": "2008-10-17",
    "Statement": [
        {
            "Action": "dynamodb:*",
            "Effect": "Allow",
            "Resource": "${aws_dynamodb_table.movies_table.arn}"
        },
        {
            "Action": "logs:*",
            "Effect": "Allow",
            "Resource": "*"
        }
    ]
}
EOF
}
