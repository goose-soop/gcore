package be.guillaumevdn.gcore.lib.npc.navigation;

import java.util.List;

import org.bukkit.World;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import be.guillaumevdn.gcore.GCore;

public abstract class PathfindingNavigator implements Navigator {

	// base data
	private World world;
	private Point start, target;
	private int pathfindingStep, pathfindingSpeed, yToleranceUp, yToleranceDown;
	private double targetDistanceTolerance;
	private long ticksPerStep;

	// running
	private State state = State.WAITING;
	private Pathfinding pathfinding = null;
	private BukkitTask task = null;
	private Integer index = null;
	private List<Point> path = null;
	private Point currentStep = null;

	// constructor
	public PathfindingNavigator(World world, Point start, Point target, int pathfindingStep, int pathfindingSpeed, int yToleranceUp, int yToleranceDown, double targetDistanceTolerance, long ticksPerStep) {
		this.world = world;
		this.start = start;
		this.target = target;
		this.pathfindingStep = pathfindingStep;
		this.pathfindingSpeed = pathfindingSpeed;
		this.yToleranceUp = yToleranceUp;
		this.yToleranceDown = yToleranceDown;
		this.targetDistanceTolerance = targetDistanceTolerance;
		this.ticksPerStep = ticksPerStep;
	}

	// get
	@Override
	public World getWorld() {
		return world;
	}

	@Override
	public Point getStart() {
		return start;
	}

	@Override
	public Point getTarget() {
		return target;
	}

	@Override
	public List<Point> getPath() {
		return path;
	}

	@Override
	public Point getCurrentStep() {
		return currentStep;
	}

	public State getState() {
		return state;
	}

	// set
	protected void setWorld(World world) {
		if (!state.equals(State.WAITING)) throw new IllegalStateException("world can only be changed while waiting");
		this.world = world;
	}

	protected void setStart(Point start) {
		if (!state.equals(State.WAITING)) throw new IllegalStateException("world can only be changed while waiting");
		this.start = start;
	}

	// abstract methods
	protected abstract void onStep(Point step);
	protected abstract void onFail();
	protected abstract void onSuccess();

	// methods
	@Override
	public void start() {
		// invalid state
		if (!state.equals(State.WAITING)) {
			return;
		}
		// start pathfinding
		state = State.FINDING_PATH;
		GCore.inst().getNpcManager().addNavigator(this);
		pathfinding = new Pathfinding(world, start, target, pathfindingStep, pathfindingSpeed, yToleranceUp, yToleranceDown, targetDistanceTolerance) {
			@Override
			protected void onFail() {
				state = PathfindingNavigator.State.DONE;
				GCore.inst().getNpcManager().removeNavigator(PathfindingNavigator.this);
				PathfindingNavigator.this.onFail();
			}
			@Override
			protected void onSuccess(final List<Point> path) {
				state = PathfindingNavigator.State.TRAVELLING;
				index = -1;
				PathfindingNavigator.this.path = path;
				task = new BukkitRunnable() {
					@Override
					public void run() {
						// done
						if (++index >= path.size()) {
							state = PathfindingNavigator.State.DONE;
							GCore.inst().getNpcManager().removeNavigator(PathfindingNavigator.this);
							PathfindingNavigator.this.onSuccess();
							cancel();
							return;
						}
						// step
						onStep(currentStep = path.get(index));
					}
				}.runTaskTimer(GCore.inst(), 0L, ticksPerStep);
			}
		};
		pathfinding.start();
	}

	@Override
	public void cancel() {
		// invalid state
		if (state.equals(State.WAITING) || state.equals(State.DONE)) {
			return;
		}
		// cancel
		state = State.DONE;
		GCore.inst().getNpcManager().removeNavigator(PathfindingNavigator.this);
		if (pathfinding != null) {
			pathfinding.stop();
		}
		if (task != null) {
			task.cancel();
		}
	}

	// state
	public static enum State {
		WAITING, FINDING_PATH, TRAVELLING, DONE;
	}

}
