#!/bin/bash
cd /home/kavia/workspace/code-generation/trade-journal-pro-241939-241953/trading_journal_android_app
./gradlew lint
LINT_EXIT_CODE=$?
if [ $LINT_EXIT_CODE -ne 0 ]; then
   exit 1
fi

