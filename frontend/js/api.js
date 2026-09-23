/* =========================================================
   SaaS Cost Guardian
   API Layer
   ========================================================= */

const API_BASE_URL = "http://localhost:8080";

const API_ENDPOINTS = {
    decision: "/decision"
};


/**
 * Fetch decision data from the Spring Boot Decision Engine.
 */
async function fetchDecisionData() {

    const response = await fetch(
        `${API_BASE_URL}${API_ENDPOINTS.decision}`,
        {
            method: "GET",
            headers: {
                "Accept": "application/json"
            }
        }
    );

    if (!response.ok) {
        throw new Error(
            `Decision Engine returned HTTP ${response.status}`
        );
    }

    return await response.json();
}


/**
 * Check whether the Decision Engine is reachable.
 */
async function checkEngineConnection() {

    try {

        await fetchDecisionData();

        return true;

    } catch (error) {

        console.error(
            "Decision Engine connection failed:",
            error
        );

        return false;
    }
}