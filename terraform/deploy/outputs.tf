output "frontend_url" {
  value = "http://localhost:80"
}

output "backend_url" {
  value = "http://droplet_ip:8080"
}

output "network_id" {
  value = docker_network.card_game_network.id
}