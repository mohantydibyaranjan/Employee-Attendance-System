import { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { checkIn, checkOut, getTodayAttendance } from '../../features/attendance/attendanceSlice';
import Loader from '../../components/Loader';

const EmployeeDashboard = () => {
  const dispatch = useDispatch();
  const { todayAttendance, isLoading, isError, message } = useSelector(
    (state) => state.attendance
  );
  const { user } = useSelector((state) => state.auth);

  useEffect(() => {
    if (user) {
      dispatch(getTodayAttendance(user.id));
    }
  }, [dispatch, user]);

  if (isLoading) {
    return <Loader />;
  }

  return (
    <div>
      <h1 className="text-3xl font-bold">Employee Dashboard</h1>
      <div className="mt-8">
        {isError && <div className="text-red-500">{message}</div>}
        {todayAttendance ? (
          <div>
            <p>Checked in at: {todayAttendance.checkInTime}</p>
            {todayAttendance.checkOutTime ? (
              <p>Checked out at: {todayAttendance.checkOutTime}</p>
            ) : (
              <button
                onClick={() => dispatch(checkOut())}
                className="px-4 py-2 mt-4 text-white bg-blue-600 rounded-md hover:bg-blue-700"
              >
                Check Out
              </button>
            )}
          </div>
        ) : (
          <button
            onClick={() => dispatch(checkIn())}
            className="px-4 py-2 mt-4 text-white bg-blue-600 rounded-md hover:bg-blue-700"
          >
            Check In
          </button>
        )}
      </div>
    </div>
  );
};

export default EmployeeDashboard;
