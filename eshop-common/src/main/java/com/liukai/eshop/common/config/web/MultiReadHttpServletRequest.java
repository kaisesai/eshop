package com.liukai.eshop.common.config.web;

import org.apache.tomcat.util.http.fileupload.IOUtils;

import javax.servlet.ReadListener;
import javax.servlet.ServletInputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import java.io.*;

/**
 * 支持多次读取 httpRequest 请求体数据的 servlet
 */
public class MultiReadHttpServletRequest extends HttpServletRequestWrapper {

  private ByteArrayOutputStream cachedBytes;

  public MultiReadHttpServletRequest(HttpServletRequest request) {
    super(request);
  }

  @Override
  public ServletInputStream getInputStream() throws IOException {
    if (cachedBytes == null) {
      cacheInputStream();
    }
    return new CachedServletInputStream();
  }

  @Override
  public BufferedReader getReader() throws IOException {
    return new BufferedReader(new InputStreamReader(getInputStream()));
  }

  private void cacheInputStream() throws IOException {
    // Cache the inputStream in order to read it multiple times.
    cachedBytes = new ByteArrayOutputStream();
    IOUtils.copy(super.getInputStream(), cachedBytes);
  }

  /* An inputStream which reads the cached request body */
  public class CachedServletInputStream extends ServletInputStream {

    private final ByteArrayInputStream input;

    public CachedServletInputStream() {
      /* create a new input stream from the cached request body */
      input = new ByteArrayInputStream(cachedBytes.toByteArray());
    }

    @Override
    public int read() throws IOException {
      return input.read();
    }

    @Override
    public int readLine(byte[] b, int off, int len) throws IOException {
      return super.readLine(b, off, len);
    }

    @Override
    public boolean isFinished() {
      return input.available() <= 0;
    }

    @Override
    public boolean isReady() {
      return true;
    }

    @Override
    public void setReadListener(ReadListener readListener) {

    }
  }
}
