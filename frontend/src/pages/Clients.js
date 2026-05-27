import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Alert, Snackbar, CircularProgress
} from '@mui/material';
import { Edit, Delete, Add, Email, Phone } from '@mui/icons-material';
import { clientApi } from '../services/api';

const Clients = () => {
    const [clients, setClients] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedClient, setSelectedClient] = useState(null);
    const [formData, setFormData] = useState({ name: '', email: '', phone: '' });
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const [loading, setLoading] = useState(false);

    useEffect(() => {
        loadClients();
    }, []);

    const loadClients = async () => {
        setLoading(true);
        try {
            const response = await clientApi.getAll();
            setClients(response.data);
        } catch (error) {
            console.error('Error loading clients:', error);
            showSnackbar('Error loading clients: ' + (error.response?.data?.message || error.message), 'error');
        } finally {
            setLoading(false);
        }
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleOpenDialog = (client = null) => {
        if (client) {
            setSelectedClient(client);
            setFormData({ name: client.name, email: client.email, phone: client.phone });
        } else {
            setSelectedClient(null);
            setFormData({ name: '', email: '', phone: '' });
        }
        setOpenDialog(true);
    };

    const handleSubmit = async () => {
        if (!formData.name.trim()) {
            showSnackbar('Please enter client name', 'warning');
            return;
        }
        if (!formData.email.trim() && !formData.phone.trim()) {
            showSnackbar('Please enter at least email or phone', 'warning');
            return;
        }

        try {
            setLoading(true);
            if (selectedClient) {
                await clientApi.update(selectedClient.id, formData);
                showSnackbar('Client updated successfully', 'success');
            } else {
                await clientApi.create(formData);
                showSnackbar('Client created successfully', 'success');
            }
            await loadClients();
            setOpenDialog(false);
            setFormData({ name: '', email: '', phone: '' });
        } catch (error) {
            console.error('Error saving client:', error);
            showSnackbar(error.response?.data?.message || 'Error saving client', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this client?')) {
            try {
                setLoading(true);
                await clientApi.delete(id);
                showSnackbar('Client deleted successfully', 'success');
                await loadClients();
            } catch (error) {
                console.error('Error deleting client:', error);
                showSnackbar('Error deleting client: ' + (error.response?.data?.message || error.message), 'error');
            } finally {
                setLoading(false);
            }
        }
    };

    if (loading && clients.length === 0) {
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
                    Clients Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    disabled={loading}
                    sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                >
                    Add Client
                </Button>
            </Box>

            {/* Таблица клиентов */}
            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 1 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#e0e0e0' }}>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 600 }}>Name</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Email</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Phone</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {clients.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={4} align="center" sx={{ py: 4 }}>
                                    <Typography color="textSecondary">
                                        No clients found. Click "Add Client" to create one.
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            clients.map((client) => (
                                <TableRow
                                    key={client.id}
                                    sx={{ '&:hover': { bgcolor: '#fafafa' }, transition: 'background-color 0.2s' }}
                                >
                                    <TableCell sx={{ fontWeight: 500 }}>{client.name}</TableCell>
                                    <TableCell>
                                        {client.email ? (
                                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                                <Email fontSize="small" color="action" />
                                                <Typography variant="body2">{client.email}</Typography>
                                            </Box>
                                        ) : (
                                            <Typography variant="body2" color="textSecondary">—</Typography>
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        {client.phone ? (
                                            <Box sx={{ display: 'flex', alignItems: 'center', gap: 0.5 }}>
                                                <Phone fontSize="small" color="action" />
                                                <Typography variant="body2">{client.phone}</Typography>
                                            </Box>
                                        ) : (
                                            <Typography variant="body2" color="textSecondary">—</Typography>
                                        )}
                                    </TableCell>
                                    <TableCell>
                                        <IconButton
                                            onClick={() => handleOpenDialog(client)}
                                            color="primary"
                                            size="small"
                                            sx={{ '&:hover': { bgcolor: 'rgba(25, 118, 210, 0.1)' } }}
                                        >
                                            <Edit fontSize="small" />
                                        </IconButton>
                                        <IconButton
                                            onClick={() => handleDelete(client.id)}
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
                    {selectedClient ? 'Edit Client' : 'Add New Client'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                        <TextField
                            fullWidth
                            label="Full Name *"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                            variant="outlined"
                        />
                        <TextField
                            fullWidth
                            label="Email"
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            variant="outlined"
                            InputProps={{
                                startAdornment: <Email color="action" sx={{ mr: 1 }} />
                            }}
                        />
                        <TextField
                            fullWidth
                            label="Phone"
                            value={formData.phone}
                            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                            variant="outlined"
                            InputProps={{
                                startAdornment: <Phone color="action" sx={{ mr: 1 }} />
                            }}
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
                                {selectedClient ? 'Update' : 'Create'}
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

export default Clients;