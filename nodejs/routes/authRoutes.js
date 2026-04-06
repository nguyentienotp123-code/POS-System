const express = require("express");
const router = express.Router();
const User = require("../models/User");

// register
router.post("/register", async (req, res) => {
    const user = await User.create(req.body);
    res.json(user);
});

// login
router.post("/login", async (req, res) => {
    const user = await User.findOne(req.body);
    if (!user) return res.status(401).json({ message: "Invalid login" });

    res.json(user);
});

module.exports = router;