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
import java.util.logging.Logger;
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
        commandList.put(new DestroyCommand(owner));
        return commandList;
    }

    private static void generateMaze(List<Pair<Location, Material>> maze, World world, Material material, double cx, double cy, double cz, int width, int height, int length, int pathWidth)
            throws IllegalArgumentException
    {
        int unit = pathWidth + 1;
        int validWidth = ((width - 1) / unit) * unit + 1;
        int validLength = ((length - 1) / unit) * unit + 1;

        if (validWidth < unit + 1 || validLength < unit + 1)
        {
            throw new IllegalArgumentException("공간이 너무 좁습니다. 최소 크기: " + (unit + 1));
        }

        boolean[][] isWall = new boolean[width + 1][length + 1];
        for (int x = 1; x <= width; x++)
        {
            for (int z = 1; z <= length; z++)
            {
                isWall[x][z] = true;
            }
        }

        dfsMaze(isWall, 2, 2, validWidth - 1, validLength - 1, pathWidth);

        // 입구
        for (int i = 0; i < pathWidth; i++)
        {
            for (int z = 1; z <= 2; z++)
            {
                isWall[2 + i][z] = false;
            }
        }
        // 출구
        int lastGridX = ((validWidth - 1) / unit - 1) * unit + 2;
        int lastGridZ = ((validLength - 1) / unit - 1) * unit + 2;
        for (int i = 0; i < pathWidth; i++)
        {
            for (int z = lastGridZ; z <= validLength; z++)
            {
                isWall[lastGridX + i][z] = false;
            }
        }

        for (int x = 1; x <= width; x++)
        {
            for (int y = 1; y <= height; y++)
            {
                for (int z = 1; z <= length; z++)
                {
                    Location targetLoc = new Location(world, cx + x, cy + y, cz + z);
                    if (y == 1 || y == height || isWall[x][z])
                    {
                        maze.add(Pair.of(targetLoc, material));
                    }
                    else
                    {
                        maze.add(Pair.of(targetLoc, Material.AIR));
                    }
                }
            }
        }
    }

    private static void dfsMaze(boolean[][] isWall, int x, int z, int maxW, int maxL, int pWidth)
    {
        isWall[x][z] = false;

        Integer[] localDirs = {0, 1, 2, 3};
        java.util.Collections.shuffle(java.util.Arrays.asList(localDirs));

        for (int dir : localDirs)
        {
            int move = pWidth + 1;
            int nx = x, nz = z;

            if (dir == 0)
                nx += move;
            else if (dir == 1)
                nx -= move;
            else if (dir == 2)
                nz += move;
            else if (dir == 3)
                nz -= move;

            if (nx > 1 && nx + pWidth - 1 <= maxW && nz > 1 && nz + pWidth - 1 <= maxL && isWall[nx][nz])
            {
                for (int i = 1; i <= move; i++)
                {
                    int ix = x + (nx - x) * i / move;
                    int iz = z + (nz - z) * i / move;

                    for (int dx = 0; dx < pWidth; dx++)
                    {
                        for (int dz = 0; dz < pWidth; dz++)
                        {
                            if (ix + dx <= maxW && iz + dz <= maxL)
                            {
                                isWall[ix + dx][iz + dz] = false;
                            }
                        }
                    }
                }
                dfsMaze(isWall, nx, nz, maxW, maxL, pWidth);
            }
        }
    }

    private static void destroyMaze(List<Pair<Location, Material>> maze, World world, double cx, double cy, double cz, int width, int height, int length)
    {
        for (int x = 1; x <= width; x++)
        {
            for (int y = 1; y <= height; y++)
            {
                for (int z = 1; z <= length; z++)
                {
                    Location targetLoc = new Location(world, cx + x, cy + y, cz + z);
                    maze.add(Pair.of(targetLoc, Material.AIR));
                }
            }
        }
    }

    private static void BlocksRunnable(List<Pair<Location, Material>> blocks, JavaPlugin plugin, Runnable complete) throws IllegalArgumentException, IllegalStateException
    {
        new BukkitRunnable()
        {
            private int index = 0;
            private final int total = blocks.size();

            @Override
            public void run()
            {
                long startTime = System.currentTimeMillis();
                while (index < total && (System.currentTimeMillis() - startTime) < 5)
                {
                    for (int i = 0; i < 2000 && index < total; i++)
                    {
                        Pair<Location, Material> pair = blocks.get(index++);
                        pair.first().getBlock().setType(pair.second());
                    }
                }

                if (index >= total)
                {
                    complete.run();
                    this.cancel();
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    public static class GenerateCommand extends CommandBase
    {
        private final JavaPlugin _owner;
        private final Logger _logger;
        private volatile boolean _isGenerating = false;

        public GenerateCommand(JavaPlugin owner)
        {
            super("generate", "미로를 생성합니다.");
            _owner = owner;
            _logger = _owner.getLogger();
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args)
        {
            if (_isGenerating)
            {
                String message = "이미 다른 생성 작업이 진행 중입니다. 잠시만 기다려주세요.";
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
                _logger.info(message);
                return true;
            }

            // 인자: /generate <x> <y> <z> <rx> <ry> <rz> <block> [pathWidth] (총 8개)
            if (args.length < 7)
            {
                String message = "generate <x> <y> <z> <rx> <ry> <rz> <block> [pathWidth]";
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
                _logger.info(message);
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

                int width = Integer.parseInt(args[3]);
                int height = Integer.parseInt(args[4]);
                int length = Integer.parseInt(args[5]);

                Material material = Material.matchMaterial(args[6].toUpperCase());
                if (material == null || !material.isBlock())
                {
                    final String message = "존재하지 않거나 설치 불가능한 블록입니다." + args[6];
                    sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
                    _logger.info(message);
                    return true;
                }

                int pathWidth = Integer.max(args.length >= 8 ? Integer.parseInt(args[7]) : 1, 1);
                final List<Pair<Location, Material>> tasks = new ArrayList<>();
                MazeCommands.generateMaze(tasks, world, material, cx, cy, cz, width, height, length, pathWidth);

                final String generateMessage = String.format("크기[%d], 위치[%.1f, %.1f, %.1f]에 미로를 생성합니다...", tasks.size(), cx, cy, cz);
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(generateMessage).build());
                _logger.info(generateMessage);
                _isGenerating = true;
                BlocksRunnable(tasks, _owner, () ->
                {
                    String message = "미로 생성 완료!";
                    sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
                    _logger.info(message);
                    _isGenerating = false;
                });
            }
            catch (NumberFormatException e)
            {
                _isGenerating = false;
                String message = "좌표와 지름은 숫자여야 합니다.";
                sender.sendMessage(KsiuCore.getErrorTextBuilder().append(message).build());
                _logger.warning(message);
            }
            catch (Exception e)
            {
                _isGenerating = false;
                String message = e.toString();
                sender.sendMessage(KsiuCore.getErrorTextBuilder().append(message).build());
                _logger.warning(message);
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

            if (args.length == 8)
            {
                return Arrays.asList("1", "2", "3");
            }

            return Collections.emptyList();
        }
    }

    public static class DestroyCommand extends CommandBase
    {
        private final JavaPlugin _owner;
        private final Logger _logger;
        private volatile boolean _isGenerating = false;

        public DestroyCommand(JavaPlugin owner)
        {
            super("destroy", "미로를 파괴합니다.");
            _owner = owner;
            _logger = _owner.getLogger();
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args)
        {
            if (_isGenerating)
            {
                String message = "이미 다른 생성 작업이 진행 중입니다. 잠시만 기다려주세요.";
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
                _logger.info(message);
                return true;
            }

            // 인자: /destroy <x> <y> <z> <rx> <ry> <rz> (총 6개)
            if (args.length < 6)
            {
                String message = "destroy <x> <y> <z> <rx> <ry> <rz>";
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
                _logger.info(message);
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

                int width = Integer.parseInt(args[3]);
                int height = Integer.parseInt(args[4]);
                int length = Integer.parseInt(args[5]);

                final List<Pair<Location, Material>> tasks = new ArrayList<>();
                MazeCommands.destroyMaze(tasks, world, cx, cy, cz, width, height, length);
                MazeCommands.BlocksRunnable(tasks, _owner, () ->
                {
                    String message = "미로 파괴 완료!";
                    sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(message).build());
                    _logger.info(message);
                    _isGenerating = false;
                });

                final String destroyMessage = String.format("크기[%d], 위치[%.1f, %.1f, %.1f]에 미로를 파괴합니다...", tasks.size(), cx, cy, cz);
                sender.sendMessage(KsiuCore.getPrefixTextBuilder().append(destroyMessage).build());
                _logger.info(destroyMessage);
                _isGenerating = true;
            }
            catch (NumberFormatException e)
            {
                _isGenerating = false;
                String message = "좌표와 지름은 숫자여야 합니다.";
                sender.sendMessage(KsiuCore.getErrorTextBuilder().append(message).build());
                _logger.warning(message);
            }
            catch (Exception e)
            {
                _isGenerating = false;
                String message = e.toString();
                sender.sendMessage(KsiuCore.getErrorTextBuilder().append(message).build());
                _logger.warning(message);
            }
            return true;
        }

        @Override
        public List<String> onTabComplete(CommandSender sender, String[] args)
        {
            if (args.length >= 1 && args.length <= 3)
                return Collections.singletonList("~");

            return Collections.emptyList();
        }


    }

}
