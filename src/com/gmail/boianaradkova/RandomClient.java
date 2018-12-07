/**
 * Coloring, Version 0.1 
 * New Bulgarian University
 *
 * Copyright (c) 2018 Boyana Kantarska
 */

package com.gmail.boianaradkova;

import java.awt.Graphics;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;

import javax.swing.JFrame;

/**
 * Simple client with random playing strategy.
 * 
 * @author Boyana Kantarska
 */
@SuppressWarnings("serial")
public class RandomClient extends JFrame {

	/** Socket for client-server communication. */
	private Socket socket = null;

	/** Input stream with information from the server. */
	private DataInputStream in = null;

	/** Output stream with information for the server. */
	private PrintWriter out = null;

	/** Server port. */
	private int port = -1;

	/** Server URL address. */
	private String address = "";

	/** Game board with lock. */
	private Board board = null;

	/**
	 * Constructor.
	 * 
	 * @param port    TCP/IP communication port.
	 * @param address Server URL address.
	 */
	public RandomClient(int port, String address) {
		super();

		this.port = port;
		this.address = address;

		do {
			try {
				socket = new Socket(address, port);
			} catch (Exception ex) {
				socket = null;
			}
		} while (socket == null);
		System.out.println("Client connected ...");

		try {
			in = new DataInputStream(socket.getInputStream());
			out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(socket.getOutputStream())), true);
		} catch (IOException ex) {
			System.err.println("Input/Output streams are not available form the socket: " + ex.toString());
		}
		
		/* Communication done in a separate thread. */
		Thread thread = new Thread() {
			@Override
			public void run() {
				String line = "";
				while (line != null) {
					int M = 0, N = 0, C = 0, P = 0;
					int X = 0, Y = 0;
					int usedColors[] = null;
					int board[][] = null;
					try {
						/* Parsing of the messages in the communication protocol. */
						line = in.readLine() + " ";
						M = (new Integer(line.substring(0, line.indexOf(' ')))).intValue();
						line = line.substring(line.indexOf(' ') + 1);
						N = (new Integer(line.substring(0, line.indexOf(' ')))).intValue();
						line = line.substring(line.indexOf(' ') + 1);
						C = (new Integer(line.substring(0, line.indexOf(' ')))).intValue();
						
						line = line.substring(line.indexOf(' ') + 1);
						P = (new Integer(line.substring(0, line.indexOf(' ')))).intValue();

						line = in.readLine() + " ";
						X = (new Integer(line.substring(0, line.indexOf(' ')))).intValue() - 1;
						line = line.substring(line.indexOf(' ') + 1);
						Y = (new Integer(line.substring(0, line.indexOf(' ')))).intValue() - 1;

						line = in.readLine() + " ";
						usedColors = new int[P];
						for (int i = 0; i < P; i++) {
							usedColors[i] = (new Integer(line.substring(0, line.indexOf(' ')))).intValue();
							line = line.substring(line.indexOf(' ') + 1);
						}

						board = new int[M][N];
						for (int j = 0; j < N; j++) {
							line = in.readLine() + " ";
							for (int i = 0; i < M; i++) {
								board[i][j] = (new Integer(line.substring(0, line.indexOf(' ')))).intValue();
								line = line.substring(line.indexOf(' ') + 1);
							}
						}
						
						/* Board creation. */
						RandomClient.this.board = new Board(M, N, C, board);
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

					/* Response from the client to the server. */
					out.println(color);
					out.flush();
					
					/* Redraw GUI. */
					repaint();
					
					try {
						Thread.sleep(100);
					} catch (InterruptedException e) {
					}
				}
			}
		};
		thread.start();
	}

	/**
	 * Draw visual component.
	 * 
	 * @param g Graphic context.
	 */
	@Override
	public void paint(Graphics g) {
		if (board == null) {
			return;
		}

		board.draw(g, this.getWidth(), this.getHeight());
	}

	/** Finalize internal state of the objects. */
	@Override
	public void finalize() {
		try {
			in.close();
		} catch (IOException ex) {
		}
		out.close();
		try {
			socket.close();
		} catch (IOException ex) {
		}
	}

	/**
	 * Main method.
	 * 
	 * @param args Command line parameters.
	 */
	public static void main(String[] args) {
		int port = Integer.valueOf(args[0]);
		String address = "" + args[1];

		RandomClient client = new RandomClient(port, address);

		client.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		client.setSize(800, 600);
		client.setVisible(true);
	}
}
