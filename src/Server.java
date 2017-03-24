import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Panel;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server {

	static final int POOL_SIZE = 10;
	static Executor execs = Executors.newFixedThreadPool(POOL_SIZE);
	static int numClients = 0;

	static List<Socket> socs = new ArrayList<Socket>();
	// Socket con;
	static List<ClientInfo> clients = new ArrayList<ClientInfo>();
	static int turnCount = 0;
	static int currentPlayer = 0;

	static List<PrintWriter> writer;
	static ListIterator<PrintWriter> itr;

	JFrame f;
	JLabel totalLabel = new JLabel();
	static JLabel total = new JLabel();
	static JTextArea scores = new JTextArea();

	JButton reset = new JButton("Reset");
	// Panel titlePanel;
	Panel scoresPanel;
	Panel titleScores;
	Panel totalPanel;
	Panel buttons;

	public Server() {
		f = new JFrame();
		f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		f.setSize(600, 400);
		f.setLayout(new BorderLayout());

		/*
		 * titlePanel = new Panel(); titlePanel.setLayout(new GridLayout(0,2));
		 * JLabel clientName=new JLabel("Player Name"); JLabel clientScore=new
		 * JLabel("Score"); titlePanel.add(clientName);
		 * titlePanel.add(clientScore);
		 */

		scoresPanel = new Panel();
		scoresPanel.setLayout(new BorderLayout());
		scores.setText("Player Name\t\t\t\t\t\tScore\n");
		scores.setEditable(false);
		scoresPanel.add(scores, BorderLayout.CENTER);

		titleScores = new Panel();
		titleScores.setLayout(new BorderLayout());
		// titleScores.add(titlePanel, BorderLayout.NORTH);
		titleScores.add(scoresPanel, BorderLayout.CENTER);

		totalPanel = new Panel();
		totalPanel.setLayout(new FlowLayout());
		totalLabel.setText("Total number of clients: ");
		total.setText("" + numClients);
		totalPanel.add(totalLabel);
		totalPanel.add(total);

		reset.addActionListener(action);
		buttons = new Panel();
		buttons.setLayout(new FlowLayout());
		buttons.add(reset);

		f.add(totalPanel, BorderLayout.NORTH);
		f.add(titleScores, BorderLayout.CENTER);
		f.add(buttons, BorderLayout.SOUTH);

		f.setVisible(true);
	}

	private ActionListener action = new ActionListener() { // change the content
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource().equals(reset)) {
				scores.setText("Player Name\t\t\t\t\t\tScore\n");
				for (int i = 0; i < clients.size(); i++) {
					clients.get(i).resetScore();
					scores.append(clients.get(i).getName() + "\t\t\t\t\t\t" + clients.get(i).getScore() + "\n");
					try {
						PrintWriter out = new PrintWriter(socs.get(i).getOutputStream(), true);
						out.println("\\TO:CLIENT reset");
						out.flush();
					} catch (IOException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}

			}

		}
	};

	public static void setFirstPlayer(int random) {
		if (numClients >= 2) {
			for (int i = 0; i < clients.size(); i++) {
				if (random == i) {
					clients.get(i).setIsFirst(true);
					// System.out.println("first player = " +
					// clients.get(i).name);

				} else {
					clients.get(i).setIsFirst(false);
				}
			}
		}
	}

	public static void main(String[] args) throws IOException {
		ServerSocket soc = new ServerSocket(10007);
		writer = new ArrayList<PrintWriter>(); // POON: change to linkedlist

		Server s = new Server();

		while (true) {
			System.out.println("Waiting for an EchoClient ...");
			// Socket con = soc.accept();
			// socs.add(con);
			socs.add(soc.accept());
			numClients++;
			total.setText("" + numClients);

			Runnable r = new DupMeRunnable(numClients, socs);
			Thread t = new Thread(r);
			execs.execute(t);
		}
	}
}

class ClientInfo {
	int score;
	String name;
	boolean isPlaying = false;
	int[] pattern;
	boolean isFirst;

	public ClientInfo(int score, String name, boolean isPlaying) {
		this.score = score;
		this.name = name;
		this.isPlaying = isPlaying;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public int getScore() {
		return score;
	}

	public void setIsPlaying(boolean isPlaying) {
		this.isPlaying = isPlaying;
	}

	public boolean getIsPlaying() {
		return isPlaying;
	}

	public void setIsFirst(boolean isFirst) {
		this.isFirst = isFirst;
	}

	public boolean getIsFirst() {
		return isFirst;
	}

	public int[] getPattern() {
		return pattern;
	}

	public void setPattern(int[] pattern) {
		this.pattern = pattern;
	}

	public void resetScore() {
		score = 0;
	}

}

class DupMeRunnable implements Runnable {
	int id;
	Socket con;
	List<Socket> socs;

	// for scoreboard
	int[] topScores = new int[10];
	String[] topNames = new String[10];

	// int turnCount = 0;

	// final int NUM_REPEAT = 5;

	public DupMeRunnable(int id, List<Socket> socs) {
		this.id = id;
		this.socs = socs;
		this.con = socs.get(id - 1);

		System.out.println("Client #" + id + " is connected.");
	}

	public String echo(String id, String msg) {
		String echoedMsg = msg;
		return id + ":" + echoedMsg;
	}

	public void printToAll(String msg) {
		/*
		 * PrintWriter outAll; for (Socket soc : socs) { try { outAll = new
		 * PrintWriter(soc.getOutputStream(), true); outAll.println(msg); }
		 * catch (IOException e) { // TODO Auto-generated catch block
		 * e.printStackTrace(); } }
		 */
		for (PrintWriter outAll : Server.writer) {
			outAll.println(msg);
			outAll.flush();
		}

	}

	public void run() {
		boolean stillOn = true;
		while (stillOn) {

			try {
				PrintWriter out = new PrintWriter(con.getOutputStream(), true);
				Server.writer.add(out);
				BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));

				String inputLine;
				while ((inputLine = in.readLine()) != null) {

					System.out.println("Server received ||" + inputLine);
					if (inputLine.contains("\\already")) {
						inputLine.replace(inputLine, "");
					} else if (inputLine.contains("\\TO:CLIENT reset")) {
						Server.scores.setText("Player Name\t\t\t\t\t\tScore\n");
						for (int i = 0; i < Server.clients.size(); i++) {
							Server.clients.get(i).resetScore();
							Server.scores.append(Server.clients.get(i).getName() + "\t\t\t\t\t\t"
									+ Server.clients.get(i).getScore() + "\n");
						}
					} else if (inputLine.contains("\\TO:CLIENT") || inputLine.contains("\\TO:BATTLE")) {
						out.println(inputLine);
						out.flush();
					} else {
						if (inputLine.contains("\\close")) {
							out.println("\\closeThe server said \"Bye bye!\"");
							out.flush();
							stillOn = false;
							break;
						} else if (inputLine.equals("\\clientid")) {
							out.println("\\clientid" + id);
							out.flush();
						} else if (inputLine.contains("battle-pause")) {
							System.out.println("pause");
							out.flush();
						} else if (inputLine.contains("\\clientName")) {
							String name = inputLine.replace("\\clientName: ", "");
							inputLine.replace(inputLine, "");
							ClientInfo c = new ClientInfo(0, name, false);
							Server.clients.add(c);

							int random = (int) (Math.random() * (Server.clients.size()));
							Server.setFirstPlayer(random);
							System.out.println("" + random);
							printToAll("\\battle first player: " + Server.clients.get(random).getName());

							Server.scores.append(name + "\t\t\t\t\t\t");
							Server.scores.append("0\n");

							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}
						} else if (inputLine.contains("\\battle-turnCount |")) {

							int turnCount = Integer.parseInt(inputLine.replace("\\battle-turnCount |", ""));

							System.out.println("turn count = " + turnCount);

							printToAll("\\TO:BATTLE battle-turnCount |" + turnCount);

							if (turnCount == 2)
								printToAll("\\TO:CLIENT battle-turnCount |" + turnCount);

							String results = "";
							if (turnCount == 4) {
								for (int i = 0; i < Server.clients.size(); i++) {
									if (i == Server.clients.size() - 1) {
										results += Server.clients.get(i).getName() + ","
												+ Server.clients.get(i).getScore();
									} else {
										results += Server.clients.get(i).getName() + ","
												+ Server.clients.get(i).getScore() + "|";
									}
								}
								// printToAll("\\TO:BATTLE stop game");
								printToAll("\\TO:CLIENT goToResults " + results);
							}

							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}

						} else if (inputLine.contains("resultsDone")) {
							// printToAll("\\already done mannnn");

							String name1 = Server.clients.get(0).getName();
							String name2 = Server.clients.get(1).getName();
							int score1 = Server.clients.get(0).getScore();
							int score2 = Server.clients.get(1).getScore();
							boolean added1 = false;
							boolean added2 = false;

							readFile("media/texts/highscore.txt");
							for (int i = 0; i < topScores.length; i++) {
								if (score1 == score2) {
									if (score1 >= topScores[i]) {
										topScores[topScores.length - 1] = score1;
										topNames[topScores.length - 1] = name1;
										topScores[topScores.length - 2] = score2;
										topNames[topScores.length - 2] = name2;
										sortList();
										break;
									}
								} else {
									if (score1 >= topScores[i] && !added1) {
										topScores[topScores.length - 1] = score1;
										topNames[topScores.length - 1] = name1;
										sortList();
										added1 = true;
									}
									if (score2 >= topScores[i] && !added2) {
										topScores[topScores.length - 1] = score2;
										topNames[topScores.length - 1] = name2;
										sortList();
										added2 = true;
									}
								}

							}
							writeFile("media/texts/newhighscore.txt");

							printToAll("\\TO:CLIENT reset");
							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}
							// printToAll("\\TO:CLIENT goToScoreboard");
							//
							// for (PrintWriter outAll : Server.writer) {
							// outAll.flush();
							// }
							// out.flush();

						} else if (inputLine.contains("scoreboard-to-mainmenu")) {
							Server.scores.setText("Player Name\t\t\t\t\t\tScore\n");
							for (int i = 0; i < Server.clients.size(); i++) {

								Server.scores.append(Server.clients.get(i).getName() + "\t\t\t\t\t\t"
										+ Server.clients.get(i).getScore() + "\n");
								printToAll("\\TO:CLIENT reset");
								for (PrintWriter outAll : Server.writer) {
									outAll.flush();
								}
							}
						} else if (inputLine.contains("tutorial-to-mainmenu")) {

							printToAll("\\TO:CLIENT reset");
							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}

						} else if (inputLine.contains("\\battle-done: ")) {

							String name = inputLine.replace("\\battle-done: ", "");
							System.out.println(name + " finished playing");
							for (int i = 0; i < Server.clients.size(); i++) {
								if (!Server.clients.get(i).getName().equals(name)) {
									printToAll(
											"\\TO:BATTLE battle " + Server.clients.get(i).getName() + " start playing");
								}
							}

							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}

						} else if (inputLine.contains("\\battle-pattern |")) {
							String s = inputLine.replace("\\battle-pattern |", "");
							int[] pattern = new int[5];
							for (int i = 0; i < pattern.length; i++) {
								if (i == pattern.length - 1)
									pattern[i] = Integer.parseInt(s.substring(i));
								else
									pattern[i] = Integer.parseInt(s.substring(i, i + 1));

								System.out.println(pattern[i]);

							}
							// Server.clients.get(id).setPattern(pattern);
							printToAll("\\TO:BATTLE battle " + Server.clients.get(id - 1).getName() + " gave pattern |"
									+ s);
							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}

						} else if (inputLine.contains("\\battle-") && inputLine.contains("-score |")) {

							int index = inputLine.indexOf("-score |");
							String s1 = inputLine.substring(0, index);
							String s2 = inputLine.substring(index);
							String playername = s1.replace("\\battle-", "");
							int score = Integer.parseInt(s2.replace("-score |", ""));
							int prevscore = 0;
							for (int i = 0; i < Server.clients.size(); i++) {
								if (Server.clients.get(i).getName().equals(playername)) {
									prevscore = Server.clients.get(i).getScore();
									Server.clients.get(i).setScore(score);
								}
							}
							String scoreboard = Server.scores.getText();
							int index1 = scoreboard.indexOf(playername + "\t\t\t\t\t\t" + prevscore);
							String namescore = playername + "\t\t\t\t\t\t" + prevscore + "\n";
							int index2 = index1 + namescore.length();
							Server.scores.replaceRange(playername + "\t\t\t\t\t\t" + score + "\n", index1, index2);

							printToAll("\\TO:BATTLE battle-" + playername + "-score |" + score);

							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}

						} else if (inputLine.contains("\\battle-new-round")) {
							int maxscore = 0;
							String firstplayer = "";
							for (int i = 0; i < Server.clients.size(); i++) {
								int currentscore = Server.clients.get(i).getScore();
								if (maxscore < currentscore) {
									maxscore = currentscore;
									firstplayer = Server.clients.get(i).getName();
								}
							}
							System.out.println("" + maxscore);
							printToAll("\\TO:BATTLE new round | battle first player: " + firstplayer);

							for (PrintWriter outAll : Server.writer) {
								outAll.flush();
							}

						} else {
							System.out.println(inputLine);
							for (PrintWriter outAll : Server.writer) {
								outAll.println(echo("Client #" + id, inputLine));
								outAll.flush();
							}
						}
						out.flush();
						in = new BufferedReader(new InputStreamReader(con.getInputStream()));
					}
				}
				Server.writer.get(id - 1).close();
				Server.writer.remove(Server.writer.get(id - 1));
				System.out.println("Client#" + id + " has left.");
				Server.numClients -= 1;
				Server.total.setText("" + Server.numClients);

				String name = Server.clients.get(id - 1).getName();
				int score = Server.clients.get(id - 1).getScore();

				String scoreboard = Server.scores.getText();

				int index1 = scoreboard.indexOf(name + "\t\t\t\t\t\t" + score);
				String namescore = name + "\t\t\t\t\t\t" + score + "\n";
				int index2 = index1 + namescore.length();
				Server.scores.replaceRange("", index1, index2);

				Server.clients.remove(id - 1);

				in.close();
				con.close();
			} catch (IOException e) {
				System.err.println("Problem with Communication Server");
				stillOn = false;
			}
		}
		Server.numClients -= 1;
		Server.total.setText("" + Server.numClients);
		System.out.println("Client #" + id + " has lost its connection.");

		String name = Server.clients.get(id - 1).getName();
		int score = Server.clients.get(id - 1).getScore();

		String scoreboard = Server.scores.getText();

		int index1 = scoreboard.indexOf(name + "\t\t\t\t\t\t" + score);
		String namescore = name + "\t\t\t\t\t\t" + score + "\n";
		int index2 = index1 + namescore.length();
		Server.scores.replaceRange("", index1, index2);

	}

	public void writeFile(String fileName) throws IOException {
		PrintWriter writer = new PrintWriter(fileName, "UTF-8");
		for (int i = 0; i < topScores.length; i++) {
			writer.println(topNames[i] + "," + topScores[i]);
		}
		writer.close();
		System.out.println("write file sed la");
	}

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
		System.out.println("read file sed la");
	}

	public void sortList() {
		String tempStr;
		int tempInt;
		for (int j = topScores.length - 1; j >= 0; j--) {
			for (int i = 0; i < j; i++) {
				if (topScores[i] < topScores[i + 1]) {
					tempInt = topScores[i + 1];
					topScores[i + 1] = topScores[i];
					topScores[i] = tempInt;
					tempStr = topNames[i + 1];
					topNames[i + 1] = topNames[i];
					topNames[i] = tempStr;
				}
			}
		}
	}
}
