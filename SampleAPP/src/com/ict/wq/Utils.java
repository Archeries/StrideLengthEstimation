package com.ict.wq;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;

 

 

public class Utils {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	 /**
     * get all pcm file in specified directory
     * @param fileAbsolutePath
     * @return
     */
    	public static ArrayList<String> getTXTInDirectory(String fileAbsolutePath) {
    		ArrayList<String> vecFile = new ArrayList<String>();
    		File file = new File(fileAbsolutePath);
    		File[] subFile = file.listFiles();

    		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
     
    			if (!subFile[iFileLength].isDirectory()) {
    				String fileName = subFile[iFileLength].getName();
    				 
    				if (fileName.trim().toLowerCase().endsWith(".txt")) {
    					vecFile.add(fileName);
    				}
    			}
    		}
    		return vecFile;
    	}

	public static ArrayList arrayListRemoveSame(ArrayList list) {
		 
		for (int i = 0; i < list.size() - 1; i++) {
			for (int j = i + 1; j < list.size(); j++) {
				if (list.get(i).equals(list.get(j))) {
					list.remove(j);
					j--;
				}
			}
		}
		return list;
	}
	
	public static int getMaxOfList(List<String> data) {
		double max = Double.parseDouble(data.get(0));
		int maxId = 0;
		for (int i = 0; i < data.size(); i++) {
			if (Double.parseDouble(data.get(i)) > max) {
				max = Double.parseDouble(data.get(i));
				maxId = i;
			}

		}
//		System.out.println("数组第" + maxId + "个值最大且 最大值为：" + max);
		return maxId;
	}
	
	public static double[] readTxtfileTodoubleArr(String filename) {
		ArrayList<Float> arrayList = readTxtFile(filename);
		// System.out.println(arrayList.size());
		double[] data = new double[arrayList.size()];
		for (int i = 0; i < arrayList.size(); i++) {
			data[i] = arrayList.get(i);
		}
		return data;
	}

	/**
	 * 
	 * @param data
	 * @param filename
	 *            把数据流写入文件
	 */
	public static void writeToFile(double[] data, String filename) {
		File f = new File(filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < data.length; i++) {
				bw.write(String.valueOf(data[i]));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeToFile(ArrayList<Double> result, String filename) {
		File f = new File(filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < result.size(); i++) {
				bw.write(String.valueOf(result.get(i)));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	/**
	 * 
	 * @param file
	 * @return 从PCM文件获取数据流
	 */
	public static double[] getArray(String file) {
		try {
			BufferedInputStream bf = new BufferedInputStream(
					new FileInputStream(file));
			double[] data1;
			try {
				byte[] data = new byte[1];
				List<Byte> allData = new ArrayList<Byte>();
				while (bf.read(data) != -1) {
					allData.add(data[0]);
				}
				data1 = new double[allData.size() / 2];
				for (int i = 0; i < allData.size() - 1; i += 2) {
					data1[i / 2] = ((allData.get(i + 1) << 8) | allData.get(i) & 0xff);
				}
			} finally {
				bf.close();
			}
			System.out.println("data.length" + data1);
			return data1;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * 
	 * @param file
	 * @return 从PCM文件获取数据流
	 */
	public static double[] getArrayfromAndroid(String file) {
		try {
//			BufferedInputStream bf = new BufferedInputStream(
//					new FileInputStream(Environment
//							.getExternalStorageDirectory() + "//audio//" + file));
			BufferedInputStream bf = new BufferedInputStream(
					new FileInputStream(  file));
			double[] data1;
			try {
				byte[] data = new byte[1];
				List<Byte> allData = new ArrayList<Byte>();
				while (bf.read(data) != -1) {
					allData.add(data[0]);
				}
				data1 = new double[allData.size() / 2];
				for (int i = 0; i < allData.size() - 1; i += 2) {
					data1[i / 2] = ((allData.get(i + 1) << 8) | allData.get(i) & 0xff);
				}
			} finally {
				bf.close();
			}
//			System.out.println(file + "   " + Arrays.toString(data1));
			return data1;
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public static int getMaxOfArray(double[] data) {
		double max = data[0];
		int maxId = 0;
		for (int i = 0; i < data.length; i++) {
			if (data[i] > max) {
				max = data[i];
				maxId = i;
			}

		}
	//	System.out.println("数组第" + maxId + "个值最大且 最大值为：" + max);
		return maxId;
	}

	public static String getMaxOfArrayList(ArrayList<Double> arrayList) {
		double max = arrayList.get(0);
		int maxid=0;
		for (int i = 0; i < arrayList.size(); i++) {
			if (arrayList.get(i) > max) {
				max = arrayList.get(i);
				maxid=i;
			}
		}
		return max+"maxid"+maxid;
	}

	/**
	 * 求一组数据的波峰波谷点 对第一个点和最后一个点的处理为：必为波峰或波谷。 如应用中不处理这两个点，则将其注释掉即可。
	 * 对波峰波谷相等点的处理：如果波峰波谷左右仍相等，则均为波峰波谷。 若应用中对这种相等点只须取一个，则将程序中标注的一段if注释掉即可。
	 * 
	 * @param wave
	 *            []:待处理数组
	 * @param direction
	 *            :记录当前的增减趋势
	 */
	public static void getWavePeak(double[] wave) {

		// double[] wave = new double[] { 9.8, 2, 3, 3, 2, 1, 0, -1, 2, 3, 4,
		// -2,
		// -2, -1, -1 };
		int direction = (wave[1] - wave[0]) > 0 ? 1 : -1;

		// 处理最前面的点
		for (int i = 0;; i++) {
			if (wave[i] == wave[i + 1])
				continue;
			else if (wave[i] < wave[i + 1]) {
				for (int j = 0; j < i; j++) {
					System.out.println("序号为" + j + "的点：" + wave[j] + "为波谷");
				}
				break;
			} else {
				for (int j = 0; j < i; j++) {
					System.out.println("序号为" + j + "的点：" + wave[j] + "为波峰");
				}
				break;
			}
		}

		// 处理中间的点
		for (int i = 0; i < wave.length - 1; i++) {

			// /*处理相等点的情况，若在应用中相等点只须取一个则将这个if注释起来即可
			if (i > 0 && (wave[i] == wave[i + 1])) {
				if (direction == -1)
					System.out.println("序号为" + i + "的点：" + wave[i] + "为波峰");
				else
					System.out.println("序号为" + i + "的点：" + wave[i] + "为波谷");
			}
			// */

			if ((wave[i + 1] - wave[i]) * direction > 0) {

				direction *= -1;
				if (direction == 1) {
					System.out.println("序号为" + i + "的点：" + wave[i] + "为波峰");
				} else {
					System.out.println("序号为" + i + "的点：" + wave[i] + "为波谷");
				}

			}
		}
		// 处理最后的点
		for (int i = wave.length - 1;; i--) {
			if (wave[i] == wave[i - 1])
				continue;
			else if (wave[i - 1] < wave[i]) {
				for (int j = i + 1; j < wave.length; j++) {
					System.out.println("序号为" + j + "的点：" + wave[j] + "为波峰");
				}
				break;
			} else {
				for (int j = i + 1; j < wave.length; j++) {
					System.out.println("序号为" + j + "的点：" + wave[j] + "为波谷");
				}
				break;
			}
		}

	}

	public static double[] ArraylstToArrays(ArrayList<Double> data) {
		double[] doubledata = new double[data.size()];
//		System.out.println("data.size()"+data.size());
		for (int i = 0; i < data.size(); i++) {
		//	System.out.println("wwww"+data.get(i));
			doubledata[i] = data.get(i);
		}
		return doubledata;
	}

 

	public static ArrayList<Float> readTxtFile(String filePath) {
		ArrayList<Float> arrayList = new ArrayList<Float>();
		try {
			String encoding = "GBK";
//			File file = new File(Environment.getExternalStorageDirectory()
//					+ "//audio//" + filePath);
			File file = new File( filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println(lineTxt);
					arrayList.add(Float.parseFloat(lineTxt));
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return arrayList;

	}

	public static ArrayList<Double> readTxtFileTodouble(String filePath) {
		ArrayList<Double> arrayList = new ArrayList<Double>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println(lineTxt);
					arrayList.add(Double.parseDouble(lineTxt));
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return arrayList;

	}

	public static ArrayList<String> readTxtFileToString(String filePath) {
		ArrayList<String> arrayList = new ArrayList<String>();
		try {
			String encoding = "GBK";
			File file = new File(filePath);
			if (file.isFile() && file.exists()) { // 判断文件是否存在
				InputStreamReader read = new InputStreamReader(
						new FileInputStream(file), encoding);// 考虑到编码格式
				BufferedReader bufferedReader = new BufferedReader(read);
				String lineTxt = null;
				while ((lineTxt = bufferedReader.readLine()) != null) {
					// System.out.println(lineTxt);
					arrayList.add(lineTxt);
				}
				read.close();
			} else {
				System.out.println("找不到指定的文件");
			}
		} catch (Exception e) {
			System.out.println("读取文件内容出错");
			e.printStackTrace();
		}
		return arrayList;

	}
	
	
	/**
	 * 两个信号合成，叠加
	 * 
	 * @param subfile1
	 * @param subfile2
	 * @param totalfile
	 */
	public static void Signalcompound(String subfile1, String subfile2,
			String totalfile) {
		ArrayList<Double> BABBBarrayList = new ArrayList<Double>();
		BABBBarrayList = readTxtFileTodouble(subfile1);
		ArrayList<Double> huanjingarrayList = new ArrayList<Double>();
		huanjingarrayList = readTxtFileTodouble(subfile2);
		double[] BABBBandHuanjing = new double[BABBBarrayList.size()];
		for (int i = 0; i < BABBBarrayList.size(); i++) {
			BABBBandHuanjing[i] = BABBBarrayList.get(i)
					+ huanjingarrayList.get(i);
		}
		writeToFile(BABBBandHuanjing, totalfile);
	}



	// 写在/mnt/sdcard/目录下面的文件
	public static void writeFileSdcard(String fileName, String message) {
		System.out.println(message);

		try {

			// FileOutputStream fout = openFileOutput(fileName, MODE_PRIVATE);

			FileOutputStream fout = new FileOutputStream(fileName);

			byte[] bytes = message.getBytes();

			fout.write(bytes);

			fout.close();

		}

		catch (Exception e) {

			e.printStackTrace();

		}

	}

	/**
	 * 在SD卡上创建文件
	 * 
	 * @throws IOException
	 */
	public static File createSDFile(String path, String fileName)
			throws IOException {
		File file = new File(path + "//" + fileName);
		if (!file.exists()) {
			file.createNewFile();
		}
		return file;
	}

	/**
	 * 删除SD卡上的文件
	 * 
	 * @param fileName
	 */
	public static boolean deleteSDFile(String path, String fileName) {
		File file = new File(path + "//" + fileName);
		if (file == null || !file.exists() || file.isDirectory())
			return false;
		return file.delete();
	}

	/**
	 * 写入内容到SD卡中的txt文本中 str为内容
	 */
	public static void writeSDFile(String path, String str, String fileName) {
		try {
			FileWriter fw = new FileWriter(path + "//" + fileName);
			File f = new File(path + "//" + fileName);
			fw.write(str);
			FileOutputStream os = new FileOutputStream(f);
			DataOutputStream out = new DataOutputStream(os);
			out.writeShort(2);
			out.writeUTF("");
			System.out.println(out);
			fw.flush();
			fw.close();
			System.out.println(fw);
		} catch (Exception e) {
		}
	}

	/**
	 * 删除文件夹下的所有文件
	 * 
	 * @param oldPath
	 */
	public static void deleteFile(File oldPath) {
		if (oldPath.isDirectory()) {
			// System.out.println(oldPath + "是文件夹--");
			File[] files = oldPath.listFiles();
			for (File file : files) {
				deleteFile(file);
			}
		} else {
			oldPath.delete();
		}
	}
	public static double[] arrlistTodoubleArr(ArrayList<String> orginarrayList) {
		double[] orgindata = new double[orginarrayList.size()];
		for (int i = 0; i < orginarrayList.size(); i++) {
			String temp = orginarrayList.get(i);
			orgindata[i] = Double.parseDouble(temp);
		}
		return orgindata;
	}

	public static Double[] arrlistToDoubleArr(ArrayList<String> orginarrayList) {
		Double[] orgindata = new Double[orginarrayList.size()];
		for (int i = 0; i < orginarrayList.size(); i++) {
			String temp = orginarrayList.get(i);
			orgindata[i] = Double.parseDouble(temp);
		}
		return orgindata;
	}
	public static ArrayList<String> doubleArrToArraylist(double[] orginarray) {
		ArrayList<String> arrayList = new ArrayList<String>();
		for (int i = 0; i < orginarray.length; i++) {
			arrayList.add(String.valueOf(orginarray[i]));
		}
		return arrayList;
	}



	public static double[] ArraylstToArrays(List<String> templList) {
		// TODO Auto-generated method stub
		double[] tempdata=new double[templList.size()];
		for (int j = 0; j < templList.size(); j++) {//最后一段数据长度不确定，单独处理
			tempdata[j] = Double.parseDouble(templList.get(j));
		}
		return tempdata;
	}



	public static void writeToFile(List<String> yList, String filename) {
		// TODO Auto-generated method stub
		File f = new File(filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < yList.size(); i++) {
				bw.write(String.valueOf(yList.get(i)));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeToFile2(List<ArrayList<String>> tempString,
			String filename) {
		// TODO Auto-generated method stub
		File f = new File(filename);
		try {
			BufferedWriter bw = new BufferedWriter(new FileWriter(f));
			for (int i = 0; i < tempString.size(); i++) {
				bw.write(String.valueOf(tempString.get(i)));
				bw.newLine();
			}
			bw.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * 复制文件或文件夹
	 * 
	 * @param srcPath
	 * @param destDir
	 *            目标文件所在的目录
	 * @return
	 */
	public static boolean copyGeneralFile(String srcPath, String destDir) {
		boolean flag = false;
		File file = new File(srcPath);
		if (!file.exists()) {
			System.out.println("源文件或源文件夹不存在!");
			return false;
		}
		if (file.isFile()) { // 源文件
			System.out.println("下面进行文件复制!");
			flag = copyFile(srcPath, destDir);
		} else if (file.isDirectory()) {
			System.out.println("下面进行文件夹复制!");
			flag = copyDirectory(srcPath, destDir);
		}

		return flag;
	}

	/**
	 * 复制文件
	 * 
	 * @param srcPath
	 *            源文件绝对路径
	 * @param destDir
	 *            目标文件所在目录
	 * @return boolean
	 */
	private static boolean copyFile(String srcPath, String destDir) {
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件不存在
			System.out.println("源文件不存在");
			return false;
		}
		// 获取待复制文件的文件名
		String fileName = srcPath
				.substring(srcPath.lastIndexOf(File.separator));
		String destPath = destDir + fileName;
		if (destPath.equals(srcPath)) { // 源文件路径和目标文件路径重复
			System.out.println("源文件路径和目标文件路径重复!");
			return false;
		}
		File destFile = new File(destPath);
		if (destFile.exists() && destFile.isFile()) { // 该路径下已经有一个同名文件
			System.out.println("目标目录下已有同名文件!");
			return false;
		}

		File destFileDir = new File(destDir);
		destFileDir.mkdirs();
		try {
			FileInputStream fis = new FileInputStream(srcPath);
			FileOutputStream fos = new FileOutputStream(destFile);
			byte[] buf = new byte[1024];
			int c;
			while ((c = fis.read(buf)) != -1) {
				fos.write(buf, 0, c);
			}
			fis.close();
			fos.close();

			flag = true;
		} catch (IOException e) {
			//
		}

		if (flag) {
			System.out.println("复制文件成功!");
		}

		return flag;
	}

	/**
	 * 
	 * @param srcPath
	 *            源文件夹路径
	 * @param destPath
	 *            目标文件夹所在目录
	 * @return
	 */
	private static boolean copyDirectory(String srcPath, String destDir) {
		System.out.println("复制文件夹开始!");
		boolean flag = false;

		File srcFile = new File(srcPath);
		if (!srcFile.exists()) { // 源文件夹不存在
			System.out.println("源文件夹不存在");
			return false;
		}
		// 获得待复制的文件夹的名字，比如待复制的文件夹为"E://dir"则获取的名字为"dir"
		String dirName = getDirName(srcPath);
		// 目标文件夹的完整路径
		String destPath = destDir + File.separator + dirName;
		// System.out.println("目标文件夹的完整路径为：" + destPath);

		if (destPath.equals(srcPath)) {
			System.out.println("目标文件夹与源文件夹重复");
			return false;
		}
		File destDirFile = new File(destPath);
		if (destDirFile.exists()) { // 目标位置有一个同名文件夹
			System.out.println("目标位置已有同名文件夹!");
			return false;
		}
		destDirFile.mkdirs(); // 生成目录

		File[] fileList = srcFile.listFiles(); // 获取源文件夹下的子文件和子文件夹
		if (fileList.length == 0) { // 如果源文件夹为空目录则直接设置flag为true，这一步非常隐蔽，debug了很久
			flag = true;
		} else {
			for (File temp : fileList) {
				if (temp.isFile()) { // 文件
					flag = copyFile(temp.getAbsolutePath(), destPath);
				} else if (temp.isDirectory()) { // 文件夹
					flag = copyDirectory(temp.getAbsolutePath(), destPath);
				}
				if (!flag) {
					break;
				}
			}
		}

		if (flag) {
			System.out.println("复制文件夹成功!");
		}

		return flag;
	}

	/**
	 * 获取待复制文件夹的文件夹名
	 * 
	 * @param dir
	 * @return String
	 */
	private static String getDirName(String dir) {
		if (dir.endsWith(File.separator)) { // 如果文件夹路径以"//"结尾，则先去除末尾的"//"
			dir = dir.substring(0, dir.lastIndexOf(File.separator));
		}
		return dir.substring(dir.lastIndexOf(File.separator) + 1);
	}
	
	/**
	 * 删除文件
	 * 
	 * @param filePathAndName
	 *            String 文件路径及名称 如c:/fqf.txt
	 * @param fileContent
	 *            String
	 * @return boolean
	 */
	public static void delFile(String filePathAndName) {
		try {
			String filePath = filePathAndName;
			filePath = filePath.toString();
			java.io.File myDelFile = new java.io.File(filePath);
			myDelFile.delete();

		} catch (Exception e) {
			System.out.println("删除文件操作出错");
			e.printStackTrace();

		}

	}

	/** */
	/**
	 * 文件重命名
	 * 
	 * @param path
	 *            文件目录
	 * @param oldname
	 *            原来的文件名
	 * @param newname
	 *            新文件名
	 */
	public static void renameFile(String path, String oldname, String newname) {
		if (!oldname.equals(newname)) {// 新的文件名和以前文件名不同时,才有必要进行重命名
			File oldfile = new File(path + "/" + oldname);
			File newfile = new File(path + "/" + newname);
			if (!oldfile.exists()) {
				return;// 重命名文件不存在
			}
			if (newfile.exists())// 若在该目录下已经有一个文件和新文件名相同，则不允许重命名
				System.out.println(newname + "已经存在！");
			else {
				oldfile.renameTo(newfile);
			}
		} else {
			System.out.println("新文件名和旧文件名相同...");
		}
	}
	
	
	/**
	 * 复制单个文件
	 * 
	 * @param oldPath
	 *            String 原文件路径 如：c:/fqf.txt
	 * @param newPath
	 *            String 复制后路径 如：f:/fqf.txt
	 * @return boolean
	 */
	public static void copyFile2(String oldPath, String newPath) {
		try {
			int bytesum = 0;
			int byteread = 0;
			File oldfile = new File(oldPath);
			if (oldfile.exists()) { // 文件存在时
				InputStream inStream = new FileInputStream(oldPath); // 读入原文件
				FileOutputStream fs = new FileOutputStream(newPath);
				byte[] buffer = new byte[1444];
				int length;
				while ((byteread = inStream.read(buffer)) != -1) {
					bytesum += byteread; // 字节数 文件大小
					// System.out.println(bytesum);
					fs.write(buffer, 0, byteread);
				}
				inStream.close();
			}
		} catch (Exception e) {
			System.out.println("复制单个文件操作出错");
			e.printStackTrace();

		}

	}
	public static String getCurrentTime1() {
		int mYear, mMonth, mDay, mHour, mMinute, msecond;
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR); // 获取当前年份
		mMonth = c.get(Calendar.MONTH);// 获取当前月份
		mMonth++;
		mDay = c.get(Calendar.DAY_OF_MONTH);// 获取当前月份的日期号码
		mHour = c.get(Calendar.HOUR_OF_DAY);// 获取当前的小时数
		mMinute = c.get(Calendar.MINUTE);// 获取当前的分钟数
		msecond = c.get(Calendar.SECOND);

		return mYear + "-" + mMonth + "-" + mDay + "-" + mHour + "-" + mMinute
				+ "-" + msecond;
	}
	/**
	 * get time:year-month-day-hour-minute
	 * 
	 * @return
	 */
	public static String getCurrentTime() {
		int mYear, mMonth, mDay, mHour, mMinute, msecond;
		Calendar c = Calendar.getInstance();
		mYear = c.get(Calendar.YEAR);
		mMonth = c.get(Calendar.MONTH);
		mMonth++;
		mDay = c.get(Calendar.DAY_OF_MONTH);
		mHour = c.get(Calendar.HOUR_OF_DAY);
		mMinute = c.get(Calendar.MINUTE);
		return "" + mYear + mMonth + mDay + mHour + mMinute;
	}
	/**
	 * get all pcm file in specified directory
	 * 
	 * @param fileAbsolutePath
	 * @return
	 */
//	public static ArrayList<String> getTXTInDirectory(String fileAbsolutePath) {
//		ArrayList<String> vecFile = new ArrayList<String>();
//		File file = new File(fileAbsolutePath);
//		File[] subFile = file.listFiles();
//
//		for (int iFileLength = 0; iFileLength < subFile.length; iFileLength++) {
//
//			if (!subFile[iFileLength].isDirectory()) {
//				String fileName = subFile[iFileLength].getName();
//
//				if (fileName.trim().toLowerCase().endsWith(".txt")) {
//					vecFile.add(fileName);
//				}
//			}
//		}
//		return vecFile;
//	}
}
