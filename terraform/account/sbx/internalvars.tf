//Internal vars
variable "account_id"       { default = "672714205403" }

variable "aws_region" {
    value = "eu-west-1"
}

variable "route53_base" {
	value = "carrierint.sbx.thecommons.nike.com"
}

//Auto collected vars based on previous input
data "aws_route53_zone" namespace_zone {
	name	= "${var.route53_base}."	
}

data "aws_vpc" namespace_vpc {
	filter {
		name	= "name"
		values	= [ "aws-*-commons*-${var.aws_region}" ]
	}
}

// if no AMI is provided, fall back to the latest security-blessed RHEL AMI
data "aws_ami" "rhel7" {
  most_recent = true

  filter {
      name   = "name"
      values = ["CIS - EL7 Hardened *"]
    }

  filter {
      name   = "virtualization-type"
      values = ["hvm"]
    }

    owners = ["334696401286"]
}

data "aws_ami" "docker" {
  most_recent = true

  filter {
      name   = "name"
      values = ["vcs docker base*"]
    }

  filter {
      name   = "virtualization-type"
      values = ["hvm"]
    }

    owners = ["${var.account_id}"]
}