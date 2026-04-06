const express = require('express');
const router = express.Router();
const Room = require('../models/Room');

// Lấy toàn bộ danh sách phòng và bàn
router.get('/', async (req, res) => {
    try {
        const rooms = await Room.find();
        res.json(rooms);
    } catch (err) {
        res.status(500).json({ message: err.message });
    }
});

// Lưu (Ghi đè) toàn bộ cấu hình phòng bàn
router.post('/save', async (req, res) => {
    try {
        await Room.deleteMany({}); // Xóa dữ liệu cũ để cập nhật bộ mới
        const savedRooms = await Room.insertMany(req.body);
        res.status(200).json(savedRooms);
    } catch (err) {
        res.status(400).json({ message: err.message });
    }
});

module.exports = router;