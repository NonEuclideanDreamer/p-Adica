//*****************************************************
// p-Adica/PadicCA.java
// author: Non-Euclidean Dreamer
// Cellular Automaton on p-Adic numbers
//****************************************************

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Random;
import javax.imageio.ImageIO; 
 
public class PadicCA
{
	public static int level=10,//how many digits deep
					prime=Padic.p;
	public static DecimalFormat df=new DecimalFormat("0000");  
	public static String name="collatz", format="png"; 
	public static int width=2560,height=1440; 
	static double scale=831	,colorwidth=2;
	static double[]center = {width/2,height/2};
	static String colorSymmetry="v"; //"v","f","e":vertex-, face- or edgesymmetric in rgb-cube, "horizontal f", horizontal v": colors repeat along x (mostly for periodic(trig) functions)
	static TileSystems system=TileSystems.sierpinski();
	static Instructions instr=new Instructions(system);	
	static int[]c= {0,0,1,2},m= {0,0,0,0},r= {0,0,0,0};
	static Tile[][]ins=instr.sierpinski(c,m,r);
	public static void main(String[] args)
	{  
	
		BufferedImage image=new BufferedImage(width,height,BufferedImage.TYPE_4BYTE_ABGR);
		
		
		ArrayList<Tile>tiles=new ArrayList<Tile>();
		
		Tile til=new Tile(system.proto[0],0,false,new Rn (new double[]{2/Math.sqrt(3),0}),Math.PI,1);
		tiles.add(til);
		tiles.add(new Tile(system.proto[0],0));
		Random rand=new Random();
		
		int current=1;
		
		ArrayList<Color> colors=new ArrayList<Color>(); 
		for(int i=0;i<level;i++)
		{
			current=iterate(tiles,current,i);	//System.out.print("tiles:"+tiles.size());
		}
	
		int[]lvl=new int[(int)Math.pow(prime, level)];
		Padic[] adics=new Padic[lvl.length]; 
		for(int i=0;i<lvl.length;i++)
		{
			adics[i]=Padic.fromInt(i);
			int size=adics[i].head.length-2+adics[i].k;
			System.out.println("i="+i+", size="+size);
			lvl[i]=size+rand.nextInt(level-size);
		}
		int k=0; 
		while(k<10000)
		{  
			
			for(Tile tile: tiles)
			{
				if(tile.size<Math.pow(2,1.1- lvl[tile.color]))
					tile.draw(image, system.min	, system.max, spectrum(75*tile.color));
			}
			update(lvl,adics);
			System.out.println(k);
	

			print(image,k);
			dampen(image); 
			k++; 
		}
	}
	
	private static void update(int[] lvl,Padic[] adics) 
	{
		int[]copy=lvl.clone();
		for(int i=0;i<lvl.length;i++)   
		{
			int min=adics[i].head.length-2+adics[i].k,sup=level;
			int value=(copy[adics[i].changeDigit(copy[i], 0).toInt()]+copy[adics[i].changeDigit(copy[i], 1).toInt()]+copy[adics[i].changeDigit(copy[i], 0).toInt()])-3*copy[i];
	
			if(((value+1)%5+5)%5<2) {lvl[i]--;}
		
			else  lvl[i]++;
			lvl[i]=(lvl[i]-2*min+sup)%(sup-min)+min;
		}
	}
	
	private static void dampen(BufferedImage image) 
	{
		for(int i=0;i<width;i++)
			for(int j=0;j<height;j++)
				image.setRGB(i, j, dampen(image.getRGB(i, j)));
	}
	
	private static int dampen(int rgb) 
	{
		double factor=0.9;
		Color c=new Color(rgb),co=new Color((int)(c.getRed()*factor),(int)(c.getGreen()*factor),(int) (c.getBlue()*factor));
		
		return co.getRGB();
	}
	
	public static void print(BufferedImage image,int k)
	{
		File outputfile = new File(name+df.format(k)+"."+format);
		try 
		{
			ImageIO.write(image, format, outputfile);
		} catch (IOException e) 
		{
			System.out.println("IOException");
			e.printStackTrace();
		}
		
	}
	
		public static int iterate(ArrayList<Tile>tiles,int current, int level)
			{
				Prototile[]proto=system.proto;
				int k=0;
			
				int out=(tiles.size()-current)/4+current;
				for(int i=tiles.size()-1;i>current-1;i--) {if(k%4!=3)
				{	System.out.print(".");
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
