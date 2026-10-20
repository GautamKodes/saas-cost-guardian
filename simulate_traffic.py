"""
SaaS Cost Guardian - Traffic Simulator
Generates realistic API calls to test the detection pipeline, charts, and anomaly alerts.

Usage:
    python simulate_traffic.py                # Runs realistic fluctuating traffic (normal with occasional spikes)
    python simulate_traffic.py --mode normal  # Continuous normal traffic (low risk)
    python simulate_traffic.py --mode spike   # Immediate traffic surge to trigger an anomaly alert!
    python simulate_traffic.py --burst 50     # Send 50 calls right now
"""

import urllib.request
import json
import time
import random
import sys
import argparse

ENDPOINT = "http://localhost:8080/simulate-call"
VENDORS = ["openai", "twilio"]
NORMAL_USERS = [f"user_{i}" for i in range(1, 20)]

def send_call(vendor: str, caller_id: str) -> bool:
    payload = json.dumps({"vendor": vendor, "callerId": caller_id}).encode("utf-8")
    req = urllib.request.Request(
        ENDPOINT,
        data=payload,
        headers={"Content-Type": "application/json"}
    )
    try:
        with urllib.request.urlopen(req, timeout=3) as resp:
            return resp.status in (200, 204)
    except Exception as e:
        print(f"[!] Error sending call: {e}")
        return False

def run_burst(count: int, caller_id: str = "burst_caller", vendor: str = "openai"):
    print(f"\n[>>] Sending burst of {count} calls (caller: {caller_id}, vendor: {vendor})...")
    success = 0
    for i in range(count):
        if send_call(vendor, caller_id):
            success += 1
        if i % 10 == 0 or i == count - 1:
            print(f"   Progress: {i+1}/{count} calls sent...", end="\r", flush=True)
        time.sleep(0.02)
    print(f"\n[OK] Burst complete: {success}/{count} calls accepted.")
    print("   Check your dashboard now! (Metrics update in ~5 seconds)\n")

def run_normal():
    print("\n[*] Running NORMAL traffic simulation...")
    print("   Calls will fluctuate naturally between 5 to 20 calls every few seconds.")
    print("   Press Ctrl+C to stop.\n")
    try:
        step = 0
        while True:
            step += 1
            batch_size = random.randint(3, 8)
            vendor = random.choice(VENDORS)
            caller = random.choice(NORMAL_USERS)
            
            for _ in range(batch_size):
                send_call(vendor, caller)
            
            print(f"[{time.strftime('%H:%M:%S')}] Step #{step:03d}: Sent {batch_size} calls ({vendor} by {caller})")
            time.sleep(random.uniform(1.0, 2.5))
    except KeyboardInterrupt:
        print("\n[STOP] Simulation stopped.")

def run_dynamic():
    print("\n[~] Running DYNAMIC realistic traffic simulation...")
    print("   Generates steady normal traffic with an ANOMALY SPIKE every ~45 seconds.")
    print("   Watch the dashboard graphs rise, trigger alerts, and return to normal!")
    print("   Press Ctrl+C to stop.\n")
    try:
        cycle = 0
        while True:
            cycle += 1
            print(f"\n--- [Cycle #{cycle}] Normal Traffic Window ---")
            start_time = time.time()
            while time.time() - start_time < 35:
                batch_size = random.randint(2, 6)
                vendor = random.choice(VENDORS)
                caller = random.choice(NORMAL_USERS)
                for _ in range(batch_size):
                    send_call(vendor, caller)
                print(f"[{time.strftime('%H:%M:%S')}] Normal: {batch_size} calls ({vendor})", end="\r", flush=True)
                time.sleep(random.uniform(1.2, 2.2))
            
            print(f"\n[{time.strftime('%H:%M:%S')}] [ALERT] SIMULATING ANOMALY SPIKE! Sending 75 calls...")
            bad_caller = f"bad_actor_{random.randint(100, 999)}"
            run_burst(75, caller_id=bad_caller, vendor="openai")
            
            print("   Waiting 15 seconds for decision engine to evaluate and alert...")
            time.sleep(15)
    except KeyboardInterrupt:
        print("\n[STOP] Dynamic simulation stopped.")

if __name__ == "__main__":
    parser = argparse.ArgumentParser(description="SaaS Cost Guardian Traffic Simulator")
    parser.add_argument("--mode", choices=["dynamic", "normal", "spike"], default="dynamic",
                        help="Simulation mode: 'dynamic' (fluctuates + periodic spikes), 'normal' (steady), 'spike' (single big surge)")
    parser.add_argument("--burst", type=int, default=None,
                        help="Send a specific number of calls immediately and exit (e.g. --burst 100)")
    args = parser.parse_args()

    # Test connectivity first
    print("[*] Testing connection to http://localhost:8080/simulate-call ...")
    if not send_call("openai", "ping_test"):
        print("[!] Could not connect to capture-and-stat service on port 8080.")
        print("   Make sure docker containers and backend services are running!")
        sys.exit(1)
    print("[OK] Connected to backend successfully!")

    if args.burst:
        run_burst(args.burst)
    elif args.mode == "spike":
        run_burst(80, caller_id="malicious_bot", vendor="openai")
    elif args.mode == "normal":
        run_normal()
    else:
        run_dynamic()
