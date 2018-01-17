provider "aws" {
  region = "eu-west-1"
}

terraform {
  backend "s3" {
//    bucket     = "entsvcs-terraform-aws-prd-commons1-eu-west-1"
	bucket	   = "carrierint-prd-eu-west-1"
    key        = "terraform/carrierint/account/terraform.tfstate"
    region     = "eu-west-1"
    encrypt    = "true"
    lock_table = "entsvcs-terraform-aws-prd-commons1-eu-west-1"
  }
}