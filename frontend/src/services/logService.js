import { apiRequest } from "./api";

export async function searchLogs(filters) {
  const params = new URLSearchParams();

  if (filters.search?.trim()) {
    params.append("q", filters.search);
  }

  if (
    filters.level &&
    filters.level !== "ALL"
  ) {
    params.append("level", filters.level);
  }

  if (
    filters.service &&
    filters.service !== "ALL"
  ) {
    params.append("service", filters.service);
  }

  return apiRequest(
    `/api/search?${params.toString()}`
  );
}