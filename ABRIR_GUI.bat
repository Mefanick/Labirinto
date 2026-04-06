@echo off
cls
echo.
echo ╔════════════════════════════════════════════════════╗
echo ║                                                    ║
echo ║        SIMULADOR DE ROBO - INTERFACE GRAFICA       ║
echo ║                                                    ║
echo ╚════════════════════════════════════════════════════╝
echo.

REM Verificar se JavaFX SDK existe
if not exist "javafx-sdk-21.0.1\lib" (
    echo [ERRO] JavaFX SDK nao encontrado!
    pause
    exit /b 1
)

REM Verificar se o projeto foi compilado
if not exist "bin\com\projetorobo\gui\MainGUI.class" (
    echo Compilando interface grafica...
    echo.
    
    REM Limpar bin
    if exist "bin" rmdir /s /q bin
    mkdir bin
    
    REM Compilar todos os arquivos JavaFX juntos
    powershell -Command "$files = Get-ChildItem -Path 'src\main\java' -Filter '*.java' -Recurse | Select-Object -ExpandProperty FullName; javac --module-path 'javafx-sdk-21.0.1\lib' --add-modules javafx.controls,javafx.fxml -d 'bin' $files"
    
    if errorlevel 1 (
        echo [ERRO] Compilacao falhou!
        pause
        exit /b 1
    )
    
    REM Copiar CSS
    if exist "src\main\resources\css" (
        mkdir "bin\css" 2>nul
        xcopy /s /y "src\main\resources\css" "bin\css\" >nul
    )
    
    echo Compilacao concluida!
    echo.
)

echo Abrindo interface grafica...
echo.

java --module-path "javafx-sdk-21.0.1\lib" --add-modules javafx.controls,javafx.fxml -cp bin com.projetorobo.gui.MainGUI

if errorlevel 1 (
    echo.
    echo [ERRO] Falha ao executar!
    pause
    exit /b 1
)
