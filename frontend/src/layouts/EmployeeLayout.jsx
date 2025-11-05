import { Outlet } from 'react-router-dom';
import Sidebar from '../components/Sidebar';
import Navbar from '../components/Navbar';

const EmployeeLayout = () => {
  return (
    <div className="flex min-h-screen bg-gray-100">
      <Sidebar />
      <div className="flex-1 p-6">
        <Navbar />
        <main className="mt-6">
          <Outlet />
        </main>
      </div>
    </div>
  );
};

export default EmployeeLayout;
