package com.ksiu.better.mm.mechanic;

import com.ksiu.better.BetterNotice;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import org.bukkit.Bukkit;

public class BetterNoticeMechanic implements ITargetedEntitySkill
{
    private final BetterNotice _plugin;
    private final String _mobNotice;

    public BetterNoticeMechanic(BetterNotice plugin, MythicLineConfig config)
    {
        _plugin = plugin;
        this._mobNotice = config.getString(new String[]{"notice", "s", "n"}, "[name]");
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity)
    {
        String casterName = skillMetadata.getCaster().getName();
        String cmd = String.format("공지 %s", _mobNotice.replace("[name]", casterName));
        Bukkit.getScheduler().runTask(_plugin, () -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd));
        return SkillResult.SUCCESS;
    }
}
