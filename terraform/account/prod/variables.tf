##################################################
#
#
#	SHARED variables
#
#
##################################################


variable "route53_zone" { default = "ZVCS72MRVCU2O" }

output "rhel7id" {
	value = "${data.aws_ami.rhel7.id}"
}

output "account_id" { 
	value = "${var.account_id}"
}

output "route53_base" {
	value = "carrierint.prd.thecommons.nike.com"
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
    value = "vpc-d64afeb1"
}

output "subnet_id" { 
	value = "subnet-f040b5ab,subnet-a25ee8eb,subnet-191fb67e" 
}

output "dbsubnet_id" { 
	value = "subnet-9e1db4f9,subnet-d841b483,subnet-f15bedb8" 
}

output "dockerid" {
	value = "${data.aws_ami.docker.id}"
}

output "route53_zone" { 
	value = "${var.route53_zone}"  //carrierint.prd.thecommons.nike.com
}

output "secgroup_id" {
	value = "sg-93e039eb"
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
    value = "vpc-25a98a4d"	//vpc aws-non-commons-eu-central-1
}

output "dr_subnet_id" { 
	value = "subnet-3a330440,subnet-0cb34467,subnet-d0da2f9d" 
}

output "dr_dbsubnet_id" { 
	value = "subnet-83c431ce,subnet-73b14618,subnet-3534034f" 
}

output "dr_dockerid" {
	value = "${data.aws_ami.docker.id}"
}

output "dr_route53_zone" { 
	value = "${var.route53_zone}"  //carrierint.non.thecommons.nike.com
}

output "dr_secgroup_id" {
	value = "sg-1ce20476"
}

output "dr_availability_zones"	{ 
	value = "eu-central-1c,eu-central-1a,eu-central-1b"
}


