//Internal vars
variable "account_id"       { default = "890831765352" }

variable "ami_owner_id"		{ default = "069272765570" }

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

    owners = ["${var.ami_owner_id}"]
}