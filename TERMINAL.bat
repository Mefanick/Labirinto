@echo off
cls
echo.
echo ╔════════════════════════════════════════════════════╗
echo ║                                                    ║
echo ║        SIMULADOR DE ROBO - MODO TERMINAL           ║
echo ║                                                    ║
echo ╚════════════════════════════════════════════════════╝
echo.

REM Verificar se está compilado
if not exist "bin\com\projetorobo\Main.class" (
    echo Compilando para terminal...
    echo.
    
    REM Criar pasta bin
    if not exist "bin" mkdir bin
    
    REM Listar e compilar (exceto GUI)
    powershell -Command "Get-ChildItem -Path 'src\main\java' -Filter '*.java' -Recurse | Where-Object { $_.FullName -notlike '*\gui\*' } | Select-Object -ExpandProperty FullName | ForEach-Object { javac -d 'bin' $_ }"
    
    if errorlevel 1 (
        echo [ERRO] Compilacao falhou!
        pause
        exit /b 1
    )
    
    echo Compilacao concluida!
    echo.
)

echo Iniciando simulador...
echo.

java -cp bin com.projetorobo.Main

pause
