const mongoose = require("mongoose");

const UserSchema = new mongoose.Schema({
    username: String,
    password: String,
    role: { type: String, default: "staff" }
});

module.exports = mongoose.model("User", UserSchema);