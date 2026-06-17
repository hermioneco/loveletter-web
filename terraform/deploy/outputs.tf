output "backend_url" {
  value       = "http://${var.droplet_ip}:${var.backend_external_port}"
  description = "URL du backend"
}

output "frontend_url" {
  value       = "http://${var.droplet_ip}:${var.frontend_external_port}"
  description = "URL du frontend"
}