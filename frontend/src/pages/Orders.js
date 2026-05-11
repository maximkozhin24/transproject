import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, MenuItem,
    IconButton, Box, Typography, Chip, Grid,
    Alert, Snackbar, CircularProgress, FormControl, InputLabel, Select
} from '@mui/material';
import { Edit, Delete, Add, LocalShipping } from '@mui/icons-material';
import { orderApi, clientApi, cargoApi, routeApi, vehicleApi } from '../services/api';

const Orders = () => {
    const [orders, setOrders] = useState([]);
    const [clients, setClients] = useState([]);
    const [cargos, setCargos] = useState([]);
    const [routes, setRoutes] = useState([]);
    const [vehicles, setVehicles] = useState([]);

    const [enrichedOrders, setEnrichedOrders] = useState([]);

    const [openDialog, setOpenDialog] = useState(false);
    const [openAssignDialog, setOpenAssignDialog] = useState(false);
    const [selectedOrder, setSelectedOrder] = useState(null);
    const [selectedOrderForAssign, setSelectedOrderForAssign] = useState(null);
    const [selectedVehicleId, setSelectedVehicleId] = useState('');
    const [loading, setLoading] = useState(false);
    const [snackbar, setSnackbar] = useState({ open: false, message: '', severity: 'success' });
    const [formData, setFormData] = useState({ price: '', status: 'NEW', clientId: '', items: [] });
    const [selectedCargo, setSelectedCargo] = useState({ cargoId: '', routeId: '' });

    useEffect(() => {
        loadData();
    }, []);

    const loadData = async () => {
        setLoading(true);
        try {
            const [ordersRes, clientsRes, cargosRes, routesRes, vehiclesRes] = await Promise.all([
                orderApi.getAll(),
                clientApi.getAll(),
                cargoApi.getAll(),
                routeApi.getAll(),
                vehicleApi.getAll()
            ]);

            let ordersData = [];
            if (Array.isArray(ordersRes.data)) {
                ordersData = ordersRes.data;
            } else if (ordersRes.data?.content) {
                ordersData = ordersRes.data.content;
            }

            setClients(clientsRes.data || []);
            setCargos(cargosRes.data || []);
            setRoutes(routesRes.data || []);
            setVehicles(vehiclesRes.data || []);
            setOrders(ordersData);

            enrichOrdersWithData(
                ordersData,
                clientsRes.data || [],
                cargosRes.data || [],
                routesRes.data || [],
                vehiclesRes.data || []
            );
        } catch (error) {
            console.error('Error loading data:', error);
            showSnackbar('Error loading data: ' + (error.response?.data?.message || error.message), 'error');
        } finally {
            setLoading(false);
        }
    };

    const enrichOrdersWithData = (ordersList, clientsList, cargosList, routesList, vehiclesList) => {
        const enriched = ordersList.map(order => {
            let clientData = order.client;
            if (!clientData && order.clientId) {
                clientData = clientsList.find(c => String(c.id) === String(order.clientId));
            }

            const rawCargos = order.cargos || [];
            const rawRoutes = order.routes || [];
            const rawVehicles = order.vehicles || [];

            const enrichedItems = rawCargos.map((cargo, index) => {
                const route = (rawRoutes[index] && rawRoutes[index] !== null) ? rawRoutes[index] : null;
                const vehicle = (rawVehicles[index] && rawVehicles[index] !== null) ? rawVehicles[index] : null;

                return {
                    id: cargo.id,
                    cargo: {
                        id: cargo.id,
                        name: cargo.name,
                        weight: cargo.weight
                    },
                    route: route ? {
                        id: route.id,
                        startLocation: route.startLocation,
                        endLocation: route.endLocation,
                        distance: route.distance
                    } : null,
                    vehicle: vehicle ? {
                        id: vehicle.id,
                        plateNumber: vehicle.plateNumber,
                        model: vehicle.model,
                        capacity: vehicle.capacity
                    } : null
                };
            });

            return {
                ...order,
                client: clientData,
                routeVehicleCargoList: enrichedItems
            };
        });

        setEnrichedOrders(enriched);
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleOpenDialog = (order = null) => {
        if (order) {
            setSelectedOrder(order);
            const items = (order.routeVehicleCargoList || []).map(item => ({
                cargoId: item.cargo?.id,
                routeId: item.route?.id,
                cargoName: item.cargo?.name,
                cargoWeight: item.cargo?.weight,
                routeStart: item.route?.startLocation,
                routeEnd: item.route?.endLocation
            }));
            setFormData({
                price: order.price,
                status: order.status || 'NEW',
                clientId: order.client?.id || '',
                items: items
            });
        } else {
            setSelectedOrder(null);
            setFormData({ price: '', status: 'NEW', clientId: '', items: [] });
        }
        setOpenDialog(true);
    };

    const handleOpenAssignDialog = (order) => {
        setSelectedOrderForAssign(order);
        setSelectedVehicleId('');
        setOpenAssignDialog(true);
    };

    const handleAssignVehicle = async () => {
        if (!selectedVehicleId) {
            showSnackbar('Please select a vehicle', 'warning');
            return;
        }
        try {
            setLoading(true);
            await vehicleApi.assign({
                orderId: selectedOrderForAssign.id,
                vehicleId: selectedVehicleId
            });
            showSnackbar('Vehicle assigned successfully', 'success');
            await loadData();
            setOpenAssignDialog(false);
        } catch (error) {
            console.error('Error assigning vehicle:', error);
            showSnackbar('Error assigning vehicle: ' + (error.response?.data?.message || error.message), 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleAddItem = () => {
        if (selectedCargo.cargoId && selectedCargo.routeId) {
            const selectedCargoObj = cargos.find(c => String(c.id) === String(selectedCargo.cargoId));
            const selectedRouteObj = routes.find(r => String(r.id) === String(selectedCargo.routeId));

            if (selectedCargoObj && selectedRouteObj) {
                setFormData({
                    ...formData,
                    items: [...formData.items, {
                        cargoId: selectedCargo.cargoId,
                        routeId: selectedCargo.routeId,
                        cargoName: selectedCargoObj.name,
                        cargoWeight: selectedCargoObj.weight,
                        routeStart: selectedRouteObj.startLocation,
                        routeEnd: selectedRouteObj.endLocation
                    }]
                });
                setSelectedCargo({ cargoId: '', routeId: '' });
            }
        } else {
            showSnackbar('Please select both cargo and route', 'warning');
        }
    };

    const handleRemoveItem = (index) => {
        const newItems = [...formData.items];
        newItems.splice(index, 1);
        setFormData({ ...formData, items: newItems });
    };

    const handleSubmit = async () => {
        if (!formData.clientId) {
            showSnackbar('Please select a client', 'warning');
            return;
        }
        if (formData.items.length === 0) {
            showSnackbar('Please add at least one item', 'warning');
            return;
        }
        if (!formData.price || formData.price <= 0) {
            showSnackbar('Please enter a valid price', 'warning');
            return;
        }

        const submitData = {
            price: parseFloat(formData.price),
            status: formData.status,
            clientId: parseInt(formData.clientId),
            items: formData.items.map(item => ({
                routeId: parseInt(item.routeId),
                cargoId: parseInt(item.cargoId)
            }))
        };

        try {
            setLoading(true);
            if (selectedOrder) {
                await orderApi.update(selectedOrder.id, submitData);
                showSnackbar('Order updated successfully', 'success');
            } else {
                await orderApi.create(submitData);
                showSnackbar('Order created successfully', 'success');
                setFormData({ price: '', status: 'NEW', clientId: '', items: [] });
            }
            await loadData();
            setOpenDialog(false);
        } catch (error) {
            console.error('Error saving order:', error);
            showSnackbar(error.response?.data?.message || 'Error saving order', 'error');
        } finally {
            setLoading(false);
        }
    };

    const handleDelete = async (id) => {
        if (window.confirm('Are you sure you want to delete this order?')) {
            try {
                setLoading(true);
                await orderApi.delete(id);
                showSnackbar('Order deleted successfully', 'success');
                await loadData();
            } catch (error) {
                console.error('Error deleting order:', error);
                showSnackbar('Error deleting order', 'error');
            } finally {
                setLoading(false);
            }
        }
    };

    const getOrderItems = (order) => order.routeVehicleCargoList || [];

    const getTotalWeight = (order) => {
        const items = order.routeVehicleCargoList || [];
        return items.reduce((sum, item) => sum + (item.cargo?.weight || 0), 0);
    };

    const getAssignedVehicle = (order) => {
        const items = order.routeVehicleCargoList || [];
        if (items.length === 0) return 'Not assigned';
        const vehicle = items[0]?.vehicle;
        if (vehicle && vehicle.plateNumber) {
            return `${vehicle.plateNumber} (${vehicle.model || ''})`.trim();
        }
        return 'Not assigned';
    };

    const displayOrders = enrichedOrders;

    if (loading && displayOrders.length === 0) {
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
                    Orders Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    disabled={loading}
                    sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                >
                    Create Order
                </Button>
            </Box>

            {/* 🗑️ Статистика удалена */}

            {/* Таблица заказов */}
            <TableContainer component={Paper} sx={{ borderRadius: 2, boxShadow: 1 }}>
                <Table>
                    <TableHead sx={{ bgcolor: '#e0e0e0' }}>
                        <TableRow>
                            <TableCell sx={{ fontWeight: 600 }}>Client</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Price</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Status</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Items</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Total Weight</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Vehicle</TableCell>
                            <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {displayOrders.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={7} align="center" sx={{ py: 4 }}>
                                    <Typography color="textSecondary">
                                        No orders found. Click "Create Order" to create one.
                                    </Typography>
                                </TableCell>
                            </TableRow>
                        ) : (
                            displayOrders.map((order) => (
                                <TableRow
                                    key={order.id}
                                    sx={{ '&:hover': { bgcolor: '#fafafa' }, transition: 'background-color 0.2s' }}
                                >
                                    <TableCell sx={{ fontWeight: 500 }}>
                                        {order.client?.name || 'N/A'}
                                    </TableCell>
                                    <TableCell>${order.price}</TableCell>
                                    <TableCell>
                                        <Chip
                                            label={order.status}
                                            color={
                                                order.status === 'COMPLETED' ? 'success' :
                                                    order.status === 'IN_PROGRESS' ? 'warning' :
                                                        order.status === 'NEW' ? 'info' : 'default'
                                            }
                                            size="small"
                                            sx={{ fontWeight: 500 }}
                                        />
                                    </TableCell>
                                    <TableCell>
                                        {getOrderItems(order).map((item, idx) => (
                                            <Typography key={idx} variant="body2" sx={{ mb: 0.25 }}>
                                                • {item.cargo?.name || 'Unknown cargo'}
                                            </Typography>
                                        ))}
                                        {getOrderItems(order).length === 0 && (
                                            <Typography variant="body2" color="textSecondary">No items</Typography>
                                        )}
                                    </TableCell>
                                    <TableCell>{getTotalWeight(order)} kg</TableCell>
                                    <TableCell>
                                        <Typography variant="body2" color="textSecondary">
                                            {getAssignedVehicle(order)}
                                        </Typography>
                                    </TableCell>
                                    <TableCell>
                                        <IconButton onClick={() => handleOpenDialog(order)} color="primary" size="small"
                                                    sx={{ '&:hover': { bgcolor: 'rgba(25, 118, 210, 0.1)' } }}>
                                            <Edit fontSize="small" />
                                        </IconButton>
                                        <IconButton onClick={() => handleOpenAssignDialog(order)} color="secondary" size="small"
                                                    sx={{ '&:hover': { bgcolor: 'rgba(156, 39, 176, 0.1)' } }}>
                                            <LocalShipping fontSize="small" />
                                        </IconButton>
                                        <IconButton onClick={() => handleDelete(order.id)} color="error" size="small"
                                                    sx={{ '&:hover': { bgcolor: 'rgba(211, 47, 47, 0.1)' } }}>
                                            <Delete fontSize="small" />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Диалог создания/редактирования заказа */}
            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth
                    PaperProps={{ sx: { borderRadius: 2 } }}>
                <DialogTitle sx={{ fontWeight: 600, pb: 1 }}>
                    {selectedOrder ? 'Edit Order' : 'Create Order'}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                        <Grid container spacing={2}>
                            <Grid item xs={12}>
                                <FormControl fullWidth>
                                    <InputLabel>Client *</InputLabel>
                                    <Select
                                        value={formData.clientId}
                                        label="Client *"
                                        onChange={(e) => setFormData({ ...formData, clientId: e.target.value })}
                                        required
                                    >
                                        {clients.map((client) => (
                                            <MenuItem key={client.id} value={client.id}>
                                                {client.name} - {client.email}
                                            </MenuItem>
                                        ))}
                                    </Select>
                                </FormControl>
                            </Grid>
                            <Grid item xs={6}>
                                <TextField
                                    fullWidth
                                    label="Price *"
                                    type="number"
                                    value={formData.price}
                                    onChange={(e) => setFormData({ ...formData, price: e.target.value })}
                                    required
                                    inputProps={{ min: 0, step: '0.01' }}
                                />
                            </Grid>
                            <Grid item xs={6}>
                                <FormControl fullWidth>
                                    <InputLabel>Status</InputLabel>
                                    <Select
                                        value={formData.status}
                                        label="Status"
                                        onChange={(e) => setFormData({ ...formData, status: e.target.value })}
                                    >
                                        <MenuItem value="NEW">NEW</MenuItem>
                                        <MenuItem value="IN_PROGRESS">IN_PROGRESS</MenuItem>
                                        <MenuItem value="COMPLETED">COMPLETED</MenuItem>
                                        <MenuItem value="CANCELLED">CANCELLED</MenuItem>
                                    </Select>
                                </FormControl>
                            </Grid>

                            {/* Добавление товаров */}
                            <Grid item xs={12}>
                                <Typography variant="subtitle1" sx={{ fontWeight: 600, mt: 1 }}>
                                    Order Items
                                </Typography>
                                <Paper variant="outlined" sx={{ p: 2, mt: 1, bgcolor: '#fafafa' }}>
                                    <Grid container spacing={2} alignItems="center">
                                        <Grid item xs={5}>
                                            <FormControl fullWidth size="small">
                                                <InputLabel>Cargo</InputLabel>
                                                <Select
                                                    value={selectedCargo.cargoId}
                                                    label="Cargo"
                                                    onChange={(e) => setSelectedCargo({ ...selectedCargo, cargoId: e.target.value })}
                                                >
                                                    {cargos.map((cargo) => (
                                                        <MenuItem key={cargo.id} value={cargo.id}>
                                                            {cargo.name} ({cargo.weight} kg)
                                                        </MenuItem>
                                                    ))}
                                                </Select>
                                            </FormControl>
                                        </Grid>
                                        <Grid item xs={5}>
                                            <FormControl fullWidth size="small">
                                                <InputLabel>Route</InputLabel>
                                                <Select
                                                    value={selectedCargo.routeId}
                                                    label="Route"
                                                    onChange={(e) => setSelectedCargo({ ...selectedCargo, routeId: e.target.value })}
                                                >
                                                    {routes.map((route) => (
                                                        <MenuItem key={route.id} value={route.id}>
                                                            {route.startLocation} → {route.endLocation} ({route.distance} km)
                                                        </MenuItem>
                                                    ))}
                                                </Select>
                                            </FormControl>
                                        </Grid>
                                        <Grid item xs={2}>
                                            <Button variant="outlined" onClick={handleAddItem} fullWidth size="small">
                                                Add
                                            </Button>
                                        </Grid>
                                    </Grid>

                                    {formData.items.length > 0 && (
                                        <TableContainer sx={{ mt: 2, maxHeight: 200 }}>
                                            <Table size="small">
                                                <TableHead>
                                                    <TableRow>
                                                        <TableCell sx={{ fontWeight: 600 }}>Cargo</TableCell>
                                                        <TableCell sx={{ fontWeight: 600 }}>Route</TableCell>
                                                        <TableCell sx={{ fontWeight: 600 }}>Actions</TableCell>
                                                    </TableRow>
                                                </TableHead>
                                                <TableBody>
                                                    {formData.items.map((item, idx) => (
                                                        <TableRow key={idx}>
                                                            <TableCell>{item.cargoName} ({item.cargoWeight} kg)</TableCell>
                                                            <TableCell>{item.routeStart} → {item.routeEnd}</TableCell>
                                                            <TableCell>
                                                                <IconButton onClick={() => handleRemoveItem(idx)} color="error" size="small">
                                                                    <Delete fontSize="small" />
                                                                </IconButton>
                                                            </TableCell>
                                                        </TableRow>
                                                    ))}
                                                </TableBody>
                                            </Table>
                                        </TableContainer>
                                    )}
                                </Paper>
                            </Grid>

                            <Grid item xs={12} sx={{ display: 'flex', gap: 2, mt: 1 }}>
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
                                    {selectedOrder ? 'Update' : 'Create'}
                                </Button>
                            </Grid>
                        </Grid>
                    </Box>
                </DialogContent>
            </Dialog>

            {/* Диалог назначения машины */}
            <Dialog open={openAssignDialog} onClose={() => setOpenAssignDialog(false)} maxWidth="sm" fullWidth
                    PaperProps={{ sx: { borderRadius: 2 } }}>
                <DialogTitle sx={{ fontWeight: 600, pb: 1 }}>
                    Assign Vehicle to Order #{selectedOrderForAssign?.id}
                </DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 1 }}>
                        <FormControl fullWidth>
                            <InputLabel>Select Vehicle</InputLabel>
                            <Select
                                value={selectedVehicleId}
                                label="Select Vehicle"
                                onChange={(e) => setSelectedVehicleId(e.target.value)}
                            >
                                {vehicles.map((vehicle) => (
                                    <MenuItem key={vehicle.id} value={vehicle.id}>
                                        {vehicle.plateNumber} - {vehicle.model} (Capacity: {vehicle.capacity} tons)
                                    </MenuItem>
                                ))}
                            </Select>
                        </FormControl>
                        <Button
                            variant="contained"
                            onClick={handleAssignVehicle}
                            disabled={loading}
                            fullWidth
                            sx={{ bgcolor: '#1976d2', '&:hover': { bgcolor: '#1565c0' } }}
                        >
                            Assign Vehicle
                        </Button>
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

export default Orders;