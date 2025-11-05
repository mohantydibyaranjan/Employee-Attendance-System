import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { getEmployees } from '../../features/employee/employeeSlice';
import Loader from '../../components/Loader';

const EmployeeList = () => {
  const dispatch = useDispatch();
  const { employees, isLoading, isError, message } = useSelector(
    (state) => state.employee
  );

  useEffect(() => {
    dispatch(getEmployees());
  }, [dispatch]);

  if (isLoading) {
    return <Loader />;
  }

  if (isError) {
    return <div>Error: {message}</div>;
  }

  return (
    <div>
      <h1 className="text-3xl font-bold">Employee List</h1>
      {/* DataTable will go here */}
    </div>
  );
};

export default EmployeeList;
