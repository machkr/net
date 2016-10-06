import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Client to generate 10 ping requests over UDP.
 */

public class PingClient
{
	// Maximum amount of time before timeout occurs (milliseconds).
	private static final int MAX_TIMEOUT = 1000;

	public static void main(String[] args) throws Exception
	{
		// Retrieve command line arguments.
		if (args.length != 2)
		{
			System.out.println("ERROR: invalid arguments.\n");
			System.out.println("SYNTAX: .java PingClient [Host] [Port]\n");
			return;
		}

		// Define host variable from command line argument.
		InetAddress host;
		host = InetAddress.getByName(args[0]);

		// Define port variable from command line argument.
		int port = Integer.parseInt(args[1]);

		// Connect to the datagram socket for receiving and sending UDP
		// packets through the port specified on the command line.
		DatagramSocket socket = new DatagramSocket();
		socket.connect(new InetSocketAddress("localhost", port));

		// Keep track of the sequence of packets sent.
		int sequence = 0;

		// Processing loop.
		while (sequence < 10)
		{
			// Timestamp in milliseconds.
			Date now = new Date();
			long timeSent = now.getTime();

			// Generate the string message to send.
			String str = "PING " + sequence + " " + timeSent + " \r\n";
			byte[] buf = new byte[1024];
			buf = str.getBytes();

			// Create a datagram packet using the above buffer and provided host/port.
			DatagramPacket ping = new DatagramPacket(buf, buf.length, host, port);

			// Send the datagram.
			socket.send(ping);

			// Try to receive response from host.
			try
			{
				// Define timeout (see global variable).
				socket.setSoTimeout(MAX_TIMEOUT);

				// Set up a UDP packet for receiving.
				DatagramPacket response = new DatagramPacket(new byte[1024], 1024);

				// Attempt to receive response -- may throw an exception.
				socket.receive(response);
				
				// Timestamp for when we received the packet
				now = new Date();
				long timeReceived = now.getTime();

				// Print the received data.
				printData(response, timeReceived - timeSent);
			}
			// If the response timed out...
			catch (IOException e)
			{
				// ...print the packet that timed out.
				System.out.println("\nPacket " + sequence + " timed out.");
			}

			// Iterate to next packet.
			sequence++;
		}
	}

   /* 
    * Print ping data to the standard output stream.
    * Modified slightly from PingServer.java to display delay.
    */
	private static void printData(DatagramPacket request, long delayTime) throws Exception
	{
		// Obtain references to the packet's array of bytes.
		byte[] buf = request.getData();

		// Wrap the bytes in a byte array input stream,
		// so that you can read the data as a stream of bytes.
		ByteArrayInputStream bais = new ByteArrayInputStream(buf);

		// Wrap the byte array output stream in an input stream reader,
		// so you can read the data as a stream of characters.
		InputStreamReader isr = new InputStreamReader(bais);

		// Wrap the input stream reader in a bufferred reader,
		// so you can read the character data a line at a time.
		// (A line is a sequence of chars terminated by any combination of \r and \n.) 
		BufferedReader br = new BufferedReader(isr);

		// The message data is contained in a single line, so read this line.
		String line = br.readLine();

		// Print host address and data received from it.
		System.out.println("\nReceived from " + request.getAddress().getHostAddress() + ": " +
			new String(line) + " | Delay of " + delayTime);
   }
}
