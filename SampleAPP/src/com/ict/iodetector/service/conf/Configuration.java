package com.ict.iodetector.service.conf;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.os.Environment;
import android.util.Xml;

public class Configuration {
	private ArrayList<Mode> modes;
	public static final String CONFIG_FITH = Environment
			.getExternalStorageDirectory().getAbsolutePath() + "/iodetector.xml";

	/**
	 * reading the configuration file 
	 */
	public Configuration() {
		modes = new ArrayList<Mode>();
		FileInputStream fin;
		try {
			fin = new FileInputStream(CONFIG_FITH);
			InputStream in = new BufferedInputStream(fin);
			parseConfigFile(in);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (XmlPullParserException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * parse the configuration file
	 * @param is
	 * @throws XmlPullParserException
	 * @throws IOException
	 */
	private void parseConfigFile(InputStream is) throws XmlPullParserException,
			IOException {
		XmlPullParser parser = Xml.newPullParser();
		parser.setInput(is, "UTF-8");

		Mode mode = null;
		Threshold threshold = null;
		HashMap<String, Threshold> thresholds = new HashMap<String, Threshold>();

		int eventType = parser.getEventType();
		while (eventType != XmlPullParser.END_DOCUMENT) {
			switch (eventType) {
			case XmlPullParser.START_DOCUMENT:
				break;
			case XmlPullParser.START_TAG:
				if (parser.getName().equals("mode")) {
					mode = new Mode();
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attribute = parser.getAttributeName(i);
						if (attribute.equals("name")) {
							mode.setName(parser.getAttributeValue(i));
						} else if (attribute.equals("enable")) {
							mode.setEnable(Boolean.parseBoolean(parser
									.getAttributeValue(i)));
						}
					}
				} else if (parser.getName().equals("threshold")) {
					threshold = new Threshold();
					for (int i = 0; i < parser.getAttributeCount(); i++) {
						String attribute = parser.getAttributeName(i);
						if (attribute.equals("high")) {
							threshold.setHigh(parser.getAttributeValue(i));
						} else if (attribute.equals("low")) {
							threshold.setLow(parser.getAttributeValue(i));
						} else if (attribute.equals("value")) {
							threshold.setValue(parser.getAttributeValue(i));
						} else if (attribute.equals("turnthreshold")) {
							threshold.setTurnthreshold(parser.getAttributeValue(i));
						} else if (attribute.equals("rssidifference")) {
							threshold.setRssidifference(parser.getAttributeValue(i));
						} else if (attribute.equals("variance")) {
							threshold.setVariance(parser.getAttributeValue(i));
						} else if (attribute.equals("model")) {
							thresholds.put(parser.getAttributeValue(i),
									threshold);
						}
					}
				}
				break;
			case XmlPullParser.END_TAG:
				if (parser.getName().equals("mode")) {
					mode.setThresholds(thresholds);
					modes.add(mode);
					thresholds = new HashMap<String, Threshold>();
					mode = null;
					threshold = null;
				}
				break;
			}
			eventType = parser.next();
//			System.out.println("eventType" + eventType);
		}
	}

	/**
	 * @return modes
	 */
	public ArrayList<Mode> getModes() {
		return modes;
	}

	/**
	 * @param modes
	 */
	public void setModes(ArrayList<Mode> modes) {
		this.modes = modes;
	}
}
