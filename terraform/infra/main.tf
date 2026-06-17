# Récupérer la clé SSH existante dans DigitalOcean
data "digitalocean_ssh_key" "my_key" {
  name = var.ssh_key_name
}

# Créer le Droplet
resource "digitalocean_droplet" "app_server" {
  name   = "card-game-server"
  image  = "docker-20-04"
  size   = var.droplet_size
  region = var.region
  ssh_keys = [data.digitalocean_ssh_key.my_key.id]
}

# Firewall — autoriser les ports nécessaires
resource "digitalocean_firewall" "app_firewall" {
  name = "card-game-firewall"
  droplet_ids = [digitalocean_droplet.app_server.id]

  inbound_rule {
    protocol         = "tcp"
    port_range       = "22"
    source_addresses = ["0.0.0.0/0" , "::/0"]
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "80"
    source_addresses = ["0.0.0.0/0","::/0"]
  }

  inbound_rule {
    protocol         = "tcp"
    port_range       = "8080"
    source_addresses = ["0.0.0.0/0", "::/0"]
  }
  inbound_rule {
    protocol         = "icmp"
    source_addresses = ["0.0.0.0/0"]
  }

  outbound_rule {
    protocol              = "tcp"
    port_range            = "all"
    destination_addresses = ["0.0.0.0/0"]
  }
}