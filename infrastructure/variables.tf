variable "product" {
  type = string
}

variable "component" {
  type = string
  default="postgres-db-v11"
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
