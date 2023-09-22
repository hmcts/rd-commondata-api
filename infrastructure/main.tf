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

# Create the database server
# Name and resource group name will be defaults (<product>-<component>-<env> and <product>-<component>-data-<env> respectively)
module "postgresql" {
  source = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"

  providers = {
    azurerm.postgres_network = azurerm.postgres_network
  }

  admin_user_object_id = var.jenkins_AAD_objectId
  business_area        = "cft"
  common_tags          = var.common_tags
  component            = var.component
  env                  = var.env
  pgsql_databases = [
    {
      name = "rd-commondata-api-db"
    }
  ]
  pgsql_version        = "14"
  product              = var.product
  name               = join("-", [var.product, var.component, "v14","demo"])
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name          = join("-", [var.component, "POSTGRES-USER"])
  value         = "dbcommondata@rd-commondata-api-postgres-db-v11-demo"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name          = join("-", [var.component, "POSTGRES-PASS"])
  value         = "!mbj?Q[wDX9w=@ua"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name          = join("-", [var.component, "POSTGRES-HOST"])
  value         = "rd-commondata-api-postgres-db-v11-demo.postgres.database.azure.com"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

data "azurerm_key_vault_secret" "s2s_secret" {
  name          = "microservicekey-rd-commondata-api"
  key_vault_id  = data.azurerm_key_vault.s2s_key_vault.id
  value="RCOYOD3IMKTVUQZV"
}


resource "azurerm_key_vault_secret" "common_data_s2s_secret" {
  name          = "common-data-api-s2s-secret"
  value         = "RCOYOD3IMKTVUQZV"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = local.s2s_key_vault_name
  resource_group_name = local.s2s_vault_resource_group
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name          = join("-", [var.component, "POSTGRES-DATABASE"])
  value         = "dbcommondata"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name          = join("-", [var.component, "POSTGRES-PORT"])
  value         = "5432"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

data "azurerm_key_vault" "rd_key_vault" {
  name                = local.key_vault_name
  resource_group_name = local.key_vault_name
}




