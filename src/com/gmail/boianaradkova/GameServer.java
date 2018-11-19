/**
 * Coloring, Version 0.1 
 * New Bulgarian University
 *
 * Copyright (c) 2018 Boyana Kantarska
 */

package com.gmail.boianaradkova;

import java.awt.Graphics;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collections;
import java.util.Vector;

import javax.swing.JFrame;

/**
 * The game engine.
 * 
 * @author Boyana Kantarska
 */
@SuppressWarnings("serial")
public class GameServer extends JFrame {
	/** Port number for the game server. */
	private final int SERVER_PORT = 3379;

	/** Clients have given seconds to connect. */
	private final int CONNECTIONS_TIMEOUT = 5;

	/** Each client should response in a given seconds. */
	private final int RESPONSE_TIMEOUT = 0;

	/** Index of the player on turn. */
	private int playingIndex;

	/** Game board with lock. */
	private Board board;

	/** Holding handle to each player as thread. */
	private Vector<Player> players = new Vector<>();

	/** Drawing area for the board. */
	private DrawingPanel canvas;

	/** Order of the players should be random. */
	private void shufflePlayers() {
		Collections.shuffle(players);
	}

	/** Matching the playing player index with the handle. */
	private Player playingNow() {
		return players.elementAt(playingIndex);
	}

	/**
	 * Check for blocked colors.
	 * 
	 * @param playerIndex Player on turn.
	 * @param color       New selected color.
	 * 
	 * @return True if the color is not in use and false if the color is in use.
	 */
	private boolean isColorUsed(int playerIndex, int color) {
		boolean isUsed = false;

		for (int p = 0; p < players.size(); p++) {
			/* If the player is the same do nothing. */
			if (p == playerIndex) {
				continue;
			}

			/* The color should correspond. */
			if (color != ((Player) players.elementAt(p)).getColor()) {
				continue;
			}

			isUsed = true;
			break;
		}

		return isUsed;
	}

	/**
	 * Check the position in first positioning of the player.
	 * 
	 * @param playerIndex Index of the player to be positioned.
	 * @param x           X coordinate on the board.
	 * @param y           Y coordinate on the board.
	 * @param color       Color which should be used.
	 * 
	 * @return True if the position is available and false if the position is not
	 *         available.
	 */
	private boolean isGoodPosition(int playerIndex, int x, int y) {
		boolean isGood = true;
		int color = board.getColorIndex(x, y);

		for (int p = 0; p < playerIndex; p++) {
			if (((Player) players.elementAt(p)).getColor() != color) {
				continue;
			}
			if (((Player) players.elementAt(p)).getX() != x) {
				continue;
			}
			if (((Player) players.elementAt(p)).getY() != y) {
				continue;
			}

			isGood = false;
			break;
		}

		return isGood;
	}

	/**
	 * Constructor with parameters.
	 * 
	 * @param title Game server title.
	 */
	public GameServer(String title) {
		super(title);

		canvas = new DrawingPanel(board);
		this.getContentPane().add(canvas);
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

	/**
	 * Initialize board and players. Should be executed after the players are
	 * connected.
	 */
	public void init() {
		Thread.currentThread().setPriority(Thread.MIN_PRIORITY);

		int x, y;

		/* Choosing rondom number of colors. */
		int numOfColors = (players.size() + 1) + (int) (Math.random() * 4 * players.size() - 1);

		/* Choosing random dimensions of the board. */
		int width = (players.size() + 1) + (int) (Math.random() * 9 * players.size() - 1);
		int heigth = (players.size() + 1) + (int) (Math.random() * 9 * players.size() - 1);

		/* Board creation. */
		board = new Board(width, heigth, numOfColors);

		/* Initializing players and positioning on the board. */
		for (int p = 0; p < players.size(); p++) {
			do {
				x = (int) (Math.random() * board.getColumns());
				y = (int) (Math.random() * board.getRows());
			} while (isGoodPosition(p, x, y) == false);

			((Player) players.elementAt(p)).init(x, y, board.getColorIndex(x, y));
		}

		/* Players should play turns in random order. */
		shufflePlayers();

		/* Players' threads should be started. */
		for (int p = 0; p < players.size(); p++) {
			((Player) players.elementAt(p)).setPriority(Thread.MIN_PRIORITY);
			((Player) players.elementAt(p)).start();
		}

		System.out.println("Game server initialization ...");

		canvas.setBoard(board);
	}

	/**
	 * Turn of a player.
	 * 
	 * @param player Player who is on turn.
	 */
	synchronized public void doTurn(Player player) {
		/* Synchronization is needed because the board is only one. */
		while (board.isLocked() == true) {
			try {
				wait();
			} catch (InterruptedException ex) {
				ex.printStackTrace();
			}
		}

		/* Only one player on time should play. */
		board.setLocked(true);

		if (player.isActive() == true) {
			for (int p = 0; p < players.size(); p++) {
				if ((players.elementAt(p)) == player) {
					playingIndex = p;
				}
			}

			/* Game state is send via TCP socket. */
			player.write(this.toString());

			/* Player's move is received via TCP socket. */
			int color = player.read(RESPONSE_TIMEOUT);

			/* Player answer should be valid. */
			if (color < 1 || color > board.getNumOfColors() || isColorUsed(playingIndex, color) == true)
				player.setNotActive();
			else {
				board.change(player.getX(), player.getY(), color);
				player.setColor(color);
			}
		}

		canvas.repaint();

		/* Unlock and notify the others. */
		board.setLocked(false);
		notifyAll();
	}

	/**
	 * Add player to the list of the players.
	 */
	public void addPlayer(Player player) {
		players.add(player);
	}

	/**
	 * Presenting game state as string.
	 */
	public String toString() {
		/* Text of the message sent by the server. */
		String text = "";

		text += board.getColumns() + " " + board.getRows() + " " + board.getNumOfColors() + " " + players.size() + "\n";

		text += (1 + playingNow().getX()) + " " + (1 + playingNow().getY()) + "\n";

		for (int p = 0; p < players.size(); p++) {
			text += ((Player) players.elementAt(p)).getColor();

			if (p != players.size() - 1) {
				text += " ";
			}
		}
		text += "\n";
		text += board + "\n";

		return text;
	}

	/** Wait players to connect. */
	public void host() {
		long start = System.currentTimeMillis();

		try {
			ServerSocket server = new ServerSocket(SERVER_PORT);
			server.setSoTimeout(CONNECTIONS_TIMEOUT * 1000);

			System.out.println("Server started on port " + SERVER_PORT + " ...");
			System.out.println("Server will wait for clients to connect " + CONNECTIONS_TIMEOUT + " seconds ...");

			do {
				Socket client = null;

				try {
					client = server.accept();
				} catch (Exception ex) {
				}

				if (client != null) {
					addPlayer(new Player("" + System.currentTimeMillis(), client, this));
				}
			} while ((System.currentTimeMillis() - start) < CONNECTIONS_TIMEOUT * 1000);
		} catch (Exception ex) {
			ex.printStackTrace();
		}

		System.out.println("Connection timeout, " + players.size() + " clients connected ...");
	}

	/**
	 * Main method.
	 * 
	 * @param args Command line parameters.
	 */
	public static void main(String args[]) {
		GameServer game = new GameServer("Game Server ...");

		game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		game.setSize(800, 600);
		game.setVisible(true);

		game.host();
		game.init();
	}
}
