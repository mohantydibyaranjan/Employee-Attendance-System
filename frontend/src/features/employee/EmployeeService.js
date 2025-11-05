import api from '../../app/api';

const getEmployees = async () => {
  const response = await api.get('/employee/all');
  return response.data.data;
};

const createEmployee = async (employeeData) => {
  const response = await api.post('/employee', employeeData);
  return response.data.data;
};

const updateEmployee = async (employeeData) => {
  const response = await api.put(`/employee/${employeeData.id}`, employeeData);
  return response.data.data;
};

const deleteEmployee = async (employeeId) => {
  const response = await api.delete(`/employee/${employeeId}`);
  return response.data.data;
};

const employeeService = {
  getEmployees,
  createEmployee,
  updateEmployee,
  deleteEmployee,
};

export default employeeService;
