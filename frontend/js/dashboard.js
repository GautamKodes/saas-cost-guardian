/* =========================================================
   SaaS Cost Guardian
   Dashboard UI Controller
   ========================================================= */


/* =========================================================
   DOM ELEMENTS
   ========================================================= */

const dashboardElements = {

    currentCalls:
        document.getElementById("currentCalls"),

    baselineCalls:
        document.getElementById("baselineCalls"),

    usageVariance:
        document.getElementById("usageVariance"),

    riskLevel:
        document.getElementById("riskLevel"),

    riskIndicator:
        document.getElementById("riskIndicator"),

    riskCard:
        document.querySelector(".risk-card"),

    anomalyAlert:
        document.getElementById("anomalyAlert"),

    alertScore:
        document.getElementById("alertScore"),

    decisionBadge:
        document.getElementById("decisionBadge"),

    decisionStatus:
        document.getElementById("decisionStatus"),

    decisionCalls:
        document.getElementById("decisionCalls"),

    decisionBaseline:
        document.getElementById("decisionBaseline"),

    decisionScore:
        document.getElementById("decisionScore"),

    decisionVariance:
        document.getElementById("decisionVariance"),

    recommendationText:
        document.getElementById("recommendationText"),

    engineStatus:
        document.getElementById("engineStatus"),

    lastUpdated:
        document.getElementById("lastUpdated")
};


/* =========================================================
   UPDATE DASHBOARD
   ========================================================= */

function updateDashboard(data) {

    if (!data) {
        return;
    }

    const score = Number(data.score) || 0;
    const calls = Number(data.calls) || 0;
    const baseline = Number(data.baseline) || 0;
    const anomaly = Boolean(data.anomaly);

    const variance = calculateVariance(
        calls,
        baseline
    );


    /* ---------------------------------------------
       Main metrics
       --------------------------------------------- */

    dashboardElements.currentCalls.textContent =
        formatNumber(calls);

    dashboardElements.baselineCalls.textContent =
        formatNumber(baseline);

    dashboardElements.usageVariance.textContent =
        formatPercentage(variance);


    /* ---------------------------------------------
       Risk
       --------------------------------------------- */

    updateRiskLevel(
        score,
        anomaly
    );


    /* ---------------------------------------------
       Anomaly alert
       --------------------------------------------- */

    updateAnomalyAlert(
        score,
        anomaly
    );


    /* ---------------------------------------------
       Decision panel
       --------------------------------------------- */

    updateDecisionPanel(
        score,
        calls,
        baseline,
        variance,
        anomaly
    );


    /* ---------------------------------------------
       Engine status
       --------------------------------------------- */

    dashboardElements.engineStatus.textContent =
        "Connected";

    dashboardElements.engineStatus.style.color =
        "var(--success)";


    /* ---------------------------------------------
       Last updated
       --------------------------------------------- */

    dashboardElements.lastUpdated.textContent =
        `Updated ${getCurrentTime()}`;
}


/* =========================================================
   CALCULATE VARIANCE
   ========================================================= */

function calculateVariance(
    current,
    baseline
) {

    if (!baseline || baseline === 0) {
        return 0;
    }

    return (
        ((current - baseline) / baseline) * 100
    );
}


/* =========================================================
   FORMATTERS
   ========================================================= */

function formatNumber(value) {

    return new Intl.NumberFormat(
        "en-IN"
    ).format(value);
}


function formatPercentage(value) {

    const rounded =
        Math.round(value * 10) / 10;

    if (rounded > 0) {
        return `+${rounded}%`;
    }

    return `${rounded}%`;
}


/* =========================================================
   RISK LEVEL
   ========================================================= */

function updateRiskLevel(
    score,
    anomaly
) {

    let risk = "Low";
    let riskClass = "low";


    if (anomaly || score >= 0.7) {

        risk = "High";
        riskClass = "high";

    } else if (score >= 0.4) {

        risk = "Medium";
        riskClass = "medium";
    }


    dashboardElements.riskLevel.textContent =
        risk;

    dashboardElements.riskCard.classList.remove(
        "low",
        "medium",
        "high"
    );

    dashboardElements.riskCard.classList.add(
        riskClass
    );
}


/* =========================================================
   ANOMALY ALERT
   ========================================================= */

function updateAnomalyAlert(
    score,
    anomaly
) {

    if (anomaly) {

        dashboardElements.anomalyAlert
            .classList.remove("hidden");

        dashboardElements.alertScore.textContent =
            score.toFixed(2);

    } else {

        dashboardElements.anomalyAlert
            .classList.add("hidden");
    }
}


/* =========================================================
   DECISION PANEL
   ========================================================= */

function updateDecisionPanel(
    score,
    calls,
    baseline,
    variance,
    anomaly
) {

    dashboardElements.decisionCalls.textContent =
        formatNumber(calls);

    dashboardElements.decisionBaseline.textContent =
        formatNumber(baseline);

    dashboardElements.decisionScore.textContent =
        score.toFixed(2);

    dashboardElements.decisionVariance.textContent =
        formatPercentage(variance);


    const decisionPanel =
        document.querySelector(".decision-panel");


    if (anomaly) {

        /* -----------------------------------------
           Anomaly state
           ----------------------------------------- */

        decisionPanel.classList.remove(
            "normal"
        );

        decisionPanel.classList.add(
            "anomaly"
        );


        dashboardElements.decisionBadge.textContent =
            "ACTION REQUIRED";

        dashboardElements.decisionBadge.classList.remove(
            "success"
        );

        dashboardElements.decisionBadge.classList.add(
            "danger"
        );


        dashboardElements.decisionStatus.innerHTML = `

            <div class="decision-icon">
                !
            </div>

            <div>
                <strong>Usage anomaly detected</strong>

                <span>
                    API activity is above the expected baseline.
                </span>
            </div>

        `;


        dashboardElements.recommendationText.textContent =
            "Review API usage and investigate the source of the increased request volume.";

    } else {

        /* -----------------------------------------
           Normal state
           ----------------------------------------- */

        decisionPanel.classList.remove(
            "anomaly"
        );

        decisionPanel.classList.add(
            "normal"
        );


        dashboardElements.decisionBadge.textContent =
            "MONITORING";

        dashboardElements.decisionBadge.classList.remove(
            "danger"
        );

        dashboardElements.decisionBadge.classList.add(
            "success"
        );


        dashboardElements.decisionStatus.innerHTML = `

            <div class="decision-icon">
                ✓
            </div>

            <div>
                <strong>Usage within expected range</strong>

                <span>
                    No significant anomaly detected.
                </span>
            </div>

        `;


        dashboardElements.recommendationText.textContent =
            "No immediate action required. Continue monitoring API usage.";
    }
}


/* =========================================================
   ENGINE ERROR STATE
   ========================================================= */

function showDashboardError() {

    dashboardElements.engineStatus.textContent =
        "Disconnected";

    dashboardElements.engineStatus.style.color =
        "var(--danger)";


    dashboardElements.currentCalls.textContent =
        "--";

    dashboardElements.baselineCalls.textContent =
        "--";

    dashboardElements.usageVariance.textContent =
        "--";

    dashboardElements.riskLevel.textContent =
        "Offline";


    dashboardElements.riskCard.classList.remove(
        "low",
        "medium",
        "high"
    );


    dashboardElements.decisionBadge.textContent =
        "OFFLINE";

    dashboardElements.decisionBadge.classList.remove(
        "danger",
        "success"
    );


    dashboardElements.decisionStatus.innerHTML = `

        <div class="decision-icon">
            ×
        </div>

        <div>
            <strong>Decision Engine unavailable</strong>

            <span>
                Unable to retrieve the latest system analysis.
            </span>
        </div>

    `;


    dashboardElements.recommendationText.textContent =
        "Start the Decision Engine and refresh the dashboard.";


    dashboardElements.lastUpdated.textContent =
        "Connection unavailable";
}


/* =========================================================
   CURRENT TIME
   ========================================================= */

function getCurrentTime() {

    return new Date().toLocaleTimeString(
        "en-IN",
        {
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        }
    );
}