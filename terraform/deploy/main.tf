resource "docker_network" "card_game_network" {
  name   = var.network_name
  driver = "bridge"
}

resource "docker_image" "backend" {
  name         = var.backend_image_name
  keep_locally = false
}

resource "docker_image" "frontend" {
  name         = var.frontend_image_name
  keep_locally = false
}

resource "docker_container" "backend" {
  name  = "card-game-backend"
  image = docker_image.backend.image_id
  restart = "always"
  ports {
    internal = 8080
    external = var.backend_port
  }
  networks_advanced {
    name = docker_network.card_game_network.name
  }

}

resource "docker_container" "frontend" {
  name  = "card-game-frontend"
  image = docker_image.frontend.image_id
  restart = "always"

  ports {
    internal = 80
    external = var.frontend_port
  }
  networks_advanced {
    name = docker_network.card_game_network.name
  }
  depends_on = [docker_container.backend]

}