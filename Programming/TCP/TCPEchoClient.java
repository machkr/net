// From TCP/IP Sockets in Java, 2nd Edition
// Kenneth L. Calvert and Michael J. Donahoo
// Published by Morgan Kaufmann, 08 February 2008
// ISBN: 9780123742551

import java.net.*;  // for Socket
import java.io.*;   // for IOException and Input/OutputStream

public class TCPEchoClient
{
  public static void main(String[] args) throws IOException
  {
    // Test for correct # of args
    if ((args.length < 2) || (args.length > 3))
        throw new IllegalArgumentException("Parameter(s): <Server> <Word> [<Port>]");

    // Server name or IP address
    String server = args[0];

    // Convert input String to bytes using the default character encoding
    byte[] byteBuffer = args[1].getBytes();

    int servPort = (args.length == 3) ? Integer.parseInt(args[2]) : 7;

    // Create socket that is connected to server on specified port
    Socket socket = new Socket(server, servPort);
    System.out.println("Connected to server...sending echo string");

    InputStream in = socket.getInputStream();
    OutputStream out = socket.getOutputStream();

    // Send the encoded string to the server
    out.write(byteBuffer);

    // Receive the same string back from the server
    int totalBytesRcvd = 0;  // Total bytes received so far
    int bytesRcvd;           // Bytes received in last read
    while (totalBytesRcvd < byteBuffer.length)
    {
        if ((bytesRcvd = in.read(byteBuffer, totalBytesRcvd, byteBuffer.length - totalBytesRcvd)) == -1)
            throw new SocketException("Connection close prematurely");
        
        totalBytesRcvd += bytesRcvd;
    }

    System.out.println("Received: " + new String(byteBuffer));

    socket.close();  // Close the socket and its streams
  }
}
