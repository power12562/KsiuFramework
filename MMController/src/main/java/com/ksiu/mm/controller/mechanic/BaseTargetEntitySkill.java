package com.ksiu.mm.controller.mechanic;

import com.ksiu.mm.controller.MMController;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Bukkit;

import java.util.logging.Logger;

public abstract class BaseTargetEntitySkill implements ITargetedEntitySkill
{
    protected final MMController _plugin;
    protected final MythicBukkit _mythicBukkit;
    protected final Logger _logger;

    protected BaseTargetEntitySkill()
    {
        this._plugin = MMController.getPlugin(MMController.class);
        this._logger = MMController.getPluginLogger();
        this._mythicBukkit = MythicBukkit.inst();
        if (_mythicBukkit == null)
        {
            _logger.warning("MythicPlugin is null.");
        }
    }

    protected void RunSchedule(Runnable task) throws IllegalArgumentException
    {
        Bukkit.getScheduler().runTask(_plugin, task);
    }

    public abstract SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity target);
}
