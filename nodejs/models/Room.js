const path = require("path");
const express = require("express");
const mongoose = require("mongoose");
const cors = require("cors");

// 1. Cấu hình nạp .env tuyệt đối
require("dotenv").config({ path: path.resolve(__dirname, './.env') });

const app = express();
app.use(cors());
app.use(express.json()); // ✅ Bắt buộc để nhận JSON từ Android

// 2. Định nghĩa Schema
const roomSchema = new mongoose.Schema({
    name: String,
    tables: [{ id: String, name: String, status: { type: String, default: "EMPTY" } }]
});
const Room = mongoose.model("Room", roomSchema);

// --- 3. CÁC ROUTE API ---

// Lấy danh sách phòng
app.get("/api/rooms", async (req, res) => {
    try {
        const rooms = await Room.find();
        console.log(`[GET] App yêu cầu dữ liệu. Đã gửi ${rooms.length} phòng.`);
        res.json(rooms);
    } catch (err) {
        res.status(500).json({ error: err.message });
    }
});

// Lưu cấu hình (Dành cho nút LƯU trên App)
app.post("/api/rooms/save", async (req, res) => {
    try {
        const roomsData = req.body;
        console.log("-----------------------------------------");
        console.log("📥 [POST] NHẬN DỮ LIỆU TỪ ANDROID:");
        console.log(JSON.stringify(roomsData, null, 2));
        
        await Room.deleteMany({});
        await Room.insertMany(roomsData);
        
        console.log("✅ Đã lưu vào MongoDB thành công!");
        console.log("-----------------------------------------");
        res.status(200).send("Lưu thành công!");
    } catch (err) {
        console.error("❌ Lỗi khi lưu:", err.message);
        res.status(500).json({ error: err.message });
    }
});

// 4. Kết nối và Chạy
const MONGO_URI = process.env.MONGO_URI || "mongodb://127.0.0.1:27017/posdb";
mongoose.connect(MONGO_URI)
    .then(() => {
        console.log("✅ KẾT NỐI MONGODB THÀNH CÔNG!");
        app.listen(3000, "0.0.0.0", () => { // "0.0.0.0" để nhận mọi kết nối
            console.log("🚀 Server POSApp đang chạy tại cổng 3000");
        });
    })
    .catch(err => console.error("❌ LỖI DB:", err.message));