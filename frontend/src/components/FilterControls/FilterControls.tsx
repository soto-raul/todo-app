import { useState } from "react";
import {
  type FilterCriteria,
  type Priority,
  type Status,
} from "../../shared/types";
import { isNameValid } from "../../shared/validators";
import "./filterControls.css";

const FilterControls = ({
  setFilters,
}: {
  setFilters: React.Dispatch<React.SetStateAction<FilterCriteria>>;
}) => {
  // inputs' state
  const [nameFilter, setNameFilter] = useState("");
  const [priorityFilter, setPriorityFilter] = useState("ALL");
  const [doneStatusFilter, setDoneStatusFilter] = useState("ALL");

  // handle search btn click
  const handleSearchClick = (e: React.MouseEvent<HTMLButtonElement>) => {
    e.preventDefault();
    setFilters({
      name: !isNameValid(nameFilter) ? null : nameFilter,
      priority: priorityFilter === "ALL" ? null : (priorityFilter as Priority),
      doneStatus:
        doneStatusFilter === "ALL" ? null : (doneStatusFilter as Status),
    });
  };
  return (
    <section>
      <div className="main-controls-container">
        <div className="control">
          <label htmlFor="name">Name</label>
          <input
            type="text"
            name="name"
            id="name"
            placeholder="Enter To Do task name..."
            maxLength={120}
            onChange={(e) => setNameFilter(e.target.value)}
          />
        </div>
        <div className="control">
          <label htmlFor="priority">Priority</label>
          <select
            name="priority"
            id="priority"
            onChange={(e) => setPriorityFilter(e.target.value)}
          >
            <option value="ALL">All</option>
            <option value="HIGH">High</option>
            <option value="MEDIUM">Medium</option>
            <option value="LOW">Low</option>
          </select>
        </div>
        <div className="control">
          <label htmlFor="status">Status</label>
          <select
            name="status"
            id="status"
            onChange={(e) => setDoneStatusFilter(e.target.value)}
          >
            <option value="ALL">All</option>
            <option value="DONE">Done</option>
            <option value="NOT_DONE">Not Done</option>
          </select>
        </div>
        <button type="submit" onClick={handleSearchClick}>
          Search
        </button>
      </div>
    </section>
  );
};

export default FilterControls;
