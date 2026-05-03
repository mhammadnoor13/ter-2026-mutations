Write-Host "=============================="
Write-Host "Running PIT mutation testing"
Write-Host "=============================="
mvn clean test-compile org.pitest:pitest-maven:mutationCoverage
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "=============================="
Write-Host "Generating mutants and instrumentation"
Write-Host "=============================="
mvn exec:java "-Dexec.mainClass=integration.pipeline.FullPipelineRunner" "-Dexec.args=generate"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "=============================="
Write-Host "Compiling generated classes"
Write-Host "=============================="
mvn compile
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "=============================="
Write-Host "Running propagation analysis"
Write-Host "=============================="
mvn exec:java "-Dexec.mainClass=integration.pipeline.FullPipelineRunner" "-Dexec.args=analyze"
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }

Write-Host ""
Write-Host "=============================="
Write-Host "Pipeline finished successfully"
Write-Host "=============================="