import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Alert, Snackbar
} from '@mui/material';
import { Edit, Delete, Add } from '@mui/icons-material';
import { routeApi } from '../services/api';

const Routes = () => {
    const [routes, setRoutes] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedRoute, setSelectedRoute] = useState(null);
    const [formData, setFormData] = useState({ startLocation: '', endLocation: '', distance: '' });
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    useEffect(() => {
        loadRoutes();
    }, []);

    const loadRoutes = async () => {
        try {
            const response = await routeApi.getAll();
            setRoutes(response.data);
        } catch (error) {
            showSnackbar('Error loading routes: ' + (error.response?.data?.message || error.message), 'error');
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleOpenDialog = (route = null) => {
        if (route) {
            setSelectedRoute(route);
            setFormData({
                startLocation: route.startLocation,
                endLocation: route.endLocation,
                distance: route.distance
            });
        } else {
            setSelectedRoute(null);
            setFormData({ startLocation: '', endLocation: '', distance: '' });
        }
        setOpenDialog(true);
    };

    const handleSubmit = async () => {
        try {
            if (selectedRoute) {
                await routeApi.update(selectedRoute.id, formData);
                showSnackbar('Route updated successfully', 'success');
            } else {
                await routeApi.create(formData);
                showSnackbar('Route created successfully', 'success');
            }
            await loadRoutes();
            setOpenDialog(false);
        } catch (error) {
            showSnackbar('Error saving route: ' + (error.response?.data?.message || error.message), 'error');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this route?')) {
            try {
                await routeApi.delete(id);
                showSnackbar('Route deleted successfully', 'success');
                await loadRoutes();
            } catch (error) {
                showSnackbar('Error deleting route: ' + (error.response?.data?.message || error.message), 'error');
            }
        }
    };

    return (
        <Box className="fade-in" sx={{ bgcolor: '#f5f5f5', minHeight: '100vh', p: { xs: 1, sm: 2, md: 3 } }}>
            {/* Заголовок и кнопка добавления */}
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3, flexWrap: 'wrap', gap: 2 }}>
                <Typography variant="h4" sx={{ color: '#000000', fontWeight: 600 }}>
                    Route Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                >
                    Add Route
                </Button>
            </Box>

            {/* Таблица маршрутов */}
            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 1 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#e0e0e0' }}>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 600 }}>Start Location</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>End Location</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Distance (km)</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {routes.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={4} align="center" sx={{ py: 4 }}>
                                    <Typography color="textSecondary">
                                        No routes found. Click "Add Route" to create one.
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            routes.map((route) => (
                                <TableRow
                                    key={route.id}
                                    sx={{ '&:hover': { bgcolor: '#fafafa' }, transition: 'background-color 0.2s' }}
                                >
                                    <TableCell sx={{ fontWeight: 500 }}>{route.startLocation}</TableCell>
                                    <TableCell>{route.endLocation}</TableCell>
                                    <TableCell>{route.distance}</TableCell>
                                    <TableCell>
                                        <IconButton
                                            onClick={() => handleOpenDialog(route)}
                                            color="primary"
                                            size="small"
                                            sx={{ '&:hover': { bgcolor: 'rgba(25, 118, 210, 0.1)' } }}
                                        >
                                            <Edit fontSize="small" />
                                        </IconButton>
                                        <IconButton
                                            onClick={() => handleDelete(route.id)}
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
                    {selectedRoute ? 'Edit Route' : 'Add New Route'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                        <TextField
                            fullWidth
                            label="Start Location"
                            value={formData.startLocation}
                            onChange={(e) => setFormData({ ...formData, startLocation: e.target.value })}
                            required
                            variant="outlined"
                        />
                        <TextField
                            fullWidth
                            label="End Location"
                            value={formData.endLocation}
                            onChange={(e) => setFormData({ ...formData, endLocation: e.target.value })}
                            required
                            variant="outlined"
                        />
                        <TextField
                            fullWidth
                            label="Distance (km)"
                            type="number"
                            value={formData.distance}
                            onChange={(e) => setFormData({ ...formData, distance: e.target.value })}
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
                                fullWidth
                                sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                            >
                                {selectedRoute ? 'Update' : 'Create'}
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

export default Routes;