//API Key
resource "aws_iam_user" "terraform_serviceaccount" {
    count = "${var.dr == 1 ? 0 : 1}"
	name = "s.${var.aws_namespace}-terraform"
}

resource "aws_iam_user_policy_attachment" "terraform_serviceaccount_attach_s3" {
  count = "${var.dr == 1 ? 0 : 1}"
  user      = "${aws_iam_user.terraform_serviceaccount.name}"
  policy_arn = "arn:aws:iam::${module.accountvars.account_id}:policy/carrierint-s3-policy"
}

resource "aws_iam_user_policy_attachment" "terraform_serviceaccount_attach_dynamodb" {
  count = "${var.dr == 1 ? 0 : 1}"
  user      =  "${aws_iam_user.terraform_serviceaccount.name}"
  policy_arn = "arn:aws:iam::${module.accountvars.account_id}:policy/carrierint-dynamodb-policy"
}

resource "aws_iam_user_policy_attachment" "terraform_serviceaccount_attach_ec2" {
  count = "${var.dr == 1 ? 0 : 1}"
  user      =  "${aws_iam_user.terraform_serviceaccount.name}"
  policy_arn = "arn:aws:iam::${module.accountvars.account_id}:policy/carrierint-ec2-policy"
}

resource "aws_iam_user_policy_attachment" "terraform_serviceaccount_attach_rds" {
  count = "${var.dr == 1 ? 0 : 1}"
  user      =  "${aws_iam_user.terraform_serviceaccount.name}"
  policy_arn = "arn:aws:iam::${module.accountvars.account_id}:policy/carrierint-rds-policy"
}


resource "aws_iam_user_policy_attachment" "terraform_serviceaccount_attach_route53" {
  count = "${var.dr == 1 ? 0 : 1}"
  user      =  "${aws_iam_user.terraform_serviceaccount.name}"
  policy_arn = "arn:aws:iam::${module.accountvars.account_id}:policy/carrierint-route53-policy"
}

resource "aws_iam_user_policy_attachment" "terraform_serviceaccount_attach_iam" {
  count = "${var.dr == 1 ? 0 : 1}"
  user      =  "${aws_iam_user.terraform_serviceaccount.name}"
  policy_arn = "arn:aws:iam::${module.accountvars.account_id}:policy/carrierint-iam-policy"
}

resource "aws_iam_access_key" "terraform_serviceaccount_access_key" {
  count = "${var.dr == 1 ? 0 : 1}"
  user    = "${aws_iam_user.terraform_serviceaccount.name}"
//  pgp_key = "keybase:some_person_that_exists"
}

output "serviceaccount_secret" {
  value = "${aws_iam_access_key.terraform_serviceaccount_access_key.*.encrypted_secret}"
}
output "serviceaccount_secret_un" {
  value = "${aws_iam_access_key.terraform_serviceaccount_access_key.*.secret}"
}

output "serviceaccount_secret_id" {
  value = "${aws_iam_access_key.terraform_serviceaccount_access_key.*.id}"
}