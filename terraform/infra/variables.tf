variable "do_token" {
  description = "DigitalOcean API Token"
  sensitive   = true
}

variable "region" {
  default = "fra1"       # Frankfurt — Europe
}

variable "droplet_size" {
  default = "s-1vcpu-1gb"   # Le moins cher
}

variable "ssh_key_name" {
  description = "Nom de la clé SSH dans DigitalOcean"
  default     = "server-monitor-key"   #

}