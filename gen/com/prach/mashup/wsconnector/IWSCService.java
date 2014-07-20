/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Korawit\\Documents\\My Dropbox\\android_mashup\\IntentLauncher\\src\\com\\prach\\mashup\\wsconnector\\IWSCService.aidl
 */
package com.prach.mashup.wsconnector;
public interface IWSCService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.prach.mashup.wsconnector.IWSCService
{
private static final java.lang.String DESCRIPTOR = "com.prach.mashup.wsconnector.IWSCService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.prach.mashup.wsconnector.IWSCService interface,
 * generating a proxy if needed.
 */
public static com.prach.mashup.wsconnector.IWSCService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.prach.mashup.wsconnector.IWSCService))) {
return ((com.prach.mashup.wsconnector.IWSCService)iin);
}
return new com.prach.mashup.wsconnector.IWSCService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_connectWS:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String _arg1;
_arg1 = data.readString();
java.lang.String _arg2;
_arg2 = data.readString();
java.lang.String[] _arg3;
_arg3 = data.createStringArray();
java.lang.String[] _arg4;
_arg4 = data.createStringArray();
java.lang.String[] _arg5;
_arg5 = data.createStringArray();
java.lang.String[] _result = this.connectWS(_arg0, _arg1, _arg2, _arg3, _arg4, _arg5);
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.prach.mashup.wsconnector.IWSCService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.lang.String[] connectWS(java.lang.String mode, java.lang.String query, java.lang.String base, java.lang.String[] paths, java.lang.String[] keys, java.lang.String[] values) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(mode);
_data.writeString(query);
_data.writeString(base);
_data.writeStringArray(paths);
_data.writeStringArray(keys);
_data.writeStringArray(values);
mRemote.transact(Stub.TRANSACTION_connectWS, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_connectWS = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public java.lang.String[] connectWS(java.lang.String mode, java.lang.String query, java.lang.String base, java.lang.String[] paths, java.lang.String[] keys, java.lang.String[] values) throws android.os.RemoteException;
}
