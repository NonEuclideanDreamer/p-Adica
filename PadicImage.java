//************************************
// p-Adica/PadicImage.java
// author: Non-Euclidean Dreamer
// Depicting the p-Adic Numbers
//***********************************

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;

import javax.imageio.ImageIO; 
 
public class PadicImage
{
	public static int level=11,prime=Padic.p
			, black=Color.black.getRGB();
	public static DecimalFormat df=new DecimalFormat("0000");  
	public static String name="collatz", format="png"; 
	public static int width=1440,height=810,start=0; 
	static double scale=831	,colorwidth=2;
	static double[]center = {width/2,height/2};
	static String colorSymmetry="v"; //"v","f","e":vertex-, face- or edgesymmetric in rgb-cube, "horizontal f", horizontal v": colors repeat along x (mostly for periodic(trig) functions)
	static TileSystems system=TileSystems.halfIsoc();
	static Instructions instr=new Instructions(system);	
	static int[]c= {0,1,2},//{0,0,1,2},
			m= {0,0,0},r= {0,0,0};
	static Tile[][]ins=instr.halfIsoc(c,m,r);//sierpinski(c,m,r);
	static Random rand=new Random();
	public static void main(String[] args)
	{  
	
		BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
		
	
		ArrayList<Tile>tiles=new ArrayList<Tile>(); 
	
		
	Tile	til=new Tile(system.proto[0],0,false,new Rn (new double[]{-0.025,0.08}),0.045695,1);//ins[0][0].copy();
	
	//	tiles.add(til);
		tiles.add(til);
		
		
		int current=0;
		ArrayList<Padic> numbers=new ArrayList<Padic>(); 
	//	numbers.add(Padic.zero);
		numbers.add(Padic.zero);
		for(int i=0;i<level;i++)
		{
			iterate(tiles,numbers,current,i);	System.out.print("tiles:"+tiles.size());
		}
	
		int k=0,l=0,n=0,m=1,o=2,p=3; 
	Padic numb=Padic.zero,number=Padic.one, numbest=Padic.fromInt(2), numissimo=Padic.fromInt(3);
		boolean[]done=new boolean[numbers.size()];
		double[]min=new double[done.length];
		int color=spectrum(0);
		for(int i=0;i<min.length;i++) {min[i]=3;}
		while(k<10000)
		{  if(l==0)
			{
				int i=n;
			//do { i=rand.nextInt(done.length);} while(done[i]);//get new random p-Adic
				numb=Padic.fromInt(i);//numbers.get(i);

				
			//	tiles.get(i).draw(image,system.min,system.max,color);
				done[i]=true;
			}color=spectrum(61*k);
		
			//if(l==level)color=black;//For voronoi
			System.out.println(k);
			for(int i=0;i<tiles.size();i++)
			{
				double d=numbers.get(i).distance(numb),d1=numbers.get(i).distance(number),d2=numbers.get(i).distance(numbest),d3=numbers.get(i).distance(numissimo);
			//	System.out.println(d);
	
				if(d*d1*d2>Math.pow(prime, -2*level+3+l-0.5)*d3&&d*d1*d2<Math.pow(prime, -2*level+3+l+0.5)*d3||(l==0&&d*d1*d2==0))
				{
					tiles.get(i).draw(image,system.min,system.max,color);
				}		
		/*		if(l==level)//for voronoi
				{
					if(d<min[i]) {
					min[i]=d; 			tiles.get(i).draw(image,system.min,system.max,color);
				}	else if(d<min[i]*Math.sqrt(prime)) 				tiles.get(i).draw(image,system.min,system.max,black);

*/
				//}
				/*if(tile.color==k)
					tile.draw(image, system.min	, system.max, spectrum(75*k));*/
			}
		/*	for(int i=0;i<numbers.size();i++)
			{ 
				draw(image, numbers.get(i), colors.get(i));
				numbers.set(i,numbers.get(i).phi());
			//	numbers.get(i).print(); 
			}
			for(int i=0;i<width;i++)
				for(int j=0;j<height;j++)
			{
				try {	
					field[i][j].print();
				image.setRGB(i, j, colormap(field[i][j].loc()));
				
				
				field[i][j]=(field[i][j].f());}catch(NullPointerException f) {}catch(ArrayIndexOutOfBoundsException e) {System.out.println("Problem with processing ");field[i][j].print();}
						 
			}*/
	print(image,k);
		//	dampen(image);
			k++;
			l++;
			if(l==3*level-3)
			{
				l=0;
			n++;
					if(n==m)
				{
					m++;
					if(m==o)
					{
						o++;
						if(o==p)
						{
							p++;
							
							o=2;

							numissimo=Padic.fromInt(p);
						}
						m=1;

						numbest=Padic.fromInt(o);
					}
					n=0;
					number=Padic.fromInt(m);
				}
				numb=Padic.fromInt(n);
			
			}
		}
	}
	
	private static int iterate(ArrayList<Tile> tiles, ArrayList<Padic> numbers, int current, int level) {
		Prototile[]proto=system.proto;
		int k=0;
	
		int out=(tiles.size()-current)/4+current;
		for(int i=tiles.size()-1;i>current-1;i--) {//if(k%4!=3)
		{	
			Tile tile=tiles.get(i);
			for(int j=0;j<proto.length;j++)
			{
			if(tile.prototype().equals(proto[j]))
			{
				for(int l=0;l<ins[j].length;l++)
					{
						Tile t =ins[j][l].copy();

						numbers.add(numbers.get(i).changeDigit(level,t.color));
						t.color*=Math.pow(prime, level);
						t=t.transform(tile);
						tiles.add(t);
					}
			}
			} 
			tiles.remove(i);
			numbers.remove(i);
		}	k++;
		}
	return out;
	}
	
	private static void dampen(BufferedImage image) 
	{
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
				image.setRGB(i, j, dampen(image.getRGB(i, j)));
	}
	private static int dampen(int rgb) 
	{
		double factor=0.9999;
		Color c=new Color(rgb),co=new Color((int)(c.getRed()*factor),(int)(c.getGreen()*factor),(int) (c.getBlue()*factor));
		
		return co.getRGB();
	}
	
	public static void print(BufferedImage image,int k)
	{
		System.out.println("printing "+k);
		File outputfile = new File(name+df.format(k+start)+"."+format);
		try 
		{
			ImageIO.write(image, format, outputfile);
		} catch (IOException e) 
		{
			System.out.println("IOException");
			e.printStackTrace();
		}
	}
	
	private static void draw(BufferedImage image, Padic padic, Color color) 
	{ 
		int c=color.getRGB();
		double[]loc=padic.loc();
		for(int i=-2;i<3;i++)
			for(int j=-2;j<3;j++)
			try {	image.setRGB((int)(center[0]+scale*loc[0]+i),(int)(center[1]+scale*loc[1]+j), c);}catch( ArrayIndexOutOfBoundsException e){}
	}
	
	//what location gives what color
		//one can change the signs +- in the definitions of x,y(,z) as well as theit order in the return statement for dirrering results
		public static int colormap(double[]loc)
		{
			//double l0=
			double factor=1;
			double r=Math.sqrt(loc[0]*loc[0]+loc[1]*loc[1]),
					phi=Math.atan2(loc[1], loc[0]);
			if(r<colorwidth&& (colorSymmetry.length()==1)) {factor=r*factor/colorwidth;} 
		
			if(colorSymmetry.equals("v"))//bw:+++ rc:-++ gm:+-+ by: ++-
			{
				double a=Math.cos(phi)*Math.sqrt(3)-Math.sin(phi),b=2*Math.sin(phi),c=-Math.cos(phi)*Math.sqrt(3)-Math.sin(phi),norm=Math.max(Math.abs(c),Math.max(Math.abs(a),Math.abs(b)))/factor;
				int x=(int)Math.max(0, Math.min((128+128*a/norm), 255)),y=(int)Math.max(0, Math.min(128+128*b/norm,255)),w=(int)Math.max(0, Math.min(128+128*c/norm,255));
				return new Color(w,x,y).getRGB();
			}
			else if(colorSymmetry.equals("f"))//w where constant
			{
				double a=Math.cos(phi),b=Math.sin(phi),norm=Math.max(Math.abs(a),Math.abs(b))/factor;
				int x=(int)Math.max(0, Math.min((128+128*a/norm), 255)),y=(int)Math.max(0, Math.min(128+128*b/norm,255)),w=128;
				return new Color(y,x,w).getRGB();
			}
			else if(colorSymmetry.equals("e"))//x different from y,w
			{
				double a=Math.cos(phi), b=Math.sin(phi),norm=Math.max(Math.abs(a), Math.abs(b))/factor;
				int x=(int)Math.max(0, Math.min((128+128*a/norm), 255)),y=(int)Math.max(0, Math.min(128+128*b/norm,255)),w=(int)Math.max(0, Math.min(128+128*b/norm,255));
				return new Color(y,x,w).getRGB();
			}
			else if(colorSymmetry.equals("horizontal f"))
			{	
				int w=(int) Math.max(0, Math.min(255,128+loc[1]/colorwidth*factor)),x,y,edge=(int)(((loc[0]+1000*colorwidth)%colorwidth)/(colorwidth/4.0)),pos=(int)Math.max(0,Math.min(255,(((loc[0]+1000*colorwidth)%(colorwidth/4.0))/(colorwidth/4.0)-0.5)*factor*256+128));
				
				if(edge==0||edge==3)x=(int)(128-factor*127.9); else x=(int) (128*(1+factor*0.999999));
				
				if(edge<2)y=pos; else y=255-pos;
			
				if (edge%2==0)return new Color(w,x,y).getRGB(); else return new Color(w,y,x).getRGB();
			}
			else if(colorSymmetry.equals("horizontal e"))
			{	
				double a=loc[0], b=loc[1]%colorwidth, norm=Math.max(Math.abs(a), Math.abs(b))/factor;
				int x=(int)Math.max(0, Math.min((128+128*a/norm), 255)),y=(int)Math.max(0, Math.min(128-128*b/norm,255)),w=(int)Math.max(0, Math.min(128+128*b/norm,255));
				return new Color(y,w,x).getRGB();
			}
			
			else return 0;
		}
	
		public static int iterate(ArrayList<Tile>tiles,int current, int level)
			{
				Prototile[]proto=system.proto;
				int k=0;
			
				int out=(tiles.size()-current)/4+current;
				for(int i=tiles.size()-1;i>current-1;i--) {if(k%4!=3)
				{	
					System.out.print(".");
					Tile tile=tiles.get(i);
					for(int j=0;j<proto.length;j++)
					{
					if(tile.prototype().equals(proto[j]))
					{
						for(int l=0;l<ins[j].length;l++)
						{
							Tile t =ins[j][l].copy();
								t.color*=Math.pow(prime, level);
								t=t.transform(tile);
								tiles.add(t);
						}
					}
					} 
					tiles.remove(i);
				}	k++;
				}
			return out;
		}
		
		public static int spectrum(int n)
		{
			n=n%(6*256);
			if (n<256)
				return new Color(255,n,0).getRGB();
			n-=256;
			if (n<256)
				return new Color(255-n,255,0).getRGB();
			n-=256;
			if (n<256)
				return new Color(0,255,n).getRGB();
			n-=256;
			if (n<256)
				return new Color(0,255-n,255).getRGB();
			n-=256;
			if (n<256)
				return new Color(n,0,255).getRGB();
			n-=256;
				return new Color(255,0,255-n).getRGB();
		}
}
