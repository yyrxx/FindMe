package com.findme.app.data.remote

data class AuthRequest(val email: String, val password: String)
data class AuthResponse(val message: String, val user_id: String?)
data class DeviceRegisterRequest(val user_id: String, val device_name: String, val device_token: String)
data class DeviceResponse(val message: String, val device_id: String?)
