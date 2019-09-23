package jiyong.colorpicker;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

// JFrame containing all the components
public class ColorPicker extends JFrame {
	private ColorPickerComponent[] components;
	private static final int SCROLL=0, NUMBER=1, CANVAS=2, RADIO=3, BUTTON=4, MENU=5, NUM_COMPONENTS=6;

	public ColorPicker() {
		super("Color Picker");

		setSize(400, 300);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		components = new ColorPickerComponent[NUM_COMPONENTS];
	}

	// initialize self
	public void init() {
		// Set the layout
		setLayout(new GridLayout(2, 2));
		add(panelScrollBars());
		add(panelNumbers());
		add(panelCanvas());
		add(panelRadiosAndButtons());

		// Set the menubar
		JMenuBar menubar = new JMenuBar();
		menubar.add(menuFile());
		menubar.add(menuAttributes());
		setJMenuBar(menubar);

		setVisible(true);

		// Set the initial color as BLACK
		modifyColor(Color.BLACK);
	}

	// Will be called by a ColorPickerComponent, to change the color
	public void modifyColor(Color c) {
		// Broadcast the given color to every component
		for (ColorPickerComponent comp: components) {
			if (comp != null)
				comp.updateColor(c);
		}
	}

	// Build a JPanel for scrollbars
	private JPanel panelScrollBars() {
		JPanel panel = new JPanel();
		JScrollBar[] sbars = new JScrollBar[3];

		panel.setLayout(new GridLayout(3, 1));
		for (int i = 0; i < 3; ++i) {
			sbars[i] = new JScrollBar();
			panel.add(sbars[i]);
		}

		ScrollComponent c = new ScrollComponent(this);
		c.setScrollBars(sbars);

		components[SCROLL] = c;

		return panel;
	}

	// Build a JPanel for numbers
	private JPanel panelNumbers() {
		JPanel panel = new JPanel();
		JTextField[] tfields = new JTextField[6];

		panel.setLayout(new GridLayout(3, 2));
		for (int i = 0; i < 3; ++i) {
			tfields[i] = new JTextField();
			tfields[i+3] = new JTextField();
			panel.add(tfields[i]);
			panel.add(tfields[i+3]);
		}

		NumberComponent c = new NumberComponent(this);
		c.setTextFields(tfields);

		components[NUMBER] = c;

		return panel;
	}

	// Build a JPanel for a canvas
	private JPanel panelCanvas() {
		JPanel panel = new JPanel();

		CanvasComponent c = new CanvasComponent(this);
		c.setPanel(panel);

		components[CANVAS] = c;

		return panel;
	}

	// Build a JPanel for radio buttons and buttons
	private JPanel panelRadiosAndButtons() {
		JPanel panel = new JPanel();

		panel.setLayout(new GridLayout(1, 2));
		panel.add(panelRadios());
		panel.add(panelButtons());

		return panel;
	}

	// Build a JPanel for radio buttons
	private JPanel panelRadios() {
		JPanel panel = new JPanel();
		JRadioButton[] bts = new JRadioButton[RadioComponent.NUM_COLORS];

		panel.setLayout(new GridLayout(RadioComponent.NUM_COLORS, 1));
		for (int i = 0; i < RadioComponent.NUM_COLORS; ++i) {
			bts[i] = new JRadioButton();
			panel.add(bts[i]);
		}

		RadioComponent c = new RadioComponent(this);
		c.setRadioButtons(bts);

		components[RADIO] = c;

		return panel;
	}

	// Build a JPanel for buttons-darker and brighter
	private JPanel panelButtons() {
		JPanel panel = new JPanel();
		JButton[] bts = new JButton[2];

		panel.setLayout(new GridLayout(2, 1));
		for (int i = 0; i < 2; ++i) {
			bts[i] = new JButton();
			panel.add(bts[i]);
		}

		ButtonComponent c = new ButtonComponent(this);
		c.setButtons(bts);

		components[BUTTON] = c;

		return panel;
	}

	// Build a JMenu for 'File'
	private JMenu menuFile() {
		JMenu menu = new JMenu("File");
		JMenuItem quit = new JMenuItem("Quit");
		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				dispose();
				System.exit(0);
			}
		});

		menu.add(quit);

		return menu;
	}

	// Build a JMenu for 'Attributes'
	private JMenu menuAttributes() {
		JMenu menu = new JMenu("Attributes");
		JCheckBoxMenuItem[] items = new JCheckBoxMenuItem[MenuComponent.NUM_COLORS];

		for (int i = 0; i < MenuComponent.NUM_COLORS; ++i) {
			items[i] = new JCheckBoxMenuItem();
			menu.add(items[i]);
		}

		MenuComponent c = new MenuComponent(this);
		c.setMenuItems(items);

		components[MENU] = c;

		return menu;
	}
}