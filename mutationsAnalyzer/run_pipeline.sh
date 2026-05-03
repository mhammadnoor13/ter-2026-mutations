#!/bin/bash
set -e

echo "=============================="
echo "Running PIT mutation testing"
echo "=============================="
mvn clean test-compile org.pitest:pitest-maven:mutationCoverage

echo
echo "=============================="
echo "Generating mutants and instrumentation"
echo "=============================="
mvn exec:java -Dexec.mainClass="integration.pipeline.FullPipelineRunner" -Dexec.args="generate"

echo
echo "=============================="
echo "Compiling generated classes"
echo "=============================="
mvn compile

echo
echo "=============================="
echo "Running propagation analysis"
echo "=============================="
mvn exec:java -Dexec.mainClass="integration.pipeline.FullPipelineRunner" -Dexec.args="analyze"

echo
echo "=============================="
echo "Pipeline finished successfully"
echo "=============================="