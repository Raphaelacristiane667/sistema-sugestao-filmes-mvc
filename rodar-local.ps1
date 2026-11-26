$ErrorActionPreference = "Stop"
[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12

$tools = Join-Path $env:USERPROFILE ".mvc-java-tools"
$downloads = Join-Path $tools "downloads"
$jdkRoot = Join-Path $tools "jdk"
$mavenRoot = Join-Path $tools "maven"
$tomcatRoot = Join-Path $tools "tomcat"
New-Item -ItemType Directory -Force -Path $downloads, $jdkRoot, $mavenRoot, $tomcatRoot | Out-Null

function Find-Home($root, $exeRelative) {
    Get-ChildItem $root -Directory -ErrorAction SilentlyContinue |
        Where-Object { Test-Path (Join-Path $_.FullName $exeRelative) } |
        Select-Object -First 1 -ExpandProperty FullName
}

function Get-Zip($url, $zipPath) {
    if (-not (Test-Path $zipPath)) {
        Write-Host "Baixando $url"
        Invoke-WebRequest -Uri $url -OutFile $zipPath
    }
}

function Expand-IfEmpty($zipPath, $dest) {
    $found = Find-Home $dest (Join-Path "bin" "java.exe")
    if (-not $found) {
        $found = Find-Home $dest (Join-Path "bin" "mvn.cmd")
    }
    if (-not $found) {
        $found = Find-Home $dest (Join-Path "bin" "catalina.bat")
    }
    if ($found) { return $found }
    Write-Host "Extraindo $zipPath"
    Expand-Archive -Path $zipPath -DestinationPath $dest -Force
    return $null
}

$jdkZip = Join-Path $downloads "jdk17.zip"
$mavenZip = Join-Path $downloads "maven.zip"
$tomcatZip = Join-Path $downloads "tomcat.zip"

Get-Zip "https://api.adoptium.net/v3/binary/latest/17/ga/windows/x64/jdk/hotspot/normal/eclipse?project=jdk" $jdkZip
Get-Zip "https://archive.apache.org/dist/maven/maven-3/3.9.9/binaries/apache-maven-3.9.9-bin.zip" $mavenZip
Get-Zip "https://archive.apache.org/dist/tomcat/tomcat-10/v10.1.40/bin/apache-tomcat-10.1.40.zip" $tomcatZip

Expand-IfEmpty $jdkZip $jdkRoot | Out-Null
Expand-IfEmpty $mavenZip $mavenRoot | Out-Null
Expand-IfEmpty $tomcatZip $tomcatRoot | Out-Null

$env:JAVA_HOME = Find-Home $jdkRoot "bin\java.exe"
$mavenHome = Find-Home $mavenRoot "bin\mvn.cmd"
$env:CATALINA_HOME = Find-Home $tomcatRoot "bin\catalina.bat"

if (-not $env:JAVA_HOME) { throw "JDK 17 nao foi extraido." }
if (-not $mavenHome) { throw "Maven nao foi extraido." }
if (-not $env:CATALINA_HOME) { throw "Tomcat nao foi extraido." }

$env:Path = "$env:JAVA_HOME\bin;$mavenHome\bin;$env:Path"
$env:DB_MODE = "h2"
$env:MAVEN_OPTS = "-Dfile.encoding=UTF-8"

Write-Host "Java: $env:JAVA_HOME"
& java -version
$mvn = Join-Path $mavenHome "bin\mvn.cmd"
$webapps = Join-Path $env:CATALINA_HOME "webapps"

# Dois projetos separados: a API (api.war) e o sistema web que consome a API (mvc.war)
foreach ($projeto in @(@{ Pasta = "api"; War = "api" }, @{ Pasta = "web"; War = "mvc" })) {
    Write-Host "Compilando $($projeto.Pasta)..."
    & $mvn -f (Join-Path $PSScriptRoot "$($projeto.Pasta)\pom.xml") -DskipTests package
    if ($LASTEXITCODE -ne 0) { throw "Falha no Maven ($($projeto.Pasta))." }

    $war = Join-Path $PSScriptRoot "$($projeto.Pasta)\target\$($projeto.War).war"
    if (-not (Test-Path $war)) { throw "WAR nao encontrado em $war" }
    Copy-Item $war (Join-Path $webapps "$($projeto.War).war") -Force
    Remove-Item (Join-Path $webapps $projeto.War) -Recurse -Force -ErrorAction SilentlyContinue
}

# O sistema web chama a API no mesmo Tomcat
$env:API_URL = "http://localhost:8080/api"

Write-Host ""
Write-Host "API:        http://localhost:8080/api/filmes"
Write-Host "Swagger:    http://localhost:8080/api/docs/"
Write-Host "Telas web:  http://localhost:8080/mvc/"
Write-Host "Deixe esta janela aberta. Ctrl+C para parar."
Set-Location $env:CATALINA_HOME
& (Join-Path $env:CATALINA_HOME "bin\catalina.bat") run
