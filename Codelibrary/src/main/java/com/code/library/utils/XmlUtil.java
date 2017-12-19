package com.code.library.utils;

import android.content.Context;
import android.text.TextUtils;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yue on 2015/11/28.
 * XML pull
 */
public class XmlUtil {

    private static XmlUtil xmlUtil;

    public static XmlUtil getXmlUtil() {
        if (xmlUtil == null) {
            xmlUtil = new XmlUtil();
        }
        return xmlUtil;
    }


    /**
     * 解析贴纸XML
     *
     * @param xmlData
     * @return
     */
    public List<Map<String, String>> parseXMLWithPull(String xmlData) {
        try {
            Map<String, String> map = new HashMap<String, String>();
            List<Map<String, String>> mapList = new ArrayList<Map<String, String>>();
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xmlPullParser = factory.newPullParser();
            xmlPullParser.setInput(new StringReader(xmlData));
            int eventType = xmlPullParser.getEventType();
            String key = "";
            String string = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    // 开始解析某个结点
                    case XmlPullParser.START_TAG: {
                        if ("key".equals(nodeName)) {
                            key = xmlPullParser.nextText();
                        } else if ("string".equals(nodeName)) {
                            string = xmlPullParser.nextText();
                            map.put(key, string);
                        }
                        break;
                    }
                    // 完成解析某个结点
                    case XmlPullParser.END_TAG: {
                        if ("dict".equals(nodeName)) {
                            // 将map存起来
                            mapList.add(map);
                            map = new HashMap<String, String>();
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

            return mapList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public List<Map<String, List<String>>> getFilters(int id, Context context) {
        List<Map<String, List<String>>> filters = new ArrayList<Map<String, List<String>>>();
        Map<String, List<String>> map = new HashMap<String, List<String>>();
        List<String> strings = new ArrayList<String>();
        List<String> arrays = new ArrayList<String>();
        XmlPullParser xmlPullParser = context.getResources().getXml(id);
        try {
            int eventType = xmlPullParser.getEventType();
            String key = "";
            String string = "";
            String array = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    // 开始解析某个结点
                    case XmlPullParser.START_TAG: {
                        if ("key".equals(nodeName)) {
                            key = xmlPullParser.nextText();
                        } else if ("string".equals(nodeName)) {
                            string = xmlPullParser.nextText();

                            if (!TextUtils.isEmpty(key) && key.equals("FilterNames")) {
                                arrays.add(string);
                            } else {
                                strings.add(string);
                                map.put(key, strings);
                                strings = new ArrayList<String>();
                            }
                        }
                        break;
                    }
                    // 完成解析某个结点
                    case XmlPullParser.END_TAG: {
                        if ("dict".equals(nodeName)) {
                            map.put(key, arrays);
                            //存Filter
                            filters.add(map);
                            arrays = new ArrayList<String>();
                            map = new HashMap<String, List<String>>();
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

        } catch (XmlPullParserException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filters;
    }

    public List<Map<String, String>> parseXMLWithPull(int id, Context context) {
        try {

            List<Map<String, String>> pList = new ArrayList<Map<String, String>>();
            Map<String, String> xmlmap = new HashMap<String, String>();

            XmlPullParser xmlPullParser = context.getResources().getXml(id);
            int eventType = xmlPullParser.getEventType();
            String key = "";
            String string = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                String nodeName = xmlPullParser.getName();
                switch (eventType) {
                    // 开始解析某个结点
                    case XmlPullParser.START_TAG: {
                        if ("key".equals(nodeName)) {
                            key = xmlPullParser.nextText();
                        } else if ("string".equals(nodeName)) {
                            string = xmlPullParser.nextText();
                            xmlmap.put(key, string);
                        }
                        break;
                    }
                    // 完成解析某个结点
                    case XmlPullParser.END_TAG: {
                        if ("dict".equals(nodeName)) {
                            // 将map存起来
                            pList.add(xmlmap);
                            xmlmap = new HashMap<String, String>();
                        }
                        break;
                    }
                    default:
                        break;
                }
                eventType = xmlPullParser.next();
            }

            return pList;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
