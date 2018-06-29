#!/bin/bash

set -e

echo "Building"
# ./latexdockercmd.sh latexmk -cd -f -interaction=batchmode -pdf main.tex
./latexdockercmd.sh pdflatex main.tex
cp report/main.pdf ./report.pdf
# cleanup
echo "Cleaning up"
./latexdockercmd.sh latexmk -c

if [[ "$(uname)" == 'Darwin' ]]; then
    open report.pdf
fi
