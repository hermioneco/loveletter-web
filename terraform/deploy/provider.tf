terraform {
  required_providers {
    docker = {
      source  = "kreuzwerker/docker"
      version = "~> 3.0"
    }
  }
}

provider "docker" {
  host = "ssh://root@${var.droplet_ip}:22"

  ssh_opts = [
    "-i", "/.ssh/do_server_monitor",
    "-o", "StrictHostKeyChecking=no",
    "-o", "ConnectTimeout=30"
  ]
}