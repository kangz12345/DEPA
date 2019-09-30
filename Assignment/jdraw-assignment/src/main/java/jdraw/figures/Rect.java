/*
 * Copyright (c) 2018 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package jdraw.figures;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import jdraw.framework.Figure;
import jdraw.framework.FigureEvent;
import jdraw.framework.FigureHandle;
import jdraw.framework.FigureListener;

/**
 * Represents rectangles in JDraw.
 * 
 * @author Christoph Denzler
 *
 */
public class Rect implements Figure {
	private static final long serialVersionUID = 9120181044386552132L;

	private CopyOnWriteArrayList<FigureListener> listeners;

	/**
	 * Use the java.awt.Rectangle in order to save/reuse code.
	 */
	private final Rectangle rectangle;
	
	/**
	 * Create a new rectangle of the given dimension.
	 * @param x the x-coordinate of the upper left corner of the rectangle
	 * @param y the y-coordinate of the upper left corner of the rectangle
	 * @param w the rectangle's width
	 * @param h the rectangle's height
	 */
	public Rect(int x, int y, int w, int h) {
		rectangle = new Rectangle(x, y, w, h);

		listeners = new CopyOnWriteArrayList<FigureListener>();
	}

	/**
	 * Notifies all the listeners if the Rectangle is changed.
	 */
	private void notifyListeners() {
		for (FigureListener listener: listeners)
			listener.figureChanged(new FigureEvent(this));
	}

	/**
	 * Draw the rectangle to the given graphics context.
	 * @param g the graphics context to use for drawing.
	 */
	@Override
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		g.setColor(Color.BLACK);
		g.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
	}
	
	@Override
	public void setBounds(Point origin, Point corner) {
		rectangle.setFrameFromDiagonal(origin, corner);
		
		notifyListeners();
	}

	@Override
	public void move(int dx, int dy) {
		rectangle.setLocation(rectangle.x + dx, rectangle.y + dy);
		
		if (dx != 0 || dy != 0) notifyListeners();
	}

	@Override
	public boolean contains(int x, int y) {
		return rectangle.contains(x, y);
	}

	@Override
	public Rectangle getBounds() {
		return rectangle.getBounds();
	}

	/**
	 * Returns a list of 8 handles for this Rectangle.
	 * @return all handles that are attached to the targeted figure.
	 * @see jdraw.framework.Figure#getHandles()
	 */	
	@Override
	public List<FigureHandle> getHandles() {
		return null;
	}

	@Override
	public void addFigureListener(FigureListener listener) {
		if (listeners.contains(listener)) {
			// throw new IllegalArgumentException("The FigureListener is already registered.");
			return;
		}
		listeners.add(listener);
	}

	@Override
	public void removeFigureListener(FigureListener listener) {
		if (!listeners.contains(listener)) {
			// throw new IllegalArgumentException("The FigureListener is not registered.");
			return;
		}
		listeners.remove(listener);
	}

	@Override
	public Figure clone() {
		Rect cloned = new Rect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		for (FigureListener listener: listeners)
			cloned.addFigureListener(listener);

		return cloned;
	}

}
