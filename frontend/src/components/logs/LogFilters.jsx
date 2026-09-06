function LogFilters({
  level,
  service,
  host,
  onLevelChange,
  onServiceChange,
  onHostChange,
  services,
  hosts,
}) {
  return (
    <div className="filters-row">
      <div className="filter-group">
        <label htmlFor="level-filter">Level</label>

        <select
          id="level-filter"
          value={level}
          onChange={(event) => onLevelChange(event.target.value)}
        >
          <option value="ALL">All Levels</option>
          <option value="INFO">Info</option>
          <option value="WARN">Warning</option>
          <option value="ERROR">Error</option>
        </select>
      </div>

      <div className="filter-group">
        <label htmlFor="service-filter">Service</label>

        <select
          id="service-filter"
          value={service}
          onChange={(event) => onServiceChange(event.target.value)}
        >
          <option value="ALL">All Services</option>

          {services.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </select>
      </div>

      <div className="filter-group">
        <label htmlFor="host-filter">Host</label>

        <select
          id="host-filter"
          value={host}
          onChange={(event) => onHostChange(event.target.value)}
        >
          <option value="ALL">All Hosts</option>

          {hosts.map((item) => (
            <option key={item} value={item}>
              {item}
            </option>
          ))}
        </select>
      </div>
    </div>
  );
}

export default LogFilters;