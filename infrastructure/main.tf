locals {
  key_vault_name          = join("-", [var.product, var.env])
  s2s_key_vault_name        = join("-", ["s2s", var.env])
  s2s_vault_resource_group  = join("-", ["rpe-service-auth-provider", var.env])
}

module "db-common-data-v11" {
  source             = "git@github.com:hmcts/cnp-module-postgres?ref=master"
  product            = var.product
  component          = var.component
  name               = join("-", [var.product, var.component, "postgres-db", "v11"])
  location           = var.location
  subscription       = var.subscription
  env                = var.env
  postgresql_user    = "dbcommondata"
  database_name      = "dbcommondata"
  common_tags        = var.common_tags
  postgresql_version = "11"
}

data "azurerm_key_vault" "rd_key_vault" {
  name                = local.key_vault_name
  resource_group_name = local.key_vault_name
}

data "azurerm_key_vault" "s2s_key_vault" {
  name                = local.s2s_key_vault_name
  resource_group_name = local.s2s_vault_resource_group
}

data "azurerm_key_vault_secret" "s2s_secret" {
  name          = "microservicekey-rd-commondata-api"
  key_vault_id  = data.azurerm_key_vault.s2s_key_vault.id
}

resource "azurerm_key_vault_secret" "common_data_s2s_secret" {
  name          = "common-data-api-s2s-secret"
  value         = data.azurerm_key_vault_secret.s2s_secret.value
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-USER" {
  name          = join("-", [var.component, "POSTGRES-USER"])
  value         = module.db-common-data-v11.user_name
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES-PASS" {
  name          = join("-", [var.component, "POSTGRES-PASS"])
  value         = module.db-common-data-v11.postgresql_password
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST" {
  name          = join("-", [var.component, "POSTGRES-HOST"])
  value         = module.db-common-data-v11.host_name
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE" {
  name          = join("-", [var.component, "POSTGRES-DATABASE"])
  value         = module.db-common-data-v11.postgresql_database
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT" {
  name          = join("-", [var.component, "POSTGRES-PORT"])
  value         = "5432"
  key_vault_id  = data.azurerm_key_vault.rd_key_vault.id
}
# FlexiServer v14
module "db-v14" {
  providers = {
    azurerm.postgres_network = azurerm.postgres_network
  }
  source               = "git@github.com:hmcts/terraform-module-postgresql-flexible?ref=master"
  env                  = var.env
  product              = var.product
  component            = var.component
  common_tags          = var.common_tags
  name                 = "${local.app_full_name}-postgres-db-v14"
  pgsql_version        = "14"
  admin_user_object_id = var.jenkins_AAD_objectId
  business_area        = "CFT"
  pgsql_databases      = [
    {
      name : "rd-commondata-api-postgres-db-v14-demo"
    }
  ]
  pgsql_server_configuration = [
    {
      name  = "azure.extensions"
      value = "plpgsql,pg_stat_statements,pg_buffercache"
    }
  ]
  //Below attributes needs to be overridden for Perftest & Prod
  pgsql_sku            = var.pgsql_sku
  pgsql_storage_mb     = var.pgsql_storage_mb
}

locals {
  app_full_name = "${var.product}-${var.component}"
  local_env = (var.env == "preview" || var.env == "spreview") ? (var.env == "preview" ) ? "aat" : "saat" : var.env
  shared_vault_name = "${var.shared_product_name}-${local.local_env}"

  previewVaultName = "${local.app_full_name}-aat"
  nonPreviewVaultName = "${local.app_full_name}-${var.env}"
  vaultName = (var.env == "preview" || var.env == "spreview") ? local.previewVaultName : local.nonPreviewVaultName
}

module "local_key_vault" {
  source = "git@github.com:hmcts/cnp-module-key-vault?ref=master"
  product = local.app_full_name
  env = var.env
  tenant_id = var.tenant_id
  object_id = var.jenkins_AAD_objectId
  resource_group_name = "${local.app_full_name}-${var.env}"
  product_group_object_id = "1c4f0704-a29e-403d-b719-b90c34ef14c9"

common_tags = var.common_tags
  managed_identity_object_ids = [data.azurerm_user_assigned_identity.rd-shared-identity.principal_id]
}
data "azurerm_user_assigned_identity" "rd-shared-identity" {
  name                = "rd-${var.env}-mi"
  resource_group_name = "managed-identities-${var.env}-rg"
}
resource "azurerm_key_vault_secret" "POSTGRES-USER-V14" {
  name         = "${var.component}-POSTGRES-USER-V14"
  value        = module.db-v14.username
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}
# Copy s2s key from shared to local vault
data "azurerm_key_vault" "local_key_vault" {
  name = module.local_key_vault.key_vault_name
  resource_group_name = module.local_key_vault.key_vault_name
}
resource "azurerm_key_vault_secret" "POSTGRES-PASS-V14" {
  name         = "${var.component}-POSTGRES-PASS-V14"
  value        = module.db-v14.password
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_HOST-V14" {
  name         = "${var.component}-POSTGRES-HOST-V14"
  value        = module.db-v14.fqdn
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_PORT-V14" {
  name         = "${var.component}-POSTGRES-PORT-V14"
  value        = "5432"
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}

resource "azurerm_key_vault_secret" "POSTGRES_DATABASE-V14" {
  name         = "${var.component}-POSTGRES-DATABASE-V14"
  value        = "emstitch"
  key_vault_id = data.azurerm_key_vault.local_key_vault.id
}
