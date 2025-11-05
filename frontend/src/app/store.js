import { configureStore } from '@reduxjs/toolkit';
import { combineReducers } from 'redux';
import { persistReducer, persistStore } from 'redux-persist';
import storage from 'redux-persist/lib/storage';
import authReducer from '../features/auth/authSlice';
import employeeReducer from '../features/employee/employeeSlice';
import attendanceReducer from '../features/attendance/attendanceSlice';
import reportReducer from '../features/report/reportSlice';

const rootReducer = combineReducers({
  auth: authReducer,
  employee: employeeReducer,
  attendance: attendanceReducer,
  report: reportReducer,
});

const persistConfig = {
  key: 'root',
  storage,
};

const persistedReducer = persistReducer(persistConfig, rootReducer);

export const store = configureStore({
  reducer: persistedReducer,
});

export const persistor = persistStore(store);
