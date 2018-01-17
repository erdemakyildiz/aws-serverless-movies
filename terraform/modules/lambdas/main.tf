output "test_lamdbda_arn" {
  value = "${aws_lambda_function.test_function.arn}"
}

resource "aws_s3_bucket_object" "test_bucket" {
  key                    = "jars/${var.resource_prefix}-lambda"
  bucket                 = "${var.bucket}"
  source                 = "../../../build/libs/${var.app_name}-${var.build_version}.${var.build_number}.jar"
  server_side_encryption = "AES256"
}

resource "aws_lambda_function" "test_function" {
  s3_bucket = "${aws_s3_bucket_object.test_bucket.bucket}"
  s3_key = "${aws_s3_bucket_object.test_bucket.key}"
  s3_object_version = "${aws_s3_bucket_object.test_bucket.version_id}"

  function_name = "${var.resource_prefix}-${var.api_lambda_name}"
  role = "${aws_iam_role.lambda_role.arn}"
  handler = "${var.api_lambda_handler}"
  runtime = "java8"
  publish = true
  memory_size = "1024"
  timeout = "20"

  vpc_config {
    subnet_ids = ["${split(",", var.subnet_id)}"]
    security_group_ids = ["${var.secgroup_id}"]
  }
  environment {
    variables = {
      TABLE_REGION = "${var.region}"
      TABLE_NAME_PREFIX = "${var.resource_prefix}-"
      requestType = "json"

    }
  }
  tags = "${merge(
	${var.tags},
	map(
	  "AppName", "${var.namespace}-${var.app_name}",
	  "Environment", "${var.environment}",
	  "Name", "${var.namespace}-${var.app_name}${length(var.environment) != 0 ? "-" : ""}${var.environment}"
	))}"
}