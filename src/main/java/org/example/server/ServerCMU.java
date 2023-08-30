package org.example.server;

import org.example.service.Cmu;
import org.jboss.resteasy.plugins.server.undertow.UndertowJaxrsServer;
import io.undertow.Undertow;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;

@WebServlet("/")
public class ServerCMU extends HttpServlet {

    private static final long serialVersionUID = 1L;

    public static void main(String[] args) {
        UndertowJaxrsServer server = new UndertowJaxrsServer();
        Undertow.Builder serverBuilder = Undertow.builder().addHttpListener(8081, "localhost");
        server.start(serverBuilder);
        server.deploy(Cmu.class);
    }
}
