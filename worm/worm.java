import java.awt.*;
import javax.swing.*;
public class worm extends JPanel{

	public static void main(String[] args){
		JFrame frame = new JFrame("test");
		frame.setSize(1000, 1000);
		frame.add(new worm());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
		JFrame menu = new JFrame("settings");
		menu.setSize(1000,100);
		menu.add(new menuObj());
		menu.setVisible(true);
		menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
	public void paint(Graphics g){
		try {
    			Thread.sleep(10);
			}
		catch(Exception ex) {
				System.out.println("Error!");
			}
			g.setColor(Color.black);
			g.fillRect(0,0,10000,10000);
			g.setColor(Color.red);
		
			repaint();
	}
}

class menuObj extends JPanel{
	
}

class drawer{
	int windowX = 1000;
	int windowY = 1000;
	int[] y={100,100,100,100,100,100};
	int[] x={10,20,30,40,50,60};
	public int length = 30;
	public Color[] colors;
	public boolean rainbowMode;
	public boolean isMirror;
	drawer drawerToMirror;
	int count, number, count2;
	long round;
	private float slopeX, slopeY;
	private boolean leftOrRight;
	private float direction;

	int xChange=(int)Math.round((Math.random()*20)-10);
	int yChange=(int)Math.round((Math.random()*20)-10);
	Graphics g;

	public drawer(Graphics g){
		this.g = g;
	}

	public drawer(Graphics g, boolean isMirror, drawer drawerToMirror){
		this.g = g;
		this.drawerToMirror = drawerToMirror;
		this.isMirror = isMirror;
	}

	public void render(){
		x[0]+=xChange;
		y[0]+=yChange;
		for(count=length-1;count>0;count--){
			x[count]=x[count-1];
			y[count]=y[count-1];
		}
		for(count=0;count<length;count++){
			g.setColor(colors[count]);
			g.fillOval(Math.abs(x[count]),Math.abs(y[count]),10,10);
		}
	}

	public void generateDirection(){
		direction = (direction + (Math.random()*19)-20)%360;

	}
}
