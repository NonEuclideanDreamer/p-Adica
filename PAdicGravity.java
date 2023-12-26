//************************************
// p-Adica/PAdicGravity.java
// author: Non-Euclidean Dreamer
// Defining Gravity in p-Adic metric
//***********************************


import java.awt.Color;
	import java.awt.image.BufferedImage;
	import java.io.File;
	import java.io.IOException;
	import java.text.DecimalFormat;
	import java.util.ArrayList;
	import java.util.Random;

	import javax.imageio.ImageIO; 

public class PAdicGravity
{
	public static int level=9,prime=Padic.p
				, black=Color.black.getRGB();
	public static double g=0.0005;//Gravity constant;
	public static DecimalFormat df=new DecimalFormat("0000");  
	public static String name="gr", format="png"; 
	public static int width=2560,height=1440,start=0; 
	static double[]center = {width/2,height/2};
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
		
			
			Tile til=new Tile(system.proto[0],0,false,new Rn (new double[]{-0.024,0.08}),0.045695,1);//ins[0][0].copy();
		
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
			
			int n=10;
			int[]where=new int[tiles.size()];
			for(int i=0;i<where.length;i++)where[numbers.get(i).toInt()]=i;
			int[][]mass=new int[n][3],m;//loc,mass,color
			double[]v=new double[n];//level
			
			for(int i=0;i<n;i++)
			{
				mass[i][0]=rand.nextInt(numbers.size());
				mass[i][1]=1;
				mass[i][2]=spectrum(256*6*i/n);
				v[i]=rand.nextDouble()*level;
			}
			int k=0;
			
			//start of time
			while(k<10000)
			{ 
				System.out.println(k);
				m=mass.clone();
				
				//acceleration by other masses
				for(int i=0;i<n;i++)
				{
					double acc=0,mas=0,dist, cur=Math.pow(3, -v[i]);
					for(int j=0;j<n;j++) 
					{if(j!=i)
					{
						dist=numbers.get(m[i][0]).distance(numbers.get(m[j][0]));
						if(dist!=0)
						acc-=Math.signum(cur-dist)*m[j][1]/dist;
					}}
					
					//add acceleration to speed
					v[i]-=acc*g;
					v[i]=Math.min(level-1, Math.max(0, v[i]));
				
					//change position
					if(rand.nextDouble()<(v[i]%1))
						mass[i][0]=where[numbers.get(mass[i][0]).moveDigit((int)v[i]+1).toInt()];
					else
						mass[i][0]=where[numbers.get(mass[i][0]).moveDigit((int)v[i]).toInt()];

					tiles.get(mass[i][0]).draw(image, system.min, system.max,mass[i][2]);
				}
	
			print(image,k);
				dampen(image);
				k++;
			}
		}
		
		//binomial coefficient
		private static int bc(int i, int j) 
		{
			int num=1,den=1;
			for(int k=0;k<j;k++)
			{
				num*=(i-k);
				den*=(k+1);
			}
			System.out.println(i+" choose "+j+" = "+num/den);
			return num/den;
		}
		
		//build the selfsimilar tiling
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
		
		//reduce color of image
		private static void dampen(BufferedImage image) 
		{
			for(int i=0;i<width;i++)
				for(int j=0;j<height;j++)
					image.setRGB(i, j, dampen(image.getRGB(i, j)));
			
		}
		private static int dampen(int rgb) 
		{
			double factor=0.99995;
			Color c=new Color(rgb),co=new Color((int)(c.getRed()*factor),(int)(c.getGreen()*factor),(int) (c.getBlue()*factor));
			
			return co.getRGB();
		}
		
		//Print image to file
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
	
		
		// Chosing a rainbow color
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
