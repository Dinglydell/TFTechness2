package dinglydell.tftechness.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import dinglydell.tftechness.TFTechness2;

//Highly experimental, mostly proof of concept at the moment
public class TFTWorldData extends WorldSavedData {

	private static final String DATA_NAME = TFTechness2.MODID + "_ClimateData";
	/** solar energy in (W m^-2) */
	protected static final int solarInput = 1361;
	protected static final int minLeaves = -100000;

	protected float greenhouseFactor = 0;
	protected float temperatureOffset = 0;
	protected int leafOffset = 0;

	public TFTWorldData() {
		super(DATA_NAME);
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		nbt.setFloat("Temperature", temperatureOffset);
		nbt.setFloat("Greenhouse", greenhouseFactor);
		nbt.setInteger("Trees", leafOffset);

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		temperatureOffset = nbt.getFloat("Temperature");
		greenhouseFactor = nbt.getFloat("Greenhouse");
		leafOffset = nbt.getInteger("Trees");
	}

	public float getTemperatureOffset() {
		return temperatureOffset;
	}

	public float getGreenhouseFactor() {
		return greenhouseFactor;
	}

	public int getNetTrees() {
		return leafOffset;
	}

	public static TFTWorldData get(World world) {
		MapStorage storage = world.perWorldStorage;
		TFTWorldData instance = (TFTWorldData) storage
				.loadData(TFTWorldData.class, DATA_NAME);

		if (instance == null) {
			instance = new TFTWorldData();
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

	public void tick(WorldTickEvent event) {

		temperatureOffset += (greenhouseFactor - temperatureOffset) * 0.0001f;
		int trees = Math.max(0, leafOffset - minLeaves);
		float treeAbsorbance = trees * 0.0001f;
		greenhouseFactor = Math.max(0, greenhouseFactor - treeAbsorbance);

	}

	public void emitGreenhouse(float amt) {
		greenhouseFactor += amt;
	}

	public float getAlbedo() {
		//max albedo 0.83 - value of snow
		//min albedo 0.01
		return Math.min(0.83f,
				Math.max(0.3f - 0.01f * temperatureOffset, 0.01f));
	}

	public void makeLeaf() {
		leafOffset++;
	}

	public void breakLeaf() {
		leafOffset--;
	}
}
