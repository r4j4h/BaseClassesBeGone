/**
 * 
 */
package net.rotten194;

import java.awt.BorderLayout;
import java.awt.CardLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.Insets;

import static java.awt.GridBagConstraints.*;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JWindow;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;

/**
 * @author Jon
 *
 */
public class Gui {
	JPanel[] screens = new JPanel[4];
	JPanel error = new JPanel();
	String desc;
	String liscense;
	JFrame window = new JFrame();
	int screen;
	
	
	CardLayout card;
	JPanel cards;
	JFileChooser choose;
	String title;
	JLabel sure;
	
	public Gui(URL fimage, String stitle, String desc, String liscense) throws IOException {
		this.desc = desc;
		this.liscense = liscense;
		this.title = stitle;
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception e) {
			e.printStackTrace();
		}

		window.setTitle("BCBG Mod Installer: " + title);
		window.setSize(800, 500);
		window.addWindowListener(new WindowAdapter(){
			public void windowClosing(WindowEvent e){
				System.exit(0);
			}
		});
		
		cards = new JPanel();
		card = new CardLayout();
		cards.setLayout(card);
		
		screens[0] = new JPanel();
		screens[0].setLayout(new BorderLayout());
		JLabel image = new JLabel(new ImageIcon(ImageIO.read(fimage)));
		screens[0].add(image, BorderLayout.PAGE_START);
		JTextArea text = new JTextArea(desc);
		text.setEditable(false);
		text.setLineWrap(true);
		JScrollPane scroll = new JScrollPane(text);
		screens[0].add(scroll, BorderLayout.CENTER);
		screens[0].add(createButtonPanel("Continue", "Back", createFlipper("NEXT"), null), BorderLayout.PAGE_END);	

		
		screens[1] = new JPanel();
		screens[1].setLayout(new BorderLayout());
		JLabel label = new JLabel("TERMS AND CONDITIONS:");
		label.setFont(new Font(Font.DIALOG, Font.BOLD, 24));
		JTextArea text2 = new JTextArea(liscense);
		text2.setEditable(false);
		text2.setLineWrap(true);
		JScrollPane scroll2 = new JScrollPane(text2);
		screens[1].add(label, BorderLayout.NORTH);
		screens[1].add(scroll2, BorderLayout.CENTER);	
		screens[1].add(createButtonPanel("Agree and Continue", "Back", createFlipper("NEXT"), createFlipper("BACK")), BorderLayout.PAGE_END);
		
		screens[2] = new JPanel();
		screens[2].setLayout(new BorderLayout());
		choose = new JFileChooser();
		choose.setSelectedFile(Installer.getDefaultDirectory("minecraft"));
		choose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		screens[2].add(new JLabel("Double-click the .minecraft folder you want to use and click continue", UIManager.getIcon("OptionPane.informationIcon"), SwingConstants.CENTER), BorderLayout.PAGE_START);
		screens[2].add(choose, BorderLayout.CENTER);	
		screens[2].add(createButtonPanel("Set .minecraft and continue", "Back", new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				System.out.println(choose.getSelectedFile());
				Installer.installDir = new File(choose.getSelectedFile(), "patches");
				if (!Installer.installDir.isDirectory()){
					if (screens[2].getComponent(0) instanceof JLabel && ((JLabel)screens[2].getComponent(0)).getIcon() ==  UIManager.getIcon("OptionPane.informationIcon")){
						screens[2].remove(0);
						screens[2].add(new JLabel("Please run BCBG at least once to create install targets", UIManager.getIcon("OptionPane.errorIcon"), SwingConstants.CENTER), BorderLayout.PAGE_START);
						screens[2].validate();
					}
				}
				else {
					sure = new JLabel("Are you sure you want to install " + title + " to " + Installer.installDir.getAbsolutePath() + "?");
					card.next(cards);
				}
			}
		}, createFlipper("BACK")), BorderLayout.PAGE_END);
		
		screens[3] = new JPanel();
		screens[3].setLayout(new BorderLayout());
		JProgressBar bar = new JProgressBar();
		screens[3].add(bar);
		
		for(int i = 0; i < screens.length; i++){
			JPanel screen = screens[i];
			if (screen != null){
				cards.add(screen, ((Integer)i).toString());
			}
		}
		
		window.add(cards);
		
		card.first(cards);
		window.setVisible(true);
	}
	
	private JPanel createButtonPanel(String next, String back, ActionListener nextClick, ActionListener backClick){
		JPanel ret = new JPanel();
		GridBagLayout layout = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		ret.setLayout(layout);
		JButton nextB = null;
		JButton backB = null;
		JPanel space = new JPanel();
		c.weightx = 1.0;
		c.anchor = GridBagConstraints.LINE_START;
		layout.setConstraints(space, c);
		ret.add(space);
		if (back != null){
			backB = new JButton(back);
			if (backClick != null)
				backB.addActionListener(backClick);
			else
				backB.setEnabled(false);
			c.weightx = 0;
			c.anchor = CENTER;
			layout.setConstraints(backB, c);
			ret.add(backB);
		}
		if (next != null){
			nextB = new JButton(next);
			if (nextClick != null)
				nextB.addActionListener(nextClick);
			else
				nextB.setEnabled(false);
			c.weightx = 0;
			c.anchor = CENTER;
			layout.setConstraints(nextB, c);
			ret.add(nextB);
		}
		return ret;
	}
	
	private ActionListener createFlipper(String direction){
		if (direction.equals("NEXT")){
			return new ActionListener() {		
				public void actionPerformed(ActionEvent arg0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							card.next(cards);
						}
					});
				}
			};
		}
		else{
			return new ActionListener() {		
				public void actionPerformed(ActionEvent arg0) {
					SwingUtilities.invokeLater(new Runnable() {
						public void run() {
							card.previous(cards);
						}
					});
				}
			};
		}
	}
	
}
