package com.wzg.dingwei;

import java.text.DecimalFormat;

import android.util.Log;

public class BluetoothDataUtil {

	public static String strRoll = "";

	public static String strPitch = "";

	public static String strAzimuth = "";

	public static final String X057Header = "0A0D7E57";
	public static final String X070Header = "0A0D7E70";
	private static DecimalFormat df = new DecimalFormat("#0.0000");

	public static String toHex(byte[] buffer, int len) {

		StringBuffer sb = new StringBuffer(buffer.length * 2);

		for (int i = 0; i < len; i++) {

			sb.append(Character.forDigit((buffer[i] & 240) >> 4, 16));

			sb.append(Character.forDigit(buffer[i] & 15, 16));
		}

		return sb.toString();

	}

	public static String reHexString(String strHex) {
		String result = "";
		for (int i = 0; i < 4; i++) {
			String hex = strHex.substring(6 - i * 2, 8 - i * 2);
			result = result + hex;
		}
		return result;
	}

	public static String reHeigHexString(String strHex) {
		String result = strHex.substring(2, 4);
		result = result + strHex.substring(0, 2);
		return result;
	}

	public static String printHexString(byte[] b, int len) {
		String result = "";
		for (int i = 0; i < len; i++) {
			String hex = Integer.toHexString(b[i] & 0xFF) + " ";
			if (hex.length() == 2) {
				hex = '0' + hex;
			}
			result = result + hex.toUpperCase();
		}
		return result;
	}

	public static void byte2hex(byte b, StringBuffer buf) {
		char[] hexChars = { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9',
				'a', 'b', 'c', 'd', 'e', 'f' };
		int high = ((b & 0xf0) >> 4);
		int low = (b & 0x0f);
		buf.append(hexChars[high]);
		buf.append(hexChars[low]);

	}

	public static String toHexString(byte[] block) {
		StringBuffer buf = new StringBuffer();
		int len = block.length;
		for (int i = 0; i < len; i++) {
			byte2hex(block[i], buf);
		}
		return buf.toString();
	}

	public static String toHexString(byte[] block, int length) {
		StringBuffer buf = new StringBuffer();
		int len = length;
		for (int i = 0; i < len; i++) {
			byte2hex(block[i], buf);
		}
		return buf.toString();
	}

	public static byte[] hex2byte(String hexString) {
		if (hexString == null || hexString.equals("")) {
			return null;
		}
		hexString = hexString.toUpperCase();
		int length = hexString.length() / 2;
		char[] hexChars = hexString.toCharArray();
		byte[] d = new byte[length];
		for (int i = 0; i < length; i++) {
			int pos = i * 2;
			d[i] = (byte) (charToByte(hexChars[pos]) << 4 | charToByte(hexChars[pos + 1]));
		}
		return d;
	}

	public static byte charToByte(char c) {
		return (byte) "0123456789ABCDEF".indexOf(c);
	}
	
	public static String bytesToHexString(byte[] bytes,int len){   
		String hexStr =  "0123456789ABCDEF";  
		String result = "";   
        String hex = "";   
        for(int i=0;i<len;i++){   
            //字节高4位   
            hex = String.valueOf(hexStr.charAt((bytes[i]&0xF0)>>4));   
            //字节低4位   
            hex += String.valueOf(hexStr.charAt(bytes[i]&0x0F));   
            result +=hex;   
        }   
        return result;       
	}



	public static boolean founded70Data(StringBuilder bluetoothData,
			int headerIndex) {

		// 获取数据长度的索引位置
		int dataCountIndex = headerIndex + X070Header.length();
		// 求数据长度的16进制数据字符串
		String sizeStr = bluetoothData.substring(dataCountIndex,
				dataCountIndex + 2);
		// 转换16进制字符串成整型
		int dataSize = Integer.parseInt(sizeStr, 16);
//		Log.d("log", "sizeHex => " + sizeStr + " , index => " + headerIndex
//				+ " , size => " + dataSize);
		// 中间的数据内容，每个字节两个字符串表示一个16进制数
		String realData = bluetoothData.substring(dataCountIndex + 2,
				dataCountIndex + 2 + dataSize * 2);

//		Log.d("log", realData + " === size => " + realData.length());

		int si = 0;
		int sum = 0;
		// 获取从数据头开始到真实数据的前面这一段数据，用于较难最后一个字节的数据正确
		StringBuilder sbd = new StringBuilder(bluetoothData.substring(
				headerIndex, headerIndex + realData.length() + 10));
		// 全部数据备份
		String allDataBak = sbd.toString();
//		Log.d("log", "sbd => " + allDataBak);

		while (true) {
			// 取每两位的16进制转字节
			int item = Integer.parseInt(sbd.substring(si, si + 2), 16);
			sum += item;

			// 除次删一个16进度数
			sbd.delete(si, si + 2);
			if (sbd.length() < 2) {
				StringBuilder hex = new StringBuilder(
						Integer.toHexString(sum & 0xFF));

				// 求出之前的全部的值用于效验
				if (hex.length() == 1) {
					hex = new StringBuilder(hex.insert(0, '0').toString()
							.toUpperCase());
				}

				int start = headerIndex + allDataBak.length();
				int end = headerIndex + allDataBak.length() + 2;
				String dataSumValidate = bluetoothData.substring(start, end);
//				Log.d("log", "start => " + start + " , end => " + end);
//				Log.d("log", "Sum => " + sum + " , SumHex => "
//						+ hex.toString().toUpperCase() + " , hex => "
//						+ Integer.toHexString(sum).toUpperCase()
//						+ " , dataSumValidate => " + dataSumValidate);

				if (hex.toString().toUpperCase()
						.equals(dataSumValidate.toUpperCase())) {


					String temp = realData.substring(0, 4);
					strRoll = String.valueOf(Integer.parseInt(
							reHeigHexString(temp), 16));

					temp = realData.substring(4, 8);
					strPitch = String.valueOf(Integer.parseInt(
							reHeigHexString(temp), 16));

					temp = realData.substring(8, 12);
					strAzimuth = String.valueOf(Integer.parseInt(
							reHeigHexString(temp), 16));

					bluetoothData.delete(0, headerIndex + allDataBak.length()
							+ 2);
//					Log.d("log", "70数据正确！删除后 => " + bluetoothData.toString());

					return true;
				} else {
					Log.d("log", "70数据错误！！！");

					return false;
				}
			}
		}
	}

//	public static ArrayList<BluetoothItemData> getItemBluetoothData(
//			StringBuilder bluetoothData) {
//
//		ArrayList<BluetoothItemData> result = new ArrayList<BluetoothItemData>();
//
//		while (true) {
//			boolean founded = false;
//
//			try {
//				// 求协议头位置
//				int header57Index = bluetoothData.indexOf(X057Header);
//				int header70Index = bluetoothData.indexOf(X070Header);
//
////				Log.d("log", "header57Index => " + header57Index
////						+ " , header70Index => " + header70Index);
//
//				if (header57Index != -1 && header70Index != -1) {
//					if (header57Index < header70Index) {
//						founded = founded57Data(bluetoothData, header57Index,
//								result);
//						if (!founded) {
//							founded = founded70Data(bluetoothData,
//									header70Index);
//						}
//					} else {
//						founded = founded70Data(bluetoothData, header70Index);
//						if (!founded) {
//							founded = founded57Data(bluetoothData,
//									header57Index, result);
//						}
//					}
//				} else if (header57Index != -1) {
//					founded = founded57Data(bluetoothData, header57Index,
//							result);
//				} else if (header70Index != -1) {
//					founded = founded70Data(bluetoothData, header70Index);
//				}
//				/**
//				 * 70，57 都找不到
//				 */
//				if (!founded) {
//					bluetoothData.delete(0, 2);
////					System.out.println(bluetoothData.toString());
//					break;
//				}
//			} catch (StringIndexOutOfBoundsException e) {
//				System.out.println("数据完了");
//				break;
//			}
//		}
//		return result;
//	}

}
