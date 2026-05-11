import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Card, CardContent, Grid, Alert, Snackbar
} from '@mui/material';
import { Edit, Delete, Add, LocationOn, Straighten } from '@mui/icons-material';
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
            showSnackbar('Error loading routes', 'error');
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
            loadRoutes();
            setOpenDialog(false);
        } catch (error) {
            showSnackbar('Error saving route', 'error');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this route?')) {
            try {
                await routeApi.delete(id);
                showSnackbar('Route deleted successfully', 'success');
                loadRoutes();
            } catch (error) {
                showSnackbar('Error deleting route', 'error');
            }
        }
    };

    const totalDistance = routes.reduce((sum, route) => sum + (route.distance || 0), 0);

    return (
        <Box className="fade-in">
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h4" sx={{ color: '#000000' }}>
                    Route Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                >
                    Add Route
                </Button>
            </Box>

            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <LocationOn color="primary" />
                            <Typography variant="h6">Total Routes</Typography>
                            <Typography variant="h3">{routes.length}</Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Straighten color="primary" />
                            <Typography variant="h6">Total Distance</Typography>
                            <Typography variant="h3">{totalDistance} km</Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Start Location</TableCell>
                            <TableCell>End Location</TableCell>
                            <TableCell>Distance (km)</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {routes.map((route) => (
                            <TableRow key={route.id}>
                                <TableCell>{route.startLocation}</TableCell>
                                <TableCell>{route.endLocation}</TableCell>
                                <TableCell>{route.distance}</TableCell>
                                <TableCell>
                                    <IconButton onClick={() => handleOpenDialog(route)} color="primary">
                                        <Edit />
                                    </IconButton>
                                    <IconButton onClick={() => handleDelete(route.id)} color="error">
                                        <Delete />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>{selectedRoute ? 'Edit Route' : 'Add New Route'}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                        <TextField
                            fullWidth
                            label="Start Location"
                            value={formData.startLocation}
                            onChange={(e) => setFormData({ ...formData, startLocation: e.target.value })}
                            required
                        />
                        <TextField
                            fullWidth
                            label="End Location"
                            value={formData.endLocation}
                            onChange={(e) => setFormData({ ...formData, endLocation: e.target.value })}
                            required
                        />
                        <TextField
                            fullWidth
                            label="Distance (km)"
                            type="number"
                            value={formData.distance}
                            onChange={(e) => setFormData({ ...formData, distance: e.target.value })}
                            required
                        />
                        <Button variant="contained" onClick={handleSubmit}>
                            {selectedRoute ? 'Update' : 'Create'}
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

export default Routes;