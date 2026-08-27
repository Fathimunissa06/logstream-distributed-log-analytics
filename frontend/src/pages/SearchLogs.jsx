import { useMemo, useState } from "react";
import { Filter, Search } from "lucide-react";

import SearchBar from "../components/logs/SearchBar";
import LogFilters from "../components/logs/LogFilters";
import LogTable from "../components/logs/LogTable";
import { mockLogs } from "../data/mockLogs";

function SearchLogs() {
  const [searchQuery, setSearchQuery] = useState("");
  const [level, setLevel] = useState("ALL");
  const [service, setService] = useState("ALL");
  const [host, setHost] = useState("ALL");

  const services = useMemo(
    () => [...new Set(mockLogs.map((log) => log.service))],
    []
  );

  const hosts = useMemo(
    () => [...new Set(mockLogs.map((log) => log.host))],
    []
  );

  const filteredLogs = useMemo(() => {
    const query = searchQuery.trim().toLowerCase();

    return mockLogs.filter((log) => {
      const matchesSearch =
        !query ||
        log.message.toLowerCase().includes(query) ||
        log.service.toLowerCase().includes(query) ||
        log.host.toLowerCase().includes(query) ||
        log.level.toLowerCase().includes(query);

      const matchesLevel =
        level === "ALL" || log.level === level;

      const matchesService =
        service === "ALL" || log.service === service;

      const matchesHost =
        host === "ALL" || log.host === host;

      return (
        matchesSearch &&
        matchesLevel &&
        matchesService &&
        matchesHost
      );
    });
  }, [searchQuery, level, service, host]);

  const resetFilters = () => {
    setSearchQuery("");
    setLevel("ALL");
    setService("ALL");
    setHost("ALL");
  };

  const hasActiveFilters =
    searchQuery ||
    level !== "ALL" ||
    service !== "ALL" ||
    host !== "ALL";

  return (
    <div className="search-page">
      <div className="page-heading">
        <div>
          <span className="page-eyebrow">LOG EXPLORER</span>

          <h1>Search Logs</h1>

          <p>
            Search and filter logs across distributed services.
          </p>
        </div>
      </div>

      <section className="search-panel">
        <SearchBar
          value={searchQuery}
          onChange={setSearchQuery}
          onSearch={() => {}}
        />

        <div className="filter-heading">
          <div>
            <Filter size={15} />
            <span>Filters</span>
          </div>

          {hasActiveFilters && (
            <button
              type="button"
              className="reset-button"
              onClick={resetFilters}
            >
              Reset filters
            </button>
          )}
        </div>

        <LogFilters
          level={level}
          service={service}
          host={host}
          onLevelChange={setLevel}
          onServiceChange={setService}
          onHostChange={setHost}
          services={services}
          hosts={hosts}
        />
      </section>

      <section className="results-section">
        <div className="results-header">
          <div>
            <h2>Log Results</h2>

            <p>
              <Search size={13} />
              {filteredLogs.length}{" "}
              {filteredLogs.length === 1 ? "result" : "results"} found
            </p>
          </div>
        </div>

        <LogTable logs={filteredLogs} />
      </section>
    </div>
  );
}

export default SearchLogs;