package com.ksiu.mm.controller.mechanic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractPlayer;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;

public class TestSkill implements ITargetedEntitySkill
{
    public TestSkill(MythicLineConfig config)
    {

    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity abstractEntity)
    {
        AbstractPlayer player = abstractEntity.asPlayer();
        player.sendMessage("너 뭐임");
        return SkillResult.SUCCESS;
    }
}
