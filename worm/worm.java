import java.awt.*;
import javax.swing.*;
public class worm extends JPanel{

	static int windowX = 1000;
	static int windowY = 1000;

	public static void main(String[] args){
		JFrame frame = new JFrame("test");
		frame.setSize(1000, 1000);
		frame.add(new worm());
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
	public void paint(Graphics g){
		int count, number, count2;
		int[] y={100,100,100,100,100,100};
		int[] x={10,20,30,40,50,60};
		Color[] colors={Color.red, Color.red, Color.orange, Color.yellow, Color.green, Color.blue};
		int xChange, yChange=0;
		long round;
		xChange=10;
		yChange=0;
			
			x[0]+=xChange;
			y[0]+=yChange;
			for(count=5;count>0;count--){
				x[count]=x[count-1];
				y[count]=y[count-1];
			}
			for(count=0;count<6;count++){
				g.setColor(colors[count]);
				g.fillOval(Math.abs(x[count]),Math.abs(y[count]),10,10);
				System.out.println(x[count]+" "+y[count]);
			}
			try {
    			Thread.sleep(100);
			}
			catch(InterruptedException ex) {
   				Thread.currentThread().interrupt();
			}
			clear(g);
			repaint();
	}
	void clear(Graphics g){
		g.setColor(Color.white);
		g.fillRect(0,0,10000,10000);
		g.setColor(Color.black);
	}
}