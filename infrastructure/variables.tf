variable "product" {
  type = string
}

variable "component" {
  type = string
}

variable "location" {
  type = string
  default = "UK South"
}

variable "env" {
  type = string
}

variable "subscription" {
  type = string
}

variable "common_tags" {
  type = map(any)
}

variable tenant_id {}

variable jenkins_AAD_objectId {
  description = "(Required) The Azure AD object ID of a user, service principal or security group in the Azure Active Directory tenant for the vault. The object ID must be unique for the list of access policies."
}

variable "aks_subscription_id" {}

variable "pgsql_sku" {
  description = "The PGSql flexible server instance sku"
  default     = "GP_Standard_D2s_v3"
}

variable "pgsql_storage_mb" {
  description = "Max storage allowed for the PGSql Flexibile instance"
  type        = number
  default     = 65536
}

variable shared_product_name {
  default = "rd"
}
variable database_storage_mb {
  default = "358400"
}
variable sku_name {
  default = "GP_Gen5_4"
}
variable sku_capacity {
  default = "4"
}
variable idam_api_base_uri {
  default = "https://idam-api.aat.platform.hmcts.net"
}

variable open_id_api_base_uri {
  default = "https://idam-api.demo.platform.hmcts.net/o"
}

