package com.ksiu.maze;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import it.unimi.dsi.fastutil.Pair;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class MazeCommands
{
    private MazeCommands()
    {
    }

    public static KsiuCommandList createCommandList(JavaPlugin owner)
    {
        KsiuCommandList commandList = new KsiuCommandList("maze");
        commandList.put(new GenerateCommand(owner));
        return commandList;
    }

    public static class GenerateCommand extends CommandBase
    {
        private final JavaPlugin _owner;
        private volatile boolean _isGenerating = false;

        public GenerateCommand(JavaPlugin owner)
        {
            super("generate", "미로를 생성합니다.");
            _owner = owner;
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args)
        {
            if (_isGenerating)
            {
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append("이미 다른 생성 작업이 진행 중입니다. 잠시만 기다려주세요.").build());
                return true;
            }

            // 인자: /generate <x> <y> <z> <rx> <ry> <rz> <block> (총 7개)
            if (args.length < 7)
            {
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(": generate <x> <y> <z> <rx> <ry> <rz> <block>").build());
                return true;
            }

            try
            {
                World world;
                Location location;
                if (sender instanceof Entity entity)
                {
                    world = entity.getWorld();
                    location = entity.getLocation();
                }
                else if (sender instanceof BlockCommandSender blockSender)
                {
                    Block block = blockSender.getBlock();
                    world = block.getWorld();
                    location = block.getLocation();
                }
                else
                {
                    return true;
                }

                double cx = args[0].equals("~") ? location.getX() : Double.parseDouble(args[0]);
                double cy = args[1].equals("~") ? location.getY() : Double.parseDouble(args[1]);
                double cz = args[2].equals("~") ? location.getZ() : Double.parseDouble(args[2]);

                int rx = Integer.parseInt(args[3]);
                int ry = Integer.parseInt(args[4]);
                int rz = Integer.parseInt(args[5]);

                Material material = Material.matchMaterial(args[6].toUpperCase());
                if (material == null || !material.isBlock())
                {
                    sender.sendMessage(KsiuCore.getPrefixTextBuilder().append("존재하지 않거나 설치 불가능한 블록입니다." + args[6]).build());
                    return true;
                }

                final List<Pair<Location, Material>> tasks = new ArrayList<>();
                for (int x = -rx; x <= rx; x++)
                {
                    for (int y = 0; y <= ry; y++)
                    {
                        for (int z = -rz; z <= rz; z++)
                        {
                            Location targetLoc = new Location(world, cx + x, cy + y, cz + z);
                            boolean isEdge = (Math.abs(x) == rx) || (y == 0 || y == ry) || (Math.abs(z) == rz);
                            if (isEdge)
                            {
                                tasks.add(Pair.of(targetLoc, material));
                            }
                            else
                            {
                                tasks.add(Pair.of(targetLoc, Material.AIR));
                            }
                        }
                    }
                }

                _isGenerating = true;
                new BukkitRunnable()
                {
                    private int index = 0;
                    private final int total = tasks.size();

                    @Override
                    public void run()
                    {
                        long startTime = System.currentTimeMillis();
                        while (index < total && (System.currentTimeMillis() - startTime) < 5)
                        {
                            for (int i = 0; i < 2000 && index < total; i++)
                            {
                                Pair<Location, Material> pair = tasks.get(index++);
                                pair.first().getBlock().setType(pair.second());
                            }
                        }

                        if (index >= total)
                        {
                            sender.sendMessage(KsiuCore.getPrefixTextBuilder().append("미로 생성 완료!").build());
                            _isGenerating = false;
                            this.cancel();
                        }
                    }
                }.runTaskTimer(_owner, 0L, 1L);
            }
            catch (NumberFormatException e)
            {
                _isGenerating = false;
                sender.sendMessage(KsiuCore.getErrorTextBuilder().append("좌표와 지름은 숫자여야 합니다.").build());
            }
            catch (Exception e)
            {
                _isGenerating = false;
                sender.sendMessage(KsiuCore.getErrorTextBuilder().append(e.toString()).build());
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (args.length >= 1 && args.length <= 3)
                return Collections.singletonList("~");

            if (args.length == 7)
            {
                return Arrays.stream(Material.values())
                        .filter(Material::isBlock)
                        .map(m -> m.name().toLowerCase())
                        .filter(name -> name.startsWith(args[6].toLowerCase()))
                        .limit(15)
                        .collect(Collectors.toList());
            }
            return Collections.emptyList();
        }
    }

}
