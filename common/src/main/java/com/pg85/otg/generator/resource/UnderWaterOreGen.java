package com.pg85.otg.generator.resource;

import com.pg85.otg.common.LocalMaterialData;
import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.configuration.biome.BiomeConfig;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.materials.MaterialSet;

import java.util.List;
import java.util.Random;

public class UnderWaterOreGen extends Resource
{
    private final int size;
    private final MaterialSet sourceBlocks;

    public UnderWaterOreGen(BiomeConfig biomeConfig, List<String> args) throws InvalidConfigException
    {
        super(biomeConfig);
        assureSize(5, args);
        material = readMaterial(args.get(0));
        size = readInt(args.get(1), 1, 8);
        frequency = readInt(args.get(2), 1, 100);
        rarity = readRarity(args.get(3));
        sourceBlocks = readMaterials(args, 4);
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
        final UnderWaterOreGen compare = (UnderWaterOreGen) other;
        return (this.sourceBlocks == null ? this.sourceBlocks == compare.sourceBlocks
                : this.sourceBlocks.equals(compare.sourceBlocks))
               && this.size == compare.size;
    }

    @Override
    public int getPriority()
    {
        return -12;
    }

    @Override
    public int hashCode()
    {
        int hash = 5;
        hash = 47 * hash + super.hashCode();
        hash = 47 * hash + (this.sourceBlocks != null ? this.sourceBlocks.hashCode() : 0);
        hash = 47 * hash + this.size;
        return hash;
    }

    @Override
    public String toString()
    {
        return "UnderWaterOre(" + material + "," + size + "," + frequency + "," + rarity + makeMaterials(sourceBlocks) + ")";
    }

    @Override
    public void spawn(LocalWorld world, Random rand, boolean villageInChunk, int x, int z)
    {
        int firstSolidBlock = world.getSolidHeight(x, z) - 1;
        if (world.getLiquidHeight(x, z) < firstSolidBlock || firstSolidBlock == -1)
        {
            return;
        }

        int currentSize = rand.nextInt(size);
        int two = 2;
        for (int currentX = x - currentSize; currentX <= x + currentSize; currentX++)
        {
            for (int currentZ = z - currentSize; currentZ <= z + currentSize; currentZ++)
            {
                int deltaX = currentX - x;
                int deltaZ = currentZ - z;
                if (deltaX * deltaX + deltaZ * deltaZ <= currentSize * currentSize)
                {
                    for (int y = firstSolidBlock - two; y <= firstSolidBlock + two; y++)
                    {
                        LocalMaterialData sourceBlock = world.getMaterial(currentX, y, currentZ, false);
                        if (sourceBlocks.contains(sourceBlock))
                        {
                            world.setBlock(currentX, y, currentZ, material, null, false);
                        }
                    }
                }
            }
        }
    }

}
