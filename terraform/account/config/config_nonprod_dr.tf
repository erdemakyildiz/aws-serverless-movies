provider "aws" {
  region = "eu-central-1"
}

terraform {
  backend "s3" {
    bucket     = "carrierint-non-eu-west-1"
    key        = "terraform/carrierint/accountdr/terraform.tfstate"
    region     = "eu-west-1"
    encrypt    = "true"
    lock_table = "carrierint-non-eu-west-1"
  }
}