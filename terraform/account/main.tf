module "globalvars" {
	source = "../global"
}

module "accountvars" {
	source = "selectedaccount"
}

resource "aws_key_pair" "carrierint-default-key" {
  count = "${var.dr == 1 ? 0 : 1}"
  key_name   = "carrierint-default-key"
  public_key = "ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCI641paOdYXOgJyWvJWPq7ZOKli0U7HmXfhXvIztpkdcvJg/zCquVoQ5E0MJVTmhBT1/gvkE6lwtqGV9LZ+StkzHL2blAxpR+V7QpfuiuJ+Y9V/ibSHWuvcO6HYECzXzvdR1jKdnk1X7p48BaNzkpLT4v9cKu09V1MO85nNYpojtGhAisiBixL/MeasaeYmCF6BhJhhCIIRXfJjcqLXjvX2Gb7tq81jLk02Q1RTjOq9UFGqZJTGuQh1L6VLkjpxtvY9oHHyc6Qg8+GcNi5Qeg051jOYokk3bvyItHNCBudkA/cbJlTXGDtgjAJNpuoHp/N3u1X4Kuf+ncaEEj+9rpb"
}