import api from '../../app/api';

const checkIn = async () => {
  const response = await api.post('/attendance/checkin');
  return response.data.data;
};

const checkOut = async () => {
  const response = await api.post('/attendance/checkout');
  return response.data.data;
};

const getTodayAttendance = async (employeeId) => {
  const response = await api.get(`/attendance/today/${employeeId}`);
  return response.data.data;
};

const getAttendanceHistory = async (employeeId) => {
  const response = await api.get(`/attendance/history/${employeeId}`);
  return response.data.data;
};

const attendanceService = {
  checkIn,
  checkOut,
  getTodayAttendance,
  getAttendanceHistory,
};

export default attendanceService;
