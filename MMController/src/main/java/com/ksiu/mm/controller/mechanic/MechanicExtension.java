package com.ksiu.mm.controller.mechanic;

import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.bukkit.BukkitAdapter;
import org.bukkit.entity.LivingEntity;

public class MechanicExtension
{
    private MechanicExtension()
    {

    }

    public static LivingEntity castToLivingEntity(AbstractEntity entity)
    {
        return (LivingEntity) BukkitAdapter.adapt(entity);
    }

    public static AbstractEntity castToAbstractEntity(LivingEntity entity)
    {
        return BukkitAdapter.adapt(entity);
    }

}
