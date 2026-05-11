import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Card, CardContent, Grid, Alert, Snackbar, Chip,
    CircularProgress
} from '@mui/material';
import { Edit, Delete, Add, FitnessCenter, Straighten } from '@mui/icons-material';
import { cargoApi } from '../services/api';

const Cargos = () => {
    const [cargos, setCargos] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedCargo, setSelectedCargo] = useState(null);
    const [formData, setFormData] = useState({ name: '', weight: '' });
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        loadCargos();
    }, []);

    const loadCargos = async () => {
        setLoading(true);
        try {
            const response = await cargoApi.getAll();
            console.log('Loaded cargos:', response.data);
            setCargos(response.data);
        } catch (error) {
            console.error('Error loading cargos:', error);
            showSnackbar('Error loading cargos: ' + (error.response?.data?.message || error.message), 'error');
        } finally {
            setLoading(false);
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleOpenDialog = (cargo = null) => {
        if (cargo) {
            setSelectedCargo(cargo);
            setFormData({ name: cargo.name, weight: cargo.weight });
        } else {
            setSelectedCargo(null);
            setFormData({ name: '', weight: '' });
        }
        setOpenDialog(true);
    };

    const handleSubmit = async () => {
        try {
            setLoading(true);
            if (selectedCargo) {
                await cargoApi.update(selectedCargo.id, formData);
                showSnackbar('Cargo updated successfully', 'success');
            } else {
                await cargoApi.create(formData);
                showSnackbar('Cargo created successfully', 'success');
            }
            await loadCargos();
            setOpenDialog(false);
        } catch (error) {
            console.error('Error saving cargo:', error);
            showSnackbar(error.response?.data?.message || 'Error saving cargo', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this cargo?')) {
            try {
                setLoading(true);
                await cargoApi.delete(id);
                showSnackbar('Cargo deleted successfully', 'success');
                await loadCargos();
            } catch (error) {
                console.error('Error deleting cargo:', error);
                showSnackbar('Error deleting cargo', 'error');
            } finally {
                setLoading(false);
            }
        }
    };

    const totalWeight = cargos.reduce((sum, cargo) => sum + (cargo.weight || 0), 0);

    if (loading && cargos.length === 0) {
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
                    Cargo Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    disabled={loading}
                >
                    Add Cargo
                </Button>
            </Box>

            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <FitnessCenter color="primary" />
                            <Typography variant="h6">Total Cargos</Typography>
                            <Typography variant="h3">{cargos.length}</Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={6}>
                    <Card>
                        <CardContent>
                            <Straighten color="primary" />
                            <Typography variant="h6">Total Weight</Typography>
                            <Typography variant="h3">{totalWeight} kg</Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Weight (kg)</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {cargos.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={4} align="center">
                                    No cargos found. Click "Add Cargo" to create one.
                                </TableCell>
                            </TableRow>
                        ) : (
                            cargos.map((cargo) => (
                                <TableRow key={cargo.id}>
                                    <TableCell>{cargo.name}</TableCell>
                                    <TableCell>{cargo.weight}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={cargo.weight > 100 ? 'Heavy' : 'Light'}
                                            color={cargo.weight > 100 ? 'error' : 'success'}
                                            size="small"
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <IconButton onClick={() => handleOpenDialog(cargo)} color="primary">
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={() => handleDelete(cargo.id)} color="error">
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
                <DialogTitle>{selectedCargo ? 'Edit Cargo' : 'Add New Cargo'}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                        <TextField
                            fullWidth
                            label="Cargo Name"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                        />
                        <TextField
                            fullWidth
                            label="Weight (kg)"
                            type="number"
                            value={formData.weight}
                            onChange={(e) => setFormData({ ...formData, weight: e.target.value })}
                            required
                        />
                        <Button variant="contained" onClick={handleSubmit} disabled={loading}>
                            {selectedCargo ? 'Update' : 'Create'}
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

export default Cargos;