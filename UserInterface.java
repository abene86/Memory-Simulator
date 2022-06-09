//TO DO: Nothing required here.

//******************************************************
//*******  DO NOT EDIT ANYTHING BELOW THIS LINE  *******
//******************************************************

import java.util.Map;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Stroke;
import java.awt.BasicStroke;
import java.awt.Font;
import java.awt.RenderingHints;
import java.awt.Component;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.awt.image.BufferedImage;

import javax.swing.JFrame;
import javax.swing.JButton;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JLabel;
import javax.swing.JRadioButton;
import javax.swing.ButtonGroup;
import javax.swing.JTextField;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
//import javax.swing.JFileChooser;


/**
 *  The graphical user interface for the simulation.
 *  
 *  @author K. Raven Russell
 */
public final class UserInterface implements ActionListener {
	
	/**
	 *  The simulation controlled by this GUI.
	 */
	private final Simulation sim;
	
	/**
	 *  The frame that holds everything.
	 */
	private final JFrame frame;
	
	/**
	 *  The actual image/graphic being displayed.
	 */
	private final BufferedImage image;
	
	/**
	 * Memory fitting algorithms.
	 */
	private final String[] fittingAlgs = {"First Fit", "Best Fit", "Worst Fit", "Next Fit"};
	
	/**
	 * Text area for allocating memory.
	 */
	private final JTextField mallocField;
	
	/**
	 * Text area for freeing memory.
	 */
	private final JTextField freeField;
	
	/**
	 * Text area for re-allocating memory.
	 */
	private final JTextField reallocField;
	
	/**
	 * Text area for nulling out variables in memory.
	 */
	private final JTextField nullField;
	
	/**
	 * Text label area for summary of variables in use.
	 */
	private final JLabel varLabel;
	
	//Opens a dialog to input a file;
	//private final JFileChooser fc;
	
	/**
	 * The table header for the variable summary field.
	 */
	private static final String HEADER = "<html><br /><center>Symbol Table</center><br /><hr /><table border=\"0\"><tr><td width=\"75px\">Name</td><td width=\"75px\">Address</td><td width=\"75px\">Bytes (8 bits)</td></tr>";
	
	/**
	 *  The number of columns of cells.
	 */
	private final int numCols;
	
	/**
	 *  The number of pixels to one cell in the image.
	 */
	private static final int BYTE_SIZE = 40;
	
	/**
	 * Creates a new user interface tied to a specific simulation.
	 * 
	 * @param sim the simulation to tie the interface to
	 * @param title the title of the window
	 * @param numRows the number of rows in memory to display
	 * @param numCols the number of columns in memory to display
	 */
	public UserInterface(Simulation sim, String title, int numRows, int numCols) {
		this.sim = sim;
		this.numCols = numCols;
		
		frame = new JFrame(title);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel contentPane = new JPanel();
		contentPane.setLayout(new BoxLayout(contentPane, BoxLayout.Y_AXIS));
		
		JPanel middlePanel = new JPanel();
		middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.X_AXIS));
		
		/**
		 * Image of memory.
		 */
		class ImagePanel extends JPanel {
			private BufferedImage image;
			public ImagePanel(BufferedImage image) { setImage(image); }
			
			/**
			 * Set the image to be displayed.
			 * @param image the image to display
			 */
			public void setImage(BufferedImage image) {
				this.image = image;
				this.setAlignmentY(Component.TOP_ALIGNMENT);
				this.setPreferredSize(new Dimension(image.getWidth(), image.getHeight()));
			}

			/**
			 *  {@inheritDoc}
			 */
			@Override
			protected void paintComponent(Graphics g) {
				super.paintComponent(g);
				g.drawImage(image, 0, 0, this);
			}

		}
		
		image = new BufferedImage(numCols * BYTE_SIZE, numRows * BYTE_SIZE, BufferedImage.TYPE_INT_RGB);
		middlePanel.add(new ImagePanel(image));
		
		varLabel = new JLabel();
		varLabel.setVerticalAlignment(JLabel.TOP);
		varLabel.setAlignmentY(Component.TOP_ALIGNMENT);
		//varLabel.setPreferredSize(new Dimension(300, image.getHeight()));
		varLabel.setText(HEADER + "</table></html>");
		
		JScrollPane sp = new JScrollPane(varLabel);
		sp.setAlignmentY(Component.TOP_ALIGNMENT);
		sp.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		
		middlePanel.add(sp);
		
		contentPane.add(middlePanel);
		
		
		//fitting algorithm choices
		
		JPanel buttonPanel = new JPanel();
		buttonPanel.setLayout(new BoxLayout(buttonPanel, BoxLayout.X_AXIS));
		buttonPanel.add(new JLabel("Fitting Algorithm: "));
		
		ButtonGroup group = new ButtonGroup();
		boolean selected = true;
		for(String alg : fittingAlgs) {
			JRadioButton fitAlg = new JRadioButton(alg);
			fitAlg.setActionCommand(alg);
			fitAlg.setSelected(selected);
			fitAlg.addActionListener(this);
			if(selected) { selected = false; }
			group.add(fitAlg);
			buttonPanel.add(fitAlg);
		}
		
		contentPane.add(buttonPanel);
		
		
		//interactions
		
		JPanel memoryOpsPanel1 = new JPanel();
		//memoryOpsPanel1.setLayout(new BoxLayout(memoryOpsPanel1, BoxLayout.X_AXIS));
		
		memoryOpsPanel1.add(new JLabel("Allocate: "));
		mallocField = new JTextField();
		mallocField.setColumns(10);
		mallocField.setActionCommand("malloc");
		mallocField.addActionListener(this);
		memoryOpsPanel1.add(mallocField);
		memoryOpsPanel1.add(new JLabel("(name:bytes)"));
		
		memoryOpsPanel1.add(javax.swing.Box.createHorizontalStrut(10));
		
		memoryOpsPanel1.add(new JLabel("Re-Allocate: "));
		reallocField = new JTextField();
		reallocField.setColumns(10);
		reallocField.setActionCommand("realloc");
		reallocField.addActionListener(this);
		memoryOpsPanel1.add(reallocField);
		memoryOpsPanel1.add(new JLabel("(name:bytes)"));
		
		/*
		JButton fileButton = new JButton("File");
		fileButton.setActionCommand("file");
		fileButton.addActionListener(this);
		memoryOpsPanel1.add(fileButton);
		*/
		
		contentPane.add(memoryOpsPanel1);
		
		
		JPanel memoryOpsPanel2 = new JPanel();
		//memoryOpsPanel2.setLayout(new BoxLayout(memoryOpsPanel2, BoxLayout.X_AXIS));
		
		memoryOpsPanel2.add(new JLabel("Free variable: "));
		freeField = new JTextField();
		freeField.setColumns(10);
		freeField.setActionCommand("free");
		freeField.addActionListener(this);
		memoryOpsPanel2.add(freeField);
		
		memoryOpsPanel2.add(new JLabel("Null variable: "));
		nullField = new JTextField();
		nullField.setColumns(10);
		nullField.setActionCommand("null");
		nullField.addActionListener(this);
		memoryOpsPanel2.add(nullField);
		
		JButton gcButton = new JButton("Run GC!");
		gcButton.setActionCommand("gc");
		gcButton.addActionListener(this);
		memoryOpsPanel2.add(gcButton);
		
		
		contentPane.add(memoryOpsPanel2);


		//pack everything up
		
		frame.setContentPane(contentPane);
		frame.pack();
		frame.setVisible(true);
	}

	/**
	 *  {@inheritDoc}
	 */
	@Override
	public void actionPerformed(ActionEvent event) {
		String action = event.getActionCommand();
		Object source = event.getSource();
		//System.out.println(action);
		
		String var;
		
		switch(action) {
			case "First Fit":
				sim.setMemMan(MemMan.FIRST_FIT);
				break;
			case "Best Fit":
				sim.setMemMan(MemMan.BEST_FIT);
				break;
			case "Worst Fit":
				sim.setMemMan(MemMan.WORST_FIT);
				break;
			case "Next Fit":
				sim.setMemMan(MemMan.NEXT_FIT);
				break;
			case "malloc":
				try {
					String[] parts = ((JTextField)source).getText().split(":");
					if(parts.length != 2) throw new IllegalArgumentException();
					if(!sim.malloc(parts[0], Integer.parseInt(parts[1]))) {
						JOptionPane.showMessageDialog(frame, "Cannot allocate "+parts[1]+" bytes.");
					}
				}
				catch(IllegalArgumentException e) {
					JOptionPane.showMessageDialog(frame, "Invalid Format.\nEnter text as \"variable:size\".");
				}
				break;
			case "free":
				var = ((JTextField)source).getText();
				if(!sim.free(var)) {
					JOptionPane.showMessageDialog(frame, "Cannot free variable \""+var+"\".");
				}
				break;
			case "realloc":
				try {
					String[] parts = ((JTextField)source).getText().split(":");
					if(parts.length != 2) throw new IllegalArgumentException();
					if(!sim.realloc(parts[0], Integer.parseInt(parts[1]))) {
						JOptionPane.showMessageDialog(frame, "Cannot re-allocate \""+parts[0]+"\" to "+parts[1]+" bytes.");
					}
				}
				catch(IllegalArgumentException e) {
					JOptionPane.showMessageDialog(frame, "Invalid Format.\nEnter text as \"variable:size\".");
				}
				break;
			case "null":
				var = ((JTextField)source).getText();
				if(!sim.nullOut(var)) {
					JOptionPane.showMessageDialog(frame, "Cannot null out variable \""+var+"\".");
				}
				break;
			case "gc":
				JOptionPane.showMessageDialog(frame, "Recovered "+sim.gc()+" bytes.");
				break;
			/*case "file":
				System.out.println(1);
				fc = new JFileChooser();
				System.out.println(1);
				int returnVal = fc.showOpenDialog(frame.getContentPane());
				if (returnVal == JFileChooser.APPROVE_OPTION) {
					sim.commandsFromFile(fc.getSelectedFile());
				}
				break;*/
			default:
				break;
		}
		
		if(source instanceof JRadioButton) {
			((JRadioButton)source).setSelected(true);
		}
		else if(source instanceof JTextField) {
			((JTextField)source).setText("");
		}
		
		sim.update();
	}
	
	/**
	 *  Resets the image to black for clean redraw.
	 */
	public void paintItBlack() {
		Graphics2D g = image.createGraphics();
		g.setColor(Color.BLACK);
		g.fillRect(0, 0, image.getWidth(), image.getHeight());
	}
	
	/**
	 *  Resets the image to black for clean redraw.
	 */
	public void repaint() {
		frame.getContentPane().repaint();
	}
	
	/**
	 * Sets the variables label to display all the program variables
	 * and their current settings in memory.
	 * 
	 * @param map the map of variables to their BareNodes (pointers)
	 */
	public void setVariables(Map<String,MemMan.BareNode> map) {
		StringBuilder sb = new StringBuilder();
		
		sb.append(HEADER);
		for(Map.Entry<String,MemMan.BareNode> entry : map.entrySet()) {
			String name = entry.getKey();
			sb.append("<tr><td>");
			sb.append(name);
			sb.append("</td><td>");
			
			MemMan.BareNode node = entry.getValue();
			if(node != null) {
				MemBlock m = node.block;
				sb.append(m.addr);
				sb.append("</td><td>");
				sb.append(m.size);
			}
			else {
				sb.append("null</td><td>0");
			}
			sb.append("</td></tr>");
			
		}
		sb.append("</table></html>");
		
		varLabel.setText(sb.toString());
	}
	
	/**
	 *  Instructs the image where to draw a particular block of memory and
	 *  how to label it.
	 *  
	 *  @param name the name of the variable that lives here
	 *  @param start the block of memory to start from
	 *  @param size how many squares in memory this block takes
	 *  @param color the color to draw with
	 */
	public void setBlock(String name, int start, int size, Color color) {
		Graphics2D g = image.createGraphics();
		
		int row = start / numCols;
		int col = start % numCols;
		
		int currRow = row;
		int currCol = col;
		int wrap = 0;
		for(int cell = 0; cell < size; cell++) {
			g.setColor(color);
			g.fillRect(currCol * BYTE_SIZE, currRow * BYTE_SIZE, BYTE_SIZE, BYTE_SIZE);
			
			if(++currCol >= this.numCols && cell != size - 1) {
				currCol = 0;
				currRow++;
				wrap++;
			}
		}
		
		//outline
		Stroke oldStroke = g.getStroke();
		g.setStroke(new BasicStroke(2));
		g.setColor(Color.BLACK);
		
		if(wrap == 0) {
			g.drawRect(col * BYTE_SIZE, row * BYTE_SIZE, BYTE_SIZE*size, BYTE_SIZE);
		}
		else {
			g.drawRect(col * BYTE_SIZE, row * BYTE_SIZE, BYTE_SIZE*(numCols-col), BYTE_SIZE);
			for(int r = 1; r < wrap; r++) {
				g.drawRect(0, (row+r) * BYTE_SIZE, BYTE_SIZE*numCols, BYTE_SIZE);
			}
			g.drawRect(0, currRow * BYTE_SIZE, BYTE_SIZE*currCol, BYTE_SIZE);
		}
		
		g.setStroke(oldStroke);
		
		g.setFont(new Font("SansSerif", Font.BOLD, 12));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
		g.drawString(name+"("+start+")", col * BYTE_SIZE + 4, (row * BYTE_SIZE) + (BYTE_SIZE/2) + 6);
	}
}