resource "aws_api_gateway_rest_api" "gateway_api" {
  name = "${var.resource_prefix}-api"
  description = "Rest Api"
}

resource "aws_api_gateway_resource" "test_resource" {
  rest_api_id = "${aws_api_gateway_rest_api.gateway_api.id}"
  parent_id = "${aws_api_gateway_rest_api.gateway_api.root_resource_id}"
  path_part = "test"
}

resource "aws_api_gateway_method" "test_method" {
  rest_api_id = "${aws_api_gateway_rest_api.gateway_api.id}"
  resource_id = "${aws_api_gateway_resource.test_resource.id}"
  http_method = "ANY"
  authorization = "NONE"
}

resource "aws_api_gateway_integration" "gateway_api_method_integration" {
  rest_api_id = "${aws_api_gateway_rest_api.gateway_api.id}"
  resource_id = "${aws_api_gateway_resource.test_resource.id}"
  http_method = "${aws_api_gateway_method.test_method.http_method}"
  type = "AWS_PROXY"
  uri = "arn:aws:apigateway:${var.region}:lambda:path/2015-03-31/functions/${var.lamda_arn}/invocations"
  integration_http_method = "POST"
}

resource "aws_api_gateway_deployment" "gateway_deployment" {
  depends_on = [
    "aws_api_gateway_integration.gateway_api_method_integration"
  ]
  rest_api_id = "${aws_api_gateway_rest_api.gateway_api.id}"
  stage_name = "api${length(var.environment) != 0 ? "_" : ""}${var.environment}"
}