import { apiRequest } from "./api";

export async function getAlerts() {
  return apiRequest("/api/alerts");
}

export async function createAlert(alert) {
  return apiRequest("/api/alerts", {
    method: "POST",
    body: JSON.stringify(alert),
  });
}

export async function updateAlert(id, alert) {
  return apiRequest(`/api/alerts/${id}`, {
    method: "PUT",
    body: JSON.stringify(alert),
  });
}

export async function deleteAlert(id) {
  return apiRequest(`/api/alerts/${id}`, {
    method: "DELETE",
  });
}