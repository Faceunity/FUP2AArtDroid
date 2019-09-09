package com.faceunity.pta_art.web;

import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

import javax.net.ssl.KeyManager;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManager;

/**
 * Created by tujh on 2018/3/8.
 */

public class CustomSslSocketFactory extends SSLSocketFactory {
    private SSLSocketFactory delegate;
    private SSLContext sslContext = SSLContext.getInstance("TLS");

    public CustomSslSocketFactory(KeyManager[] keyManagers, TrustManager[] trustManagers) throws KeyManagementException, NoSuchAlgorithmException {
        this.sslContext.init(keyManagers, trustManagers, (SecureRandom) null);
        this.delegate = this.sslContext.getSocketFactory();
    }

    public SSLContext getSslContext() {
        return this.sslContext;
    }

    public String[] getDefaultCipherSuites() {
        return this.delegate.getDefaultCipherSuites();
    }

    public String[] getSupportedCipherSuites() {
        return this.delegate.getSupportedCipherSuites();
    }

    public Socket createSocket(Socket s, String host, int port, boolean autoClose) throws IOException {
        return this.enableTLSOnSocket(this.delegate.createSocket(s, host, port, autoClose));
    }

    public Socket createSocket(String host, int port) throws IOException, UnknownHostException {
        return this.enableTLSOnSocket(this.delegate.createSocket(host, port));
    }

    public Socket createSocket(String host, int port, InetAddress localHost, int localPort) throws IOException, UnknownHostException {
        return this.enableTLSOnSocket(this.delegate.createSocket(host, port, localHost, localPort));
    }

    public Socket createSocket(InetAddress host, int port) throws IOException {
        return this.enableTLSOnSocket(this.delegate.createSocket(host, port));
    }

    public Socket createSocket(InetAddress address, int port, InetAddress localAddress, int localPort) throws IOException {
        return this.enableTLSOnSocket(this.delegate.createSocket(address, port, localAddress, localPort));
    }

    public Socket createSocket() throws IOException {
        return this.enableTLSOnSocket(this.delegate.createSocket());
    }

    private Socket enableTLSOnSocket(Socket socket) {
        ((SSLSocket) socket).setEnabledProtocols(new String[]{"TLSv1.2", "TLSv1.1"});
        return socket;
    }
}