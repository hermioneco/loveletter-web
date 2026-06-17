variable "do_token" {
  description = "DigitalOcean API Token"
  sensitive   = true
}

variable "region" {
  default = "fra1"
}

variable "droplet_size" {
  default = "s-1vcpu-1gb"
}

variable "ssh_key_name" {
  description = "Nom de la clé SSH dans DigitalOcean"
}