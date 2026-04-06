const mongoose = require("mongoose");

const TableSchema = new mongoose.Schema({
    name: String,
    status: { type: String, default: "empty" }
});

module.exports = mongoose.model("Table", TableSchema);