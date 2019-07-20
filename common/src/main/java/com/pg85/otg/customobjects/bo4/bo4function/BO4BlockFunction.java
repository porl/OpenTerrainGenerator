package com.pg85.otg.customobjects.bo4.bo4function;

import java.io.DataInputStream;
import java.io.DataOutput;
import java.io.File;
import java.io.IOException;

import com.pg85.otg.customobjects.bo3.BO3Loader;
import com.pg85.otg.customobjects.bo4.BO4Config;
import com.pg85.otg.customobjects.bofunctions.BlockFunction;
import com.pg85.otg.customobjects.structures.bo4.BO4CustomStructureCoordinate;
import com.pg85.otg.exception.InvalidConfigException;
import com.pg85.otg.util.bo3.Rotation;
import com.pg85.otg.util.helpers.MaterialHelper;
import com.pg85.otg.util.helpers.StreamHelper;

/**
 * Represents a block in a BO3.
 */
public class BO4BlockFunction extends BlockFunction<BO4Config>
{
    public BO4BlockFunction() { }
	
    public BO4BlockFunction(BO4Config holder)
    {
    	this.holder = holder;
    }
	
    public BO4BlockFunction rotate(Rotation rotation)
    {
        BO4BlockFunction rotatedBlock = new BO4BlockFunction(this.getHolder());

        rotatedBlock.material = material; // TODO: Make sure this won't cause problems

        BO4CustomStructureCoordinate rotatedCoords = BO4CustomStructureCoordinate.getRotatedBO3CoordsJustified(x, y, z, rotation);

        rotatedBlock.x = rotatedCoords.getX();
        rotatedBlock.y = rotatedCoords.getY();
        rotatedBlock.z = rotatedCoords.getZ();

    	// TODO: This makes no sense, why is rotation inverted??? Should be: NORTH:0,WEST:1,SOUTH:2,EAST:3

        // Apply rotation
    	if(rotation.getRotationId() == 3)
    	{
    		rotatedBlock.material = rotatedBlock.material.rotate(1);
    	}
    	if(rotation.getRotationId() == 2)
    	{
    		rotatedBlock.material = rotatedBlock.material.rotate(2);
    	}
    	if(rotation.getRotationId() == 1)
    	{
    		rotatedBlock.material = rotatedBlock.material.rotate(3);
    	}

        rotatedBlock.metaDataTag = metaDataTag;
        rotatedBlock.metaDataName = metaDataName;

        return rotatedBlock;
    }   
    
    @Override
    public Class<BO4Config> getHolderType()
    {
        return BO4Config.class;
    }
    
    public void writeToStream(DataOutput stream) throws IOException
    {
        stream.writeByte(this.x);
        stream.writeShort(this.y);
        stream.writeByte(this.z);       
        StreamHelper.writeStringToStream(stream, this.material.getName());
        StreamHelper.writeStringToStream(stream, this.metaDataName);
    }
    
    public static BO4BlockFunction fromStream(BO4Config holder, DataInputStream stream) throws IOException
    {
    	BO4BlockFunction rbf = new BO4BlockFunction(holder);
    	
    	File file = holder.getFile();
    	   	
    	rbf.x = stream.readByte();
    	rbf.y = stream.readShort();
    	rbf.z = stream.readByte();
    	
    	try {
			rbf.material = MaterialHelper.readMaterial(StreamHelper.readStringFromStream(stream));
		}
    	catch (InvalidConfigException e) { }
    	    	
    	rbf.metaDataName = StreamHelper.readStringFromStream(stream);
    	
    	if(rbf.metaDataName != null)
    	{
	        // Get the file
        	rbf.metaDataTag = BO3Loader.loadMetadata(rbf.metaDataName, file);
        	if(rbf.metaDataTag == null)
        	{
        		rbf.metaDataName = null;
        	}
        }
    	
    	return rbf;
    }
}