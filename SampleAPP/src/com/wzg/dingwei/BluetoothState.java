package com.wzg.dingwei;

public interface BluetoothState {
	public static final String bluetooth_connect_state = "bluetooth_connect_state";

	public static final int STATE_NONE = 0;//
	public static final int STATE_LISTEN = 1;//
	public static final int STATE_CONNECTING = 2;
	public static final int STATE_CONNECTED = 3;
	public static final int STATE_CONNECT_SUCCESS = 4;
	public static final int STATE_CONNECT_FAIL = 5;

	public static final int BLUETOOTH_NOT_FOUND = 6;

	public static final String bluetooth_name = "bluetooth_name";

}
