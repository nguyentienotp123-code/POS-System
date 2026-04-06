const express = require("express");
const router = express.Router();
const Table = require("../models/Table");

// get tables
router.get("/", async (req, res) => {
    res.json(await Table.find());
});

// create table
router.post("/", async (req, res) => {
    await Table.create(req.body);
    res.sendStatus(200);
});

module.exports = router;