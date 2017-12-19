package com.code.library.share.utils;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

//从服务器获取数据的类
public class HttpHelper {

	private OnHttpHelperListener onHttpHelperListener;
	private Map<String, String> postdata;

	// 定义数据接口
	public interface OnHttpHelperListener {
		public void onData(String data);
	}

	// 设置回调接口
	public void setHttpHelperListener(OnHttpHelperListener onHttpHelperListener) {
		this.onHttpHelperListener = onHttpHelperListener;
	}

	public void returnData(String data) {
		if (onHttpHelperListener != null) {
			onHttpHelperListener.onData(data);
		}
	}

	public HttpHelper(OnHttpHelperListener onHttpHelperListener) {
		this.onHttpHelperListener = onHttpHelperListener;
	}

	// 通过get请求返回数据的方法
	public void GetResult(String urlString) {
		new DataTask().execute(urlString);
	}

	// 通过post请求返回数据的方法
	public void PostResult(String urlString, Map<String, String> postdata,
			String encode) {
		this.postdata = postdata;
		String datas[] = { urlString, encode };
		new PostTask().execute(datas);
	}

	private class DataTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			HttpURLConnection conn = null; // 连接对象
			InputStream is = null;
			String dataResult = "";
			try {
				URL url = new URL(params[0]); // URL对象
				conn = (HttpURLConnection) url.openConnection(); // 使用URL打开一个链接
				conn.setRequestMethod("GET"); // 使用get请求
				conn.setConnectTimeout(5000);// 设置5秒超时
				conn.setReadTimeout(5000);
				is = conn.getInputStream(); // 获取输入流
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader bufferReader = new BufferedReader(isr);
				String inputLine;
				while ((inputLine = bufferReader.readLine()) != null) {
					dataResult += inputLine + "\n";
				}
			} catch (Exception e) {
				e.printStackTrace();
				dataResult = null;
			} finally {
				if (is != null) {
					try {
						is.close();
					} catch (Exception e) {
						e.printStackTrace();
						dataResult = null;
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
			}
			return dataResult;
		}

		@Override
		protected void onPostExecute(String dataResult) {
			returnData(dataResult);
		}
	}

	// Post请求
	private class PostTask extends AsyncTask<String, Void, String> {
		@Override
		protected String doInBackground(String... params) {
			byte[] data = getRequestData(postdata, params[1]).toString()
					.getBytes();
			HttpURLConnection conn = null; // 连接对象
			InputStream is = null;
			OutputStream os = null;
			String dataResult = "";
			try {
				URL url = new URL(params[0]); // URL对象
				conn = (HttpURLConnection) url.openConnection(); // 使用URL打开一个链接
				conn.setDoInput(true); // 允许输入流，即允许下载
				conn.setDoOutput(true); // 允许输出流，即允许上传
				conn.setUseCaches(false); // 不使用缓冲
				conn.setRequestMethod("POST"); // 使用post请求
				conn.setConnectTimeout(5000);// 设置5秒超时
				conn.setReadTimeout(5000);
				// 设置请求体的类型是文本类型
				conn.setRequestProperty("Content-Type",
						"application/x-www-form-urlencoded");
				// 设置请求体的长度
				conn.setRequestProperty("Content-Length",
						String.valueOf(data.length));
				// 获得服务器的响应码
				// 获得输出流，向服务器写入数据
				os = conn.getOutputStream();
				os.write(data);
				// 获取返回的数据
				is = conn.getInputStream();
				InputStreamReader isr = new InputStreamReader(is);
				BufferedReader bufferReader = new BufferedReader(isr);
				String inputLine;
				while ((inputLine = bufferReader.readLine()) != null) {
					dataResult += inputLine + "\n";
				}
			} catch (Exception e) {
				e.printStackTrace();
				dataResult = null;
			} finally {
				if (is != null) {
					try {
						is.close();
						os.close();
					} catch (Exception e) {
						e.printStackTrace();
						dataResult = null;
					}
				}
				if (conn != null) {
					conn.disconnect();
				}
			}
			return dataResult;
		}

		@Override
		protected void onPostExecute(String dataResult) {
			returnData(dataResult);
		}
	}

	/*
	 * Function : 封装请求体信息 Param : params请求体内容，encode编码格式
	 */
	private static StringBuffer getRequestData(Map<String, String> params,
			String encode) {
		StringBuffer stringBuffer = new StringBuffer(); // 存储封装好的请求体信息
		try {
			for (Map.Entry<String, String> entry : params.entrySet()) {
				stringBuffer.append(entry.getKey()).append("=")
						.append(URLEncoder.encode(entry.getValue(), encode))
						.append("&");
			}
			stringBuffer.deleteCharAt(stringBuffer.length() - 1); // 删除最后的一个"&"
		} catch (Exception e) {
			e.printStackTrace();
		}
		return stringBuffer;
	}

}
