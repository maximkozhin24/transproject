import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Card, CardContent, Grid, Alert, Snackbar, Chip,
    CircularProgress
} from '@mui/material';
import { Edit, Delete, Add, DirectionsCar, Speed } from '@mui/icons-material';
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

    const totalCapacity = vehicles.reduce((sum, vehicle) => sum + (vehicle.capacity || 0), 0);

    if (loading && vehicles.length === 0) {
        return (
            <Box display="flex" justifyContent="center" alignItems="center" minHeight="400px">
                <CircularProgress />
            </Box>
        );
    }

    return (
        <Box className="fade-in">
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h4" sx={{ color: '#000000' }}>
                    Vehicle Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    disabled={loading}
                >
                    Add Vehicle
                </Button>
            </Box>

            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <DirectionsCar color="primary" />
                            <Typography variant="h6">Total Vehicles</Typography>
                            <Typography variant="h3">{vehicles.length}</Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Speed color="primary" />
                            <Typography variant="h6">Total Capacity</Typography>
                            <Typography variant="h3">{totalCapacity} tons</Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Plate Number</TableCell>
                            <TableCell>Model</TableCell>
                            <TableCell>Capacity (tons)</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {vehicles.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={5} align="center">
                                    No vehicles found. Click "Add Vehicle" to create one.
                                </TableCell>
                            </TableRow>
                        ) : (
                            vehicles.map((vehicle) => (
                                <TableRow key={vehicle.id}>
                                    <TableCell>{vehicle.plateNumber}</TableCell>
                                    <TableCell>{vehicle.model}</TableCell>
                                    <TableCell>{vehicle.capacity}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={vehicle.capacity > 10 ? 'Large' : vehicle.capacity > 5 ? 'Medium' : 'Small'}
                                            color={vehicle.capacity > 10 ? 'error' : vehicle.capacity > 5 ? 'warning' : 'success'}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <IconButton onClick={() => handleOpenDialog(vehicle)} color="primary">
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={() => handleDelete(vehicle.id)} color="error">
                                            <Delete />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>{selectedVehicle ? 'Edit Vehicle' : 'Add New Vehicle'}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                        <TextField
                            fullWidth
                            label="Plate Number"
                            value={formData.plateNumber}
                            onChange={(e) => setFormData({ ...formData, plateNumber: e.target.value })}
                            required
                        />
                        <TextField
                            fullWidth
                            label="Model"
                            value={formData.model}
                            onChange={(e) => setFormData({ ...formData, model: e.target.value })}
                            required
                        />
                        <TextField
                            fullWidth
                            label="Capacity (tons)"
                            type="number"
                            value={formData.capacity}
                            onChange={(e) => setFormData({ ...formData, capacity: e.target.value })}
                            required
                        />
                        <Button variant="contained" onClick={handleSubmit} disabled={loading}>
                            {selectedVehicle ? 'Update' : 'Create'}
                        </Button>
                    </Box>
                </DialogContent>
            </Dialog>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
            >
                <Alert severity={snackbar.severity} onClose={() => setSnackbar({ ...snackbar, open: false })}>
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Vehicles;