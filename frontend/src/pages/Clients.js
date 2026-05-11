import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, IconButton,
    Box, Typography, Card, CardContent, Grid, Alert, Snackbar
} from '@mui/material';
import { Edit, Delete, Add, Phone, Email, Person } from '@mui/icons-material';
import { clientApi } from '../services/api';

const Clients = () => {
    const [clients, setClients] = useState([]);
    const [openDialog, setOpenDialog] = useState(false);
    const [selectedClient, setSelectedClient] = useState(null);
    const [formData, setFormData] = useState({ name: '', email: '', phone: '' });
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });

    useEffect(() => {
        loadClients();
    }, []);

    const loadClients = async () => {
        try {
            const response = await clientApi.getAll();
            setClients(response.data);
        } catch (error) {
            showSnackbar('Error loading clients', 'error');
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
        try {
            if (selectedClient) {
                await clientApi.update(selectedClient.id, formData);
                showSnackbar('Client updated successfully', 'success');
            } else {
                await clientApi.create(formData);
                showSnackbar('Client created successfully', 'success');
            }
            loadClients();
            setOpenDialog(false);
        } catch (error) {
            showSnackbar(error.response?.data?.message || 'Error saving client', 'error');
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this client?')) {
            try {
                await clientApi.delete(id);
                showSnackbar('Client deleted successfully', 'success');
                loadClients();
            } catch (error) {
                showSnackbar('Error deleting client', 'error');
            }
        }
    };

    return (
        <Box className="fade-in">
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h4" sx={{ color: '#000000' }}>
                    Clients Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                >
                    Add Client
                </Button>
            </Box>

            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Person color="primary" />
                            <Typography variant="h6">Total Clients</Typography>
                            <Typography variant="h3">{clients.length}</Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Email color="primary" />
                            <Typography variant="h6">Active Emails</Typography>
                            <Typography variant="h3">
                                {clients.filter(c => c.email).length}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Phone color="primary" />
                            <Typography variant="h6">With Phone</Typography>
                            <Typography variant="h3">
                                {clients.filter(c => c.phone).length}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Name</TableCell>
                            <TableCell>Email</TableCell>
                            <TableCell>Phone</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {clients.map((client) => (
                            <TableRow key={client.id}>
                                <TableCell>{client.name}</TableCell>
                                <TableCell>{client.email}</TableCell>
                                <TableCell>{client.phone}</TableCell>
                                <TableCell>
                                    <IconButton onClick={() => handleOpenDialog(client)} color="primary">
                                        <Edit />
                                    </IconButton>
                                    <IconButton onClick={() => handleDelete(client.id)} color="error">
                                        <Delete />
                                    </IconButton>
                                </TableCell>
                            </TableRow>
                        ))}
                    </TableBody>
                </Table>
            </TableContainer>

            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>{selectedClient ? 'Edit Client' : 'Add New Client'}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
                        <TextField
                            fullWidth
                            label="Name"
                            value={formData.name}
                            onChange={(e) => setFormData({ ...formData, name: e.target.value })}
                            required
                        />
                        <TextField
                            fullWidth
                            label="Email"
                            type="email"
                            value={formData.email}
                            onChange={(e) => setFormData({ ...formData, email: e.target.value })}
                            required
                        />
                        <TextField
                            fullWidth
                            label="Phone"
                            value={formData.phone}
                            onChange={(e) => setFormData({ ...formData, phone: e.target.value })}
                            required
                        />
                        <Button variant="contained" onClick={handleSubmit}>
                            {selectedClient ? 'Update' : 'Create'}
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

export default Clients;