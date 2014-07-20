/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Korawit\\Documents\\My Dropbox\\android_mashup\\IntentLauncher\\src\\com\\prach\\mashup\\waextractor\\IWAEService.aidl
 */
package com.prach.mashup.waextractor;
public interface IWAEService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.prach.mashup.waextractor.IWAEService
{
private static final java.lang.String DESCRIPTOR = "com.prach.mashup.waextractor.IWAEService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.prach.mashup.waextractor.IWAEService interface,
 * generating a proxy if needed.
 */
public static com.prach.mashup.waextractor.IWAEService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.prach.mashup.waextractor.IWAEService))) {
return ((com.prach.mashup.waextractor.IWAEService)iin);
}
return new com.prach.mashup.waextractor.IWAEService.Stub.Proxy(obj);
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
case TRANSACTION_startExtract:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String _arg0;
_arg0 = data.readString();
java.lang.String[] _arg1;
_arg1 = data.createStringArray();
this.startExtract(_arg0, _arg1);
reply.writeNoException();
return true;
}
case TRANSACTION_getNames:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.getNames();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_getOutputs:
{
data.enforceInterface(DESCRIPTOR);
java.lang.String[] _result = this.getOutputs();
reply.writeNoException();
reply.writeStringArray(_result);
return true;
}
case TRANSACTION_registerCallback:
{
data.enforceInterface(DESCRIPTOR);
com.prach.mashup.waextractor.IWAEServiceCallback _arg0;
_arg0 = com.prach.mashup.waextractor.IWAEServiceCallback.Stub.asInterface(data.readStrongBinder());
this.registerCallback(_arg0);
reply.writeNoException();
return true;
}
case TRANSACTION_unregisterCallback:
{
data.enforceInterface(DESCRIPTOR);
com.prach.mashup.waextractor.IWAEServiceCallback _arg0;
_arg0 = com.prach.mashup.waextractor.IWAEServiceCallback.Stub.asInterface(data.readStrongBinder());
this.unregisterCallback(_arg0);
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.prach.mashup.waextractor.IWAEService
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
public void startExtract(java.lang.String URL, java.lang.String[] scripts) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeString(URL);
_data.writeStringArray(scripts);
mRemote.transact(Stub.TRANSACTION_startExtract, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public java.lang.String[] getNames() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getNames, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public java.lang.String[] getOutputs() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String[] _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getOutputs, _data, _reply, 0);
_reply.readException();
_result = _reply.createStringArray();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public void registerCallback(com.prach.mashup.waextractor.IWAEServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_registerCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
public void unregisterCallback(com.prach.mashup.waextractor.IWAEServiceCallback cb) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
_data.writeStrongBinder((((cb!=null))?(cb.asBinder()):(null)));
mRemote.transact(Stub.TRANSACTION_unregisterCallback, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_startExtract = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_getNames = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_getOutputs = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_registerCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
static final int TRANSACTION_unregisterCallback = (android.os.IBinder.FIRST_CALL_TRANSACTION + 4);
}
public void startExtract(java.lang.String URL, java.lang.String[] scripts) throws android.os.RemoteException;
public java.lang.String[] getNames() throws android.os.RemoteException;
public java.lang.String[] getOutputs() throws android.os.RemoteException;
public void registerCallback(com.prach.mashup.waextractor.IWAEServiceCallback cb) throws android.os.RemoteException;
public void unregisterCallback(com.prach.mashup.waextractor.IWAEServiceCallback cb) throws android.os.RemoteException;
}
