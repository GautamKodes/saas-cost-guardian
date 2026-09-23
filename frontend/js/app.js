/* =========================================================
   SaaS Cost Guardian
   Main Application Controller
   ========================================================= */


/* =========================================================
   APPLICATION STATE
   ========================================================= */

const APP_CONFIG = {
    refreshInterval: 5000
};

let refreshTimer = null;


/* =========================================================
   LOAD DECISION DATA
   ========================================================= */

async function loadDecisionData() {

    try {

        const data =
            await fetchDecisionData();


        /* Update dashboard */

        updateDashboard(data);


        /* Update chart */

        updateUsageChart(
            Number(data.baseline) || 0,
            Number(data.calls) || 0
        );


    } catch (error) {

        console.error(
            "Failed to load Decision Engine data:",
            error
        );


        showDashboardError();

        resetUsageChart();
    }
}


/* =========================================================
   REFRESH DASHBOARD
   ========================================================= */

async function refreshDashboard() {

    const refreshButton =
        document.getElementById("refreshButton");


    if (refreshButton) {

        refreshButton.classList.add(
            "refreshing"
        );
    }


    await loadDecisionData();


    if (refreshButton) {

        refreshButton.classList.remove(
            "refreshing"
        );
    }
}


/* =========================================================
   START AUTO REFRESH
   ========================================================= */

function startAutoRefresh() {

    if (refreshTimer) {
        clearInterval(refreshTimer);
    }


    refreshTimer = setInterval(
        refreshDashboard,
        APP_CONFIG.refreshInterval
    );
}


/* =========================================================
   REFRESH BUTTON
   ========================================================= */

function setupRefreshButton() {

    const refreshButton =
        document.getElementById("refreshButton");


    if (!refreshButton) {
        return;
    }


    refreshButton.addEventListener(
        "click",
        refreshDashboard
    );
}


/* =========================================================
   NAVIGATION
   ========================================================= */

function setupNavigation() {

    const navItems =
        document.querySelectorAll(".nav-item");


    navItems.forEach(item => {

        item.addEventListener(
            "click",
            function(event) {

                event.preventDefault();


                navItems.forEach(nav => {

                    nav.classList.remove(
                        "active"
                    );
                });


                this.classList.add(
                    "active"
                );
            }
        );

    });
}


/* =========================================================
   APPLICATION INITIALIZATION
   ========================================================= */

async function initializeApplication() {

    console.log(
        "SaaS Cost Guardian initializing..."
    );


    /* Initialize chart */

    initializeUsageChart();


    /* Setup controls */

    setupRefreshButton();

    setupNavigation();


    /* Load backend data */

    await loadDecisionData();


    /* Start automatic refresh */

    startAutoRefresh();


    console.log(
        "SaaS Cost Guardian ready."
    );
}


/* =========================================================
   DOM READY
   ========================================================= */

document.addEventListener(
    "DOMContentLoaded",
    initializeApplication
);