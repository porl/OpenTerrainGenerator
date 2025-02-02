package com.pg85.otg.customobjects.bo3.bo3function;

import com.pg85.otg.common.LocalWorld;
import com.pg85.otg.customobjects.bo3.BO3Config;
import com.pg85.otg.customobjects.bofunctions.BranchFunction;
import com.pg85.otg.customobjects.bofunctions.BranchNode;
import com.pg85.otg.customobjects.structures.CustomStructureCoordinate;
import com.pg85.otg.customobjects.structures.bo3.BO3CustomStructureCoordinate;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.bo3.Rotation;

import java.util.*;

/**
 * Represents the Branch(..) function in the BO3 files.
 *
 */
public class BO3BranchFunction extends BranchFunction<BO3Config>
{	
    public BO3BranchFunction rotate()
    {
        BO3BranchFunction rotatedBranch = new BO3BranchFunction();
        rotatedBranch.x = z - 1;
        rotatedBranch.y = y;
        rotatedBranch.z = -x;
        rotatedBranch.branches = new TreeSet<BranchNode>();
        rotatedBranch.totalChance = totalChance;
        rotatedBranch.totalChanceSet = totalChanceSet;
        for (BranchNode holder : this.branches)
        {
            rotatedBranch.branches.add(new BranchNode(holder.getRotation().next(), holder.getChance(), holder.getCustomObject(false, null), holder.customObjectName));
        }
        return rotatedBranch;
    }
	
    // TODO: accumulateChances is only used for weightedbranches, remove from this class (will affect loading..).
	@Override
    protected double readArgs(List<String> args, boolean accumulateChances) throws InvalidConfigException
    {
        double cumulativeChance = 0;
        assureSize(6, args);
        x = readInt(args.get(0), -32, 32);
        y = readInt(args.get(1), -64, 64);
        z = readInt(args.get(2), -32, 32);
        int i;
        for (i = 3; i < args.size() - 2; i += 3)
        {
            double branchChance = readDouble(args.get(i + 2), 0, Double.MAX_VALUE);
            if (accumulateChances)
            {
                cumulativeChance += branchChance;
                // CustomObjects are inserted into the Set in ascending chance order with Chance being cumulative.
                branches.add(new BranchNode(Rotation.getRotation(args.get(i + 1)), cumulativeChance, null, args.get(i)));
            } else {
                branches.add(new BranchNode(Rotation.getRotation(args.get(i + 1)), branchChance, null, args.get(i)));
            }
        }
        if (i < args.size())
        {
        	totalChanceSet = true;
            totalChance = readDouble(args.get(i), 0, Double.MAX_VALUE);
        }
    	return cumulativeChance;
    }
	
    /**
     * This method iterates all the possible branches in this branchFunction object
     * and uses a random number and the branch's spawn chance to check if the branch
     * should spawn. Returns null if no branch passes the check.
     */
    @Override
    public CustomStructureCoordinate toCustomObjectCoordinate(LocalWorld world, Random random, Rotation rotation, int x, int y, int z, String startBO3Name)
    {
        for (Iterator<BranchNode> it = branches.iterator(); it.hasNext();)
        {
            BranchNode branch = it.next();

            double randomChance = random.nextDouble() * totalChance;
            if (randomChance < branch.getChance())
            {
                return new BO3CustomStructureCoordinate(world, branch.getCustomObject(false, world), branch.customObjectName, branch.getRotation(), x + this.x, (short)(y + this.y), z + this.z);
            }
        }
        return null;
    }

	@Override
	public Class<BO3Config> getHolderType()
	{
		return BO3Config.class;
	}
}
