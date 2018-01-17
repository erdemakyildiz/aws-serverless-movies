variable "app_name"				{ default = "" }
variable "environment"			{ default = "" }
variable "namespace" 			{ default = "" }
variable "resource_prefix"		{ default = "" }

variable "table_name"	 		{ default = "carrier-pickup-notifications" }
variable "table_ttl"			{ default = "true" }

variable "read_capacity"		{ default = "1" }
variable "write_capacity"		{ default = "1" }

variable "hash_key"				{ default = "shipmentReference" }
variable "hash_key_type"		{ default = "S" }

variable "range_key"			{ default = "creationDateTime" }
variable "range_key_type"		{ default = "N" }

variable "tags"    				{ type = "map" }