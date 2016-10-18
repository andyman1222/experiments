//******************************************************************************
// Scribble.java:	Applet
//
//******************************************************************************
import java.applet.*;
import java.awt.*;
import java.awt.image.*;
import java.util.*;
import java.net.*;

//==============================================================================
// Main Class for applet Scribble
//
//==============================================================================

class CONST {
	static final int MAX_OBJS= 50;
	static final int INIT_HEIGHT_SUB= 100;
	static final int TRAIN_LEN= 50;
	static final int OBJ_RADIUS= 5;
	static final int MIN_CACC= 1;
	static final int MAX_CACC= 8;
	static final int MIN_TURN_CACC= 10;
	static final int MAX_TURN_CACC= 11;
	static final int MIN_SMALL_TURN_CACC= 5;
	static final int MAX_SMALL_TURN_CACC= 6;
	static final double ISVAL= 3.0;
	static final int MAX_COLORS= 30;
	static final double ACC_DIVISOR= 10.0;
	static final double MINDIS_NUM= 20.0;
	static final int MAX_ITER= 15;
	static final int MIN_ITER= 5;
}

public class Scribble extends Applet implements Runnable
{	
	Field field= new Field();
	myObjSet oset;
	
	Canvas playarea= new Canvas();
	Panel ctl_panel_main = new Panel();
	Panel ctl_panel1= new Panel();
	Panel ctl_panel2= new Panel();
	Panel ctl_panel3 = new Panel();
	int sleep_num = 0;
	urlText tf;
	Label label, label2;
	String color_str[]= {"Blue", "Red", "Green", "Cool Colors-1",
			"Cool Colors-2", "Primary Colors", "Two Colors", "Random" };
	String number_str[] = { "1", "2", "3", "4", "5", "10", "20", "30", "40", "50" };
	MenuItem color_items[]= new MenuItem[color_str.length];
	String checkbox_str[] = {"Reflect", "Erase", "Pause"};
	String button_str[]= {"Clear", "Reset"};
	Button buttons[]= new Button[button_str.length];
	Scrollbar scrollbar = new Scrollbar(Scrollbar.HORIZONTAL, 0, 5, 0, 50);
	Label scroll_label = new Label("Speed: " + sleep_num);
	Choice choose_color = new Choice();
	Label color_label = new Label("Color: ");
	Choice choose_number = new Choice();
	Label number_label = new Label("Number: ");

//	Checkbox cbox = new Checkbox[color_str.length];
	
//	CheckboxGroup cbg;


	int picPixels[];

	int width;
	int height;
	
/*	public boolean keyDown(Event evt, int key)
	  {
		  boolean go=true;
		  int q=0,test;
		  do {
			  switch (key) {
			  case '1': oset.make_primary(0, 0, 1); break;
			  case '2': oset.make_random(30); break;
			  case '3': oset.make_random(2); break;
			  case 'z': case 'Z': oset.toggle_radcolor(); break;
			  case 'e': case 'E': oset.toggle_erase(); break;
			  case 'r': case 'R': oset.toggle_reflect(); break;
			  case 'c': case 'C': oset.field.clear(oset.g); break;
			  }
		  } while (!go);
		  return (true);
	  }
*/	
	public boolean handleEvent(Event e) {
		if (e.target == scrollbar) {
			sleep_num = scrollbar.getValue();
			scroll_label.setText("Speed: " + sleep_num);
			return true;
		} else {
			return super.handleEvent(e);
		}
	}
	  public boolean action(Event e, Object oo) {
		if (e.target == choose_color) {
			if ("Blue".equals( (String) oo) ) {
				oset.make_primary(0, 0, 1);
			} else if ("Red".equals( (String) oo) ) {
				oset.make_primary(1, 0, 0);
			} else if ("Green".equals( (String) oo) ) {
				oset.make_primary(0, 1, 0);
			} else if ("Primary Colors".equals( (String) oo) ) {
				oset.make_random_primarys();
			} else if ("Two Colors".equals( (String) oo) ) {
				oset.make_random(2);
			} else if ("Random".equals( (String) oo) ) {
				oset.make_random(30);
			} else if ("Cool Colors-1".equals( (String) oo) ) {
				oset.set_radcolor(true);
			} else if ("Cool Colors-2".equals( (String) oo) ) {
				oset.set_radcolor(false);
			}
		} else if (e.target == choose_number) {
			int num;
			num = Integer.parseInt((String) oo);
			oset.set_run_objs(num);
		} else if (e.target instanceof Checkbox) {
			Checkbox cbx = (Checkbox) e.target;
			if (cbx.getLabel().equals("Pause")) {
				oset.toggle_pause(((Boolean) oo).booleanValue());
			} else if (cbx.getLabel().equals("Reflect")) {
				oset.toggle_reflect(((Boolean) oo).booleanValue());
			} else if (cbx.getLabel().equals("Erase")) {
				oset.toggle_erase(((Boolean) oo).booleanValue());
			}
		} else if (e.target instanceof Button)	{
			if ("Clear".equals( (String) oo) ) {
				oset.field.clear(oset.g);
			}
			if ("Reset".equals( (String) oo) ) {
				oset.reset();
			}
		}
		return (true);
	  }

	Thread m_Scribble= null;

	private Image image;
	private MediaTracker tracker;

	public void start() {
		tracker= new MediaTracker(this);
	//	load_image("http://caretaker32.jpg");
		
		initialize();
		if (m_Scribble == null) {
			m_Scribble = new Thread(this);
			m_Scribble.start();
		}
	}

	public void load_image(String urlstr)
	{
		label2.setText("Loading");
		return;
/*		tracker= new MediaTracker(this);
		try {
			image= getImage(new URL(getCodeBase(), urlstr));
		} catch (MalformedURLException e) {
			label2.setText("URL error: " + e.getMessage());
			return;
		}
		
		label2.setText("Loaded " + urlstr);
		
		tracker.addImage(image, 0);
		try {
			tracker.waitForID(0);
		} catch (InterruptedException e) {
			label2.setText("URL error2: " + e.getMessage());
			return;
		}
		label2.setText("Loaded " + urlstr);

		int imgWidth= image.getWidth(this);
		int imgHeight= image.getHeight(this);
		int nPixels[]= new int[imgWidth * imgHeight];

		PixelGrabber pg= new PixelGrabber(image, 0, 0, imgWidth, imgHeight, nPixels, 0,
			imgWidth);
		try {
		   pg.grabPixels();
	   } catch (InterruptedException e) {
	   }
	   int row, col;
	   for (int i= 0; i < height; i++) {
		   for (int j= 0; j < width; j++) {
			   if (i < imgHeight && j < imgWidth) {
				   picPixels[i * width + j]= nPixels[i * imgWidth + j];
			   }
			   *
			   
			   row= (i * imgHeight) / height;
			   col= (j * imgWidth) / width;
			   picPixels[i * width + j]= nPixels[row * imgWidth + col];

		   }
	   }
//	   oset.init(field, playarea.getGraphics(), playarea.getBackground(), image, playarea,
//			picPixels);
*/
	}


	public void initField() {
//		if (image!= null) {
//			if (width > image.getWidth(this)) {
//				width= image.getWidth(this);
//			}
//			if (height > image.getHeight(this)) {
//				height= image.getHeight(this);
//			}
//			playarea.resize(width, height);
//			ctl_panel.resize(width, 100);
//			ctl_panel2.resize(width, 100);
//		}

		field.init((double) width / 2, (double) height / 2, - (double) width / 2,
			- (double) height / 2, width / 2, height / 2);
					
		oset= new myObjSet(CONST.MAX_OBJS, this);
		oset.init(field, playarea.getGraphics(), playarea.getBackground(), image, playarea,
			picPixels);
		
		oset.random_init();
		oset.initObjs();
		oset.set_run_objs(1);
	}


	public void initialize() {
			Dimension d= size();
			GridBagConstraints b= new GridBagConstraints();
			GridBagLayout gridbagb= new GridBagLayout();
			setLayout(gridbagb);
			width= d.width;
			height= d.height - CONST.INIT_HEIGHT_SUB;

	//		frame= new Frame();
   
			if (image!= null) {
			   if (width > image.getWidth(this)) {
				width= image.getWidth(this);
			   }
			   if (height > image.getHeight(this)) {
				height= image.getHeight(this);
			   }
			}
			
			picPixels= new int[width * height];
			b.gridwidth= GridBagConstraints.REMAINDER;
			b.fill= GridBagConstraints.BOTH;
			b.anchor= GridBagConstraints.SOUTHEAST;
			b.weightx= 1.0;

			add(playarea);
			add(ctl_panel_main);

			ctl_panel_main.setLayout(new GridLayout(1, 3));
			ctl_panel_main.add(ctl_panel1);
			ctl_panel_main.add(ctl_panel2);
			ctl_panel_main.add(ctl_panel3);

			playarea.setBackground(new Color(0, 0 ,0));
			gridbagb.setConstraints(playarea, b);
			playarea.resize(width, height);

	//		frame.add(ctl_panel);
			ctl_panel1.setLayout(new GridLayout(3, 2));
			ctl_panel2.setLayout(new GridLayout(3, 2));
			ctl_panel3.setLayout(new GridLayout(3, 2));

//			add(ctl_panel1);
//			add(ctl_panel2);
//			ctl_panel1.setBackground(new Color(50, 50 ,0));
			gridbagb.setConstraints(ctl_panel1, b);

			for (int i= 1; i <= button_str.length; i++) {
//				if (i % 4 == 0 || i == button_str.length) {
//					b.gridwidth= GridBagConstraints.REMAINDER;
//				} else {
//					b.gridwidth= 1;
//				}
				buttons[i - 1]= new Button(button_str[i - 1]);
//				gridbagb.setConstraints(buttons[i - 1], b);
				ctl_panel3.add(new Label("  "));
				ctl_panel3.add(buttons[i - 1]);
			}
//			label2 = new Label("URL");
//			ctl_panel3.add(label2);
			b.gridwidth = 1;
			gridbagb.setConstraints(scrollbar, b);

			ctl_panel1.add(scroll_label);
			ctl_panel1.add(scrollbar);
	//		gridbagb.setConstraints(menuBar, b);
	//		ctl_panel.add(menuBar);
			for (int i = 1; i <= color_str.length; i++) {
					choose_color.addItem(color_str[i - 1]);
			}
			
			b.gridwidth = 1;
			gridbagb.setConstraints(choose_color, b);
			ctl_panel1.add(color_label);
			ctl_panel1.add(choose_color);
			
			for (int i = 1; i <= number_str.length; i++) {
					choose_number.addItem(number_str[i - 1]);
			}

			b.gridwidth = 1;
			gridbagb.setConstraints(choose_number, b);
			ctl_panel1.add(number_label);
			ctl_panel1.add(choose_number);

			b.gridwidth= 1;

			for (int i = 1; i <= checkbox_str.length; i++) {
				b.gridwidth = 1;
				ctl_panel2.add(new Label(" "));
				ctl_panel2.add(new Checkbox(checkbox_str[i - 1]));
			}

			
			initField();
	/*		setToImage();
		//	sesize(width, height + 200);
			
			field.init((double) width / 2, (double) height / 2, - (double) width / 2,
				- (double) height / 2, width / 2, height / 2);
					
			oset= new myObjSet(CONST.MAX_OBJS, this);
		    oset.init(field, playarea.getGraphics(), playarea.getBackground(), image, playarea);
		
		    oset.random_init();
			oset.initObjs();
			oset.set_run_objs(1);
			*/
			
	}


	public void destroy()
	{
		// TODO: Place applet cleanup code here
	}

	// Scribble Paint Handler
	//--------------------------------------------------------------------------

	//		The start() method is called when the page containing the applet
	// first appears on the screen. The AppletWizard's initial implementation
	// of this method starts execution of the applet's thread.
	//--------------------------------------------------------------------------
	
	
	//		The stop() method is called when the page containing the applet is
	// no longer on the screen. The AppletWizard's initial implementation of
	// this method stops execution of the applet's thread.
	//--------------------------------------------------------------------------
	
	// THREAD SUPPORT
	//		The run() method is called when the applet's thread is started. If
	// your applet performs any ongoing activities without waiting for user
	// input, the code for implementing that behavior typically goes here. For
	// example, for an applet that performs animation, the run() method controls
	// the display of images.
	//--------------------------------------------------------------------------

	public void paint(Graphics g) {
//		playarea.getGraphics().drawImage(image, 0, 0, this);
	}

	public void run()
	{  
		int sleep_do;
//		playarea.getGraphics().drawImage(image, 0, 0, this);

		while (true)
		{	
			try
			{
				oset.moveOne();
				if (sleep_num != 0) {
					Thread.sleep(sleep_num);
				}
			}
			catch (InterruptedException e)
			{
				stop();
			}
		}
	}
}



class Pair
{
	double x;
	double y;
};

class Ipair
{
	int x;
	int y;
};

class Emath {
 	public double rad(double t)
	{
		return((t/180.0)*Math.PI);
	}

	public double deg(double t)
	{
		return((180.0*t)/Math.PI);
	}

	public double com_angle(double x,double y)
	{
		double ang;
		if (x==0.0) 
			if (y>=0) ang=90.0;
			else ang= 270.0;
		else
		{
			ang= deg(Math.atan(y/x));
			if (x<0) ang+= 180;
		}
		return ang;
	}

	public void convert_to_xy(double r,double theta,double xref, double yref, Pair coor)
	{
		coor.x= Math.cos(rad(theta))+xref;
		coor.y= r*Math.sin(rad(theta))+yref;
	}
}

class Field {
	double xmax;
	double ymax;
	double xmin;
	double ymin;
	int cx;
	int cy;
	Graphics g;

	public void init(double _xmax, double _ymax, double _xmin, double _ymin, int _cx, int _cy) {
		xmax= _xmax; ymax= _ymax; xmin= _xmin; ymin= _ymin;
		cx= _cx; cy= _cy;
	}
	void clear(Graphics g) {
		g.clearRect(0, 0, 2 * cx, 2 *cy);
	}
};

class myObj {
   Emath em= new Emath();
   Field field;
   Graphics g;
   myObjSet oset;
   
   private boolean randome= false;
   private boolean record= false;
   private boolean rotate= true;
   private boolean rad_color= false;

   private Color colors[]= new Color[CONST.MAX_COLORS];
   private int max_colors= 0;
   private int col_index;
   private int col_dir= 0;
  
   private double x= 0;
   private double y= 0;
   private double xv= CONST.ISVAL;
   private double yv= 0;
   private int ix= 0;
   private int iy= 0;
   
   private boolean start= false;
   private boolean stop= false;
   private boolean erase= false;
   private boolean reflect= false;
   private int mindis;
   private int max_radius;
   private int color;

   private boolean locked= false;
   private int iter= 0, adir= 0;
   private double acc, accacc= 0;
   private int radi;
   private Random rand;
   private int ecounter= 0;
   private int train_len;
   private boolean erasing= false;
   private Ipair train[];
   private Color background_color;
   private int cos_val[];
   private int obj_radius;
   private Image image;
   private Canvas canvas;
   private boolean picturing= false;
   private int picPixels[];
   private int width, height;

   public void init(myObjSet _oset, Graphics _g, Field _f, Random _r, Color _b, Image _i,
	   Canvas _c, int _picPixels[], int _w, int _h) {
	   oset= _oset;
	   g= _g;
	   field= _f;
	   rand= _r;
	   background_color= _b;
	   image= _i;
	   canvas= _c;
	   picPixels= _picPixels;
	   width= _w;
	   height= _h;
   }

   private void pre_calc(int inradi) {
	   int i;
	   radi= inradi;

	   cos_val= new int[radi + 1];
	   for (i=0; i<= radi; i++) {
		   cos_val[i]= (int) Math.round(radi*Math.cos(Math.asin((double)i/radi)));
	   }
   }

   private double calc_acc(int min,int max)
   {
	   int a;
	   a= rand.nextInt() % (max-min);
	   return((double) (a+min)/CONST.ACC_DIVISOR);
   }
   
   Image GetPixels(Image img, int ix, int iy, int awidth, int aheight) {
	   Image rimg= canvas.createImage(new MemoryImageSource(awidth, aheight, picPixels,
		   iy * width + ix, width));
	   return (rimg);
   }
	
   
   private int run_count= 0;
   private void ball(int ix,int iy, Color color)
   {
	   int	i;
	   Image rimg;

	   if (picturing) {
//		int sx= (ix - radi) % (image.getWidth(canvas) - 2 * radi);
//		int sy= (iy - radi) % (image.getHeight(canvas) - 2 * radi);
		   
		rimg= GetPixels(image, ix - radi, iy - radi, 2 * radi, 2 * radi);
		g.drawImage(rimg, ix- radi, iy- radi, null);
		if (++run_count % 5 == 0) {
					Runtime.getRuntime().gc();
		}

	   } else {
	      for (i=0; i<= radi; i++)
	      {
		   g.setColor(color);
		   g.drawLine(ix-cos_val[i],iy+i,ix+cos_val[i]+1,iy+i);
		   g.drawLine(ix-cos_val[i],iy-i,ix+cos_val[i]+1,iy-i);
	      }
	   }
   }  
  
   public void make_primary(int r, int g, int b) {
	   int i;
	   int j;
										
	   for (j= 0, i=50; i <= 255 && j < CONST.MAX_COLORS; i+=10, j++) {
			colors[j]= new Color(r * i, g * i, b* i);
		}
		max_colors= j;
		rad_color= false;
   }

   public void make_random(int n) {
		int i;
	
		for (i= 0; i < n; i++) {
			colors[i]= new Color(rand.nextInt() % 256, rand.nextInt() %256,
				rand.nextInt() %256);
		}
		max_colors= n;
		rad_color= false;
   }

   public void set_picturing() {
	   if (picturing) {
		   picturing= false;
	   } else {
		   picturing= true;
	   }
   }

   public void set_erase(int _train_len) {
	   if (_train_len == 0) {
		   erase= false;
	   } else {
		   erase= true;
		   train_len= _train_len;
		   erasing= false;
		   ecounter= 0;
		   train= new Ipair[train_len];
		   for (int i= 0; i < train_len; i++) {
			   train[i]= new Ipair();
		   }
	   }
   }

   public void toggle_reflect(boolean val) {
	   reflect= val;
   }

   public void set_radcolor(boolean reset) {
	   rad_color= true;
	   if (reset) {
		   step = 0;
		   cgamma = 0;
		   ctheta = 0;
	   } else {
		   step = rand.nextInt() % 720;
		   cgamma = rand.nextInt() % 360;
		   ctheta = rand.nextInt() % 360;
	   }
   }

   public void reset() {
	   x= 0;
	   y= 0;
   }
   int ctheta = 0, cgamma = 0;

   public void oinit(int radius)
   {
	  	   make_primary(0, 0, 1);
		   pre_calc(radius);
		   acc= calc_acc(CONST.MIN_TURN_CACC - 1, CONST.MAX_TURN_CACC);
		   mindis= (int) Math.round(CONST.MINDIS_NUM / acc) + radius;
		   max_radius= (int) Math.sqrt(field.xmax * field.xmax + field.ymax * field.ymax);
		   obj_radius= radius;
   }

   int step= 0;
   
   private int norm_color(double mult, double divis)
   {
	   return (int) (Math.round((mult * 256) / max_radius) % 256);
   }

   public void scribblemove()
   {
	   double theta,speedf, cosv, sinv, bx;
	   int	c,i,j,s, fdir;
	   Color color;
	   
	   if (iter-- == 0) {	
		   iter= (rand.nextInt() & 0xffff) %
			   (CONST.MAX_ITER - CONST.MIN_ITER - 1) + CONST.MIN_ITER;
		   if (locked) {
			   accacc= 0.0;
		   } else {
			   double acc2;
			   acc= calc_acc(CONST.MIN_CACC, CONST.MAX_CACC);
			   acc2= calc_acc(CONST.MIN_CACC, CONST.MAX_CACC);
			   if (iter != 0) {
				   accacc= (acc2 - acc) / ((double) iter);
			   }			 
			   adir= 2*(rand.nextInt() & 0x1)-1;
		   }
	   }
		
	   if (randome) {
		   s= rand.nextInt() % (int) Math.round(field.xmax-field.xmin);
		   ix= (int) Math.round(field.xmin)+s;
		   s= rand.nextInt() % (int) Math.round(field.ymax-field.ymin);
		   iy= (int) Math.round(field.ymin)+s;
		   return;
	   } else {
			   if (Math.round(xv) == 0) {
				   if (yv>0) {
					   theta= em.rad(90.0);
				   } else {
					   theta= em.rad(270.0);
				   }
			   } else {
				   theta= xv>0 ? Math.atan(yv/xv) : em.rad(180.0)+Math.atan(yv/xv);
			   }
			   
			   if (!locked) {
				   acc= acc + accacc;
			   }

			   xv+= acc* Math.cos(theta+em.rad((double)90*adir));
			   yv+= acc* Math.sin(theta+em.rad((double)90*adir));
			   speedf= CONST.ISVAL/Math.sqrt(xv*xv+yv*yv);
			   xv*= speedf;
			   yv*= speedf;
			   x+= xv;
			   y+= yv;
			   cosv= Math.cos(theta);
			   sinv= Math.sin(theta);
			   if (locked) {
				   if (x >= field.xmin + mindis &&
						   x <= field.xmax - mindis &&
						   y >= field.ymin + mindis &&
						   y <= field.ymax - mindis) {
						   acc= calc_acc(CONST.MIN_SMALL_TURN_CACC, CONST.MAX_SMALL_TURN_CACC);
						   adir= -adir;
						   locked= false;
				   }
			   } else if (x < field.xmin + mindis || x > field.xmax - mindis) {
						   acc= calc_acc(CONST.MIN_TURN_CACC, CONST.MAX_TURN_CACC);
						   adir= yv < 0 ? -1 : 1;
						   locked= true;
			   } else if (y < field.ymin + mindis || y > field.ymax - mindis) {
							acc= calc_acc(CONST.MIN_TURN_CACC, CONST.MAX_TURN_CACC);
						    adir= xv < 0 ? -1 : 1;
						    locked= true;
			   } 
				   
			   ix= (int) Math.round(x);
			   iy= (int) Math.round(-y);
		}
			
		if (!rad_color) {
				color= colors[col_index];
				if (col_dir == 0) {
					   if (col_index + 1 >= max_colors) {
						   col_index--;
						   col_dir= 1;
					   } else {
						col_index++;
				   }
				 } else {
					   if (col_index == 0) {
						   col_index++;
						   col_dir= 0;
					   } else {
						   col_index--;
					   }
				 }
/*		} else {
			int red, green, blue;
			step++;
			step= (step % 720);
			red= (int) 127 + (int) Math.round(127 * Math.sin(em.rad((double) (step/2) * 1.0)));
			blue= (int) 127 + (int) Math.round(127 * Math.sin(em.rad((double) (step/2) * 2.0)));
			green= (int) 127 + (int) Math.round(127 * Math.sin(em.rad((double) (step/2) * 3.0)));
			color= new Color(blue, green, red);
		}
			   
*/
		} else {
			int red, green, blue;
			ctheta += 1;
			cgamma += 2;
			red = (int) Math.abs(Math.round(255 * Math.cos(em.rad((double) ctheta / 2)) *
				Math.sin(em.rad((double) cgamma / 2))));
			green = (int) Math.abs(Math.round(255 * Math.sin(em.rad((double) ctheta / 2)) *
				Math.sin(em.rad((double) cgamma / 2))));
			blue = (int) Math.abs(Math.round(255 * Math.cos(em.rad((double) cgamma / 2))));
			color = new Color(blue, green, red);
		}
		ball(ix+field.cx,iy+field.cy, color);
		if (reflect) {
			   ball(ix+field.cx,-iy+field.cy,color);
			   ball(-ix+field.cx,iy+field.cy,color);
			   ball(-ix+field.cx,-iy+field.cy,color);
		}
		if (erase) {
			   int ox, oy;
			   ecounter= (ecounter + 1) % train_len;
			   if (erasing || ecounter == 0) {
				   erasing= true;
				   ox= train[ecounter].x;
				   oy= train[ecounter].y;
				   
				   ball(ox+field.cx,oy+field.cy, background_color);
				   if (reflect) {
					  ball(ox+field.cx,-oy+field.cy, background_color);
					  ball(-ox+field.cx,oy+field.cy, background_color);
					  ball(-ox+field.cx,-oy+field.cy, background_color);
				   }
			   }
			   train[ecounter].x= ix;
			   train[ecounter].y= iy;
		}
		start= true;
	}	   
}

class myObjSet {
	int num_objs;
	int run_objs= 0;
	public myObj myObjs[];
	Graphics g;
	Color background_color;
	Field field;
	Random rand= new Random();
	boolean pause;
	boolean erase;
	Image image;
	Canvas canvas;
	Scribble scra;
	int picPixels[];

	myObjSet(int num, Scribble _scra) {
		num_objs= num;
		myObjs= new myObj[num];
		for (int i= 0; i < num; i++) {
			myObjs[i]= new myObj();
		}
		scra= _scra;
	}

	public void init(Field _f, Graphics _g, Color _b, Image _i, Canvas _c, int _picPixels[]) {
		g= _g;
		field= _f;
		background_color= _b;
		image= _i;
		canvas= _c;
		picPixels= _picPixels;
	}

	public void initObjs() {
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].init(this, g, field, rand, background_color, image, canvas, picPixels,
				scra.width, scra.height);
			myObjs[i].oinit(CONST.OBJ_RADIUS);
		}
	}

	public void make_primary(int r, int g, int b) {
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].make_primary(r, g, b);
		}
	}

	public void make_random_primarys() {
		int val;
		
		for (int i= 0; i < num_objs; i++) {
			do {
				val= rand.nextInt() & 0x7;
			} while (val == 0);
			myObjs[i].make_primary((val & 0x1) != 0 ? 1 : 0, (val & 0x2) != 0 ? 1 : 0,
				(val & 0x4) != 0 ? 1 : 0);
		}
	}
	
	public void make_random(int num) {
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].make_random(num);
		}
	}

	public void set_radcolor(boolean reset) {
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].set_radcolor(reset);
		}
	}

	public void toggle_erase(boolean bval) {
		int val;
		if (erase == bval) {
			return;
		}
		erase = bval;
		val = erase ? CONST.TRAIN_LEN : 0;
		
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].set_erase(val);
		}
	}

	public void set_picturing() {
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].set_picturing();
		}
	}

	public void toggle_reflect(boolean val){
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].toggle_reflect(val);
		}
	}
	
	public void random_init() {
		field.clear(g);
	}

	public void set_run_objs(int n) {
		run_objs= n;
	}

	public void reset() {
		for (int i= 0; i < num_objs; i++) {
			myObjs[i].reset();
		}
	}

	public void moveOne() {
		if (pause) {
			return;
		}
		for (int i= 0; i < run_objs; i++) {
			myObjs[i].scribblemove();
		} 	
	}

	public void toggle_pause(boolean val) {
		pause = val;
	}
}

class urlText extends TextField {
	Scribble scra;
	urlText(int cols, Scribble _scra) {
		super(cols);
		scra= _scra;
	}
	public boolean action(Event e, Object oo) {
		if (e.id == Event.ACTION_EVENT) {
			scra.load_image((String) oo);
			scra.initField();
			return (true);
		}
		return (false);
	}	
}

