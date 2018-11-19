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
@SuppressWarnings("serial")
class DrawingPanel extends JPanel {

	/** Handle to the board which should be drawn. */
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
	 * 
	 * @param g Graphic context.
	 */
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		board.draw(g, getWidth(), getHeight());
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
