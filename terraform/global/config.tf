provider "aws" {
  region = "${var.region}"
}

terraform {
  backend "s3" {
    key        = "carrier-pickup-notification/terraform.tfstate"
    region     = "eu-west-1"
    encrypt    = "true"
  }
}