import { useSelector } from 'react-redux';
import { Navigate, Outlet } from 'react-router-dom';

const ProtectedRoute = ({ allowedRoles }) => {
  const { isAuthenticated, user } = useSelector((state) => state.auth);

  if (!isAuthenticated) {
    return <Navigate to="/login" replace />;
  }

  const userRole = user?.role; // Replace with actual role from decoded token

  return allowedRoles.includes(userRole) ? <Outlet /> : <Navigate to="/unauthorized" replace />;
};

export default ProtectedRoute;
