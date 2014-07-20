package com.prach.mashup.waextractor;

import com.prach.mashup.waextractor.IWAEServiceCallback;

interface IWAEService{
	void startExtract(String URL, in String[] scripts);
	String[] getNames();
	String[] getOutputs();
	void registerCallback(IWAEServiceCallback cb);
    void unregisterCallback(IWAEServiceCallback cb);
}