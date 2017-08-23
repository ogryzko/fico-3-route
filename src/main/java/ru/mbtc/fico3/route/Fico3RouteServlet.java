package ru.mbtc.fico3.route;


import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import ru.mbtc.fico3.route.util.BodyParser;
import ru.mbtc.fico3.route.util.EndpointManager;
import ru.mbtc.fico3.route.util.RequestResponseConverter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.URI;
import java.util.Properties;

public class Fico3RouteServlet extends HttpServlet {

    private static final Logger log = LogManager.getLogger(Fico3RouteServlet.class);

    private CloseableHttpClient routeClient;
    private Properties properties;
    private EndpointManager endpointManager;
    private  RequestResponseConverter converter;

    @Override
    public void init() throws ServletException {

        try {
            properties = new Properties();
            converter = new RequestResponseConverter();
            routeClient = HttpClients.createDefault();

            try (InputStream propertiesInput = getClass().getResourceAsStream("/fico-3-route.properties")) {
                properties.load(propertiesInput);
            }

            endpointManager = new EndpointManager(properties);
        } catch (Exception e) {
            log.error("Failed to start Fico-3 Routing Service", e);
            throw new ServletException(e);
        }
        log.info("Fico-3 Routing Service started");
    }

    @Override
    public void destroy() {
        log.info("Fico-3 Routing Service stopped");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("Received GET-request from " + req.getRemoteAddr());
        resp.getWriter().print("Use POST method.");
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.trace("Received POST-request from " + req.getRemoteAddr());

        HttpPost httpPost = new HttpPost();
        converter.ServletHttpRequestToHttpPost(req, httpPost);

        InputStream body = converter.getRequestBodyStreamCopy();
        String memberCode = BodyParser.getMemberCode(body);
        log.debug("Found member code: " + memberCode);

        URI endpoint = endpointManager.getEndpoint(memberCode);
        log.trace("Redirecting to" + endpoint);
        httpPost.setURI(endpoint);
        CloseableHttpResponse httpResponse = routeClient.execute(httpPost);
        log.trace("Received response from " + endpoint);
        try {
            converter.HttpResponseToHttServletResponse(httpResponse, resp);
        } finally {
            httpResponse.close();
        }
        log.trace("Sent response back to " + req.getRemoteAddr());
    }

}
