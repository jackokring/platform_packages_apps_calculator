package com.android.calculator2;

import java.util.Stack;

public class Complex {

	double x, y;
	String alt;
	final int sum = 4096;
	public static Complex acc = new Complex(0, 0);
	public static Stack<Complex> mem = new Stack<Complex>();
	
	public Complex(double xi, double yi) {
		x = xi;
		y = yi;
	}
	
	private Complex addTo(Complex z) {
		z.x += x;
		z.y += y;
		return z;
	}
	
	public String display() {
		if(alt == null) {
			return Float.toString((float)y) + "\n" +
					Float.toString((float)x);
		}
		return alt;
	}
	
	private void release() {
		alt = null;
	}
	
	//main key
	public Complex addTo() {
		release();
		return addTo(acc);
	}
	
	//basic
	public Complex i() {
		release();
		x = -y;
		y = x;
		return this;
	}
	
	public Complex k() {
		release();
		return this.ln().i().exp();
	}
	
	public Complex exp() {
		release();
		double i = Math.exp(x);
		x = Math.cos(y)*i;
		y = Math.sin(y)*i;
		return this;
	}
	
	public Complex ln() {
		release();
		double i = Math.log(Math.sqrt(x*x+y*y));
		y = Math.atan2(y, x);
		x = i;
		return this;
	}
	
	//advanced operations
	//row 1
	public Complex lnGamma() {
		release();
		//1/t*prod(n=1, inf, (1+1/n)^t/(1+t/n))
		Complex acc = new Complex(0, 0);
		for(int n = 1; n < sum; n++) {
			double f = Math.log(1.0+1.0/n);
			Complex c = new Complex(f*x, f*y);
			c.addTo(acc);
			c = new Complex(1+x/n, y/n).ln().i().i();
			c.addTo(acc);
		}
		//do 1/t
		ln().i().i();
		addTo(acc);
		return acc;
	}
	
	public Complex eta() {
		release();
		if(x>0.5) {
			Complex o = new Complex(0, 0);
			Complex e = new Complex(0, 0);
			for(int n = 0; n < sum; n++) {
				double d = Math.log(2*n+1);
				Complex t = new Complex(-x*d, -y*d).exp();
				t.addTo(o);
				d = Math.log(2*n+2);
				t = new Complex(-x*d, -y*d).exp();
				t.addTo(e);
			}
			e.i().i();
			e.addTo(o);
			return o;
		} else {
			Complex z = xfactor().ln();
			zeta().ln().addTo(z);
			return z.exp();
		}
	}
	
	private Complex xfactor() {
		Complex s = new Complex(x, y).oneMinus();
		double xy = Math.log(2);
		s = new Complex(xy*s.x, xy*s.y).exp().oneMinus();
		return s;
	}
	
	public Complex zeta() {
		release();
		if(x>0.5) {
			Complex z = xfactor().ln().i().i();
			eta().ln().addTo(z);
			return z.exp();
		} else {
			//function reflection
			Complex z = (new Complex(x, y)).oneMinus();
			Complex r = (new Complex(z.x, z.y)).zeta().ln();
			(new Complex(z.x, z.y)).lnGamma().addTo(r);
			double pi2 = Math.PI/2;
			(new Complex(pi2*z.x, pi2*z.y)).cos().ln().addTo(r);
			z.i().i().ln();
			z.x *= Math.PI;
			z.y *= Math.PI;
			z.addTo(r);
			ln();
			x *= 2;
			y *= 2;
			addTo(r);
			return r.exp();
		}
	}
	
	private Complex cos() {
		i();
		Complex t = (new Complex(x, y)).i().i().exp();
		exp();
		t.addTo(this);
		x /= 2;
		y /= 2;
		return this;
	}
	
	//advanced operations
	//row 2
	public Complex invRoot() {
		release();
		this.ln();
		x = -x/2;
		y = -y/2;
		return this.exp();
	}
	
	public Complex oneMinus() {
		release();
		x = 1 - x;
		y = -y;
		return this;
	}
	
	public Complex square() {
		release();
		ln();
		x *= 2;
		y *= 2;
		exp();
		return this;
	}
	
	//advanced operations
	//row 3
	public Complex factor() {
		release();
		Complex z = new Complex(x, y);
		z.ln();
		int t = (int)Math.exp(z.x);
		int e;
		try {
			e = (int)(2*Math.PI/z.y);
		} catch (Exception f) {
			e = 0;
		}
		alt = factor(e) + "\n" + factor(t);
		return this;
	}
	
	private String factor(int x) {
		String out = "";
		int limit = (int)(Math.sqrt(x)+1);
		if(x < 0) x = -x;//flip
		if(x == 0) return "0";
		if(x == 1) return "1";
		while(x % 2 == 0) {
			out += "2.";
			x /= 2;
		}
		if(x != 1) {
			for(int n = 3; n < limit; n+=2) {
				while(x % n == 0) {
					out += Integer.toString(n) + ".";
					x /= n;
					if(x == 1) n = limit;
				}
			}
		}
		return out.substring(0, out.length()-1);//drop dp
	}
	
	public Complex frac() {
		release();
		alt = frac(y) + "\n" + frac(x);
		return this;
	}
	
	private String frac(double x) {
		int t = (int)x;
		String out = "";
		if(t != 0) {
			out += Integer.toString(t) + "/";
			x -= t;
		}
		if(x == 0) return out.substring(0, out.length()-1);
		//now for fractional part
		int n;
		int f = 2;
		double err = 1.0;
		for(int d = 2; d < 10000; d++) {
			n = (int)(x*d);//get numerator
			double rr = x - (0.0 + n)/d;
			if(rr*rr < err) {
				f = d;
				err = rr*rr;
			}
		}
		n = (int)(x*f);//get numerator
		out += Integer.toString(n) + "/" + Integer.toString(f);
		return out;
	}
	
	public Complex polar() {
		release();
		Complex z = new Complex(x, y);
		z.ln();
		z.x = Math.exp(z.x);
		//now for angle from rads
		double t = z.y*180/Math.PI;//degrees
		String out = Integer.toString((int)t) + "°";
		t -= (int)t;
		t *= 60;
		out += Integer.toString((int)t) + "′";
		t -= (int)t;
		t *= 60;
		out += Integer.toString((int)t) + ".";
		t -= (int)t;
		t *= 100;
		out += Integer.toString((int)t) + "″";
		out += "\n" + Float.toString((float)z.x);
		return this;
	}
	
	//advanced operations
	//row 4
	public Complex push() {
		mem.push(acc);
		acc = new Complex(0, 0);
		return this;
	}
	
	public Complex pop() {
		acc = mem.pop();
		return this;
	}
	
	public Complex swap() {
		Complex t = mem.pop();
		mem.push(acc);
		acc = t;
		return this;
	}
}