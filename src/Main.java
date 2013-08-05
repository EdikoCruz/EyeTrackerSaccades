import java.awt.BorderLayout;
import java.awt.JobAttributes;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Timer;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;

public class Main extends JFrame implements ActionListener {
	// gui
	JButton select = new JButton("Select files");
	JButton convert = new JButton("Convert files");
	JPanel north = new JPanel();
	JPanel south = new JPanel();
	JPanel panel = new JPanel();
	JLabel label = new JLabel("0/0");

	// fileChoose
	javax.swing.JFileChooser fileChooser = new javax.swing.JFileChooser();
	File[] filenames;

	// Files reader
	private FileReader fileReader;
	private BufferedReader bufferedReader;
	private java.io.File resultsFile;

	// Files Whriter
	private FileWriter fileWriter;
	private PrintWriter printWriter;

	// System
	private String line;
	private String time = "0";
	private String lastFixation = "0";
	private String[] subline;
	private String fname;
	private boolean checked = false;
	private boolean printed = true;
	private int count = 0;
	private int t1 = 0, t2 = 0;

	public Main() {

		select.addActionListener(this);
		north.add(select);
		convert.addActionListener(this);
		north.add(convert);

		south.add(label);

		panel.setLayout(new BorderLayout());
		panel.add(north, BorderLayout.NORTH);
		panel.add(south, BorderLayout.SOUTH);

		this.setVisible(true);
		this.setSize(300, 100);
		this.setLocation((int) (Toolkit.getDefaultToolkit().getScreenSize().getWidth() / 2) - 150, (int) (Toolkit.getDefaultToolkit().getScreenSize().getHeight() / 2) - 50);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setContentPane(panel);
		this.setTitle("EyeTracker - Saccades Translater");

		fileChooser.setFileSelectionMode(javax.swing.JFileChooser.FILES_ONLY);
		fileChooser.setMultiSelectionEnabled(true);
	}

	@Override
	public void actionPerformed(ActionEvent action) {
		if (action.getSource() == select) {
			fileChooser.showOpenDialog(null);

			filenames = fileChooser.getSelectedFiles();
			label.setText("0/" + filenames.length);
		}
		if (action.getSource() == convert) {
			for (int i = 0; i < filenames.length; i++) {
				//label.setText((i+1)+"/"+(i+1));
				try {
					String parent = fileChooser.getSelectedFile().getParent();
					String name = filenames[i].getName();
					resultsFile = new java.io.File(parent, name);

					fileReader = new FileReader(resultsFile);
					bufferedReader = new BufferedReader(fileReader);
					
					name = name.substring(0, name.length()-3);
					name = name + "txt";
					fname = parent + "/Converted" + name;
					fileWriter = new FileWriter(fname, false);
					printWriter = new PrintWriter(fileWriter);
					
					for (int j = 0; j <= 19; j++) {
						line = bufferedReader.readLine();
						subline = line.split(",");
						if (subline.length > 10) {
							break;
						}
					}
					
					count = 0;
					
					printWriter.println("saccades: time in milliseconds");
					while ((line = bufferedReader.readLine()) != null) {
						subline = line.split(",");
						if (subline[11].equals("1")) {
							if (!checked) {
								count++;
								t1 = Integer.parseInt(subline[0]);
							}
							checked = true;
							printed = false;
							t2 = Integer.parseInt(subline[0]);
						} else {
							if (!printed) {
								printWriter.println("Saccades " + count + ": " + (t2 - t1));
								printed = true;
							}
							checked = false;
						}
						time = subline[0];
						lastFixation = subline[11];
					}
					if (lastFixation.equals("1")) {
						printWriter.println("Saccades " + count + ": " + (Integer.parseInt(time) - t1));
					}
					printWriter.println("total time: seconds/milliseconds");
					printWriter.print("total time: " + (Double.parseDouble(time) / 1000) + "/" + time);

					printWriter.flush();
					printWriter.close();
					
					fileReader.close();
					bufferedReader.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
			label.setText("All files was converted!");
		}
	}

	public static void main(String[] args) {
		new Main();
	}
}
