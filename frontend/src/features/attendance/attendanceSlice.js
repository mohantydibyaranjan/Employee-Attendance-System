import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import attendanceService from './AttendanceService';

const initialState = {
  todayAttendance: null,
  attendanceHistory: [],
  isLoading: false,
  isError: false,
  message: '',
};

export const checkIn = createAsyncThunk('attendance/checkIn', async (_, thunkAPI) => {
  try {
    return await attendanceService.checkIn();
  } catch (error) {
    const message =
      (error.response && error.response.data && error.response.data.message) ||
      error.message ||
      error.toString();
    return thunkAPI.rejectWithValue(message);
  }
});

export const checkOut = createAsyncThunk('attendance/checkOut', async (_, thunkAPI) => {
  try {
    return await attendanceService.checkOut();
  } catch (error) {
    const message =
      (error.response && error.response.data && error.response.data.message) ||
      error.message ||
      error.toString();
    return thunkAPI.rejectWithValue(message);
  }
});

export const getTodayAttendance = createAsyncThunk('attendance/getToday', async (employeeId, thunkAPI) => {
  try {
    return await attendanceService.getTodayAttendance(employeeId);
  } catch (error) {
    const message =
      (error.response && error.response.data && error.response.data.message) ||
      error.message ||
      error.toString();
    return thunkAPI.rejectWithValue(message);
  }
});

export const getAttendanceHistory = createAsyncThunk('attendance/getHistory', async (employeeId, thunkAPI) => {
  try {
    return await attendanceService.getAttendanceHistory(employeeId);
  } catch (error) {
    const message =
      (error.response && error.response.data && error.response.data.message) ||
      error.message ||
      error.toString();
    return thunkAPI.rejectWithValue(message);
  }
});

export const attendanceSlice = createSlice({
  name: 'attendance',
  initialState,
  reducers: {
    reset: (state) => initialState,
  },
  extraReducers: (builder) => {
    builder
      .addCase(checkIn.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(checkIn.fulfilled, (state, action) => {
        state.isLoading = false;
        state.todayAttendance = action.payload;
      })
      .addCase(checkIn.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.message = action.payload;
      })
      .addCase(checkOut.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(checkOut.fulfilled, (state, action) => {
        state.isLoading = false;
        state.todayAttendance = action.payload;
      })
      .addCase(checkOut.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.message = action.payload;
      })
      .addCase(getTodayAttendance.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getTodayAttendance.fulfilled, (state, action) => {
        state.isLoading = false;
        state.todayAttendance = action.payload;
      })
      .addCase(getTodayAttendance.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.message = action.payload;
      })
      .addCase(getAttendanceHistory.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getAttendanceHistory.fulfilled, (state, action) => {
        state.isLoading = false;
        state.attendanceHistory = action.payload;
      })
      .addCase(getAttendanceHistory.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.message = action.payload;
      });
  },
});

export const { reset } = attendanceSlice.actions;
export default attendanceSlice.reducer;
