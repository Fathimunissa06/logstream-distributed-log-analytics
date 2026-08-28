import { mockLogs } from "../data/mockLogs";
// import { apiRequest } from "./api";

export async function searchLogs(filters) {
  const query = filters.search?.trim().toLowerCase() || "";

  const filtered = mockLogs.filter((log) => {
    const matchesSearch =
      !query ||
      log.message.toLowerCase().includes(query) ||
      log.service.toLowerCase().includes(query) ||
      log.host.toLowerCase().includes(query) ||
      log.level.toLowerCase().includes(query);

    const matchesLevel =
      filters.level === "ALL" || log.level === filters.level;

    const matchesService =
      filters.service === "ALL" || log.service === filters.service;

    const matchesHost =
      filters.host === "ALL" || log.host === filters.host;

    return (
      matchesSearch &&
      matchesLevel &&
      matchesService &&
      matchesHost
    );
  });

  return Promise.resolve(filtered);
}