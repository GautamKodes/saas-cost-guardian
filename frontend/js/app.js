/* =========================================
   SaaS Cost Guardian
   Frontend Application
   ========================================= */

"use strict";


/* =========================================
   Configuration
   ========================================= */

const API_BASE_URL = "http://localhost:8080";

const API_ENDPOINTS = {
    decision: "/decision"
};


/* =========================================
   Global Variables
   ========================================= */

let costChart = null;
let isLoading = false;


/* =========================================
   DOM Elements
   ========================================= */

const elements = {
    currentCost: document.getElementById("currentCost"),
    projectedCost: document.getElementById("projectedCost"),

    anomalyScore: document.getElementById("anomalyScore"),
    anomalyStatus: document.getElementById("anomalyStatus"),

    riskLevel: document.getElementById("riskLevel"),

    decisionStatus: document.getElementById("decisionStatus"),
    recommendation: document.getElementById("recommendation"),
    confidence: document.getElementById("confidence"),
    estimatedSavings: document.getElementById("estimatedSavings"),

    servicesTable: document.getElementById("servicesTable"),

    alertBox: document.getElementById("alertBox"),
    alertTitle: document.getElementById("alertTitle"),
    alertMessage: document.getElementById("alertMessage"),

    lastUpdated: document.getElementById("lastUpdated"),

    refreshBtn: document.getElementById("refreshBtn"),

    timeRange: document.getElementById("timeRange"),

    costChart: document.getElementById("costChart")
};


/* =========================================
   Utility Functions
   ========================================= */

function formatNumber(value, decimals = 2) {

    const number = Number(value);

    if (!Number.isFinite(number)) {
        return "0";
    }

    return number.toFixed(decimals);
}


function updateTimestamp() {

    const now = new Date();

    elements.lastUpdated.textContent =
        now.toLocaleTimeString([], {
            hour: "2-digit",
            minute: "2-digit",
            second: "2-digit"
        });
}


/* =========================================
   Decision Engine API
   ========================================= */

async function fetchDecision() {

    const response = await fetch(
        `${API_BASE_URL}${API_ENDPOINTS.decision}`
    );

    if (!response.ok) {

        throw new Error(
            `Decision Engine returned HTTP ${response.status}`
        );
    }

    return await response.json();
}


/* =========================================
   Dashboard Update
   ========================================= */

function updateDashboard(data) {

    console.log("Decision Engine response:", data);


    /* -----------------------------------------
       Read actual Decision Engine fields
       ----------------------------------------- */

    const score = Number(data.score) || 0;

    const calls = Number(data.calls) || 0;

    const baseline = Number(data.baseline) || 0;

    const anomaly = Boolean(data.anomaly);


    /* -----------------------------------------
       API Calls
       ----------------------------------------- */

    elements.currentCost.textContent =
        formatNumber(calls, 0);

    elements.projectedCost.textContent =
        formatNumber(baseline, 0);


    /* -----------------------------------------
       Anomaly Score
       ----------------------------------------- */

    elements.anomalyScore.textContent =
        formatNumber(score, 2);


    /* -----------------------------------------
       Anomaly Status
       ----------------------------------------- */

    updateAnomalyStatus(score, anomaly);


    /* -----------------------------------------
       Risk Level
       ----------------------------------------- */

    updateRiskLevel(score, anomaly);


    /* -----------------------------------------
       Decision Engine
       ----------------------------------------- */

    updateDecisionPanel(
        score,
        calls,
        baseline,
        anomaly
    );


    /* -----------------------------------------
       Chart
       ----------------------------------------- */

    updateChart(calls, baseline);


    /* -----------------------------------------
       Services
       ----------------------------------------- */

    updateServicesTable();


    /* -----------------------------------------
       Timestamp
       ----------------------------------------- */

    updateTimestamp();
}


/* =========================================
   Anomaly Status
   ========================================= */

function updateAnomalyStatus(score, anomaly) {

    if (anomaly) {

        elements.anomalyStatus.textContent =
            "Anomaly Detected";

        elements.anomalyStatus.style.background =
            "#fee2e2";

        elements.anomalyStatus.style.color =
            "#dc2626";

        showAlert(
            "Anomaly Detected",
            `Unusual API usage detected. Current score: ${formatNumber(score, 2)}`
        );

    } else {

        elements.anomalyStatus.textContent =
            "Normal";

        elements.anomalyStatus.style.background =
            "#dcfce7";

        elements.anomalyStatus.style.color =
            "#16a34a";

        hideAlert();
    }
}


/* =========================================
   Risk Level
   ========================================= */

function updateRiskLevel(score, anomaly) {

    let risk = "Low";

    if (anomaly || score >= 0.7) {

        risk = "High";

    } else if (score >= 0.4) {

        risk = "Medium";
    }

    elements.riskLevel.textContent = risk;
}


/* =========================================
   Decision Panel
   ========================================= */

function updateDecisionPanel(
    score,
    calls,
    baseline,
    anomaly
) {

    if (anomaly) {

        elements.decisionStatus.textContent =
            "Action Required";

        elements.recommendation.textContent =
            "Review API usage";

    } else {

        elements.decisionStatus.textContent =
            "Monitoring";

        elements.recommendation.textContent =
            "No action required";
    }


    /*
     * The current Decision Engine API does not
     * provide a separate confidence value.
     *
     * We therefore display the anomaly score
     * instead of inventing a confidence value.
     */

    elements.confidence.textContent =
        formatNumber(score, 2);


    /*
     * The current API does not provide savings
     * information.
     */

    elements.estimatedSavings.textContent =
        calls;
}


/* =========================================
   Alert
   ========================================= */

function showAlert(title, message) {

    elements.alertTitle.textContent = title;

    elements.alertMessage.textContent = message;

    elements.alertBox.classList.remove("hidden");
}


function hideAlert() {

    elements.alertBox.classList.add("hidden");
}


/* =========================================
   Services Table
   ========================================= */

function updateServicesTable() {

    elements.servicesTable.innerHTML = `
        <tr>
            <td colspan="5" class="empty-state">
                Service-level cost data is not available
                from the current Decision Engine API.
            </td>
        </tr>
    `;
}


/* =========================================
   Chart Creation
   ========================================= */

function createChart() {

    if (!elements.costChart) {
        return;
    }


    const context =
        elements.costChart.getContext("2d");


    costChart = new Chart(context, {

        type: "line",

        data: {

            labels: [
                "Baseline",
                "Current"
            ],

            datasets: [

                {
                    label: "API Calls",

                    data: [0, 0],

                    borderWidth: 3,

                    tension: 0.35,

                    fill: true,

                    backgroundColor:
                        "rgba(79, 70, 229, 0.08)",

                    borderColor:
                        "#4f46e5",

                    pointRadius: 5,

                    pointHoverRadius: 7
                }

            ]
        },


        options: {

            responsive: true,

            maintainAspectRatio: false,

            interaction: {
                intersect: false,
                mode: "index"
            },

            plugins: {

                legend: {
                    display: false
                }

            },

            scales: {

                x: {

                    grid: {
                        display: false
                    },

                    ticks: {
                        color: "#9ca3af"
                    }

                },

                y: {

                    beginAtZero: true,

                    ticks: {
                        color: "#9ca3af"
                    },

                    grid: {
                        color: "#f1f5f9"
                    }

                }

            }

        }

    });
}


/* =========================================
   Update Chart
   ========================================= */

function updateChart(calls, baseline) {

    if (!costChart) {
        return;
    }


    costChart.data.labels = [
        "Baseline",
        "Current"
    ];


    costChart.data.datasets[0].data = [
        baseline,
        calls
    ];


    costChart.update();
}


/* =========================================
   Load Dashboard
   ========================================= */

async function loadDashboard() {

    if (isLoading) {
        return;
    }


    isLoading = true;


    try {

        const data = await fetchDecision();

        updateDashboard(data);

        console.log(
            "SaaS Cost Guardian: Dashboard updated successfully."
        );

    } catch (error) {

        console.error(
            "Decision Engine connection failed:",
            error
        );


        elements.decisionStatus.textContent =
            "Backend Offline";


        elements.recommendation.textContent =
            "Unable to connect";


        elements.confidence.textContent =
            "--";


        elements.estimatedSavings.textContent =
            calls;


        elements.servicesTable.innerHTML = `
            <tr>
                <td colspan="5" class="empty-state">
                    Unable to connect to the Decision Engine.
                </td>
            </tr>
        `;

    } finally {

        isLoading = false;
    }
}


/* =========================================
   Refresh Button
   ========================================= */

elements.refreshBtn.addEventListener(
    "click",
    async () => {

        elements.refreshBtn.textContent =
            "↻ Loading...";

        await loadDashboard();

        elements.refreshBtn.textContent =
            "↻ Refresh";
    }
);


/* =========================================
   Time Range
   ========================================= */

elements.timeRange.addEventListener(
    "change",
    () => {

        console.log(
            "Selected range:",
            elements.timeRange.value
        );

        /*
         * Historical data endpoint is not yet
         * provided by the Decision Engine.
         *
         * For now the chart displays:
         * Baseline vs Current API calls.
         */
    }
);


/* =========================================
   Application Start
   ========================================= */

document.addEventListener(
    "DOMContentLoaded",
    () => {

        createChart();

        loadDashboard();


        /*
         * Automatically refresh every 5 seconds.
         */

        setInterval(
            loadDashboard,
            5000
        );

    }
);