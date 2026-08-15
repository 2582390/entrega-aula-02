@echo off
REM Script para compilar ambos os serviços da Aula 02

setlocal enabledelayedexpansion

REM Configurar Maven
set MAVEN_HOME=%USERPROFILE%\AppData\Local\Maven\apache-maven-3.9.5
set PATH=%MAVEN_HOME%\bin;%PATH%

REM Ir para o diretório do projeto
cd /d "%~dp0"

echo.
echo ========================================
echo Compilação Aula 02 - Entrega
echo ========================================
echo.

REM Compilar servico-pedidos
echo.
echo [1/2] Compilando servico-pedidos (producer)...
echo.
call mvn -f servico-pedidos\pom.xml clean package -DskipTests
if errorlevel 1 (
    echo.
    echo ERRO na compilacao do servico-pedidos!
    pause
    exit /b 1
)

REM Compilar servico-estoque
echo.
echo [2/2] Compilando servico-estoque (consumer)...
echo.
call mvn -f servico-estoque\pom.xml clean package -DskipTests
if errorlevel 1 (
    echo.
    echo ERRO na compilacao do servico-estoque!
    pause
    exit /b 1
)

REM Verificar JARs
echo.
echo ========================================
echo Verificando JARs gerados...
echo ========================================
echo.

if exist "servico-pedidos\target\servico-pedidos-1.0.jar" (
    echo [OK] servico-pedidos-1.0.jar
) else (
    echo [ERRO] servico-pedidos-1.0.jar nao encontrado
)

if exist "servico-estoque\target\servico-estoque-1.0.jar" (
    echo [OK] servico-estoque-1.0.jar
) else (
    echo [ERRO] servico-estoque-1.0.jar nao encontrado
)

echo.
echo ========================================
echo Proximos passos:
echo ========================================
echo.
echo 1. Iniciar Docker:
echo    docker compose up -d
echo.
echo 2. Em terminais separados, rodar:
echo    java -jar servico-pedidos\target\servico-pedidos-1.0.jar
echo    java -jar servico-estoque\target\servico-estoque-1.0.jar
echo.
echo 3. Testar:
echo    curl -X POST http://localhost:8080/pedidos/confirmados ^
echo      -H "Content-Type: application/json" ^
echo      -d "@pedidos/pedido-4711.json"
echo.
echo 4. Criar tag e fazer push:
echo    git tag entrega-aula-02
echo    git push origin entrega-aula-02
echo.
echo ========================================
echo.
pause
