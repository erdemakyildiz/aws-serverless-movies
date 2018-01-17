variable "app_name"				{ default = "" }
variable "environment"			{ default = "" }
variable "region"	 			{ default = "" }
variable "namespace" 			{ default = "" }
variable "bucket"      			{ default = "" }
variable "build_number" 		{ default = "" }
variable "build_version" 		{ default = "" }
variable "subnet_id" 			{ default = "" }
variable "secgroup_id" 			{ default = "" }
variable "resource_prefix"		{ default = "" }

variable "api_lambda_name" 		{ default = "api-lambda" }
variable "api_lambda_handler"	{ default = "com.nike.emeasc.carrierpickupnotification.PickupNotificationLambda::handleRequest" }

variable "tags"    				{ type = "map" }