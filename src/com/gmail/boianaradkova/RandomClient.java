/**
 * Coloring, Version 0.1 
 * New Bulgarian University
 *
 * Copyright (c) 2018 Boyana Kantarska
 */

package com.gmail.boianaradkova;

import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Simple client with random playing strategy.
 * 
 * @author Boyana Kantarska
 */
public class RandomClient {
	/**
	 * Port number of the game server.
	 */
	static private final int SERVER_PORT = 3379;

	/**
	 * Host address of the game server.
	 */
	static private final String SERVER_ADDRESS = "127.0.0.1";

	/**
	 * Main method.
	 * 
	 * @param args
	 *            Command line parameters.
	 */
	public static void main(String[] args) {
		Socket socket = null;
		DataInputStream in = null;
		PrintWriter out = null;

		do {
			try {
				socket = new Socket(SERVER_ADDRESS, SERVER_PORT);
			} catch (Exception ex) {
				socket = null;
			}
		} while (socket == null);
		System.out.println("Client connected ...");

		try {
			in = new DataInputStream(socket.getInputStream());
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(
					socket.getOutputStream())), true);
		} catch (IOException ex) {
			System.err
					.println("Input/Output streams are not available form the socket: "
							+ ex.toString());
		}

		String line = "";
		while (line != null) {
			int M = 0, N = 0, C = 0, P = 0;
			int X = 0, Y = 0;
			int usedColors[] = null;
			int board[][] = null;
			try {
				line = in.readLine() + " ";
				M = (new Integer(line.substring(0, line.indexOf(' '))))
						.intValue();
				line = line.substring(line.indexOf(' ') + 1);
				N = (new Integer(line.substring(0, line.indexOf(' '))))
						.intValue();
				line = line.substring(line.indexOf(' ') + 1);
				C = (new Integer(line.substring(0, line.indexOf(' '))))
						.intValue();
				line = line.substring(line.indexOf(' ') + 1);
				P = (new Integer(line.substring(0, line.indexOf(' '))))
						.intValue();

				line = in.readLine() + " ";
				X = (new Integer(line.substring(0, line.indexOf(' '))))
						.intValue() - 1;
				line = line.substring(line.indexOf(' ') + 1);
				Y = (new Integer(line.substring(0, line.indexOf(' '))))
						.intValue() - 1;

				line = in.readLine() + " ";
				usedColors = new int[P];
				for (int i = 0; i < P; i++) {
					usedColors[i] = (new Integer(line.substring(0, line
							.indexOf(' ')))).intValue();
					line = line.substring(line.indexOf(' ') + 1);
				}

				board = new int[M][];
				for (int i = 0; i < M; i++) {
					board[i] = new int[N];
				}
				for (int j = 0; j < N; j++) {
					line = in.readLine() + " ";
					for (int i = 0; i < M; i++) {
						board[i][j] = (new Integer(line.substring(0, line
								.indexOf(' ')))).intValue();
						line = line.substring(line.indexOf(' ') + 1);
					}
				}
			} catch (IOException ex) {
				System.err.println("Incorrect imput data: " + ex.toString());
			}

			int color = 0;
			boolean done;
			do {
				done = true;

				color = 1 + (int) (Math.random() * C);

				for (int i = 0; i < P; i++) {
					if (color == usedColors[i]) {
						done = false;
					}
				}
			} while (done == false);

			out.println(color);
			out.flush();
		}

		try {
			socket.close();
		} catch (IOException ex) {
		}
	}
}
