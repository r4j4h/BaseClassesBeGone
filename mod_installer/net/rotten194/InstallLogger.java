package net.rotten194;

import javax.swing.JProgressBar;
import javax.swing.JTextArea;

/**
 * @author Jonathon "Rotten194" Vogel (jonathon.vogel[at]gmail[dot]com)
 *
 */
public class InstallLogger {
	private JTextArea text;
	private JProgressBar bar;

	public InstallLogger(JTextArea text, JProgressBar bar){
		this.text = text;
		this.bar= bar;
		bar.setStringPainted(true);
	}
	
	public void log(String s){
		System.out.println(s);
		text.append(s + "\n");
	}
	
	public void logError(String s){
		System.err.println(s);
		text.append("[ERROR]" + s + "\n");
	}
	
	public void setBarRange(int min, int max){
		bar.setMinimum(min);
		bar.setMaximum(max);
	}
	
	public void setBarPoint(int i){
		bar.setValue(i);
	}
	
	public void setBarString(String s){
		bar.setString(s);
	}
}
