# Copyright (c) TIKI Inc.
# MIT license. See LICENSE file in root directory.

resource "digitalocean_database_cluster" "db-cluster-l0-registry" {
  name                 = "l0-registry-db-cluster-${local.region}"
  engine               = "pg"
  version              = "15"
  size                 = "db-s-1vcpu-1gb"
  region               = local.region
  node_count           = 1
  private_network_uuid = local.vpc_uuid
}

resource "digitalocean_database_db" "db-l0-registry" {
  cluster_id = digitalocean_database_cluster.db-cluster-l0-registry.id
  name       = "l0_registry"
}

resource "digitalocean_database_firewall" "db-cluster-l0-registry-fw" {
  cluster_id = digitalocean_database_cluster.db-cluster-l0-registry.id

  rule {
    type  = "app"
    value = digitalocean_app.l0-registry-app.id
  }
}

resource "digitalocean_database_user" "db-user-l0-registry" {
  cluster_id = digitalocean_database_cluster.db-cluster-l0-registry.id
  name       = "l0-registry-service"
}
