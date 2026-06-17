output "frontend_url" {
  value = "http://${var.droplet_ip}:${var.frontend_port}"
}

output "backend_url" {
  value = "http://${var.droplet_ip}:${var.backend_port}"
}

output "network_id" {
  value = docker_network.card_game_network.id
}