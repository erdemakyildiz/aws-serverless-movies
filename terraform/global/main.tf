module "aws_api_gateway" {
  source            =   "../../modules/api-gateway"

  app_name          =   "${var.app_name}"
  environment       =   "${var.environment}"
  region            =   "${var.region}"

  lamda_arn         =   "${module.aws_lambdas.test_lamdbda_arn}"

  resource_prefix   =   "${var.namespace}-${var.app_name}-${var.environment}"
                        //carrierint-carrier-pickup-notification-systemtesting
}

module "aws_lambdas" {
  source            =   "../../modules/lambdas"

  app_name          =   "${var.app_name}"
  environment       =   "${var.environment}"
  region            =   "${var.region}"
  namespace         =   "${var.namespace}"
  bucket            =   "${var.bucket}"
  build_number      =   "${var.build_number}"
  build_version     =   "${var.build_version}"
  subnet_id         =   "${var.subnet_id}"
  secgroup_id       =   "${var.secgroup_id}"

  resource_prefix   =   "${var.namespace}-${var.app_name}-${var.environment}"

  tags              =   "${var.tags}"
}

module "aws_dynamodb" {
  source            =   "../../modules/dynamodb"

  app_name          =   "${var.app_name}"
  environment       =   "${var.environment}"
  namespace         =   "${var.namespace}"

  resource_prefix   =   "${var.namespace}-${var.app_name}-${var.environment}"

  tags              =   "${var.tags}"
}