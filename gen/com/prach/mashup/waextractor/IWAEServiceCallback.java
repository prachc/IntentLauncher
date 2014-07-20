/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: C:\\Users\\Korawit\\Documents\\My Dropbox\\android_mashup\\IntentLauncher\\src\\com\\prach\\mashup\\waextractor\\IWAEServiceCallback.aidl
 */
package com.prach.mashup.waextractor;
public interface IWAEServiceCallback extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements com.prach.mashup.waextractor.IWAEServiceCallback
{
private static final java.lang.String DESCRIPTOR = "com.prach.mashup.waextractor.IWAEServiceCallback";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an com.prach.mashup.waextractor.IWAEServiceCallback interface,
 * generating a proxy if needed.
 */
public static com.prach.mashup.waextractor.IWAEServiceCallback asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof com.prach.mashup.waextractor.IWAEServiceCallback))) {
return ((com.prach.mashup.waextractor.IWAEServiceCallback)iin);
}
return new com.prach.mashup.waextractor.IWAEServiceCallback.Stub.Proxy(obj);
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
case TRANSACTION_dofinish:
{
data.enforceInterface(DESCRIPTOR);
this.dofinish();
reply.writeNoException();
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements com.prach.mashup.waextractor.IWAEServiceCallback
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
public void dofinish() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_dofinish, _data, _reply, 0);
_reply.readException();
}
finally {
_reply.recycle();
_data.recycle();
}
}
}
static final int TRANSACTION_dofinish = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
}
public void dofinish() throws android.os.RemoteException;
}
