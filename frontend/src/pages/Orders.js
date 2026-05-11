import React, { useState, useEffect } from 'react';
import {
    Paper, Table, TableBody, TableCell, TableContainer, TableHead, TableRow,
    Button, Dialog, DialogTitle, DialogContent, TextField, MenuItem,
    IconButton, Box, Typography, Chip, Grid, Card, CardContent,
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

    // enrichedOrders - это данные, готовые к отображению
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
            // Загружаем все справочники и заказы параллельно
            const [ordersRes, clientsRes, cargosRes, routesRes, vehiclesRes] = await Promise.all([
                orderApi.getAll(),
                clientApi.getAll(),
                cargoApi.getAll(),
                routeApi.getAll(),
                vehicleApi.getAll()
            ]);

            console.log('Raw Orders Response:', ordersRes.data);

            let ordersData = [];
            // Обработка ответа: может быть массив или объект с content
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

            // Обогащаем данные сразу после загрузки
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

    // === ГЛАВНАЯ ФУНКЦИЯ ТРАНСФОРМАЦИИ ДАННЫХ ===
    // Превращает плоский JSON (cargos[], routes[]) в удобную структуру
    const enrichOrdersWithData = (ordersList, clientsList, cargosList, routesList, vehiclesList) => {
        const enriched = ordersList.map(order => {
            // 1. Обработка Клиента
            // Если клиент пришел объектом в заказе (как в вашем новом JSON), берем его.
            // Иначе ищем по ID в списке клиентов.
            let clientData = order.client;
            if (!clientData && order.clientId) {
                clientData = clientsList.find(c => String(c.id) === String(order.clientId));
            }

            // 2. Сборка Items (RouteVehicleCargoList)
            // В вашем JSON приходят массивы: cargos, routes, vehicles.
            // Мы собираем их обратно в единый объект для UI.
            const rawCargos = order.cargos || [];
            const rawRoutes = order.routes || [];
            const rawVehicles = order.vehicles || [];

            const enrichedItems = rawCargos.map((cargo, index) => {
                // Предполагаем, что индексы массивов совпадают (1 груз -> 1 маршрут -> 1 машина)
                const route = (rawRoutes[index] && rawRoutes[index] !== null) ? rawRoutes[index] : null;
                const vehicle = (rawVehicles[index] && rawVehicles[index] !== null) ? rawVehicles[index] : null;

                return {
                    // Формируем структуру, которую ожидает UI
                    id: cargo.id, // временный ID
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
        console.log('Enriched Orders:', enriched);
    };

    const showSnackbar = (message, severity) => {
        setSnackbar({ open: true, message, severity });
    };

    const handleOpenDialog = (order = null) => {
        if (order) {
            setSelectedOrder(order);

            // Заполняем форму данными для редактирования
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
            // TODO: Убедитесь, что API принимает такие параметры
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

        // Подготовка данных для отправки на бэкенд
        const submitData = {
            price: parseFloat(formData.price),
            status: formData.status,
            clientId: parseInt(formData.clientId),
            // Бэкенд ожидает массив items с routeId и cargoId
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

    // === ВСПОМОГАТЕЛЬНЫЕ ФУНКЦИИ ДЛЯ UI ===

    const getOrderItems = (order) => {
        return order.routeVehicleCargoList || [];
    };

    const getTotalWeight = (order) => {
        const items = order.routeVehicleCargoList || [];
        return items.reduce((sum, item) => sum + (item.cargo?.weight || 0), 0);
    };

    const getAssignedVehicle = (order) => {
        const items = order.routeVehicleCargoList || [];
        if (items.length === 0) return 'Not assigned';

        // Берем первую машину из списка привязок
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
        <Box className="fade-in" sx={{ p: 3 }}>
            <Box sx={{ display: 'flex', justifyContent: 'space-between', mb: 3 }}>
                <Typography variant="h4" sx={{ color: '#000000' }}>
                    Orders Management
                </Typography>
                <Button
                    variant="contained"
                    startIcon={<Add />}
                    onClick={() => handleOpenDialog()}
                    disabled={loading}
                >
                    Create Order
                </Button>
            </Box>

            {/* Статистика */}
            <Grid container spacing={3} sx={{ mb: 3 }}>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">Total Orders</Typography>
                            <Typography variant="h3">{displayOrders.length}</Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">Completed</Typography>
                            <Typography variant="h3">
                                {displayOrders.filter(o => o.status === 'COMPLETED').length}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
                <Grid item xs={12} md={4}>
                    <Card>
                        <CardContent>
                            <Typography variant="h6">In Progress</Typography>
                            <Typography variant="h3">
                                {displayOrders.filter(o => o.status === 'IN_PROGRESS').length}
                            </Typography>
                        </CardContent>
                    </Card>
                </Grid>
            </Grid>

            {/* Таблица */}
            <TableContainer component={Paper}>
                <Table>
                    <TableHead>
                        <TableRow>
                            <TableCell>Client</TableCell>
                            <TableCell>Price</TableCell>
                            <TableCell>Status</TableCell>
                            <TableCell>Items</TableCell>
                            <TableCell>Total Weight</TableCell>
                            <TableCell>Vehicle</TableCell>
                            <TableCell>Actions</TableCell>
                        </TableRow>
                    </TableHead>
                    <TableBody>
                        {displayOrders.length === 0 ? (
                            <TableRow>
                                <TableCell colSpan={7} align="center">
                                    No orders found. Click "Create Order" to create one.
                                </TableCell>
                            </TableRow>
                        ) : (
                            displayOrders.map((order) => (
                                <TableRow key={order.id}>
                                    <TableCell>
                                        {/* ОТОБРАЖЕНИЕ КЛИЕНТА */}
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
                                        />
                                    </TableCell>
                                    <TableCell>
                                        {getOrderItems(order).map((item, idx) => (
                                            <Box key={idx} sx={{ mb: 0.5 }}>
                                                <Typography variant="body2">
                                                    • {item.cargo?.name || 'Unknown cargo'}
                                                </Typography>
                                            </Box>
                                        ))}
                                        {getOrderItems(order).length === 0 && <Typography color="textSecondary">No items</Typography>}
                                    </TableCell>
                                    <TableCell>{getTotalWeight(order)} kg</TableCell>
                                    <TableCell>{getAssignedVehicle(order)}</TableCell>
                                    <TableCell>
                                        <IconButton onClick={() => handleOpenDialog(order)} color="primary" size="small">
                                            <Edit />
                                        </IconButton>
                                        <IconButton onClick={() => handleOpenAssignDialog(order)} color="secondary" size="small">
                                            <LocalShipping />
                                        </IconButton>
                                        <IconButton onClick={() => handleDelete(order.id)} color="error" size="small">
                                            <Delete />
                                        </IconButton>
                                    </TableCell>
                                </TableRow>
                            ))
                        )}
                    </TableBody>
                </Table>
            </TableContainer>

            {/* Диалог создания/редактирования */}
            <Dialog open={openDialog} onClose={() => setOpenDialog(false)} maxWidth="md" fullWidth>
                <DialogTitle>{selectedOrder ? 'Edit Order' : 'Create Order'}</DialogTitle>
                <DialogContent>
                    <Grid container spacing={2} sx={{ mt: 1 }}>
                        <Grid item xs={12}>
                            <FormControl fullWidth>
                                <InputLabel>Client</InputLabel>
                                <Select
                                    value={formData.clientId}
                                    label="Client"
                                    onChange={(e) => setFormData({ ...formData, clientId: e.target.value })}
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
                                label="Price"
                                type="number"
                                value={formData.price}
                                onChange={(e) => setFormData({ ...formData, price: e.target.value })}
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

                        {/* Секция добавления товаров */}
                        <Grid item xs={12}>
                            <Typography variant="h6">Order Items</Typography>
                            <Paper sx={{ p: 2, mt: 1 }}>
                                <Grid container spacing={2}>
                                    <Grid item xs={5}>
                                        <FormControl fullWidth>
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
                                        <FormControl fullWidth>
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
                                        <Button variant="outlined" onClick={handleAddItem} fullWidth sx={{ height: '100%' }}>
                                            Add
                                        </Button>
                                    </Grid>
                                </Grid>

                                {formData.items.length > 0 && (
                                    <Box sx={{ mt: 2 }}>
                                        <Typography variant="subtitle2">Added Items:</Typography>
                                        <TableContainer component={Paper} sx={{ mt: 1, maxHeight: 300 }}>
                                            <Table size="small">
                                                <TableHead>
                                                    <TableRow>
                                                        <TableCell>Cargo</TableCell>
                                                        <TableCell>Route</TableCell>
                                                        <TableCell>Actions</TableCell>
                                                    </TableRow>
                                                </TableHead>
                                                <TableBody>
                                                    {formData.items.map((item, idx) => (
                                                        <TableRow key={idx}>
                                                            <TableCell>{item.cargoName} ({item.cargoWeight} kg)</TableCell>
                                                            <TableCell>{item.routeStart} → {item.routeEnd}</TableCell>
                                                            <TableCell>
                                                                <IconButton onClick={() => handleRemoveItem(idx)} color="error" size="small">
                                                                    <Delete />
                                                                </IconButton>
                                                            </TableCell>
                                                        </TableRow>
                                                    ))}
                                                </TableBody>
                                            </Table>
                                        </TableContainer>
                                    </Box>
                                )}
                            </Paper>
                        </Grid>

                        <Grid item xs={12}>
                            <Button
                                variant="contained"
                                onClick={handleSubmit}
                                fullWidth
                                disabled={loading}
                            >
                                {selectedOrder ? 'Update' : 'Create'}
                            </Button>
                        </Grid>
                    </Grid>
                </DialogContent>
            </Dialog>

            {/* Диалог назначения машины */}
            <Dialog open={openAssignDialog} onClose={() => setOpenAssignDialog(false)} maxWidth="sm" fullWidth>
                <DialogTitle>Assign Vehicle to Order #{selectedOrderForAssign?.id}</DialogTitle>
                <DialogContent>
                    <Box sx={{ display: 'flex', flexDirection: 'column', gap: 2, mt: 2 }}>
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
                        >
                            Assign Vehicle
                        </Button>
                    </Box>
                </DialogContent>
            </Dialog>

            <Snackbar
                open={snackbar.open}
                autoHideDuration={6000}
                onClose={() => setSnackbar({ ...snackbar, open: false })}
            >
                <Alert
                    severity={snackbar.severity}
                    onClose={() => setSnackbar({ ...snackbar, open: false })}
                >
                    {snackbar.message}
                </Alert>
            </Snackbar>
        </Box>
    );
};

export default Orders;