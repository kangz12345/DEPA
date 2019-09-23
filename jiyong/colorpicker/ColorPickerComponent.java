package jiyong.colorpicker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public abstract class ColorPickerComponent {
	protected ColorPicker picker;

	public ColorPickerComponent(ColorPicker p) {
		picker = p;
	}

	// Will be called by the picker, to update the component with the color c
	public abstract void updateColor(Color c);
}

// Scrollbars
class ScrollComponent extends ColorPickerComponent implements AdjustmentListener {
	private JScrollBar[] scroll;
	private static final int R=0, G=1, B=2;

	public ScrollComponent(ColorPicker p) {
		super(p);
		scroll = new JScrollBar[3];
	}

	// Set scrollbars
	public void setScrollBars(JScrollBar[] sbars) {
		for (int i = 0; i < 3; ++i) {
			initScrollBar(sbars[i]);
			scroll[i] = sbars[i];
		}
	}

	// Overrides for ColorPickerComponent
	public void updateColor(Color c) {
		scroll[R].setValue(c.getRed());
		scroll[G].setValue(c.getGreen());
		scroll[B].setValue(c.getBlue());
	}

	// Overrides for AdjustmentListener
	public void adjustmentValueChanged(AdjustmentEvent e) {
		picker.modifyColor(getColor());
	}

	// private methods
	//   Initialize the given scrollbar
	private void initScrollBar(JScrollBar sbar) {
		sbar.setOrientation(Adjustable.HORIZONTAL);
		sbar.setMinimum(0);
		sbar.setMaximum(255);
		sbar.setVisibleAmount(0);
		sbar.setEnabled(true);
		sbar.addAdjustmentListener(this);
	}

	//   Get a color object from the scrollbars.
	private Color getColor() {
		return new Color(scroll[R].getValue(), scroll[G].getValue(), scroll[B].getValue());
	}
}

// Number textfields
class NumberComponent extends ColorPickerComponent implements KeyListener {
	private JTextField[] text;
	private static final int DEC_R=0, DEC_G=1, DEC_B=2, HEX_R=3, HEX_G=4, HEX_B=5;

	public NumberComponent(ColorPicker p) {
		super(p);
		text = new JTextField[6];
	}

	// Set textfields
	public void setTextFields(JTextField[] tfields) {
		for (int i = 0; i < 6; ++i) {
			text[i] = tfields[i];
			initTextField(i);
		}
	}

	// Overrides for ColorPickerComponent
	public void updateColor(Color c) {
		int[] rgb = {c.getRed(), c.getGreen(), c.getBlue()};
		int caretPos;
		String content;

		for (int i = 0; i < 3; ++i) {
			// decimal
			updateText(text[i], ""+rgb[i]);

			// hexadecimal
			updateText(text[i+3], Integer.toString(rgb[i], 16).toUpperCase());
		}
	}

	// Overrides for KeyListener
	public void keyPressed(KeyEvent e) {

	}

	public void keyReleased(KeyEvent e) {
		picker.modifyColor(new Color(
			getValue(text[DEC_R].getText()),
			getValue(text[DEC_G].getText()),
			getValue(text[DEC_B].getText())));
	}

	public void keyTyped(KeyEvent e) {

	}

	// private methods
	//   Initialize the textfield at the given index
	private void initTextField(int i) {
		JTextField tfield = text[i];
		if (i < 3) {
			tfield.setEnabled(true);
			tfield.addKeyListener(this);
		}
		// The textfields for hexadecimal are not enabled
		else tfield.setEnabled(false);

	}

	//   Update the text in the given textfield with the given string, 
	//   keeping the caret position same
	private void updateText(JTextField tfield, String s) {
		int caretPos = tfield.getCaretPosition();
		tfield.setText(s);
		if (caretPos < s.length()) tfield.setCaretPosition(caretPos);
		else tfield.setCaretPosition(s.length());
	}

	//   Get the int value from the given string, ignoring non-digits
	//   Also fit the value into 0-255
	private int getValue(String s) {
		int value = -1;
		s = s.replaceAll("[^\\d]", "");

		if (s.length() == 0) value = 0;
		else value = Integer.parseInt(s);

		if (value < 0) return 0;
		if (value > 255) return 255;

		return value;
	}
}

// Canvas; JPanel
class CanvasComponent extends ColorPickerComponent {
	private JPanel canvas;

	public CanvasComponent(ColorPicker p) {
		super(p);
	}

	// set the panel
	public void setPanel(JPanel p) {
		canvas = p;
	}

	// Overrides for ColorPickerComponent
	public void updateColor(Color c) {
		canvas.setBackground(c);
	}
}

// Radio buttons
class RadioComponent extends ColorPickerComponent implements ActionListener {
	public static final int NUM_COLORS = 8;

	private JRadioButton[] radio;
	private static final String[] COLOR_NAMES = {
		"Red", "Green", "Blue", "Yellow", "Cyan", "Orange", "White", "Black"
	};
	private static final Color[] COLORS = {
		Color.RED, Color.GREEN, Color.BLUE, Color.YELLOW,
		Color.CYAN, Color.ORANGE, Color.WHITE, Color.BLACK
	};

	public RadioComponent(ColorPicker p) {
		super(p);
		radio = new JRadioButton[NUM_COLORS];
	}

	// Set radiobuttons
	public void setRadioButtons(JRadioButton[] bts) {
		for (int i = 0; i < NUM_COLORS; ++i) {
			radio[i] = bts[i];
			initRadioButton(i);
		}
	}

	// Overrides for ColorPickerComponent
	public void updateColor(Color c) {
		for (int i = 0; i < NUM_COLORS; ++i) {
			if (c.equals(COLORS[i])) radio[i].setSelected(true);
			else radio[i].setSelected(false);
		}
	}

	// Overrides for ActionListener
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < NUM_COLORS; ++i) {
			if ((JRadioButton) e.getSource() == radio[i]) {
				picker.modifyColor(COLORS[i]);
				break;
			}
		}
	}

	// private methods
	//   Initialize the radio button at the given index
	private void initRadioButton(int i) {
		JRadioButton bt = radio[i];
		bt.setText(COLOR_NAMES[i]);
		bt.setSelected(false);
		bt.addActionListener(this);
	}
}

// Buttons; darker and brighter
class ButtonComponent extends ColorPickerComponent implements ActionListener {
	private JButton[] button;
	private static final String[] BUTTON_NAMES = {"Darker", "Brighter"};
	private static final int DARKER=0, BRIGHTER=1;
	private Color color;

	public ButtonComponent(ColorPicker p) {
		super(p);
		button = new JButton[2];
		color = null;
	}

	// Set buttons
	public void setButtons(JButton[] bts) {
		for (int i = 0; i < 2; ++i) {
			button[i] = bts[i];
			initButton(i);
		}
	}

	// Overrides for ColorPickerComponent
	public void updateColor(Color c) {
		// If c.darker() is the same color as c, disable the darker button
		if (c.equals(c.darker())) button[DARKER].setEnabled(false);
		else button[DARKER].setEnabled(true);
		
		// If c.brighter() is the same color as c, disable the brighter button
		if (c.equals(c.brighter())) button[BRIGHTER].setEnabled(false);
		else button[BRIGHTER].setEnabled(true);

		color = c;
	}

	// Overrides for ActionListener
	public void actionPerformed(ActionEvent e) {
		if ((JButton) e.getSource() == button[DARKER]) picker.modifyColor(color.darker());
		else picker.modifyColor(color.brighter());
	}

	// private methods
	//   Initialize the button at the given index
	private void initButton(int i) {
		button[i].setText(BUTTON_NAMES[i]);
		button[i].addActionListener(this);
	}
}

// Menu items
class MenuComponent extends ColorPickerComponent implements ActionListener {
	public static final int NUM_COLORS = 13;

	private JCheckBoxMenuItem[] item;
	private static final String[] COLOR_NAMES = {
		"Red", "Green", "Blue", "Orange", "Yellow", "Cyan", "Pink",
		"Magenta", "White", "Light Gray", "Gray", "Dark Gray", "Black"
	};
	private static final Color[] COLORS = {
		Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, 
		Color.YELLOW, Color.CYAN, Color.PINK, Color.MAGENTA, 
		Color.WHITE, Color.LIGHT_GRAY, Color.GRAY, Color.DARK_GRAY, Color.BLACK
	};

	public MenuComponent(ColorPicker p) {
		super(p);
		item = new JCheckBoxMenuItem[NUM_COLORS];
	}

	// Set radiobuttons
	public void setMenuItems(JCheckBoxMenuItem[] items) {
		for (int i = 0; i < NUM_COLORS; ++i) {
			item[i] = items[i];
			initMenuItem(i);
		}
	}

	// Overrides for ColorPickerComponent
	public void updateColor(Color c) {
		for (int i = 0; i < NUM_COLORS; ++i) {
			if (c.equals(COLORS[i])) item[i].setSelected(true);
			else item[i].setSelected(false);
		}
	}

	// Overrides for ActionListener
	public void actionPerformed(ActionEvent e) {
		for (int i = 0; i < NUM_COLORS; ++i) {
			if ((JCheckBoxMenuItem) e.getSource() == item[i]) {
				picker.modifyColor(COLORS[i]);
				break;
			}
		}
	}

	// private methods
	//   Initialize the menu item at the given index
	private void initMenuItem(int i) {
		JCheckBoxMenuItem mi = item[i];
		mi.setText(COLOR_NAMES[i]);
		mi.setSelected(false);
		mi.addActionListener(this);
	}
}