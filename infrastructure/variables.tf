variable "product" {
  type = string
  default     = "rd-commondata-api"
}

variable "component" {
  type = string
  default="postgres-db-v14"
}

variable "location" {
  type = string
  default = "UK South"
}

variable "env" {
  type = string
  default="demo"
}

variable "subscription" {
  type = string
}

variable "common_tags" {
  type = map(any)
}
variable "aks_subscription_id" {
  default = "d025fece-ce99-4df2-b7a9-b649d3ff2060"
}
