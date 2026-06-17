variable "droplet_ip" {
  description = "IP publique du Droplet"
}

variable "ssh_key_path" {
  description = "Chemin local vers la clé SSH privée"
  default     = "~/.ssh/do_server_monitor"
}

variable "backend_image_name" {
  description = "Image Docker du backend"
  default     = "hahahahahahaha853/card-game-backend:latest"
}

variable "frontend_image_name" {
  description = "Image Docker du frontend"
  default     = "hahahahahahaha853/card-game-frontend:latest"
}

variable "backend_internal_port" {
  default = 8080
}

variable "backend_external_port" {
  default = 8080    # ✅ corrigé
}

variable "frontend_internal_port" {
  default = 80
}

variable "frontend_external_port" {
  default = 80      # ✅ corrigé
}

variable "network_name" {
  default = "card-game-network"
}