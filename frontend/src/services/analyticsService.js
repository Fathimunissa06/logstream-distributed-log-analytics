import { apiRequest } from "./api";

export async function getLogLevelAnalytics() {
  return apiRequest("/api/analytics/levels");
}

export async function getServiceAnalytics() {
  return apiRequest("/api/analytics/services");
}