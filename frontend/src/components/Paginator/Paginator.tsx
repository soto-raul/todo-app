import type { PaginationData } from "../../shared/types";
import "./Paginator.css";

const Paginator = ({
  pagination,
  onPageChange,
}: {
  pagination: PaginationData;
  onPageChange: (targetPage: number) => void;
}) => {
  return (
    <section>
      <div className="paginator">
        {/* go to first page */}
        <div
          className="paginator-item"
          onClick={() => {
            if (!pagination.first) {
              onPageChange(0);
            }
          }}
        >
          «
        </div>

        {/* go to previous page*/}
        <div
          className="paginator-item"
          onClick={() => {
            if (!pagination.first) {
              onPageChange(pagination.currentPage - 1);
            }
          }}
        >
          &#8249;
        </div>

        {/* show current page */}
        <div className="paginator-item page-num">
          {pagination.currentPage + 1}
        </div>

        {/* go to next page */}
        <div
          className="paginator-item"
          onClick={() => {
            if (!pagination.last) {
              onPageChange(pagination.currentPage + 1);
            }
          }}
        >
          &#8250;
        </div>

        {/* go to last page */}
        <div
          className="paginator-item"
          onClick={() => {
            if (!pagination.last) {
              onPageChange(pagination.totalPages - 1);
            }
          }}
        >
          »
        </div>
      </div>
    </section>
  );
};

export default Paginator;
