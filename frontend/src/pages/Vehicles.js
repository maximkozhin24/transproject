import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Chip, Alert, Snackbar, CircularProgress
} from '@mui/material';
import { Edit, Delete, Add } from '@mui/icons-material';
import { vehicleApi } from '../services/api';

const Vehicles = () => {
    const [vehicles, setVehicles] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedVehicle, setSelectedVehicle] = useState(null);
    const [formData, setFormData] = useState({ plateNumber: '', model: '', capacity: '' });
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        loadVehicles();
    }, []);

    const loadVehicles = async () => {
        setLoading(true);
        try {
            const response = await vehicleApi.getAll();
            console.log('Loaded vehicles:', response.data);
            setVehicles(response.data);
        } catch (error) {
            console.error('Error loading vehicles:', error);
            showSnackbar('Error loading vehicles: ' + (error.response?.data?.message || error.message), 'error');
        } finally {
            setLoading(false);
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleOpenDialog = (vehicle = null) => {
        if (vehicle) {
            setSelectedVehicle(vehicle);
            setFormData({
                plateNumber: vehicle.plateNumber,
                model: vehicle.model,
                capacity: vehicle.capacity
            });
        } else {
            setSelectedVehicle(null);
            setFormData({ plateNumber: '', model: '', capacity: '' });
        }
        setOpenDialog(true);
    };

    const handleSubmit = async () => {
        try {
            setLoading(true);
            if (selectedVehicle) {
                await vehicleApi.update(selectedVehicle.id, formData);
                showSnackbar('Vehicle updated successfully', 'success');
            } else {
                await vehicleApi.create(formData);
                showSnackbar('Vehicle created successfully', 'success');
            }
            await loadVehicles();
            setOpenDialog(false);
        } catch (error) {
            console.error('Error saving vehicle:', error);
            showSnackbar(error.response?.data?.message || 'Error saving vehicle', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this vehicle?')) {
            try {
                setLoading(true);
                await vehicleApi.delete(id);
                showSnackbar('Vehicle deleted successfully', 'success');
                await loadVehicles();
            } catch (error) {
                console.error('Error deleting vehicle:', error);
                showSnackbar('Error deleting vehicle', 'error');
            } finally {
                setLoading(false);
            }
        }
    };

    if (loading && vehicles.length === 0) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box className="fade-in" sx={{ bgcolor: '#f5f5f5', minHeight: '100vh', p: { xs: 1, sm: 2, md: 3 } }}>
            {/* Заголовок и кнопка добавления */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3, flexWrap: 'wrap', gap: 2 }}>
                <Typography variant="h4" sx={{ color: '#000000', fontWeight: 600 }}>
                    Vehicle Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    disabled={loading}
                    sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                >
                    Add Vehicle
                </Button>
            </Box>

            {/* Таблица транспорта */}
            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 1 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#e0e0e0' }}>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 600 }}>Plate Number</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Model</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Capacity (tons)</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {vehicles.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={5} align="center" sx={{ py: 4 }}>
                                    <Typography color="textSecondary">
                                        No vehicles found. Click "Add Vehicle" to create one.
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            vehicles.map((vehicle) => (
                                <TableRow
                                    key={vehicle.id}
                                    sx={{ '&:hover': { bgcolor: '#fafafa' }, transition: 'background-color 0.2s' }}
                                >
                                    <TableCell sx={{ fontWeight: 500 }}>{vehicle.plateNumber}</TableCell>
                                    <TableCell>{vehicle.model}</TableCell>
                                    <TableCell>{vehicle.capacity}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={vehicle.capacity > 10 ? 'Large' : vehicle.capacity > 5 ? 'Medium' : 'Small'}
                                            color={vehicle.capacity > 10 ? 'error' : vehicle.capacity > 5 ? 'warning' : 'success'}
                                            size="small"
                                            sx={{ fontWeight: 500 }}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <IconButton
                                            onClick={() => handleOpenDialog(vehicle)}
                                            color="primary"
                                            size="small"
                                            sx={{ '&:hover': { bgcolor: 'rgba(25, 118, 210, 0.1)' } }}
                                        >
                                            <Edit fontSize="small" />
                                        </IconButton>
                                        <IconButton
                                            onClick={() => handleDelete(vehicle.id)}
                                            color="error"
                                            size="small"
                                            sx={{ '&:hover': { bgcolor: 'rgba(211, 47, 47, 0.1)' } }}
                                        >
                                            <Delete fontSize="small" />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Диалог создания/редактирования */}
            <Dialog
                open={openDialog}
                onClose={() => setOpenDialog(false)}
                maxWidth="sm"
                fullWidth
                PaperProps={{
                    sx: { borderRadius: 2 }
                }}
            >
                <DialogTitle sx={{ fontWeight: 600, pb: 1 }}>
                    {selectedVehicle ? 'Edit Vehicle' : 'Add New Vehicle'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                        <TextField
                            fullWidth
                            label="Plate Number"
                            value={formData.plateNumber}
                            onChange={(e) => setFormData({ ...formData, plateNumber: e.target.value })}
                            required
                            variant="outlined"
                        />
                        <TextField
                            fullWidth
                            label="Model"
                            value={formData.model}
                            onChange={(e) => setFormData({ ...formData, model: e.target.value })}
                            required
                            variant="outlined"
                        />
                        <TextField
                            fullWidth
                            label="Capacity (tons)"
                            type="number"
                            value={formData.capacity}
                            onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
                            required
                            variant="outlined"
                            inputProps={{ min: 0 }}
                        />
                        <Box sx={{ display: 'flex', gap: 2, mt: 2 }}>
                            <Button
                                variant="outlined"
                                onClick={() => setOpenDialog(false)}
                                fullWidth
                            >
                                Cancel
                            </Button>
                            <Button
                                variant="contained"
                                onClick={handleSubmit}
                                disabled={loading}
                                fullWidth
                                sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                            >
                                {selectedVehicle ? 'Update' : 'Create'}
                            </Button>
                        </Box>
                    </Box>
                </DialogContent>
            </Dialog>

            {/* Snackbar для уведомлений */}
            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
                anchorOrigin={{ vertical: 'bottom', horizontal: 'right' }}
            >
                <Alert
                    severity={snackbar.severity}
                    onClose={() => setSnackbar({ ...snackbar, open: false })}
                    sx={{ width: '100%' }}
                    variant="filled"
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Vehicles;