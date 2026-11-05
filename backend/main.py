from fastapi import FastAPI, HTTPException, status
from pydantic import BaseModel, EmailStr
from typing import Optional

app = FastAPI(title="FindMe Backend API", version="1.0.0")

# نماذج البيانات (Pydantic Models) المطابقة لتطبيق الأندرويد
class AuthRequest(BaseModel):
    email: EmailStr
    password: str

class AuthResponse(BaseModel):
    message: str
    user_id: Optional[str] = None

class DeviceRegisterRequest(BaseModel):
    user_id: str
    device_name: str
    device_token: str

class DeviceResponse(BaseModel):
    message: str
    device_id: Optional[str] = None


@app.get("/")
def read_root():
    return {"status": "FindMe API is running securely."}


@app.post("/api/auth/login", response_model=AuthResponse)
def login(request: AuthRequest):
    # محاكاة عملية التحقق من بيانات المستخدم
    if request.email and len(request.password) >= 6:
        # هنا يمكنك ربط قاعدة بيانات حقيقية مثل PostgreSQL أو SQLite في الخادم
        return AuthResponse(
            message="تم تسجيل الدخول بنجاح",
            user_id="user_internal_id_12345"
        )
    raise HTTPException(
        status_code=status.HTTP_401_UNAUTHORIZED,
        detail="بيانات الاعتماد غير صحيحة"
    )


@app.post("/api/device/register", response_model=DeviceResponse)
def register_device(request: DeviceRegisterRequest):
    if not request.user_id or not request.device_token:
        raise HTTPException(
            status_code=status.HTTP_400_BAD_REQUEST,
            detail="بيانات الجهاز غير مكتملة"
        )
    
    # حفظ بيانات الجهاز في الخادم لإرسال الأوامر لاحقاً
    return DeviceResponse(
        message="تم تسجيل الجهاز بنجاح",
        device_id="dev_uuid_98765"
    )
