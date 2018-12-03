package game;

import java.util.ArrayList;

public class Camera {
	
	public PIDSystem speed = new PIDSystem("speed", 1.0, 3, 0, 0);
	public PIDSystem zoom = new PIDSystem("zoom", 1.0, 3, 0, 0);
	public PIDSystem x_pos = new PIDSystem("x_pos", 0, 3, 0, 0);
	public PIDSystem y_pos = new PIDSystem("y_pos", 0, 3, 0, 0);
	
	public PIDSystem pids[] = {speed, zoom, x_pos, y_pos};
	
	public void Camera() {
		
	}
	
	/**
	 * Updates the position, slowdown, and zoom of the camera object for a given timestep.
	 * 
	 * @param dt
	 * Timestep (seconds)
	 * 
	 * @return
	 * New timestep after slowdown is applied (seconds)
	 */
	public double update(double dt) {
		
		for (PIDSystem system : pids) {
			system.update(dt);
		}
		
		double new_dt = dt * speed.get_value();
		return new_dt;
	}
	
	/**
	 * Sets the focal center of the camera to a point (x, y)
	 * @param x
	 * X component of the target position
	 * @param y
	 * Y component of the target position
	 */
	public void set_target_pos(double x, double y) {
		x_pos.set_target_value(x);
		y_pos.set_target_value(y);
	}
	
	/**
	 * Sets the zoom amount of the camera to a value, where 1.0 is no zoom.
	 * @param zoom_value
	 * Target zoom value
	 */
	public void set_target_zoom(double zoom_value) {
		zoom.set_target_value(zoom_value);
	}
	
	/**
	 * Sets the speed of the camera to a value. A value less than one results in a slow-motion effect, while a value greater than one results in faster capture.
	 * @param speed
	 * Proportional speed value
	 */
	public void set_target_speed(double speed) {
		this.speed.set_target_value(speed);
	}
	
	public boolean set_pid_for_item(String item, double p, double i, double d) {
		for (PIDSystem system : pids) {
			if (system.get_name() == item) {
				system.set_pid(p, i, d);
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Returns the current x position of the camera focus
	 * @return
	 * Current x position
	 */
	public int get_x_pos() {
		return (int)(x_pos.get_value());
	}
	
	/**
	 * Returns the current y position of the camera focus
	 * @return
	 * Current y position
	 */
	public int get_y_pos() {
		return (int)(y_pos.get_value());
	}
	
	public double get_zoom() {
		return zoom.get_value();
	}
}
