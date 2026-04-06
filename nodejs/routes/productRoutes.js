const express = require("express");
const router = express.Router();
const Product = require("../models/Product");

// get all
router.get("/", async (req, res) => {
    res.json(await Product.find());
});

// add product
router.post("/", async (req, res) => {
    await Product.create(req.body);
    res.sendStatus(200);
});

module.exports = router;