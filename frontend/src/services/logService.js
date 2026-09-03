import { apiRequest } from "./api";

export async function searchLogs(filters = {}) {
  const params = new URLSearchParams();

  const query = filters.search?.trim();

  if (query) {
    params.set("q", query);
  }

  if (filters.service && filters.service !== "ALL") {
    params.set("service", filters.service);
  }

  if (filters.level && filters.level !== "ALL") {
    params.set("level", filters.level);
  }

  const queryString = params.toString();

  return apiRequest(
    `/api/search${queryString ? `?${queryString}` : ""}`
  );
}