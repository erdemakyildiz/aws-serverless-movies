variable "app_name"			{ default = "carrier-pickup-notification" }
variable "namespace"    	{ default = "carrierint" }
variable "bucket"       	{ default = "" }
variable "environment" 		{ default = "" }
variable "region" 			{ default = "" }
variable "build_number" 	{ default = "" }
variable "build_version" 	{ default = "" }
variable "subnet_id"	 	{ default = "" }
variable "secgroup_id"	 	{ default = "" }

variable "tags" {
  type = "map"

  default = {
	Requestor 	= 	"Lst-Technology.ELC.Solutions.Technical@nike.com"
	Department 	= 	"ELC Solutions"
	AppId		=	"n/a"
	CostCenter	=	"114572"
	ProjectCode	=	"KLO"
	DataClass	=	"2"
  }
}