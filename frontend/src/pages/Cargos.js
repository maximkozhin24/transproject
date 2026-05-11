import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Alert, Snackbar, Chip, CircularProgress
} from '@mui/material';
import { Edit, Delete, Add } from '@mui/icons-material';
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

    if (loading && cargos.length === 0) {
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
                    Cargo Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    disabled={loading}
                    sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                >
                    Add Cargo
                </Button>
            </Box>

            {/* Таблица грузов */}
            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 1 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#e0e0e0' }}>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 600 }}>Name</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Weight (kg)</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {cargos.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={4} align="center" sx={{ py: 4 }}>
                                    <Typography color="textSecondary">
                                        No cargos found. Click "Add Cargo" to create one.
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            cargos.map((cargo) => (
                                <TableRow
                                    key={cargo.id}
                                    sx={{ '&:hover': { bgcolor: '#fafafa' }, transition: 'background-color 0.2s' }}
                                >
                                    <TableCell>{cargo.name}</TableCell>
                                    <TableCell>{cargo.weight}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={cargo.weight > 100 ? 'Heavy' : 'Light'}
                                            color={cargo.weight > 100 ? 'error' : 'success'}
                                            size="small"
                                            sx={{ fontWeight: 500 }}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        <IconButton
                                            onClick={() => handleOpenDialog(cargo)}
                                            color="primary"
                                            size="small"
                                            sx={{ '&:hover': { bgcolor: 'rgba(25, 118, 210, 0.1)' } }}
                                        >
                                            <Edit fontSize="small" />
                                        </IconButton>
                                        <IconButton
                                            onClick={() => handleDelete(cargo.id)}
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
                    {selectedCargo ? 'Edit Cargo' : 'Add New Cargo'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                        <TextField
                            fullWidth
                            label="Cargo Name"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                            variant="outlined"
                        />
                        <TextField
                            fullWidth
                            label="Weight (kg)"
                            type="number"
                            value={formData.weight}
                            onChange={(e) => setFormData({ ...formData, weight: e.target.value })}
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
                                {selectedCargo ? 'Update' : 'Create'}
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

export default Cargos;