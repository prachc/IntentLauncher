package com.prach.mashup.wsconnector;

interface IWSCService {
	String[] connectWS(String mode, String query,String base,in String[] paths,in String[] keys,in String[] values);
}