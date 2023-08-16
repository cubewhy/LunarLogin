package org.cubewhy;

import org.cubewhy.server.LoginService;
import org.cubewhy.utils.FileUtils;

import java.io.IOException;
import java.io.InputStream;

public class LunarLogin {
    public static LoginService service;
    public static final String loginIndex;
    public static final String loginIndexEN;

    static {
        System.setProperty("file.encoding", "UTF-8"); // set encoding
        try {
            loginIndex = new String(FileUtils.getFile("page/index.html").readAllBytes());
            loginIndexEN = new String(FileUtils.getFile("page/index-en.html").readAllBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public static void main(String[] args) throws IOException, InterruptedException {
        LoginServer server = LoginServer.getInstance();
        service = new LoginService();
        service.startServer(); // start service
        server.startServer();
    }
}
