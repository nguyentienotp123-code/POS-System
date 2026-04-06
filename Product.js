const mongoose = require("mongoose");

const ProductSchema = new mongoose.Schema({
    name: String,
    price: Number,
    barcode: String
});

module.exports = mongoose.model("Product", ProductSchema);