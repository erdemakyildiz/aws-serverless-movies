resource "aws_dynamodb_table" "movies_table" {
  name = "movies"
  read_capacity = "1"
  write_capacity = "1"
  hash_key = "id"

  attribute {
    name = "id"
    type = "S"
  }

//  stream_enabled = true
//  stream_view_type = "NEW_IMAGE"
}

//output "dynamodb-stream-arn" {
//  value = "${aws_dynamodb_table.movies_table.stream_arn}"
//}
