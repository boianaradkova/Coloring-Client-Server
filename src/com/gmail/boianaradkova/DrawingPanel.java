/**
 * Coloring, Version 0.1 
 * New Bulgarian University
 *
 * Copyright (c) 2018 Boyana Kantarska
 */

package com.gmail.boianaradkova;

import java.awt.Graphics;

import javax.swing.JPanel;

/**
 * Drawing board.
 * 
 * @author Boyana Kantarska
 */
class DrawingPanel extends JPanel {
	/**
	 * Serial Version ID is needed because of the JPanel.
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * Handle to the board which should be drawn.
	 */
	private Board board;

	/**
	 * Constructor.
	 * 
	 * @param board Handle to the board.
	 */
	DrawingPanel(Board board) {
		this.board = board;
	}

	/**
	 * Control paint method.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		board.draw(g, this.getWidth(), this.getHeight());
	}

	/**
	 * Change the board handle.
	 * 
	 * @param board Board handle.
	 */
	public void setBoard(Board board) {
		this.board = board;
	}
}