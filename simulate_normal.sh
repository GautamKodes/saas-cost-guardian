#!/bin/bash
echo "Simulating 15 normal API calls across OpenAI and Twilio..."

for i in {1..10}; do
  curl -s -X POST http://localhost:8081/simulate-call \
    -H "Content-Type: application/json" \
    -d "{\"vendor\": \"openai\", \"callerId\": \"user_$i\"}"
  echo "Sent OpenAI call from user_$i"
  sleep 0.2
done

for i in {1..5}; do
  curl -s -X POST http://localhost:8081/simulate-call \
    -H "Content-Type: application/json" \
    -d "{\"vendor\": \"twilio\", \"callerId\": \"caller_$i\"}"
  echo "Sent Twilio call from caller_$i"
  sleep 0.2
done

echo "Done! Check your dashboard."
