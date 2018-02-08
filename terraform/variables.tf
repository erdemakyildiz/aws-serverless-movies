variable "region" {
  default = "eu-west-1"
}

locals {
  application_version = "${trimspace(file("../version"))}"
}

provider "aws" {
  region = "${var.region}"
}
