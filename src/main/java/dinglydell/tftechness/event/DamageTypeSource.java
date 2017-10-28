package dinglydell.tftechness.event;

import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;
import net.minecraft.world.World;

import com.bioxx.tfc.api.Enums.EnumDamageType;
import com.bioxx.tfc.api.Interfaces.ICausesDamage;

public class DamageTypeSource extends Entity implements ICausesDamage {

	private EnumDamageType type;

	public DamageTypeSource(World world, EnumDamageType type) {
		super(world);
		DamageSource s;
		this.type = type;
	}

	@Override
	public EnumDamageType getDamageType() {
		return type;
	}

	@Override
	protected void entityInit() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void readEntityFromNBT(NBTTagCompound p_70037_1_) {
		// TODO Auto-generated method stub

	}

	@Override
	protected void writeEntityToNBT(NBTTagCompound p_70014_1_) {
		// TODO Auto-generated method stub

	}

}
