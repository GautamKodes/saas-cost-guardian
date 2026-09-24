import joblib
import numpy as np
import pandas as pd
from sklearn.ensemble import IsolationForest
from generate_data import build_dataset

def train():
    df = build_dataset()
    df.to_csv("traffic.csv", index=False)
    print(f"Wrote traffic.csv with {len(df)} rows ({df['is_anomaly'].sum()} labeled anomaly minutes)")

    df["prev_window_avg"] = df["calls"].shift(1).rolling(window=5, min_periods=1).mean().fillna(df["calls"])
    df["calls_last_window"] = df["calls"]
    df["top_caller_concentration"] = df["top_caller_share"]

    feature_cols = ["calls_last_window", "prev_window_avg", "unique_callers", "top_caller_concentration"]
    X = df[feature_cols].values

    model = IsolationForest(n_estimators=100, contamination=0.1, random_state=42)
    model.fit(X)

    bundle = {
        "model": model,
        "features": feature_cols
    }
    joblib.dump(bundle, "model.joblib")
    print("Saved model and feature definitions to model.joblib successfully!")

if __name__ == "__main__":
    train()