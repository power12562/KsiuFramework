package com.ksiu.mm.controller.mechanic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.config.MythicLineConfig;
import io.lumine.mythic.api.skills.SkillCaster;
import io.lumine.mythic.api.skills.SkillMetadata;
import io.lumine.mythic.api.skills.SkillResult;
import io.lumine.mythic.api.volatilecode.handlers.VolatileAIHandler;
import org.bukkit.entity.LivingEntity;

public class MoveToTargetMechanic extends BaseTargetEntitySkill
{
    private final VolatileAIHandler _aiHandler;
    private final long _interval;
    private final double _speed;
    private LivingEntity _livingTarget;
    private LivingEntity _livingCaster;

    public MoveToTargetMechanic(MythicLineConfig config)
    {
        this._aiHandler = _mythicBukkit.getVolatileCodeHandler().getAIHandler();
        this._interval = Math.max(5, config.getLong(new String[]{"interval", "i"}, 20L));
        this._speed = Math.max(0.5, config.getDouble(new String[]{"speed", "s"}, 1.0));

        // _logger.info("MoveToTargetMechanic._interval: " + _interval);
        // _logger.info("MoveToTargetMechanic._speed: " + _speed);
    }

    @Override
    public SkillResult castAtEntity(SkillMetadata skillMetadata, AbstractEntity target)
    {
        try
        {
            SkillCaster skillCaster = skillMetadata.getCaster();
            AbstractEntity abstractCaster = skillCaster.getEntity();
            _livingCaster = MechanicExtension.castToLivingEntity(abstractCaster);
            _livingTarget = MechanicExtension.castToLivingEntity(target);
            executeGotoTargetMechanic();

            return SkillResult.SUCCESS;
        }
        catch (Exception ex)
        {
            _logger.warning("MoveToTargetMechanic: " + ex.toString());
            return SkillResult.ERROR;
        }
    }

    private void executeGotoTargetMechanic() throws Exception
    {
        AbstractEntity caster = MechanicExtension.castToAbstractEntity(_livingCaster);
        AbstractEntity target = MechanicExtension.castToAbstractEntity(_livingTarget);
        AbstractLocation targetLocation = target.getLocation();
        long targetX = Math.round(targetLocation.getX());
        long targetY = Math.round(targetLocation.getY());
        long targetZ = Math.round(targetLocation.getZ());
        RunSchedule(() -> _aiHandler.navigateToLocation(caster, targetLocation, _speed));

        // _livingTarget.sendMessage(_livingCaster.getName() + String.format(": 당신의 위치는 [%d, %d, %d]", targetX, targetY, targetZ));
    }

}
