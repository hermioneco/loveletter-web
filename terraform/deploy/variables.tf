variable "droplet_ip" {
  description = "IP publique du Droplet"
  default     = ""
}

variable "frontend_image_name" {
  description = "Image Docker du frontend"
  default     = "card-game-frontend:latest"
}

variable "backend_image_name" {
  description = "Image Docker du backend"
  default     = "card-game-backend:latest"
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