#!/bin/bash
echo "Simulating 40 rapid bot abuse calls (anomaly traffic)..."

for i in {1..40}; do
  curl -s -X POST http://localhost:8081/simulate-call \
    -H "Content-Type: application/json" \
    -d '{"vendor": "openai", "callerId": "bot_attacker_99"}'
  echo "Sent abuse call $i from bot_attacker_99"
  sleep 0.15
done

echo "Done! Anomaly burst complete. Check dashboard for alert banner!"
