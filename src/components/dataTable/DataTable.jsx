// DataTable.jsx
import { DataGrid, GridToolbar } from "@mui/x-data-grid";
import "./dataTable.scss";
import { Link } from "react-router-dom";
// import { useMutation, useQueryClient } from "@tanstack/react-query";

const DataTable = (props) => {
  // Example props:
  // props.columns = array of column definitions
  // props.rows = array of data objects
  // props.slug = string used for routing (e.g., "users")

  // const queryClient = useQueryClient();
  // const mutation = useMutation({
  //   mutationFn: (id) => {
  //     return fetch(`http://localhost:8800/api/${props.slug}/${id}`, {
  //       method: "delete",
  //     });
  //   },
  //   onSuccess: () => {
  //     queryClient.invalidateQueries([`all${props.slug}`]);
  //   },
  // });

  const handleDelete = (id) => {
    // Delete item logic (API call or mutation)
    // mutation.mutate(id);
    console.log("Deleting item with id:", id);
  };

  const actionColumn = {
    field: "action",
    headerName: "Action",
    width: 200,
    renderCell: (params) => (
      <div className="action">
        <Link to={`/${props.slug}/${params.row.id}`}>
          <img src="/view.svg" alt="View" />
        </Link>
        <div className="delete" onClick={() => handleDelete(params.row.id)}>
          <img src="/delete.svg" alt="Delete" />
        </div>
      </div>
    ),
  };

  return (
    <div className="dataTable">
      <DataGrid
        className="dataGrid"
        rows={props.rows}
        columns={[...props.columns, actionColumn]}
        initialState={{
          pagination: {
            paginationModel: { pageSize: 10 },
          },
        }}
        slots={{ toolbar: GridToolbar }}
        slotProps={{
          toolbar: {
            showQuickFilter: true,
            quickFilterProps: { debounceMs: 500 },
          },
        }}
        pageSizeOptions={[5]}
        checkboxSelection
        disableRowSelectionOnClick
        disableColumnFilter
        disableDensitySelector
        disableColumnSelector
      />
    </div>
  );
};

export default DataTable;
