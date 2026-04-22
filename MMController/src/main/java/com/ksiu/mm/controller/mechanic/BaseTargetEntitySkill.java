package com.ksiu.mm.controller.mechanic;

import com.ksiu.mm.controller.MMController;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.MythicBukkit;

import java.util.logging.Logger;

public abstract class BaseTargetEntitySkill implements ITargetedEntitySkill
{
    protected final MythicBukkit _mythicBukkit;
    protected final Logger _logger;
    protected BaseTargetEntitySkill()
    {
        this._logger = MMController.getPluginLogger();
        this._mythicBukkit = MythicBukkit.inst();
        if (_mythicBukkit == null)
        {
            _logger.warning("MythicPlugin is null.");
        }
    }

    public abstract SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity target);
}
