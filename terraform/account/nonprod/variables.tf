##################################################
#
#
#	SHARED variables
#
#
##################################################


variable "route53_zone" { default = "Z3S9NGSUERB4QA" }

output "rhel7id" {
	value = "${data.aws_ami.rhel7.id}"
}

output "account_id" { 
	value = "${var.account_id}"
}

output "route53_base" {
	value = "carrierint.non.thecommons.nike.com"
}


##################################################
#
#
#	PRIMARY variables
#
#
##################################################


output "aws_region" {
	value = "eu-west-1"
}

output "vpc_id" {
    value = "vpc-f718c290"
}

output "subnet_id" { 
	value = "subnet-03eb0f58,subnet-b7d375fe,subnet-46b26a21" 
}

output "dbsubnet_id" { 
	value = "subnet-66d3752f,subnet-8bf410d0,subnet-fabd659d" 
}

output "dockerid" {
	value = "${data.aws_ami.docker.id}"
}

output "route53_zone" { 
	value = "${var.route53_zone}"  //carrierint.non.thecommons.nike.com
}

output "secgroup_id" {
	value = "sg-e92cc291"
}

output "availability_zones"	{ 
	value = "eu-west-1c,eu-west-1a,eu-west-1b"
}


##################################################
#
#
#	DR variables
#
#
##################################################


output "dr_aws_region" {
	value = "eu-central-1"
}

output "dr_vpc_id" {
    value = "vpc-d5aa89bd"	//vpc aws-non-commons-eu-central-1
}

output "dr_subnet_id" { 
	value = "subnet-38c43175,subnet-e435029e,subnet-acb245c7" 
}

output "dr_dbsubnet_id" { 
	value = "subnet-e9c530a4,subnet-913502eb,subnet-1fb14674" 
}

output "dr_dockerid" {
	value = "${data.aws_ami.docker.id}"
}

output "dr_route53_zone" { 
	value = "${var.route53_zone}"  //carrierint.non.thecommons.nike.com
}

output "dr_secgroup_id" {
	value = "sg-88e204e2"
}

output "dr_availability_zones"	{ 
	value = "eu-central-1c,eu-central-1a,eu-central-1b"
}


