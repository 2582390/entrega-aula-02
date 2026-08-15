# Script para rodar os serviços localmente

param(
    [string]$Service = "all", # all, pedidos, estoque
    [int]$PedidosPort = 8080,
    [int]$EstoquePort = 9090
)

# Verificar se os JARs existem
if (-not (Test-Path "servico-pedidos/target/servico-pedidos-1.0.jar")) {
    Write-Host "ERRO: servico-pedidos-1.0.jar nao encontrado!" -ForegroundColor Red
    Write-Host "Execute primeiro: build.bat" -ForegroundColor Yellow
    exit 1
}

if (-not (Test-Path "servico-estoque/target/servico-estoque-1.0.jar")) {
    Write-Host "ERRO: servico-estoque-1.0.jar nao encontrado!" -ForegroundColor Red
    Write-Host "Execute primeiro: build.bat" -ForegroundColor Yellow
    exit 1
}

# Verificar se Docker está rodando
Write-Host "Verificando Docker..." -ForegroundColor Cyan
$dockerRunning = docker ps 2>$null
if ($LASTEXITCODE -ne 0) {
    Write-Host "AVISO: Docker nao esta rodando!" -ForegroundColor Yellow
    Write-Host "Execute: docker compose up -d" -ForegroundColor Yellow
    Read-Host "Pressione Enter para continuar mesmo assim..."
}

Write-Host ""
Write-Host "========================================" -ForegroundColor Cyan
Write-Host "Rodando Servicos - Aula 02" -ForegroundColor Cyan
Write-Host "========================================" -ForegroundColor Cyan
Write-Host ""

# Rodar Producer
if ($Service -eq "all" -or $Service -eq "pedidos") {
    Write-Host "Iniciando servico-pedidos na porta $PedidosPort..." -ForegroundColor Green
    Write-Host "Press Ctrl+C para parar" -ForegroundColor Gray
    Write-Host ""
    
    java -jar "servico-pedidos/target/servico-pedidos-1.0.jar" `
        --server.port=$PedidosPort
}

# Rodar Consumer (apenas se não for rodar pedidos)
if ($Service -eq "estoque") {
    Write-Host "Iniciando servico-estoque..." -ForegroundColor Green
    Write-Host "Press Ctrl+C para parar" -ForegroundColor Gray
    Write-Host ""
    
    java -jar "servico-estoque/target/servico-estoque-1.0.jar"
}

if ($Service -eq "all") {
    Write-Host "Abra outro terminal e execute:" -ForegroundColor Yellow
    Write-Host "  .\run.ps1 -Service estoque" -ForegroundColor Yellow
    Read-Host "Pressione Enter quando estiver pronto..."
}
