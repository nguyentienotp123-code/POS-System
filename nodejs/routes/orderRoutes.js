const express = require("express");
const router = express.Router();
const Order = require("../models/Order");

// create order
router.post("/", async (req, res) => {
    await Order.create(req.body);
    res.json({ message: "Order saved" });
});

// get all orders
router.get("/", async (req, res) => {
    res.json(await Order.find());
});

// report revenue
router.get("/report", async (req, res) => {
    const orders = await Order.find();

    const total = orders.reduce((sum, o) => sum + o.total, 0);

    res.json({
        totalRevenue: total,
        totalOrders: orders.length
    });
});

module.exports = router;