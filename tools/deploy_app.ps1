# 잔광 프로토콜 앱 배포: 테스트 → 앱 빌드 → GitHub의 gh-pages 가지에 올림 (GitHub Pages가 그 내용을 앱 주소로 보여 줌)
# 사용법: powershell -ExecutionPolicy Bypass -File tools\deploy_app.ps1
$ErrorActionPreference = 'Stop'
$env:Path = [Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [Environment]::GetEnvironmentVariable("Path","User")
$root = Split-Path $PSScriptRoot -Parent
Set-Location $root
node tests/regression.js; if ($LASTEXITCODE) { throw "regression 테스트 실패 - 배포 중단" }
node tests/invest_relic.js; if ($LASTEXITCODE) { throw "invest_relic 테스트 실패 - 배포 중단" }
node tools/build_app.js; if ($LASTEXITCODE) { throw "앱 빌드 실패" }
$remote = git remote get-url origin
$tmp = Join-Path $env:TEMP ("afterglow_pages_" + [guid]::NewGuid().ToString('N'))
New-Item -ItemType Directory $tmp | Out-Null
Copy-Item (Join-Path $root 'app\*') $tmp -Recurse
New-Item -ItemType File (Join-Path $tmp '.nojekyll') | Out-Null
Push-Location $tmp
git init -q -b gh-pages
git config core.autocrlf false
git config user.name (git -C $root config user.name)
git config user.email (git -C $root config user.email)
git add -A
git commit -q -m ("앱 배포 " + (Get-Date -Format 'yyyy-MM-dd HH:mm'))
git push -q -f $remote gh-pages
Pop-Location
Remove-Item -Recurse -Force $tmp
Write-Host "배포 완료 (gh-pages). 1~2분 뒤 앱 주소에 반영됩니다."
