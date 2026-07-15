package com.smsmanager.app;

import android.Manifest;
import android.telephony.SmsManager;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.PermissionState;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;
import com.getcapacitor.annotation.PermissionCallback;

import java.util.ArrayList;

// إضافة Capacitor أصلية لإرسال رسائل SMS مباشرة عبر شريحة SIM الافتراضية
// في الجهاز (بدون إنترنت، فقط تغطية شبكة جوال). تُسجَّل هذه الإضافة داخل
// MainActivity باسم "SmsSender" وتُستدعى من public/smsBridge.js.
@CapacitorPlugin(
    name = "SmsSender",
    permissions = {
        @Permission(strings = { Manifest.permission.SEND_SMS }, alias = "sms")
    }
)
public class SmsSenderPlugin extends Plugin {

    @PluginMethod
    public void send(PluginCall call) {
        String phone = call.getString("phone");
        String message = call.getString("message");

        if (phone == null || phone.isEmpty() || message == null || message.isEmpty()) {
            call.reject("رقم الهاتف أو نص الرسالة مفقود");
            return;
        }

        if (getPermissionState("sms") != PermissionState.GRANTED) {
            saveCall(call);
            requestPermissionForAlias("sms", call, "smsPermissionCallback");
            return;
        }

        sendSmsNow(call, phone, message);
    }

    @PermissionCallback
    private void smsPermissionCallback(PluginCall call) {
        if (getPermissionState("sms") == PermissionState.GRANTED) {
            sendSmsNow(call, call.getString("phone"), call.getString("message"));
        } else {
            call.reject("تم رفض إذن إرسال الرسائل (SEND_SMS)");
        }
    }

    private void sendSmsNow(PluginCall call, String phone, String message) {
        try {
            SmsManager smsManager = SmsManager.getDefault();
            ArrayList<String> parts = smsManager.divideMessage(message);
            if (parts.size() > 1) {
                smsManager.sendMultipartTextMessage(phone, null, parts, null, null);
            } else {
                smsManager.sendTextMessage(phone, null, message, null, null);
            }
            JSObject ret = new JSObject();
            ret.put("success", true);
            call.resolve(ret);
        } catch (Exception e) {
            call.reject("فشل إرسال الرسالة: " + e.getMessage(), e);
        }
    }
}
