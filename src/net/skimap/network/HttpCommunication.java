package net.skimap.network;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpCommunication
{
//	public static String executeHttpGet(String url) throws Exception
//	{
//		// ziska JSON data ze serveru dle URL adresy
//		BufferedReader bufferedReader = null;
//		try
//		{
//			// zaslani pozadavku
//			HttpClient client = new DefaultHttpClient();
//			HttpGet request = new HttpGet();
//			//request.setHeader("Content-Type", "text/plain; charset=utf-8");
//			request.setHeader("Content-Type", "application/json; charset=utf-8");
//			request.setURI(new URI(url));
//			HttpResponse response = client.execute(request);
//			
//			// zpracovani odpovedi
//			bufferedReader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
//			StringBuffer stringBuffer = new StringBuffer("");
//			String line = "";
//			String newLine = System.getProperty("line.separator");
//			
//			while ((line = bufferedReader.readLine()) != null)
//			{
//				stringBuffer.append(line + newLine);
//			}
//			bufferedReader.close();
//			
//			String content = stringBuffer.toString();
//			return content;
//		}
//		finally
//		{
//			if (bufferedReader != null)
//			{
//				try
//				{
//					bufferedReader.close();
//				}
//				catch (IOException e)
//				{
//					e.printStackTrace();
//				}
//			}
//		}
//	}
	
	
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
