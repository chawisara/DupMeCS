import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.concurrent.Executor;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class Scoreboard extends JPanel {
	int[] topScores = new int[10];
	String[] topNames = new String[10];
	JLabel[] labels = new JLabel[topScores.length * 2];
	static String name1 = "";
	static String name2 = "";
	static int score1 = 0;
	static int score2 = 0;
	JButton menu;
	JPanel scoreboard, nameList;
	Socket con;
	Executor x;
	PrintWriter out;
	MainMenu mm;

	// from Results page
	public Scoreboard(String name_one, int score_one, String name_two,
			int score_two, Socket con, Executor x) throws IOException {
		super();
		System.out.println("at scoreboard!!!");
		name1 = name_one;
		score1 = score_one;
		name2 = name_two.substring(0, name_two.indexOf(","));
		score2 = score_two;
		this.con = con;
		this.x = x;
		// out = new PrintWriter(con.getOutputStream(), true);
		try {
			this.out = new PrintWriter(con.getOutputStream(), true);
			// this.in = new BufferedReader(new InputStreamReader(
			// con.getInputStream()));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		setLayout(new BorderLayout());
		JLabel title = new JLabel("Hall of Fame");
		add(title, BorderLayout.NORTH);
		title.setHorizontalAlignment(JLabel.CENTER);

		nameList = new JPanel();
		nameList.setLayout(new GridLayout(0, 2));
		add(nameList, BorderLayout.CENTER);

		// getTopTen(name1, score1, name2, score2);
		readFile("media/texts/newhighscore.txt");
		setGUI();
	}

	// from Main Menu page
//	 public Scoreboard(Socket con) throws IOException {
//	 super();
//	
//	 this.con = con;
//	 this.out = new PrintWriter(con.getOutputStream(), true);
//	 
//	 }

	// from Main Menu page
	public Scoreboard(Socket con, Executor x) throws IOException {
		super();
		this.con = con;
		this.out = new PrintWriter(con.getOutputStream(), true);
		mm = new MainMenu(name1, con, x);
		this.x = x;

		setLayout(new BorderLayout());
		scoreboard = new JPanel(new BorderLayout());

		JLabel title = new JLabel("Hall of Fame");
		scoreboard.add(title, BorderLayout.NORTH);
		title.setHorizontalAlignment(JLabel.CENTER);

		nameList = new JPanel();
		nameList.setLayout(new GridLayout(0, 2));
		scoreboard.add(nameList, BorderLayout.CENTER);

		this.add(scoreboard, BorderLayout.CENTER);
		// getTopTen();
		readFile("media/texts/newhighscore.txt");
		setGUI();

	}

	public void setGUI() {
		int n = 0, s = 0;

		for (int i = 0; i < labels.length; i++) {
			if (i % 2 == 0 && n < topNames.length) {
				labels[i] = new JLabel(topNames[n]);
				if (topNames[n].equals(name1) && topScores[s] == score1) {
					labels[i].setForeground(Color.RED);
					// labels[i].setBackground(Color.BLUE);
				}
				System.out.println(topNames[n]);
				n++;
				// System.out.println(n);
			} else if (i % 2 != 0 && s < topScores.length) {
				labels[i] = new JLabel("" + topScores[s]);
				if (topNames[n - 1].equals(name1) && topScores[s] == score1) {
					labels[i].setForeground(Color.RED);
					// labels[i-1].setForeground(Color.RED);
					// System.out.println(topNames[n]);
					// labels[i].setBackground(Color.BLUE);
				}
				s++;
				// System.out.println(s);
			}
			// System.out.println(topNames[n]);
			nameList.add(labels[i]);
			labels[i].setHorizontalAlignment(JLabel.CENTER);
			menu = new JButton("Back to Main Menu");
			scoreboard.add(menu, BorderLayout.SOUTH);
			menu.addActionListener(action);

			setVisible(true);

		}

//		menu = new JButton("Back to Main Menu");
//		// scoreboard.add(menu, BorderLayout.SOUTH);
//		menu.addActionListener(action);
//
//		setVisible(true);
	}

	// public void sortList() {
	// String tempStr;
	// int tempInt;
	// for (int j = topScores.length - 1; j >= 0; j--) {
	// for (int i = 0; i < j; i++) {
	// if (topScores[i] < topScores[i + 1]) {
	// tempInt = topScores[i + 1];
	// topScores[i + 1] = topScores[i];
	// topScores[i] = tempInt;
	// tempStr = topNames[i + 1];
	// topNames[i + 1] = topNames[i];
	// topNames[i] = tempStr;
	// }
	// }
	// }
	// }

	private ActionListener action = new ActionListener() { // change the content
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource().equals(menu)) {
				System.out.println("back to main menu");

				revalidate();
				repaint();
				out.println("scoreboard-to-mainmenu");
				out.flush();

			}
		}
	};

	public void getTopTen() throws IOException {
		readFile("media/texts/highscore.txt");

	}

	// public void getTopTen(String name1, int score1, String name2, int score2)
	// throws IOException {
	// readFile("media/texts/highscore.txt");
	//
	// for (int i = 0; i < topScores.length; i++) {
	// if (score1 >= topScores[i]) {
	// topScores[topScores.length - 1] = score1;
	// topNames[topScores.length - 1] = name1;
	// // break;
	// }
	// else if (score2 >= topScores[i]){
	// topScores[topScores.length - 1] = score2;
	// topNames[topScores.length - 1] = name2;
	// }
	// }
	// sortList();
	//
	// // for (int i = 0; i < topScores.length; i++) {
	// // if (score2 >= topScores[i]) {
	// // topScores[topScores.length - 1] = score2;
	// // topNames[topScores.length - 1] = name2;
	// // break;
	// // }
	// // }
	// // sortList();
	//
	// writeFile("media/texts/newhighscore.txt");
	// }

	// public void writeFile(String fileName) throws IOException {
	// PrintWriter writer = new PrintWriter(fileName, "UTF-8");
	// for (int i = 0; i < topScores.length; i++) {
	// writer.println(topNames[i] + "," + topScores[i]);
	// }
	// writer.close();
	// }
	//
	public void readFile(String fileName) throws IOException {
		String line = null;
		try {
			FileReader fileReader = new FileReader(fileName);
			BufferedReader bufferedReader = new BufferedReader(fileReader);
			int i = 0;
			while ((line = bufferedReader.readLine()) != null && i < 10) {
				String[] values = line.split(",");
				topNames[i] = values[0];
				// System.out.println(topNames[i]);
				topScores[i] = Integer.parseInt(values[1]);
				i++;
			}
			bufferedReader.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Unable to open file '" + fileName + "'");
		} catch (IOException ex) {
			System.out.println("Error reading file '" + fileName + "'");
			// ex.printStackTrace();
		}
	}

	public static void main(String[] args) {

	}
}
