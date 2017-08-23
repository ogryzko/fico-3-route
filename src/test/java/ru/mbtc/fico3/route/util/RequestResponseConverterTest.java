package ru.mbtc.fico3.route.util;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.junit.Before;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RequestResponseConverterTest {
    private HttpServletRequest httpServletRequest;
    private HttpServletResponse httpServletResponse;
    private CloseableHttpResponse closeableHttpResponse;
    private HttpPost httpPost;

    @Before
    public void init(){
    }
}
