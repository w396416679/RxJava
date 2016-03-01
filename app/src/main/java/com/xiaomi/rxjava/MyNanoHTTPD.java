package com.xiaomi.rxjava;

import android.os.Environment;
import android.util.Log;

import com.xiaomi.rxjava.NanoHTTPD.NanoFileUpload;
import com.xiaomi.rxjava.NanoHTTPD.NanoHTTPD;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/1/22.
 */
class MyNanoHTTPD extends NanoHTTPD {

    public Response response = newFixedLengthResponse("");

    public String uri;

    public Method method;

    public Map<String, String> header;

    public Map<String, String> parms;

    public Map<String, List<FileItem>> files;

    public Map<String, List<String>> decodedParamters;

    public Map<String, List<String>> decodedParamtersFromParameter;

    public String queryParameterString;

    public MyNanoHTTPD() {
        super(8192);
        uploader = new NanoFileUpload(new DiskFileItemFactory());
    }

    public HTTPSession createSession(TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream) {
        return new HTTPSession(tempFileManager, inputStream, outputStream);
    }

    public HTTPSession createSession(TempFileManager tempFileManager, InputStream inputStream, OutputStream outputStream, InetAddress inetAddress) {
        return new HTTPSession(tempFileManager, inputStream, outputStream, inetAddress);
    }

    NanoFileUpload uploader;

    @Override
    public Response serve(IHTTPSession session) {

        this.uri = session.getUri();
        this.method = session.getMethod();
        this.header = session.getHeaders();
        this.parms = session.getParms();
        Log.d("uri=", "-----uri--->" + this.uri + "-----method--->" + this.method + "-----headeer--->" + this.header + "-----parms--->" + this.parms);

        String msg = "<html><body> \n <form enctype=multipart/form-data  method=post >\n  <input type=file id=datafile1 name=file size= 40 ><br>\n <input type=submit> \n </form>";
        try {
            files = uploader.parseParameterMap(session);

            if ("/uploadFile".equals(this.uri) && !files.get("file").isEmpty()) {
                InputStream in = files.get("file").get(0).getInputStream();
                File f = Environment.getExternalStorageDirectory();
                File fileDir = new File(f, files.get("file").get(0).getName());
                FileOutputStream os = new FileOutputStream(fileDir);
                byte[] buffer = new byte[4 * 1024];
                int length;
                while ((length = (in.read(buffer))) > 0) {
                    os.write(buffer, 0, length);
                }
                in.close();
                os.close();

            }
        } catch (Exception e) {
            this.response.setStatus(Response.Status.INTERNAL_ERROR);
            e.printStackTrace();
        }
        this.queryParameterString = session.getQueryParameterString();
        this.decodedParamtersFromParameter = decodeParameters(this.queryParameterString);
        this.decodedParamters = decodeParameters(session.getQueryParameterString());
        return newFixedLengthResponse(msg + "</body></html>\n");
    }

}
