# 안드로이드 서명 키를 GitHub 저장소의 비밀값(Secrets)에 등록한다. (사용자가 직접 한 번 실행)
# 키 파일·비밀번호는 C:\Users\bbaja\afterglow-signing 폴더에만 있고, 이 스크립트는 그 값을 GitHub에 암호화해서 보낸다.
$ErrorActionPreference = 'Stop'
$env:Path = [Environment]::GetEnvironmentVariable("Path","Machine") + ";" + [Environment]::GetEnvironmentVariable("Path","User")
$repo = 'max9ksj-dotcom/afterglow-protocol'
$dir = 'C:\Users\bbaja\afterglow-signing'
$pwLine = (Get-Content "$dir\README_비밀번호.txt" -Encoding utf8) | Where-Object { $_ -like '비밀번호: *' }
$pw = $pwLine.Substring(6).Trim()
[Convert]::ToBase64String([IO.File]::ReadAllBytes("$dir\afterglow.jks")) | gh secret set AG_KEYSTORE_B64 -R $repo
$pw | gh secret set AG_KEYSTORE_PASSWORD -R $repo
'afterglow' | gh secret set AG_KEY_ALIAS -R $repo
gh secret list -R $repo
Write-Host '서명 키 등록 완료'
