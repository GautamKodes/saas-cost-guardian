# SaaS Cost Guardian - PowerShell Traffic Simulator
param(
    [string]$Mode = "dynamic",
    [int]$Burst = 0
)

$Endpoint = "http://localhost:8080/simulate-call"
$Vendors = @("openai", "twilio")

function Send-ApiCall {
    param([string]$Vendor, [string]$CallerId)
    $body = @{ vendor = $Vendor; callerId = $CallerId } | ConvertTo-Json
    try {
        $null = Invoke-RestMethod -Uri $Endpoint -Method Post -Body $body -ContentType "application/json" -TimeoutSec 2
        return $true
    } catch {
        return $false
    }
}

Write-Host "🔍 Testing connection to $Endpoint..." -ForegroundColor Cyan
if (-not (Send-ApiCall -Vendor "openai" -CallerId "ping")) {
    Write-Host "❌ Could not reach $Endpoint. Please check if service on port 8080 is running." -ForegroundColor Red
    exit
}
Write-Host "✅ Connected to backend successfully!`n" -ForegroundColor Green

if ($Burst -gt 0) {
    Write-Host "🚀 Sending burst of $Burst calls..." -ForegroundColor Yellow
    for ($i = 1; $i -le $Burst; $i++) {
        $v = $Vendors | Get-Random
        $null = Send-ApiCall -Vendor $v -CallerId "burst_user"
        Start-Sleep -Milliseconds 20
    }
    Write-Host "✅ Burst completed! Dashboard will update shortly." -ForegroundColor Green
    exit
}

if ($Mode -eq "spike") {
    Write-Host "🚨 Triggering Anomaly Spike (80 rapid calls)..." -ForegroundColor Red
    for ($i = 1; $i -le 80; $i++) {
        $null = Send-ApiCall -Vendor "openai" -CallerId "malicious_bot"
        Start-Sleep -Milliseconds 20
    }
    Write-Host "✅ Anomaly spike sent! Check dashboard for Alert & Risk level change." -ForegroundColor Red
    exit
}

Write-Host "🔄 Running traffic simulation (Press Ctrl+C to stop)..." -ForegroundColor Cyan
while ($true) {
    $batch = Get-Random -Minimum 3 -Maximum 10
    $v = $Vendors | Get-Random
    $caller = "user_" + (Get-Random -Minimum 1 -Maximum 15)
    for ($i = 0; $i -lt $batch; $i++) {
        $null = Send-ApiCall -Vendor $v -CallerId $caller
    }
    Write-Host "[$(Get-Date -Format 'HH:mm:ss')] Sent $batch calls ($v by $caller)" -ForegroundColor Gray
    Start-Sleep -Seconds (Get-Random -Minimum 1 -Maximum 3)
}
