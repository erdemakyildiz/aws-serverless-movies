resource "aws_dynamodb_table" "test_dynamodb" {
  name           	= "${var.resource_prefix}-${var.table_name}"
  read_capacity  	= "${var.read_capacity}"
  write_capacity 	= "${var.write_capacity}"
  hash_key       	= "${var.hash_key}"
  range_key			= "${var.range_key}"

  attribute {
	name = "${var.hash_key}"
	type = "${var.hash_key_type}"
  }

  attribute {
	name = "${var.range_key}"
	type = "${var.range_key_type}"
  }

  stream_enabled = true
  stream_view_type = "NEW_IMAGE"

  ttl {
	attribute_name = "expirationDate"
	enabled = "${var.table_ttl}"
  }

  tags = "${merge(
	${var.tags},
	map(
	  "AppName", "${var.namespace}-${var.app_name}",
	  "Environment", "${var.environment}",
	  "Name", "${var.namespace}-${var.app_name}${length(var.environment) != 0 ? "-" : ""}${var.environment}-dynamo"
	))}"
}

output "dynamodb-stream-arn" {
  value = "${aws_dynamodb_table.test_dynamodb.stream_arn}"
}
