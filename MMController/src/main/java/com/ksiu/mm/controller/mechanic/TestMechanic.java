package com.ksiu.mm.controller.mechanic;

import io.lumine.mythic.api.MythicPlugin;
import io.lumine.mythic.api.MythicProvider;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.ITargetedEntitySkill;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.LivingEntity;

public class TestMechanic extends BaseTargetEntitySkill
{
    public TestMechanic(MythicLineConfig config)
    {

    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity target)
    {
        SkillCaster caster = skillMetadata.getCaster(); //스킬 사용한 몹
        LivingEntity bukkitTarget = (LivingEntity) BukkitAdapter.adapt(target); //넘어온 대상을 버킷 엔티티로 변환
        bukkitTarget.sendMessage(String.format("%s: 테스트", caster.getName()));
        return SkillResult.SUCCESS;
    }
}
