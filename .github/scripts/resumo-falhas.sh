#!/usr/bin/env bash
# Extrai do build.log os erros de compilação e os testes que falharam,
# e publica como anotação do GitHub Actions e no resumo do job.
# Assim a causa da falha aparece na página do run, sem abrir o log inteiro.

SUITE="${1:-Testes}"
LOG="build.log"

[ -f "$LOG" ] || { echo "::error title=$SUITE::build.log não encontrado"; exit 0; }

FALHAS=$(grep -E "^\[ERROR\]|expected|Expected|but was" "$LOG" \
  | grep -vE "^\[ERROR\] *$|Help 1|re-run Maven|full stack trace|For more information|cwiki.apache" \
  | sed 's/\t/  /g' | head -40)

{
  echo "## Falhas — $SUITE"
  echo '```'
  echo "$FALHAS"
  echo '```'
} >> "$GITHUB_STEP_SUMMARY"

# Anotação única com quebras de linha codificadas (%0A)
MSG=$(echo "$FALHAS" | cut -c1-300 | sed ':a;N;$!ba;s/%/%25/g;s/\r//g;s/\n/%0A/g')
echo "::error title=Falhas $SUITE::$MSG"
