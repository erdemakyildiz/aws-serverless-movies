variable "region" {
  default = "eu-west-1"
}

locals {
  application_version = "${trimspace(file("../version"))}"
}

provider "aws" {
  region = "${var.region}"
}

terraform {
  backend "s3" {
    bucket = "houbie-terraform"
    key    = "aws-serverless-movies"
    region = "eu-west-1"
  }
}
