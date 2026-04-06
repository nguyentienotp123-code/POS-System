const express = require('express');
const mongoose = require('mongoose');
const cors = require('cors');

const app = express();
app.use(express.json());
app.use(cors());

// =============================================================
// 1. KẾT NỐI DATABASE
// =============================================================
const MONGO_URI = 'mongodb://127.0.0.1:27017/posdb';
mongoose.connect(MONGO_URI)
    .then(() => console.log("✅ MongoDB LUXURY POS: ĐÃ SẴN SÀNG"))
    .catch(err => console.error("❌ Lỗi kết nối MongoDB:", err.message));

// =============================================================
// 2. CÔNG CỤ HỖ TRỢ (UTILITIES)
// =============================================================
const safeParse = (str, fallback = {}) => {
    try {
        if (!str || str === "{}") return fallback;
        return typeof str === 'string' ? JSON.parse(str) : str;
    } catch (e) { return fallback; }
};

const safeStringify = (data) => {
    if (typeof data === 'string') return data;
    return JSON.stringify(data || {});
};

// =============================================================
// 3. ĐỊNH NGHĨA SCHEMAS (Cấu trúc linh hoạt để tránh lỗi 400)
// =============================================================
const TableSchema = new mongoose.Schema({
    id: String,
    name: String,
    status: { type: String, default: 'EMPTY' },
    currentTotal: { type: Number, default: 0 },
    orderedItems: { type: String, default: "{}" },
    itemNotes: { type: String, default: "{}" },
    checkInTime: { type: Number, default: 0 }
}, { _id: false, strict: false }); 

const Room = mongoose.model('Room', new mongoose.Schema({ 
    name: { type: String, required: true }, 
    tables: [TableSchema] 
}, { timestamps: true, strict: false }));

const Menu = mongoose.model('Menu', new mongoose.Schema({ 
    name: String, 
    products: [{ name: String, price: Number, image: String, id: String }] 
}));

const User = mongoose.model('User', new mongoose.Schema({ 
    username: { type: String, required: true, unique: true }, 
    password: { type: String, required: true }, 
    fullName: String, 
    role: { type: String, default: 'STAFF' } 
}));

const Customer = mongoose.model('Customer', new mongoose.Schema({ 
    name: { type: String, required: true }, 
    phone: { type: String, required: true }, 
    note: { type: String, default: "" }, 
    points: { type: Number, default: 0 }, 
    debt: { type: Number, default: 0 } 
}));

const Inventory = mongoose.model('Inventory', new mongoose.Schema({ 
    itemName: String, 
    type: String, 
    quantity: Number, 
    unit: String, 
    note: String, 
    timestamp: { type: Date, default: Date.now } 
}));

const Report = mongoose.model('Report', new mongoose.Schema({
    tableId: String,
    tableName: String,
    totalAmount: Number,
    details: { type: String, default: "{}" },
    timestamp: { type: Date, default: Date.now }
}));

// =============================================================
// 4. API ROUTES
// =============================================================

// --- 4.1 QUẢN LÝ KHU VỰC (ROOMS) ---

// Lấy danh sách khu vực
app.get('/api/rooms', async (req, res) => {
    try {
        const rooms = await Room.find().lean();
        const formatted = rooms.map(r => ({
            ...r,
            tables: (r.tables || []).map(t => ({
                ...t,
                orderedItems: safeParse(t.orderedItems),
                itemNotes: safeParse(t.itemNotes)
            }))
        }));
        res.json(formatted);
    } catch (e) { res.status(500).json({ error: e.message }); }
});

// THÊM 1 KHU VỰC LẺ
app.post('/api/rooms', async (req, res) => {
    try {
        const { name, tables } = req.body;
        if (!name) return res.status(400).json({ error: "Tên khu vực không được để trống" });
        
        // Ép các object giỏ hàng về String
        const processedTables = (tables || []).map(t => ({
            ...t,
            orderedItems: safeStringify(t.orderedItems),
            itemNotes: safeStringify(t.itemNotes)
        }));

        const newRoom = new Room({ name, tables: processedTables });
        res.json(await newRoom.save());
    } catch (e) { res.status(400).json({ error: e.message }); }
});

// ✅ LƯU TOÀN BỘ DANH SÁCH (Đã fix lỗi ép kiểu và _id)
app.post('/api/rooms/save', async (req, res) => { 
    try { 
        if (!Array.isArray(req.body)) return res.status(400).json({ error: "Dữ liệu phải là một mảng []" });
        
        // Xử lý làm sạch dữ liệu trước khi lưu
        const processedRooms = req.body.map(room => {
            const r = { ...room };
            
            // Xóa _id nếu có để tránh lỗi CastError do UUID của Android
            delete r._id; 
            
            // Ép toàn bộ object giỏ hàng về dạng String để MongoDB không báo lỗi
            r.tables = (r.tables || []).map(t => ({
                ...t,
                orderedItems: safeStringify(t.orderedItems),
                itemNotes: safeStringify(t.itemNotes)
            }));
            
            return r;
        });

        await Room.deleteMany({}); 
        res.json(await Room.insertMany(processedRooms)); 
    } catch (e) { 
        console.error("Lỗi API saveRooms:", e);
        res.status(400).json({ error: e.message }); 
    } 
});

// Cập nhật tên/thông খুন khu vực
app.put('/api/rooms/:id', async (req, res) => {
    try {
        const updated = await Room.findByIdAndUpdate(req.params.id, req.body, { new: true });
        res.json(updated);
    } catch (e) { res.status(400).json({ error: e.message }); }
});

// Xóa khu vực
app.delete('/api/rooms/:id', async (req, res) => {
    try {
        await Room.findByIdAndDelete(req.params.id);
        res.json({ success: true });
    } catch (e) { res.status(500).json({ error: e.message }); }
});

// --- 4.2 QUẢN LÝ BÀN & ORDER ---

// Cập nhật Order (Gửi bếp)
app.post('/api/tables/update-order', async (req, res) => {
    try {
        const { tableId, status, orderedItems, currentTotal, itemNotes, checkInTime } = req.body;
        await Room.findOneAndUpdate(
            { "tables.id": tableId },
            { $set: { 
                "tables.$.status": status, 
                "tables.$.orderedItems": safeStringify(orderedItems), 
                "tables.$.currentTotal": currentTotal, 
                "tables.$.itemNotes": safeStringify(itemNotes), 
                "tables.$.checkInTime": checkInTime 
            }}
        );
        res.json({ success: true });
    } catch (e) { res.status(500).json({ error: e.message }); }
});

// Thanh toán & Xóa trạng thái bàn
app.post('/api/tables/pay-and-clear', async (req, res) => {
    try {
        const { tableId, totalAmount, tableName } = req.body;
        const room = await Room.findOne({ "tables.id": tableId }).lean();
        
        if (room) {
            const table = room.tables.find(t => t.id === tableId);
            const finalItemsStr = (table && table.orderedItems !== "{}") ? table.orderedItems : safeStringify(req.body.orderedItems);
            
            if (totalAmount > 0) {
                await Report.create({
                    tableId, tableName: tableName || "Bàn",
                    totalAmount, details: finalItemsStr, timestamp: new Date()
                });
            }
        }

        await Room.findOneAndUpdate(
            { "tables.id": tableId },
            { $set: { "tables.$.status": "EMPTY", "tables.$.orderedItems": "{}", "tables.$.currentTotal": 0, "tables.$.checkInTime": 0 }}
        );
        res.json({ success: true });
    } catch (e) { res.status(500).json({ error: e.message }); }
});

// --- 4.3 KHO, BÁO CÁO, NHÂN VIÊN ---

// INVENTORIES (Kho)
app.get('/api/inventories', async (req, res) => {
    try {
        const { date } = req.query;
        let query = {};
        if (date) {
            const start = new Date(date); start.setHours(0,0,0,0);
            const end = new Date(date); end.setHours(23,59,59,999);
            query.timestamp = { $gte: start, $lte: end };
        }
        res.json(await Inventory.find(query).sort({ timestamp: -1 }));
    } catch (e) { res.status(500).json({ error: e.message }); }
});
app.post('/api/inventories', async (req, res) => {
    try { res.json(await new Inventory({ ...req.body, timestamp: new Date() }).save()); }
    catch (e) { res.status(400).json({ error: e.message }); }
});
// ✅ Bổ sung PUT & DELETE cho Kho
app.put('/api/inventories/:id', async (req, res) => {
    try { res.json(await Inventory.findByIdAndUpdate(req.params.id, req.body, { new: true })); } 
    catch (e) { res.status(400).json({ error: e.message }); }
});
app.delete('/api/inventories/:id', async (req, res) => {
    try { await Inventory.findByIdAndDelete(req.params.id); res.json({ success: true }); } 
    catch (e) { res.status(500).json({ error: e.message }); }
});

// REPORTS (Báo cáo)
app.get('/api/reports', async (req, res) => {
    try {
        const { date } = req.query;
        let query = {};
        if (date) {
            const start = new Date(date); start.setHours(0,0,0,0);
            const end = new Date(date); end.setHours(23,59,59,999);
            query.timestamp = { $gte: start, $lte: end };
        }
        const data = await Report.find(query).sort({ timestamp: -1 }).lean();
        res.json(data.map(doc => ({ ...doc, details: safeParse(doc.details) })));
    } catch (e) { res.status(500).json({ error: e.message }); }
});

// USERS (Nhân viên)
app.get('/api/users', async (req, res) => res.json(await User.find().lean()));
app.post('/api/users', async (req, res) => {
    try { res.json(await new User(req.body).save()); } 
    catch (e) { res.status(400).send("Lỗi: Tài khoản đã tồn tại!"); }
});
// ✅ Bổ sung PUT & DELETE cho Nhân viên
app.put('/api/users/:id', async (req, res) => {
    try { res.json(await User.findByIdAndUpdate(req.params.id, req.body, { new: true })); } 
    catch (e) { res.status(400).json({ error: e.message }); }
});
app.delete('/api/users/:id', async (req, res) => {
    try { await User.findByIdAndDelete(req.params.id); res.json({ success: true }); } 
    catch (e) { res.status(500).json({ error: e.message }); }
});

// CUSTOMERS (Khách hàng)
app.get('/api/customers', async (req, res) => res.json(await Customer.find().sort({ points: -1 }).lean()));
app.post('/api/customers', async (req, res) => res.json(await new Customer(req.body).save()));
// ✅ Bổ sung PUT & DELETE cho Khách hàng
app.put('/api/customers/:id', async (req, res) => {
    try { res.json(await Customer.findByIdAndUpdate(req.params.id, req.body, { new: true })); } 
    catch (e) { res.status(400).json({ error: e.message }); }
});
app.delete('/api/customers/:id', async (req, res) => {
    try { await Customer.findByIdAndDelete(req.params.id); res.json({ success: true }); } 
    catch (e) { res.status(500).json({ error: e.message }); }
});

// MENU (Thực đơn)
app.get('/api/menu', async (req, res) => res.json(await Menu.find().lean()));
app.post('/api/menu/save', async (req, res) => {
    try { await Menu.deleteMany({}); res.json(await Menu.insertMany(req.body)); } 
    catch (e) { res.status(400).json({ error: e.message }); }
});
// ✅ Bổ sung PUT cho Menu
app.put('/api/menu/:id', async (req, res) => {
    try { res.json(await Menu.findByIdAndUpdate(req.params.id, req.body, { new: true })); } 
    catch (e) { res.status(400).json({ error: e.message }); }
});

// =============================================================
// 5. KHỞI CHẠY SERVER
// =============================================================
const PORT = 3000;
app.listen(PORT, '0.0.0.0', () => {
    console.log(`===========================================`);
    console.log(`🚀 LUXURY POS: http://localhost:${PORT}`);
    console.log(`===========================================`);
});