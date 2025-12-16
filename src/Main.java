import java.io.*;
import java.net.*;

void main() throws IOException, InterruptedException {

    ServerSocket serverSocket = new ServerSocket(42069);
    System.out.println("Server is running and waiting for client connection...");
    while (true) {
        Socket clientSocket = serverSocket.accept();

        BufferedReader in = new BufferedReader(
                new InputStreamReader(clientSocket.getInputStream())
        );

        OutputStream out = clientSocket.getOutputStream();

        // --- START timing AFTER accept ---
        final long startNs = System.nanoTime();

        String requestLine = in.readLine();
        if (requestLine == null) {
            clientSocket.close();
            continue;
        }

        String line;
        while ((line = in.readLine()) != null && !line.isEmpty()) {
            // TODO: kill myself
        }

        String[] reqHead = requestLine.split(" ");
        String body;
        String status;

        if ("GET".equals(reqHead[0])) {
            body = "ok";
            status = "200 OK";
        } else if ("POST".equals(reqHead[0])) {
            body = "post received";
            status = "200 OK";
        } else {
            body = "";
            status = "405 Method Not Allowed";
        }
        long elapsedMs = (System.nanoTime() - startNs) / 1_000_000;

        String response =
                "HTTP/1.1 " + status + "\r\n" +
                        "Content-Type: text/plain; charset=UTF-8\r\n" +
                        "X-Response-Time: " + elapsedMs + "ms\r\n" +
                        "Content-Length: " + body.length() + "\r\n" +
                        "Connection: close\r\n\r\n" +
                        body;

        out.write(response.getBytes(StandardCharsets.UTF_8));
        out.flush();

        clientSocket.close();

        System.out.println("Request from " + clientSocket.getInetAddress()
                + " took " + elapsedMs + "ms");
    }

}

String makeResponse(String statusCode, String contentType, String input){
    String res = "HTTP/1.1 "+statusCode+"\r\n";
    res += "Server: Java/25.0.1\r\n";
    res += "Content-Type: "+contentType+"; charset=UTF-8\r\n";
    res += "Content-Length: "+input.length()+"\r\n";
    res += "Connection: close\r\n\r\n";
    if(!input.isEmpty()) res += input;

    return res;
}