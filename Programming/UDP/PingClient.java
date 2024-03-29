import java.io.*;
import java.net.*;
import java.util.*;

/*
 * Client to generate 10 ping requests using UDP.
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
		socket.connect(new InetSocketAddress(host, port));

		// Keep track of the sequence of packets sent and their round trip times.
		int sequence = 0;
		long[] arr = new long[10];

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

				// Calculate round trip time and enter it into the array
				arr[sequence] = timeReceived - timeSent;

				// Print the received data.
				printData(response, timeReceived - timeSent);
			}
			// If the response timed out...
			catch (IOException e)
			{
				// Enter round trip time as MAX_TIMEOUT in array
				arr[sequence] = (long) MAX_TIMEOUT;

				// ...print the packet that timed out.
				System.out.println("\nPacket " + sequence + " timed out.");
			}

			// Iterate to next packet.
			sequence++;
		}

		// Calculate minimum round trip time
		long min = Long.MAX_VALUE;
		for (int i = 0; i < arr.length; i++)
		{
			if(arr[i] < min) min = arr[i];
		}

		// Calculate maximum round trip time
		long max = Long.MIN_VALUE;
		for (int i = 0; i < arr.length; i++)
		{
			if(arr[i] > max && arr[i] < MAX_TIMEOUT) max = arr[i];
		}

		// Calculate average round trip time
		long sum = 0;
		int num = 0;
		long avg;

		for(int i = 0; i < arr.length; i++)
		{
			if(arr[i] < MAX_TIMEOUT)
			{
				sum += arr[i];
				num++;
			}
		}

		avg = (long) sum / num;

		// Calculate hit/miss rates
		int hit = num * 10;
		int miss = (10 - num) * 10;

		// Output minimum, maximum, and average round trip time
		System.out.println("\nRound Trip Time");
		System.out.println("  Minimum: " + min + " ms");
		System.out.println("  Maximum: " + max + " ms");
		System.out.println("  Average: " + avg + " ms\n");
		System.out.println("Hit: " + hit + "%, Miss: " + miss + "%\n");
	}

   /* 
    * Print ping data to the standard output stream.
    * Modified slightly from PingServer.java to display round trip time.
    */
	private static void printData(DatagramPacket request, long rtt) throws Exception
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
			new String(line) + " | Round Trip Time: " + rtt + " ms");
   }
}
