// models/Order.js
const mongoose = require('mongoose');

const OrderSchema = new mongoose.Schema({
    tableId: { type: mongoose.Schema.Types.ObjectId, ref: 'Table' },
    items: [{
        productId: { type: mongoose.Schema.Types.ObjectId, ref: 'Product' },
        quantity: Number,
        price: Number
    }],
    totalAmount: Number,
    discount: { type: Number, default: 0 },
    staffName: String,
    status: { type: String, default: 'PAID' }, // PAID, CANCELLED
    createdAt: { type: Date, default: Date.now }
});

module.exports = mongoose.model('Order', OrderSchema);