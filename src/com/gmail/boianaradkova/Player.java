/**
 * Coloring, Version 0.1 
 * New Bulgarian University
 *
 * Copyright (c) 2018 Boyana Kantarska
 */

package com.gmail.boianaradkova;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

/**
 * Presentation of each player.
 * 
 * @author Boyana Kantarska
 */
class Player extends Thread {
	/**
	 * Connection socket for each player.
	 */
	private Socket socket;

	/**
	 * Input stream.
	 */
	private BufferedReader in;

	/**
	 * Output stream.
	 */
	private PrintWriter out;

	/**
	 * Handle to the game object.
	 */
	private GameServer game;

	/**
	 * Player's name.
	 */
	private String name;

	/**
	 * X initial coordinate on the board.
	 */
	private int x;

	/**
	 * Y initial coordinate on the board.
	 */
	private int y;

	/**
	 * Current color used.
	 */
	private int color;

	/**
	 * Total score of the player.
	 */
	private int score;

	/**
	 * Player is active until the response is correct.
	 */
	private boolean active;

	/**
	 * Constructor.
	 * 
	 * @param name   Name of the player.
	 * @param socket Socket handle.
	 * @param game   Game handle.
	 * 
	 * @throws IOException If the socket is broken.
	 */
	public Player(String name, Socket socket, GameServer game) throws IOException {
		this.game = game;
		this.name = name;
		this.score = 0;

		this.socket = socket;

		this.in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.out = new PrintWriter(socket.getOutputStream());
	}

	/**
	 * Initialize player in the beginning of the game.
	 * 
	 * @param color Color of the player.
	 * @param x     X coordinate.
	 * @param y     Y Coordinate.
	 */
	public void init(int x, int y, int color) {
		this.color = color;
		this.x = x;
		this.y = y;
		active = true;

		System.out.println("Player " + name + " initialized ...");
	}

	/**
	 * Sets the player not to be active.
	 */
	public void setNotActive() {
		active = false;
	}

	/**
	 * Check is the player active.
	 * 
	 * @return True if the player is active and false if the player is not active.
	 */
	public boolean isActive() {
		return (active);
	}

	/**
	 * Player's x initial coordinate.
	 * 
	 * @return X coordinate.
	 */
	public int getX() {
		return (x);
	}

	/**
	 * Player's y initial coordinate.
	 * 
	 * @return Y coordinate.
	 */
	public int getY() {
		return (y);
	}

	/**
	 * Set new player's color.
	 * 
	 * @param color New color.
	 */
	public void setColor(int color) {
		this.color = color;
	}

	/**
	 * Get current player's color.
	 * 
	 * @return Color.
	 */
	public int getColor() {
		return (color);
	}

	/**
	 * Read data from the socket in given timeout.
	 * 
	 * @param timeout Number or seconds to wait for response.
	 * 
	 * @return Chosen color.
	 */
	public int read(int timeout) {
		int color = 0;
		String str = "";

		try {
			Thread.currentThread().sleep(1000 * timeout);
		} catch (InterruptedException ex) {
			System.err.println("Thread can not be interrupted: " + ex);
		}

		try {
			str = in.readLine();
		} catch (IOException ex) {
			str = "";
			System.err.println("Receive socket message failed: " + ex);
		}

		try {
			color = (new Integer(str)).intValue();
		} catch (Exception ex) {
			color = 0;
			System.err.println("Incorrect data receieved: " + ex.toString());
		}

		return (color);
	}

	/**
	 * Write data into socket.
	 * 
	 * @param str Data which should be written.
	 */
	public void write(String str) {
		out.print(str);
		out.flush();
	}

	/**
	 * Try to make move on each thread loop.
	 */
	public void run() {
		while (true) {
			game.doTurn(this);

			try {
				sleep((long) (Math.random()*100));
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}
	}
}
