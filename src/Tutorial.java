import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Enumeration;
 
import javax.swing.*;
import javax.swing.plaf.FontUIResource;

public class Tutorial extends JPanel{
	
	JPanel topPanel, bottomPanel, middlePanel;
	JButton menu, next, back;
	JLabel title, image;
	Color myColor = new Color(153, 0, 76);
	int count = 1;
	Socket con;
	
	public Tutorial(Socket con){
		super();
		this.con=con;
		setSize(1100, 400);
		setLayout(new BorderLayout());
		init();
		setGUI();
	}
	
	public void init(){
		topPanel = new JPanel(new BorderLayout());
		bottomPanel = new JPanel(new GridLayout(1,0));
		//middlePanel = new JPanel(new BorderLayout());
		image = new JLabel();
		menu = new JButton("Back to Main Menu");
		next = new JButton("Next");
		back = new JButton("Back");
	}
	
	public void setGUI(){
		//top panel
		add(topPanel, BorderLayout.NORTH);
		setPic();
        topPanel.add(image);
        image.setHorizontalAlignment(JLabel.CENTER);
        topPanel.setBackground(myColor);
		
		//add(middlePanel);
		//middlePanel.add(new JLabel("test"));
        
        //bottom panel
		add(bottomPanel, BorderLayout.SOUTH);
		
		menu.setFont(new Font("Courier", Font.PLAIN, 16));
		next.setFont(new Font("Courier", Font.PLAIN, 16));
		back.setFont(new Font("Courier", Font.PLAIN, 16));
		
		menu.addActionListener(action);
		next.addActionListener(action);
		back.addActionListener(action);
		
		bottomPanel.setBackground(myColor);
		bottomPanel.add(menu);
		bottomPanel.add(back);
		bottomPanel.add(next);
		
	}
	
	public void setPic(){
		if(count==1){
			ImageIcon ii = new ImageIcon("media/images/t1.jpg"); //add winner gif later
	        image.setIcon(ii);
	        back.setEnabled(false);
		}else if(count==2){
			ImageIcon ii = new ImageIcon("media/images/t2.jpg"); //add winner gif later
	        image.setIcon(ii);
	        back.setEnabled(true);
		}else if (count==3){
			ImageIcon ii = new ImageIcon("media/images/t3.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==4){
			ImageIcon ii = new ImageIcon("media/images/t4.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==5){
			ImageIcon ii = new ImageIcon("media/images/t5.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==6){
			ImageIcon ii = new ImageIcon("media/images/t6.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==7){
			ImageIcon ii = new ImageIcon("media/images/t7.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==8){
			ImageIcon ii = new ImageIcon("media/images/t8.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==9){
			ImageIcon ii = new ImageIcon("media/images/t9.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==10){
			ImageIcon ii = new ImageIcon("media/images/t10.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==11){
			ImageIcon ii = new ImageIcon("media/images/t11.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==12){
			ImageIcon ii = new ImageIcon("media/images/t12.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==13){
			ImageIcon ii = new ImageIcon("media/images/t13.jpg"); //add winner gif later
	        image.setIcon(ii);
		}else if (count==14){
			ImageIcon ii = new ImageIcon("media/images/t14.jpg"); //add winner gif later
	        image.setIcon(ii);
	        next.setEnabled(false);
		}
		
	}
	
	private ActionListener action = new ActionListener() { // change the content
		public void actionPerformed(ActionEvent e) {
			// TODO Auto-generated method stub
			if (e.getSource().equals(menu)) {
				
				PrintWriter out = null;
				try {
					out = new PrintWriter(con.getOutputStream(), true);
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				revalidate();
				repaint();
				out.println("tutorial-to-mainmenu");
				out.flush();

			}
			if (e.getSource().equals(next)) {
				
				count++;
				System.out.println("Go to next image");
				System.out.println("count: "+count);
				//ImageIcon ii = new ImageIcon("media/images/t2.jpg"); //add winner gif later
		        //image.setIcon(ii);
				setPic();
				topPanel.add(image);
				
			}
			if (e.getSource().equals(back)) {
				
				count--;
				System.out.println("Go to previous image");
				System.out.println("count: "+count);
				setPic();
				topPanel.add(image);
				
			}

		}
	};
	
	public static void main(String[] args){
		
	}
	

}
