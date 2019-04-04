package com.guillaumevdn.gcore.commands;

public class CommandPathfindingTest {/*extends CommandArgument {

	public CommandPathfindingTest() {
		super(GCore.inst(), Utils.asList("pathfindingtest"), "start pathfinding test", PCPerm.GCORE_ADMIN, true); 
	}

	@Override
	public void perform(CommandCall call) {
		final Player player = call.getSenderAsPlayer();
		final Block target = player.getTargetBlock((Set<Material>) null, 100);
		if (target == null) {
			player.sendMessage("§cInvalid target block.");
			return;
		}
		player.sendMessage("§aStarting in 5s...");
		final Location ploc = player.getWorld().getHighestBlockAt(player.getLocation()).getLocation().clone().add(0d, 1d, 0d);
		new BukkitRunnable() {
			@Override
			public void run() {
				player.sendMessage("§aStarting !");
				Pathfinding pathfinding = new Pathfinding(ploc.getWorld(), new Point(ploc.getBlockX(), ploc.getBlockY(), ploc.getBlockZ()),
						new Point(target.getX(), target.getY(), target.getZ()), 1, 50, 1, 1) {

				};
				pathfinding.start();
			}
		}.runTaskLater(GCore.inst(), 100L);
	}*/

}
