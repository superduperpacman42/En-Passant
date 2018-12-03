package game;

public class PIDSystem {
	
	public String name;
	public double value;
	public double target_value;
	public double p;
	public double i;
	public double d;
	
	public double integral_sum;
	public double last_derivative;
	
	/**
	 * Constructs the PID system.
	 * @param value
	 * Starting value of the system
	 * @param p
	 * Proportionality constant
	 * @param i
	 * Integral constant
	 * @param d
	 * Derivative constant
	 */
	public PIDSystem(String name, double value, double p, double i, double d) {
		
		this.name = name;
		this.value = value;
		this.target_value = value;
		this.p = p;
		this.i = i;
		this.d = d;
		
	}
	
	/**
	 * Updates the PID system based on a time step.
	 * @param dt
	 * Elapsed time (seconds)
	 */
	public void update(double dt) {
		
		double diff = target_value - value;
		integral_sum += diff*dt;
		double delta = p*diff + i*integral_sum + d*last_derivative;
		value += delta*dt;
		last_derivative = delta;
	}
	
	/**
	 * Sets the P, I, and D values for the PID system.
	 * @param p
	 * New P value
	 * @param i
	 * New I value
	 * @param d
	 * New D value
	 */
	public void set_pid(double p, double i, double d) {
		
		this.p = p;
		this.i = i;
		this.d = d;
	}
	
	/**
	 * Sets the target value of the PID system.
	 * @param new_target
	 * New value
	 */
	public void set_target_value(double new_target) {
		target_value = new_target;
		integral_sum = 0;
	}
	
	public double get_value() {
		return value;
	}
	
	public String get_name() {
		return name;
	}
	
}
