import java.util.ArrayList;

public class Padic 
{
	static int p=3;
	int k,period;
	int[] head;
	static int[][]multable=mult();
	
	static Padic zero=new Padic(0, new int[] {0},1),
				one=fromInt(1),
				two=fromInt(2),
				third=one.div(fromInt(3)),
				mone=fromInt(-1);
	public Padic(int k0, int[]h,int per)
	{
		k=k0;
		head=h;
		period=per;
	}
	
	public static void main(String[]args)
	{
		Padic a=one;
		//Padic b=fromInt(2);
		//Padic c=b.div(a);
		//third.print();
		for(int i=0;i<100;i++)
		{a.print();a=one.div(a).subtract(mone); }//System.out.println("3 "); a.f().print(); // System.out.println("=");c.print(); c.div(a).print();
	}
	
	public void print()
	{
	
		if(period-head.length>k)
		{
			for(int i=0;i<period+1;i++)System.out.print("_");
			System.out.println();	
		}
		else
		{
			for(int i=0;i<period;i++)System.out.print("_");
			System.out.println();	
		}
		for(int i=head.length-1;i>Math.max(-1,-k-1);i--) 
			System.out.print(head[i]);
		if(k<0)
		{
			System.out.print(".");
			for(int i=-k-1;i>-1;i--)
				System.out.print(head[i]);
		}
		else if(k>0)
		{
			for(int i=0;i<k;i++)
				System.out.print(0);
		}
		System.out.println();
	}
	
	//writing an integer as a p-adic
	public static Padic fromInt(int m)
	{
		if(m==0) {return zero;}
	
		if(m<0) 
		{
			 return zero.subtract(fromInt(-m));
		}
		else
		{
			int n=m;
			int k=0;
			while(n%p==0)
			{
				k++;
				n/=p;
			}
			int length=(int) (Math.log(n)/Math.log(p))+2;
			int[]head=new int[length];
			for (int i=0;i<length;i++)
			{
				head[i]=n%p;
				n/=p;
			}
			return new Padic(k,head,1);
		}
	}
	
	public boolean equals(Padic z)
	{
	//	System.out.println("k="+k+", z.k="+z.k);
		if(k!=z.k)return false;
		//System.out.println("period="+period+", z.period="+z.period);

		if(period!=z.period)return false;
		if(head.length!=z.head.length)return false;
		for(int i=0;i<head.length;i++)
		if(head[i]!=(z.head[i]))return false;
	//	print(head);
		//print(z.head);
		return true;
	}
	
	private static void print(int[] v) 
	{
		System.out.print("{");
		for(int i=0;i<v.length;i++)
			System.out.print(v[i]+",");
		System.out.println("}");
	}

	public Padic div(Padic x)
	{
	//	print();System.out.println("divided by ");x.print();
		int k0=k-x.k, i=0;
		Padic z=x.shift(-x.k);
		int p0=0, first=z.head[0];
		ArrayList<Padic> rem=new ArrayList<Padic>();
		ArrayList<Integer> digits=new ArrayList<Integer>();
		rem.add(this.shift(-k));
		Padic rest=rem.get(0);
		while(p0==0&&rem.size()+k<100)
		{
			//rest.print();
			if(rest.isZero())
			{
				digits.add(0);
				p0=1;
			}
			else if(rest.k>0)
			{
				digits.add(0);
				rest=rest.shift(-1);
				p0=notin(rem,rest);
				if(p0==0)
				rem.add(rest);
			}
			else
			{
			//	rest.print();
				int n=multable[first][rest.head[0]];
			//	System.out.println("nz=");z.times(n).print();
				digits.add(n);
				
				rest=rest.subtract(z.times(n));
				rest=rest.shift(-1);
				p0=notin(rem,rest);
				if(p0==0)
				rem.add(rest);
			}
		}
		
		int[] h=new int[digits.size()];
		for(i=0;i<digits.size();i++)
			h[i]=digits.get(i);
		return new Padic(k0,h,p0);
	}
	//return out for rem.get(length-out)==rest
	private int notin(ArrayList<Padic> rem, Padic rest) 
	{
		for(int i=0;i<rem.size();i++)
		{
			if(rem.get(i).equals(rest))
				return rem.size()-i;
		}
		return 0;
	}

	public boolean isZero()
	{
		boolean out = (period==1&&head.length==1);
		
		if(out) return (head[0]==0);
		else return false;
	}
	
	Padic subtract(Padic z) 
	{
		if(z.isZero())return this;
		ArrayList<Integer>digits=new ArrayList<Integer>();
		int takeover=0, offset=z.k-k, start=Math.min(0,offset), k0=Math.min(k, z.k), stage1=Math.max(head.length-period, z.head.length-z.period+offset),a,b;
		if(isZero()) {offset=0;start=0;k0=z.k;stage1=z.head.length-z.period;}
		int[][] to=new int[period][z.period];
		for(int i=0;i<period;i++)
		for(int j=0;j<z.period;j++)
			to[i][j]=-1;
		for(int i=start;i<stage1;i++)
		{
			if(i<0)a=0;
			else if(i<head.length) a=head[i];
			else a=head[i-period*((i-head.length)/period+1)];
			int j=i-offset;
			if(j<0)b=0;
			else if(j<z.head.length) b=z.head[j];
			else b=z.head[j-z.period*((j-z.head.length)/z.period+1)];
			int x=a-b-takeover;
			takeover=0;
			while(x<0)
			{
				takeover++;
				x+=p;
			}
			digits.add(x);
		}
		boolean notdone=true;
		int i=stage1, j=stage1-offset,  p0=1;
		while(notdone)
		{
			if(i<0)a=0;
			else if(i<head.length) a=head[i];
			else a=head[i-period*((i-head.length)/period+1)];
			if(j<0)b=0;
			else if(j<z.head.length) b=z.head[j];
			else b=z.head[j-z.period*((j-z.head.length)/z.period+1)];
			int x=a-b-takeover;
			takeover=0;
			while(x<0)
			{
				takeover++;
				x+=p;
			}	
			if(to[i%period][j%z.period]==takeover)notdone=false;
			else
			{
				 to[i%period][j%z.period]=takeover;
			}
			digits.add(x);
			i++;j++;
		
		}
		int pmax=lcm(period,z.period);
		notdone=true;
		while(notdone)
		{
			if(pmax%p0==0)
			{
				boolean periodic = true;
				i=digits.size()-1;
				while(periodic&&i>digits.size()-pmax+p0-1)
				{
					if(digits.get(i)==digits.get(i-p0))i--;
					else periodic=false;
				}
				if(periodic)notdone=false;
				else p0++;
			}
			else p0++;
		}
		
		//discard upper repeated digits
		int last=digits.size()-1, l=digits.get(last),l0;
		if(last>=p0)l0=digits.get(last-p0);
		else l0=-1;
		while(l==l0)
		{
			digits.remove(last);
			last--;
			 l=digits.get(last);
			 if(last>=p0)l0=digits.get(last-p0);
				else l0=-1;
		}
		
		//find lower zeroes
		while(digits.get(0)==0&&digits.size()>1)
		{
			k0++;
			digits.remove(0);
			if(digits.size()<p0)digits.add(0);
		}

		if(digits.get(0)==0)return zero;
		int[]h=new int[digits.size()];
		for(i=0;i<h.length;i++)
		{
			h[i]=digits.get(i);
		}
		
		return new Padic(k0,h,p0);
	}
	
	//lowest common divisor
	static int lcm(int i,int j)
	{
		int out =i;
		
		while(out%j!=0)out+=i;
		
		return out;
	}
	
	//for 0<n<p
	private Padic times(int n) 
	{
		ArrayList<Integer> digits=new ArrayList<Integer>();
		int takeover=0, x;
		boolean notdone=true;
		for(int i=0;i<head.length-period;i++)
		{
			x=head[i]*n+takeover;
			takeover=x/p;
			digits.add(x%p);
		}
		int r=takeover;
		do
		{
			for(int i=head.length-period;i<head.length;i++)
			{
				x=head[i]*n+takeover;
				takeover=x/p;
				digits.add(x%p);

			}
			if(takeover==r)notdone=false;
			else r=takeover;
		}	while(notdone);
		
		//find period
		int l=digits.size()-1;
		notdone=true;
		int i=1;
		while(notdone)
		{
			if(period%i==0)
			{
				boolean periodic=true;	
				for(int j=0;j<period-i;j++)
				if(digits.get(l-j)!=digits.get(l-j-i))periodic=false;
				
				if(periodic)notdone=false;
				else i++;
			}
			else i++;
		}
		
		//i=period
		int p0=i;
		i=l;
		while(digits.get(i)==digits.get(i-p0)) {i--;}
		int[]h=new int[i+1];
		
		for(int j=0;j<h.length;j++)
			h[j]=digits.get(j);
	
		return new Padic(k,h,p0);
	}

	//*3^i
	private Padic shift(int i) 
	{
		return new Padic(k+i,head.clone(),period);
	}

	//multiplication table: (i)*what=j
	public static int[][] mult()
	{
		int[][]out=new int[p][p];
		for(int i=1;i<p;i++)
			for(int j=1;j<p;j++)
			{
				int l=0;
				boolean found=false;
				while(!found)
				{
					if((l*i)%p==j)
					{
						found=true;
						out[i][j]=l;
					}
					else l++;
				}
			}
		return out;
	}
	
	public double[]loc()
	{
		double[]out=new double[2];
	
		int dir=1-Math.abs(k)%2;
	
		for(int i=head.length-period;i<head.length;i++)
		{
			if(head[i]==1) {out[dir]-=Math.pow(p, 0.5*(-k-i));}
			else if(head[i]==2){out[dir]+=Math.pow(p,0.5*( -k-i));}
			
			dir=1-dir;
		}
		for(int i=0;i<2;i++) out[i]/=(1-Math.pow(p, period));
		for(int i=0;i<head.length-period;i++)
		{
			if(head[i]==1) {out[dir]-=Math.pow(p, 0.5*(-k-i));}
			else if(head[i]==2){out[dir]+=Math.pow(p,0.5*( -k-i));}
			
			dir=1-dir;
		}	if(p==2) {out[0]+=1;out[1]+=Math.sqrt(0.5);}
		return out;
	}

	public Padic phi()
	{
		if(isZero())return mone;
		else return one.div(this).subtract(mone);
	}
	public Padic f() 
	{
		//Collatz
		if(isZero())return this;
	
		Padic out=div(two);
		  if(out.k>-1)
			return out;
			return(div(third).subtract(mone));
	
	}
	//only implemented for nonneg.integers
	public int toInt()
	{
		int out=0;
		for(int i=0;i<head.length;i++)out+=Math.pow(p, i)*head[i];
		out*=Math.pow(p,k);
		return out;
	}
	
	//only implemented for positive integers
	public Padic changeDigit(int place, int value)
	{
		if(isZero())return fromInt((int)Math.pow(p, place)*value);

		if(head.length+k-period>place && k<=place)
		{
			int[]h=head.clone();
			h[place-k]=value;
			return new Padic(k,h,period);
		}
		else if(place<k)
		{
			int[] h=new int[head.length+k-place];
			h[0]=value;
			for(int i=1;i<h.length-head.length;i++)h[i]=0;
			for(int i=h.length-head.length;i<h.length;i++)h[i]=head[i+head.length-h.length];
			return new Padic(place,h,period);
		}
		else 
		{
			int[]h=new int[place+2-k];
			for(int i=0;i<head.length;i++)
				h[i]=head[i];
			h[place-k]=value;
			Padic out=new Padic(k,h,period);
			return out;
		}
	}
	
	public double distance (Padic n)
	{
		Padic numb=subtract(n);
		if(numb.isZero())return 0;
		return Math.pow(p,- numb.k);
	}

	public Padic moveDigit(int place) 
	{
		if(isZero())return fromInt((int)Math.pow(p, place));
			if(head.length+k-period>place && k<=place)
			{
				int[]h=head.clone();
				h[place-k]=(h[place-k]+1)%p;
			
				return new Padic(k,h,period);
			}
			else if(place<k)
			{
				int[] h=new int[head.length+k-place];
				h[0]=1;
				for(int i=1;i<h.length-head.length;i++)h[i]=0;
				for(int i=h.length-head.length;i<h.length;i++)h[i]=head[i+head.length-h.length];
				return new Padic(place,h,period);
			}
			else 
			{
				int[]h=new int[place+2-k];
				for(int i=0;i<head.length;i++)
					h[i]=head[i];
				h[place-k]=1;
				Padic out=new Padic(k,h,period);
				return out;
			}
	}
}
