package com.android.calculator2;

public class Complex {

	double x, y;
	final int sum = 4096;
	
	public Complex(double xi, double yi) {
		x = xi;
		y = yi;
	}
	
	public Complex addTo(Complex z) {
		z.x += x;
		z.y += y;
		return this;
	}
	
	//basic
	public Complex i() {
		x = -y;
		y = x;
		return this;
	}
	
	public Complex k() {
		return this.ln().i().exp();
	}
	
	public Complex exp() {
		double i = Math.exp(x);
		x = Math.cos(y)*i;
		y = Math.sin(y)*i;
		return this;
	}
	
	public Complex ln() {
		double i = Math.log(Math.sqrt(x*x+y*y));
		y = Math.atan2(y, x);
		x = i;
		return this;
	}
	
	//advanced operations
	//row 1
	public Complex lnGamma() {
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
			(new Complex(pi2*x, pi2*y)).sin().ln().addTo(r);
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
	
	private Complex sin() {
		i();
		Complex t = (new Complex(x, y)).i().i().exp().i().i();
		exp().addTo(t);
		x = t.y/2;
		y = t.x/2;
		return this;
	}
	
	//advanced operations
	//row 2
	public Complex invRoot() {
		this.ln();
		x = -x/2;
		y = -y/2;
		return this.exp();
	}
	
	public Complex oneMinus() {
		x = 1 - x;
		y = -y;
		return this;
	}
	
	public Complex modulus() {
		x = x*x+y*y;
		y = 0;
		return this;
	}
	
	//advanced operations
	//row 3
	public Complex lnGamma() {
		
		return this;
	}
	
	public Complex eta() {
		
		return this;
	}
	
	public Complex zeta() {
		
		return this;
	}
	
	//advanced operations
	//row 4
	public Complex lnGamma() {
		
		return this;
	}
	
	public Complex eta() {
		
		return this;
	}
	
	public Complex zeta() {
		
		return this;
	}
	
	/*
	//other functions
	//Algorithm Akiyama–Tanigawa algorithm for second Bernoulli numbers Bn
	//Input: Integer n≥0.
	//Output: Second Bernoulli number Bn.
	public double bernoulli(int n) {
		double[] a = new double[n];
		for(int m = 0;  m < n+1 ; m++) {
			a[m] = 1/(m+1);
			for(int j = m; j > 0 ; j--)
				a[j-1] = j*(a[j-1] - a[j]);
		}
		return a[0];
	} */
}
