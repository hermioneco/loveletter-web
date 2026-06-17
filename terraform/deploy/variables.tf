variable "droplet_ip" {
  description = "IP publique du Droplet"
  default     = ""
}

variable "frontend_image_name" {
  description = "Image Docker du frontend"
  default     = "hahahahahahaha853/card-game-frontend:latest"
}

variable "backend_image_name" {
  description = "Image Docker du backend"
  default     = "hahahahahahaha853/card-game-backend:latest"
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
variable "docker_username" {
  description = "Docker Hub username"
  default     = "hahahahahahaha853"
}

variable "docker_password" {
  description = "Docker Hub password ou access token"
  sensitive   = true
}