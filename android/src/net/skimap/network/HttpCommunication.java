package net.skimap.network;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

public class HttpCommunication
{
//	public static InputStream getInputStreamFromUrl(String url)
//	{
//		InputStream content = null;
//		try
//		{
//			HttpClient httpclient = new DefaultHttpClient();
//			HttpResponse response = httpclient.execute(new HttpGet(url));
//			content = response.getEntity().getContent();
//		}
//		catch (Exception e)
//		{
//			e.printStackTrace();
//		}
//		return content;
//	}
//	
//	
//	public static String convertStreamToString(InputStream is)
//	{
//		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
//		StringBuilder sb = new StringBuilder();
//		String line = null;
//		try
//		{
//			while ((line = reader.readLine()) != null)
//			{
//					sb.append(line + "\n");
//			}
//		}
//		catch (IOException e)
//		{
//			e.printStackTrace();
//		}
//		finally
//		{
//			try
//			{
//				is.close();
//			}
//			catch (IOException e)
//			{
//				e.printStackTrace();
//			}
//		}
//		return sb.toString();
//	}
//	
//	
//	public static String executeHttpGet(String url) throws Exception
//	{
//		InputStream is = getInputStreamFromUrl(url);
//		String str = convertStreamToString(is);
//		return str;
//	}
	

	public static String executeHttpGet(String url) throws Exception
	{
		// ziska JSON data ze serveru dle URL adresy
		BufferedReader bufferedReader = null;
		try
		{
			// zaslani pozadavku
			HttpClient client = new DefaultHttpClient();
			HttpGet request = new HttpGet();
			//request.setHeader("Content-Type", "text/plain; charset=utf-8");
			request.setHeader("Content-Type", "application/json; charset=utf-8");
			request.setURI(new URI(url));
			HttpResponse response = client.execute(request);
			
			// zpracovani odpovedi
			bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
			StringBuffer stringBuffer = new StringBuffer("");
			String line = "";
			String newLine = System.getProperty("line.separator");
			
			while ((line = bufferedReader.readLine()) != null)
			{
				stringBuffer.append(line + newLine);
			}
			bufferedReader.close();
			
			String content = stringBuffer.toString();
			return content;
		}
		finally
		{
			if (bufferedReader != null)
			{
				try
				{
					bufferedReader.close();
				}
				catch (IOException e)
				{
					e.printStackTrace();
				}
			}
		}
	}
	
	
	/**
	 * Downloads a remote file and stores it locally
	 * @param from Remote URL of the file to download
	 * @param to Local path where to store the file
	 * @throws Exception Read/write exception
	 */
	public static int downloadFile(String url, String path, String filename) throws Exception 
	{
		int byteCount = 0;
		
	    HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
	    connection.setDoInput(true);
	    connection.setConnectTimeout(10000); // timeout 10 secs
	    connection.connect();
	    InputStream input = connection.getInputStream();
	    
	    File directory = new File(path);
	    directory.mkdirs();
	    File file = new File(path, filename);

	    FileOutputStream fOut = new FileOutputStream(file);
		
	    byte[] buffer = new byte[4096];
	    int bytesRead = -1;
	    while ((bytesRead = input.read(buffer)) != -1) 
	    {
	        fOut.write(buffer, 0, bytesRead);
	        byteCount += bytesRead;
	    }
	    fOut.flush();
	    fOut.close();
	    
	    return byteCount;
	}
}
