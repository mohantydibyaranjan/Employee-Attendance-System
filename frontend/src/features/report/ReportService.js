import api from '../../app/api';

const getMonthlySummary = async (employeeId, month) => {
  const response = await api.get(`/report/summary/monthly?employeeId=${employeeId}&month=${month}`);
  return response.data.data;
};

const getDailySummary = async () => {
  const response = await api.get('/report/summary/daily');
  return response.data.data;
};

const reportService = {
  getMonthlySummary,
  getDailySummary,
};

export default reportService;
