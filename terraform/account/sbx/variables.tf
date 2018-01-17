##################################################
#
#
#	SHARED variables
#
#
##################################################

output "rhel7id" {
	value = "${data.aws_ami.rhel7.id}"
}

output "account_id" { 
	value = "${var.account_id}"
}


##################################################
#
#
#	PRIMARY variables
#
#
##################################################

output "aws_region" {
    value = "${var.aws_region}"
}

output "vpc_id" {
    value = "${data.aws_vpc.namespace_vpc.id}"
}

output "subnet_id" { 
	value = "subnet-21e70d7a,subnet-78bf111f,subnet-b604b0ff" 
}

output "dbsubnet_id" { 
	value = "subnet-e304b0aa,subnet-55bc1232,subnet-b5e40eee" 
}

output "dockerid" {
	value = "${data.aws_ami.docker.id}"
}

output "secgroup_id" {
	value = "sg-1baf7263"
}

output "route53_zone" { 
	value = "${data.aws_route53_zone.namespace_zone.zone_id}" 
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
    value = "vpc-4ca68524"
}

output "dr_subnet_id" { 
	value = "subnet-ebd92ca6,subnet-cd497eb7,subnet-bd4bbfd6" 
}

output "dr_dbsubnet_id" { 
	value = "subnet-5ed92c13,subnet-da4a7da0,subnet-3e4abe55" 
}

output "dr_dockerid" {
	value = "${data.aws_ami.docker.id}"
}

output "dr_secgroup_id" {
	value = "sg-2d9e7847"
}

output "dr_route53_zone" { 
	value = "${data.aws_route53_zone.namespace_zone.zone_id}" 
}

output "dr_availability_zones"	{ 
	value = "eu-central-1c,eu-central-1a,eu-central-1b"
}
