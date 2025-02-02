package com.pg85.otg.generator.resource;

import com.pg85.otg.common.LocalMaterialData;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.configuration.standard.PluginStandardValues;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

public class WellGen extends Resource
{
    private final int maxAltitude;
    private final int minAltitude;
    private final LocalMaterialData slab;
    private final LocalMaterialData water;
    private final MaterialSet sourceBlocks;

    public WellGen(BiomeConfig biomeConfig, List<String> args) throws InvalidConfigException
    {
        super(biomeConfig);
        assureSize(8, args);

        material = readMaterial(args.get(0));
        slab = readMaterial(args.get(1));
        water = readMaterial(args.get(2));
        frequency = readInt(args.get(3), 1, 100);
        rarity = readRarity(args.get(4));
        minAltitude = readInt(args.get(5), PluginStandardValues.WORLD_DEPTH, PluginStandardValues.WORLD_HEIGHT);
        maxAltitude = readInt(args.get(6), minAltitude + 1, PluginStandardValues.WORLD_HEIGHT);
        sourceBlocks = readMaterials(args, 7);
    }

    @Override
    public boolean equals(Object other)
    {
        if (!super.equals(other))
            return false;
        if (other == null)
            return false;
        if (other == this)
            return true;
        if (getClass() != other.getClass())
            return false;
        final WellGen compare = (WellGen) other;
        return this.maxAltitude == compare.maxAltitude
               && this.minAltitude == compare.minAltitude
               && this.slab.equals(compare.slab)
               && this.sourceBlocks.equals(compare.sourceBlocks)
               && this.water.equals(compare.water);
    }

    @Override
    public int getPriority()
    {
        return 0;
    }

    @Override
    public int hashCode()
    {
        int hash = 3;
        hash = 17 * hash + super.hashCode();
        hash = 17 * hash + this.minAltitude;
        hash = 17 * hash + this.maxAltitude;
        hash = 17 * hash + this.slab.hashCode();
        hash = 17 * hash + this.water.hashCode();
        hash = 17 * hash + this.sourceBlocks.hashCode();
        return hash;
    }

    @Override
    public String toString()
    {
        String output = "Well(" + material + "," + slab + "," + water + ",";
        output += frequency + "," + rarity + "," + minAltitude + "," + maxAltitude + this.makeMaterials(sourceBlocks) + ")";
        return output;
    }

    @Override
    public void spawn(LocalWorld world, Random random, boolean villageInChunk, int x, int z)
    {
        int y = random.nextInt(maxAltitude - minAltitude) + minAltitude;

        while (world.isNullOrAir(x, y, z, false) && y > minAltitude)
        {
            --y;
        }

        LocalMaterialData sourceBlock = world.getMaterial(x, y, z, false);

        if (!sourceBlocks.contains(sourceBlock))
        {
            return;
        }
        int i;
        int j;

        for (i = -2; i <= 2; ++i)
        {
            for (j = -2; j <= 2; ++j)
            {
                if (world.isNullOrAir(x + i, y - 1, z + j, false) && world.isNullOrAir(x + i, y - 2, z + j, false))
                {
                    return;
                }
            }
        }

        for (i = -1; i <= 0; ++i)
        {
            for (j = -2; j <= 2; ++j)
            {
                for (int var9 = -2; var9 <= 2; ++var9)
                {
                    world.setBlock(x + j, y + i, z + var9, material, null, false);
                }
            }
        }

        world.setBlock(x, y, z, water, null, false);
        world.setBlock(x - 1, y, z, water, null, false);
        world.setBlock(x + 1, y, z, water, null, false);
        world.setBlock(x, y, z - 1, water, null, false);
        world.setBlock(x, y, z + 1, water, null, false);

        for (i = -2; i <= 2; ++i)
        {
            for (j = -2; j <= 2; ++j)
            {
                if (i == -2 || i == 2 || j == -2 || j == 2)
                {
                    world.setBlock(x + i, y + 1, z + j, material, null, false);
                }
            }
        }

        world.setBlock(x + 2, y + 1, z, slab, null, false);
        world.setBlock(x - 2, y + 1, z, slab, null, false);
        world.setBlock(x, y + 1, z + 2, slab, null, false);
        world.setBlock(x, y + 1, z - 2, slab, null, false);

        for (i = -1; i <= 1; ++i)
        {
            for (j = -1; j <= 1; ++j)
            {
                if (i == 0 && j == 0)
                {
                    world.setBlock(x + i, y + 4, z + j, material, null, false);
                } else
                {
                    world.setBlock(x + i, y + 4, z + j, slab, null, false);
                }
            }
        }

        for (i = 1; i <= 3; ++i)
        {
            world.setBlock(x - 1, y + i, z - 1, material, null, false);
            world.setBlock(x - 1, y + i, z + 1, material, null, false);
            world.setBlock(x + 1, y + i, z - 1, material, null, false);
            world.setBlock(x + 1, y + i, z + 1, material, null, false);
        }
    }
}