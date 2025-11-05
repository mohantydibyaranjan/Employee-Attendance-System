import api from '../../app/api';

const login = async (userData) => {
  const response = await api.post('/auth/login', userData);
  if (response.data.success) {
    localStorage.setItem('token', response.data.data.token);
  }
  return response.data;
};

const register = async (userData) => {
  const response = await api.post('/auth/register', userData);
  return response.data;
};

const authService = {
  login,
  register,
};

export default authService;
