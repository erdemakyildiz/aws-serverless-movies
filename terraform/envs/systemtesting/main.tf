module "accountvars" {
  source  =   "../../account/nonprod"
}

module "global" {
  source  =  "../../global"

  environment = "${terraform.workspace}"
  region = "${module.accountvars.aws_region}"
  build_number = "${var.BUILD_NUMBER}"
  build_version = "${var.BUILD_VERSION}"
  bucket = "${var.BUCKET_NAME}"
  subnet_id = "${module.accountvars.subnet_id}"
  secgroup_id = "${module.accountvars.secgroup_id}"
}