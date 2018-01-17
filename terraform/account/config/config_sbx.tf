provider "aws" {
  region = "eu-west-1"
}

terraform {
  backend "s3" {
    bucket     = "carrierint-sbx-eu-west-1"
    key        = "terraform/carrierint/account/terraform.tfstate"
    region     = "eu-west-1"
    encrypt    = "true"
    lock_table = "carrierint-sbx-eu-west-1"
  }
}