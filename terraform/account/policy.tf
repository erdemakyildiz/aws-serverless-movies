resource "aws_iam_role" "carrierint-assume_role" {
  count = "${var.dr == 1 ? 0 : 1}"
  name = "${var.aws_namespace}-assume_role"
  assume_role_policy = "${file("./policies/carrierint-assume-role.json")}"
}

resource "aws_iam_role_policy_attachment" "carrierint-attach" {
  count = "${var.dr == 1 ? 0 : 1}"
  role      =  "${aws_iam_role.carrierint-assume_role.name}"
  policy_arn =  "arn:aws:iam::${module.accountvars.account_id}:policy/carrierint-s3-policy"
}

resource "aws_iam_instance_profile" "ec2_instance_profile" {
  count = "${var.dr == 1 ? 0 : 1}"
  name = "${var.aws_namespace}-assume_role"
  role = "${aws_iam_role.carrierint-assume_role.name}"
}