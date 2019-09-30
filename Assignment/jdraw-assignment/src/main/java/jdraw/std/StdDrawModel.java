/*
 * Copyright (c) 2018 Fachhochschule Nordwestschweiz (FHNW)
 * All Rights Reserved. 
 */

package jdraw.std;

import java.util.stream.Stream;
import java.util.concurrent.CopyOnWriteArrayList;

import jdraw.framework.DrawCommandHandler;
import jdraw.framework.DrawModel;
import jdraw.framework.DrawModelEvent;
import jdraw.framework.DrawModelListener;
import jdraw.framework.Figure;
import jdraw.framework.FigureListener;

/**
 * Provide a standard behavior for the drawing model. This class initially does not implement the methods
 * in a proper way.
 * It is part of the course assignments to do so.
 * @author Jiyong Kang
 *
 */
public class StdDrawModel implements DrawModel {

	private CopyOnWriteArrayList<Figure> figures;
	private CopyOnWriteArrayList<DrawModelListener> listeners;
	private FigureListener figListener;

	public StdDrawModel() {
		figures = new CopyOnWriteArrayList<Figure>();
		listeners = new CopyOnWriteArrayList<DrawModelListener>();
		figListener = e -> {
			notifyListeners(e.getFigure(), DrawModelEvent.Type.FIGURE_CHANGED);
		};
	}

	/**
	 * Notifies all the listeners if the DrawModel is changed.
	 * 
	 * @param f The Figure involved in the event
	 * @param t The Type of the event
	 */
	private void notifyListeners(Figure f, DrawModelEvent.Type t) {
		for (DrawModelListener listener: listeners)
			listener.modelChanged(new DrawModelEvent(this, f, t));
	}

	@Override
	public void addFigure(Figure f) {
		if (figures.contains(f)) {
			//throw new IllegalArgumentException("The Figure is already in the DrawModel.");
			return;
		}
		figures.add(f);
		f.addFigureListener(figListener);

		notifyListeners(f, DrawModelEvent.Type.FIGURE_ADDED);
	}

	@Override
	public Stream<Figure> getFigures() {
		return figures.stream(); // Only guarantees, that the application starts -- has to be replaced !!!
	}

	@Override
	public void removeFigure(Figure f) {
		if (!figures.contains(f)) {
			//throw new IllegalArgumentException("The Figure is not in the DrawModel.");
			return;
		}
		figures.remove(f);
		f.removeFigureListener(figListener);

		notifyListeners(f, DrawModelEvent.Type.FIGURE_REMOVED);
	}

	@Override
	public void addModelChangeListener(DrawModelListener listener) {
		if (listeners.contains(listener)) {
			//throw new IllegalArgumentException("The DrawModelListener is already registered.");
			return;
		}
		listeners.add(listener);
	}

	@Override
	public void removeModelChangeListener(DrawModelListener listener) {
		if (!listeners.contains(listener)) {
			//throw new IllegalArgumentException("The DrawModelListener is not registered.");
			return;
		}
		listeners.remove(listener);
	}

	/** The draw command handler. Initialized here with a dummy implementation. */
	// TODO initialize with your implementation of the undo/redo-assignment.
	private DrawCommandHandler handler = new EmptyDrawCommandHandler();

	/**
	 * Retrieve the draw command handler in use.
	 * @return the draw command handler.
	 */
	@Override
	public DrawCommandHandler getDrawCommandHandler() {
		return handler;
	}

	@Override
	public void setFigureIndex(Figure f, int index) {
		if (!figures.contains(f)) {
			throw new IllegalArgumentException("The Figure is not in the DrawModel.");
		}
		
		if (index < 0 || index >= figures.size()) {
			throw new IndexOutOfBoundsException("The index " + index + " is invalid.");
		}
		figures.remove(f);
		figures.add(index, f);

		notifyListeners(f, DrawModelEvent.Type.DRAWING_CHANGED);
	}

	@Override
	public void removeAllFigures() {
		for (Figure f: figures)
			f.removeFigureListener(figListener);
		figures.clear();

		notifyListeners(null, DrawModelEvent.Type.DRAWING_CLEARED);
	}

}
