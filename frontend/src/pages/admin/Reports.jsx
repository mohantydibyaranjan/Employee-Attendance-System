import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { getDailySummary } from '../../features/report/reportSlice';
import Loader from '../../components/Loader';

const Reports = () => {
  const dispatch = useDispatch();
  const { dailySummary, isLoading, isError, message } = useSelector(
    (state) => state.report
  );

  useEffect(() => {
    dispatch(getDailySummary());
  }, [dispatch]);

  if (isLoading) {
    return <Loader />;
  }

  if (isError) {
    return <div>Error: {message}</div>;
  }

  return (
    <div>
      <h1 className="text-3xl font-bold">Reports</h1>
      {/* Charts will go here */}
    </div>
  );
};

export default Reports;
