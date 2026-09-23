"""
Generates ~3 hours of simulated API call traffic in 1-minute windows.
Produces: baseline (normal, noisy) -> spike segment -> concentrated-caller anomaly segment.
Output: traffic.csv with columns [minute, calls, unique_callers, top_caller_share, is_anomaly]
"""
import numpy as np
import pandas as pd

np.random.seed(42)

def gen_baseline(n_minutes, base_calls=20, base_callers=8):
    rows = []
    for m in range(n_minutes):
        calls = max(0, int(np.random.normal(base_calls, 3)))
        unique_callers = max(1, int(np.random.normal(base_callers, 1.5)))
        # normal traffic: calls spread fairly evenly across callers
        top_caller_share = np.clip(np.random.normal(0.15, 0.05), 0.05, 0.4)
        rows.append([calls, unique_callers, top_caller_share, 0])
    return rows

def gen_spike(n_minutes, base_calls=20):
    rows = []
    for m in range(n_minutes):
        # sharp volume spike, callers roughly normal (broad traffic surge)
        calls = int(base_calls * np.random.uniform(4, 7))
        unique_callers = max(1, int(np.random.normal(10, 2)))
        top_caller_share = np.clip(np.random.normal(0.15, 0.05), 0.05, 0.4)
        rows.append([calls, unique_callers, top_caller_share, 1])
    return rows

def gen_concentrated_caller_anomaly(n_minutes, base_calls=20):
    rows = []
    for m in range(n_minutes):
        # volume looks near-normal, but one caller dominates (e.g. a runaway loop / abuse)
        calls = max(0, int(np.random.normal(base_calls * 1.3, 4)))
        unique_callers = max(1, int(np.random.normal(4, 1)))  # fewer distinct callers
        top_caller_share = np.clip(np.random.normal(0.75, 0.08), 0.5, 0.95)
        rows.append([calls, unique_callers, top_caller_share, 1])
    return rows

def gen_extreme_anomaly(n_minutes, base_calls=20):
    rows = []
    for m in range(n_minutes):
        # catastrophic case: massive volume, essentially one caller, near-total concentration
        # (e.g. a runaway retry loop or an active abuse/attack scenario, not just a busy period)
        calls = int(base_calls * np.random.uniform(15, 30))
        unique_callers = 1
        top_caller_share = np.clip(np.random.normal(0.98, 0.015), 0.9, 1.0)
        rows.append([calls, unique_callers, top_caller_share, 1])
    return rows

def build_dataset():
    rows = []
    rows += gen_baseline(90)                       # 90 min normal
    rows += gen_spike(15)                           # 15 min spike
    rows += gen_baseline(30)                        # back to normal
    rows += gen_concentrated_caller_anomaly(20)      # 20 min concentrated-caller anomaly
    rows += gen_baseline(20)                        # back to normal
    rows += gen_extreme_anomaly(15)                  # 15 min catastrophic anomaly
    rows += gen_baseline(15)                        # tail normal

    df = pd.DataFrame(rows, columns=["calls", "unique_callers", "top_caller_share", "is_anomaly"])
    df.insert(0, "minute", range(len(df)))
    return df

if __name__ == "__main__":
    df = build_dataset()
    df.to_csv("traffic.csv", index=False)
    print(f"Wrote traffic.csv with {len(df)} rows ({df['is_anomaly'].sum()} labeled anomaly minutes)")
    print(df.head())