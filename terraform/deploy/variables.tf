variable "backend_image_name" {
  default = "hahahahahahaha853/card-game-backend:latest"
}

variable "frontend_image_name" {
  default = "hahahahahahaha853/card-game-frontend:latest"
}
variable "frontend_port" {
  default = 80
}
variable "backend_port" {
  default = 8080
}
variable "network_name" {
  default = "card-game-network"
}
variable "droplet_ip" {
  description = "IP publique du Droplet"

}


