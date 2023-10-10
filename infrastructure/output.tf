output "username" {
  value = azurerm_postgresql_flexible_server.pgsql_server.administrator_login
}
