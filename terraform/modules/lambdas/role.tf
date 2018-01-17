data "aws_iam_policy_document" "lambda-assume-role-policy" {
  statement {
	actions = ["sts:AssumeRole"]

	principals {
	  type        = "Service"
	  identifiers = ["lambda.amazonaws.com"]
	}
  }
}

resource "aws_iam_role" "lambda_role" {
  name = "${var.namespace}-${var.app_name}-lambda${length(var.environment) != 0 ? "-" : ""}${var.environment}"
  assume_role_policy = "${data.aws_iam_policy_document.lambda-assume-role-policy.json}"
}

resource "aws_iam_role_policy_attachment" "network-and-logs" {
  role      =  "${aws_iam_role.lambda_role.name}"
  policy_arn =  "arn:aws:iam::aws:policy/service-role/AWSLambdaVPCAccessExecutionRole"
}