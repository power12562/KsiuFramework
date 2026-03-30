package com.ksiu.gui;

import com.ksiu.core.KsiuCore;
import com.ksiu.core.commands.base.CommandBase;
import com.ksiu.core.commands.container.KsiuCommandList;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public final class KsiuGUI extends JavaPlugin
{
    public static String getVersion() { return "1.0.0"; }

    private final KsiuCommandList _commandList = new KsiuCommandList("gui");

    @Override
    public void onEnable()
    {
        KsiuCore _ksiuCore = KsiuCore.getInstance();
        if (_ksiuCore == null) {
            getLogger().severe("KsiuCore를 찾을 수 없습니다. GUI 플러그인을 비활성화합니다.");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getLogger().info("KsiuGUI 플러그인 활성화.");
        _ksiuCore.getCommandRouter().registerCommandBundle(_commandList.getModuleName(), _commandList);
        _commandList.put(new VersionCommand());
    }

    @Override
    public void onDisable()
    {
        getLogger().info("KsiuGUI 플러그인 비활성화.");
    }

    private static final class VersionCommand extends CommandBase
    {
        public VersionCommand()
        {
            super("version", "플러그인 버전입니다.");
        }

        @Override
        public boolean onCommand(CommandSender sender, String[] args)
        {
            sender.sendMessage(KsiuCore.getPrefixTextBuilder()
                    .append(Component.text(getVersion()))
                    .build());
            return true;
        }
    }
}
