$logFile = "c:\Users\ULTRAPC\Desktop\microservice\Etude_de_Cas_Clients_Synchrones\tests\resources.csv"
"Timestamp,Process,Id,CPU,RAM_MB" | Out-File $logFile -Encoding ascii

$start = Get-Date

while ((Get-Date) -lt $start.AddSeconds(60)) {
    $timestamp = (Get-Date).ToString("HH:mm:ss")
    Get-Process java | ForEach-Object {
        $name = $_.Name
        $id = $_.Id
        $cpu = $_.CPU
        $ram = [math]::Round($_.WorkingSet / 1MB, 2)
        "$timestamp,$name,$id,$cpu,$ram" | Out-File $logFile -Append -Encoding ascii
    }
    Start-Sleep -Seconds 1
}
