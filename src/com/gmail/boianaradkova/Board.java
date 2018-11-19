/**
 * Coloring, Version 0.1 
 * New Bulgarian University
 *
 * Copyright (c) 2018 Boyana Kantarska
 */

package com.gmail.boianaradkova;

import java.awt.Color;
import java.awt.Graphics;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Board with color tiles.
 * 
 * @author Boyana Kantarska
 */
class Board {
	/** Lock for synchronization. */
	private AtomicBoolean locked = new AtomicBoolean(false);

	/** Board width. */
	private int columns = -1;

	/** Board height. */
	private int rows = -1;

	/** Number of colors on the board. */
	private int numOfColors = -1;

	/** Grid of the board. */
	private int grid[][] = {};

	/**
	 * Recursive flooding with a new color.
	 * 
	 * @param x        Start flooding from coordinate x.
	 * @param y        Start flooding from coordinate y.
	 * @param oldColor Old color which should be replaced.
	 * @param newColor New color for replacement.
	 */
	private void flood(int x, int y, int oldColor, int newColor) {
		if (x < 0) {
			return;
		}

		if (x >= grid.length) {
			return;
		}

		if (y < 0) {
			return;
		}

		if (y >= grid[x].length) {
			return;
		}

		if (grid[x][y] != oldColor) {
			return;
		}

		grid[x][y] = newColor;

		flood(x - 1, y, oldColor, newColor);
		flood(x, y - 1, oldColor, newColor);
		flood(x + 1, y, oldColor, newColor);
		flood(x, y + 1, oldColor, newColor);
	}

	/**
	 * Constructor.
	 * 
	 * @param columns     Board width.
	 * @param rows        Board height.
	 * @param numOfColors Number of colors on the board.
	 */
	public Board(int columns, int rows, int numOfColors) {
		locked.set(false);

		this.columns = columns;
		this.rows = rows;
		this.numOfColors = numOfColors;

		grid = new int[columns][rows];

		/* Random colors arrangement. */
		for (int i = 0; i < columns; i++) {
			for (int j = 0; j < rows; j++) {
				grid[i][j] = 1 + (int) (Math.random() * numOfColors);
			}
		}
	}

	/**
	 * Board width.
	 * 
	 * @return Width.
	 */
	public int getColumns() {
		return columns;
	}

	/**
	 * Board height.
	 * 
	 * @return Height.
	 */
	public int getRows() {
		return rows;
	}

	/**
	 * Number of colors on the board.
	 * 
	 * @return Number of colors.
	 */
	public int getNumOfColors() {
		return numOfColors;
	}

	/**
	 * Color on a specific position.
	 * 
	 * @param x X coordinate.
	 * @param y Y coordinate.
	 * 
	 * @return Color.
	 */
	public int getColorIndex(int x, int y) {
		return grid[x][y];
	}

	/**
	 * Convert color index from HSV to RGB color value.
	 * 
	 * https://en.wikipedia.org/wiki/HSL_and_HSV
	 * 
	 * @param x X coordinate of the cell.
	 * @param y Y coordinate of the cell.
	 * 
	 * @return Color object.
	 */
	private Color getColor(int x, int y) {
		int Hi;
		int R = 0, G = 0, B = 0;
		double H, S, V;
		double f, p, q, t;

		int val = grid[x][y];
		int min_val = 1;
		int max_val = numOfColors;

		val -= min_val;
		max_val -= min_val;

		H = 360 * val / max_val;
		S = 0.8;
		V = 0.8;

		Hi = (int) (H / 60) % 6;

		f = H / 60 - Hi;
		p = V * (1 - S);
		q = V * (1 - f * S);
		t = V * (1 - (1 - f) * S);

		switch (Hi) {
		case 0:
			R = (int) (V * 255);
			G = (int) (t * 255);
			B = (int) (p * 255);
			break;
		case 1:
			R = (int) (q * 255);
			G = (int) (V * 255);
			B = (int) (p * 255);
			break;
		case 2:
			R = (int) (p * 255);
			G = (int) (V * 255);
			B = (int) (t * 255);
			break;
		case 3:
			R = (int) (p * 255);
			G = (int) (q * 255);
			B = (int) (V * 255);
			break;
		case 4:
			R = (int) (t * 255);
			G = (int) (p * 255);
			B = (int) (V * 255);
			break;
		case 5:
			R = (int) (V * 255);
			G = (int) (p * 255);
			B = (int) (q * 255);
			break;
		}

		while (R > 0xFF) {
			R >>= 1;
		}
		while (G > 0xFF) {
			G >>= 1;
		}
		while (B > 0xFF) {
			B >>= 1;
		}

		return new Color(R, G, B);
	}

	/**
	 * Check for the synchronization lock.
	 * 
	 * @return True if the board is locked and false if the board is not locked.
	 */
	public boolean isLocked() {
		return locked.get();
	}

	/**
	 * Lock and unlock the board.
	 * 
	 * @param locked State.
	 */
	public void setLocked(boolean locked) {
		this.locked.set(locked);
	}

	/**
	 * Change the color of region starting on specific coordinate.
	 * 
	 * @param x     X coordinate.
	 * @param y     Y coordinate.
	 * @param color New color.
	 */
	public void change(int x, int y, int color) {
		flood(x, y, grid[x][y], color);
	}

	/**
	 * Draws the board on a specific graphic context.
	 * 
	 * @param g      Graphic context.
	 * @param width  Width of the drawing area.
	 * @param height Height of the drawing area.
	 */
	public void draw(Graphics g, int width, int height) {
		if (getColumns() == 0) {
			return;
		}

		if (getRows() == 0) {
			return;
		}

		int cellSize = 0;
		int a = (width - 1) / (getColumns() + 1);
		int b = (height - 1) / (getRows() + 1);

		if (a < b) {
			cellSize = a;
		} else {
			cellSize = b;
		}

		cellSize--;

		int xOffset = width / 2 - (getColumns() * (cellSize + 1)) / 2;
		int yOffset = height / 2 - (getRows() * (cellSize + 1)) / 2;

		for (int j = 0; j < getRows(); j++) {
			for (int i = 0; i < getColumns(); i++) {
				g.setColor(getColor(i, j));
				g.fillRect(xOffset + i * (cellSize + 1), yOffset + j * (cellSize + 1), cellSize, cellSize);
			}
		}
	}

	/** Representing the board as a string. */
	public String toString() {
		String text = "";

		for (int j = 0; j < rows; j++) {
			for (int i = 0; i < columns; i++) {
				text += grid[i][j];

				if (i < columns - 1) {
					text += " ";
				}
			}

			if (j < rows - 1) {
				text += "\n";
			}
		}

		return text;
	}
}
