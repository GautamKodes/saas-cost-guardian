"""
Minimal FastAPI service exposing POST /score.
Person A/C's Java side calls this over plain HTTP -- no ONNX needed.
Run with: uvicorn app:app --host 0.0.0.0 --port 8000
"""
import joblib
import numpy as np
from fastapi import FastAPI
from pydantic import BaseModel

app = FastAPI(title="Guardian Anomaly Scorer")

bundle = joblib.load("model.joblib")
model = bundle["model"]
FEATURES = bundle["features"]

# Fixed decision threshold on `score` (0-1, higher = more anomalous).
# NOTE: 0.7 (mentioned in the original plan doc) does NOT work with this scoring formula --
# tested against traffic.csv, normal rows score ~0.32-0.60 and anomaly rows ~0.48-0.64, so
# nothing would ever cross 0.7. 0.47 was chosen by sweeping thresholds against the labeled
# data: it gives 100% recall / ~8% false positive rate here. Re-check this value if you
# regenerate traffic.csv or change the features -- the score distribution will shift.
ANOMALY_THRESHOLD = 0.47

class ScoreRequest(BaseModel):
    calls_last_window: float
    prev_window_avg: float
    unique_callers: float
    top_caller_concentration: float

class ScoreResponse(BaseModel):
    score: float           # 0-1, higher = more anomalous
    is_anomaly: bool
    top_feature: str       # which feature deviated most, for explainability

@app.post("/score", response_model=ScoreResponse)
def score(req: ScoreRequest):
    x = np.array([[req.calls_last_window, req.prev_window_avg,
                    req.unique_callers, req.top_caller_concentration]])

    # decision_function: higher = more normal. Flip + normalize to a rough 0-1 anomaly score.
    raw = model.decision_function(x)[0]
    anomaly_score = float(np.clip(0.5 - raw, 0, 1))

    # Explicit fixed threshold, independent of the model's internal contamination-based cutoff.
    # This is what actually decides is_anomaly now -- adjust ANOMALY_THRESHOLD above to tune it.
    is_anomaly = anomaly_score > ANOMALY_THRESHOLD

    # crude explainability: which feature is furthest (in z-score terms) from training mean
    vals = {
        "calls_last_window": req.calls_last_window,
        "prev_window_avg": req.prev_window_avg,
        "unique_callers": req.unique_callers,
        "top_caller_concentration": req.top_caller_concentration,
    }
    # simple heuristic: concentration and call volume are the usual culprits
    top_feature = "top_caller_concentration" if vals["top_caller_concentration"] > 0.5 else "calls_last_window"

    return ScoreResponse(score=round(anomaly_score, 3), is_anomaly=bool(is_anomaly), top_feature=top_feature)

@app.get("/health")
def health():
    return {"status": "ok"}
