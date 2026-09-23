/* =========================================================
   SaaS Cost Guardian
   Chart Controller
   ========================================================= */

let usageChart = null;


/* =========================================================
   INITIALIZE CHART
   ========================================================= */

function initializeUsageChart() {

    const canvas =
        document.getElementById("usageChart");

    if (!canvas) {
        return;
    }

    const context =
        canvas.getContext("2d");


    usageChart = new Chart(
        context,
        {
            type: "line",

            data: {
                labels: [
                    "Baseline",
                    "Current"
                ],

                datasets: [
                    {
                        label: "API Calls",

                        data: [
                            0,
                            0
                        ],

                        borderColor: "#7c5cff",

                        backgroundColor:
                            "rgba(124, 92, 255, 0.10)",

                        borderWidth: 2,

                        pointBackgroundColor:
                            "#7c5cff",

                        pointBorderColor:
                            "#11161e",

                        pointBorderWidth: 3,

                        pointRadius: 5,

                        pointHoverRadius: 7,

                        tension: 0.35,

                        fill: true
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
                    },

                    tooltip: {
                        backgroundColor:
                            "#151b24",

                        borderColor:
                            "#2b3441",

                        borderWidth: 1,

                        titleColor:
                            "#f2f5f8",

                        bodyColor:
                            "#9aa5b3",

                        padding: 10,

                        displayColors: false,

                        callbacks: {

                            label: function(context) {

                                return (
                                    " API Calls: " +
                                    context.parsed.y
                                );
                            }
                        }
                    }
                },

                scales: {

                    x: {

                        grid: {
                            display: false
                        },

                        ticks: {
                            color: "#626d7a",

                            font: {
                                size: 9
                            }
                        },

                        border: {
                            display: false
                        }
                    },

                    y: {

                        beginAtZero: true,

                        grid: {
                            color:
                                "rgba(255,255,255,0.045)"
                        },

                        ticks: {

                            color: "#626d7a",

                            font: {
                                size: 9
                            },

                            padding: 8
                        },

                        border: {
                            display: false
                        }
                    }
                }
            }
        }
    );
}


/* =========================================================
   UPDATE CHART
   ========================================================= */

function updateUsageChart(
    baseline,
    current
) {

    if (!usageChart) {
        return;
    }

    usageChart.data.datasets[0].data = [
        baseline,
        current
    ];

    usageChart.update("none");
}


/* =========================================================
   RESET CHART
   ========================================================= */

function resetUsageChart() {

    if (!usageChart) {
        return;
    }

    usageChart.data.datasets[0].data = [
        0,
        0
    ];

    usageChart.update("none");
}