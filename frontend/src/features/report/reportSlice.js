import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import reportService from './ReportService';

const initialState = {
  monthlySummary: null,
  dailySummary: [],
  isLoading: false,
  isError: false,
  message: '',
};

export const getMonthlySummary = createAsyncThunk(
  'report/getMonthlySummary',
  async ({ employeeId, month }, thunkAPI) => {
    try {
      return await reportService.getMonthlySummary(employeeId, month);
    } catch (error) {
      const message =
        (error.response && error.response.data && error.response.data.message) ||
        error.message ||
        error.toString();
      return thunkAPI.rejectWithValue(message);
    }
  }
);

export const getDailySummary = createAsyncThunk('report/getDailySummary', async (_, thunkAPI) => {
  try {
    return await reportService.getDailySummary();
  } catch (error) {
    const message =
      (error.response && error.response.data && error.response.data.message) ||
      error.message ||
      error.toString();
    return thunkAPI.rejectWithValue(message);
  }
});

export const reportSlice = createSlice({
  name: 'report',
  initialState,
  reducers: {
    reset: (state) => initialState,
  },
  extraReducers: (builder) => {
    builder
      .addCase(getMonthlySummary.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getMonthlySummary.fulfilled, (state, action) => {
        state.isLoading = false;
        state.monthlySummary = action.payload;
      })
      .addCase(getMonthlySummary.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.message = action.payload;
      })
      .addCase(getDailySummary.pending, (state) => {
        state.isLoading = true;
      })
      .addCase(getDailySummary.fulfilled, (state, action) => {
        state.isLoading = false;
        state.dailySummary = action.payload;
      })
      .addCase(getDailySummary.rejected, (state, action) => {
        state.isLoading = false;
        state.isError = true;
        state.message = action.payload;
      });
  },
});

export const { reset } = reportSlice.actions;
export default reportSlice.reducer;
