locals {
  tags = (merge(
    var.common_tags,
    tomap({
      "Team Contact" = var.team_contact
      "Destroy Me"   = var.destroy_me
    })
  ))
  key_vault_name          = join("-", [var.product, var.env])
  s2s_key_vault_name        = join("-", ["s2s", var.env])
  s2s_vault_resource_group  = join("-", ["rpe-service-auth-provider", var.env])
}

resource "azurerm_resource_group" "rd-commondata-api-postgres-db-v14-demo" {
  name     = "${var.product}-${var.component}-${var.env}"
  location = var.location
}

