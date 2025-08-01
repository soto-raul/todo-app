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
    <section data-testid="paginator-component">
      <div className="paginator" data-testid="paginator">
        {/* go to first page */}
        <div
          className="paginator-item"
          onClick={() => {
            if (!pagination.first) {
              onPageChange(0);
            }
          }}
          data-testid="go-to-first"
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
          data-testid="go-to-prev"
        >
          &#8249;
        </div>

        {/* show current page */}
        <div className="paginator-item page-num" data-testid="page-number">
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
          data-testid="go-to-next"
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
          data-testid="go-to-last"
        >
          »
        </div>
      </div>
    </section>
  );
};

export default Paginator;
