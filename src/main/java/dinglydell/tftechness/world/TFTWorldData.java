package dinglydell.tftechness.world;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraft.world.WorldSavedData;
import net.minecraft.world.storage.MapStorage;
import net.minecraftforge.event.terraingen.SaplingGrowTreeEvent;
import zmaster587.advancedRocketry.api.AdvancedRocketryAPI;
import zmaster587.advancedRocketry.dimension.DimensionProperties;
import cpw.mods.fml.common.gameevent.TickEvent.WorldTickEvent;
import dinglydell.tftechness.TFTechness2;

//Highly experimental, mostly proof of concept at the moment
public class TFTWorldData extends WorldSavedData {

	private static final String DATA_NAME = TFTechness2.MODID + "_ClimateData";
	/** solar energy in * cross-sec area of world (W) */
	protected static final long solarInput = (long) (1361 * 1.275e14);
	protected static final int minLeaves = -100000;
	/** Average temperature of the world with no warming effects */
	public static final int baseTemperature = 288;
	/**
	 * passive greenhouse gas emissions from natural causes - required to
	 * maintain equilibrium. This is equal to the default amount absorbed by
	 * leaves
	 * */
	protected static final int baseGreenhouseEmission = 1;

	/**
	 * constant / greenhouseFactor is multiplied to energy leaving the system to
	 * work out how much is re-emitted
	 */
	protected static final float greenhouseAbsorbanceConstant = 8.351695636635798f;
	/** stefan-boltzmann constant * surface area of world */
	protected static final int stefAreaConstant = (int) 2.9e7;
	/** energy multiplied by this to be converted to temperature */
	protected static final float energyTemperatureConstant = 1e-17f;
	/** amount of CO2 (ppm) absorbed by each leaf per tick */
	protected static final float leafAbsorbanceConstant = 0.00001f;
	private static final int SUN_RADIUS = 695508000;
	private static final float stefanBoltzConstant = 5.67e-8f;
	private static final float EARTH_RADIUS = 6300000;
	private static final float EARTH_ESCAPE = 11186;
	/** atmospheric density is multiplied by this to get atmospheric albedo */
	private static final float atmosAlbedoConstant = 0.0004f;
	/** How the albedo from water in the atmosphere scales with temperature */
	private static final float waterInAtmosScale = (0.208f / 30);
	private static final int SUN_TEMP = 5800;
	/** used to convert AR distance-to-sun into meters */
	private static final float distanceConstant = 20245874168.102461820689347048944f;

	/** roughly equivalent to concentration of CO2 in PPM */
	protected float greenhouseFactor = 180;
	protected float temperatureOffset = 0;
	protected int leafOffset = 0;

	protected World world;
	protected DimensionProperties dimProps;
	//private float sunIntensity;
	private float planetSurfaceArea;
	//private float planetCrossArea;
	private float sunInput;
	/** stefan-boltzmann constant * surface area of planet */
	private float stefArea;

	//private static float getTemp0(World world, int day, int hour, int x, int z,
	//		boolean bio) {
	//	return TFT_Climate.;
	//}

	public TFTWorldData(World world) {
		super(DATA_NAME);
		this.world = world;
		//if (world.provider.dimensionId != 0) {
		//not the overworld!
		this.dimProps = (DimensionProperties) AdvancedRocketryAPI.dimensionManager
				.getDimensionProperties(world.provider.dimensionId);
		temperatureOffset = dimProps.averageTemperature * 2.88f
				- baseTemperature;
		//greenhouseFactor = dimProps.getAtmosphereDensity() * 2;
		//if(Temps.getTempFromValue(dimProps.averageTemperature) == Temps.TOOHOT){

		TFTechness2.logger.info("[" + dimProps.getName() + " - rotation ]: "
				+ dimProps.rotationalPhi);

		float sunR = SUN_RADIUS
				* (dimProps.getStar().getDisplayRadius() / 200f);
		float sunSA = sunR * sunR;
		float sunTemp = SUN_TEMP * dimProps.getStar().getTemperature() / 100;
		float sunPower = stefanBoltzConstant * sunSA * sunTemp * sunTemp
				* sunTemp * sunTemp;
		float distToSun = (float) Math.pow(Math.E,
				dimProps.getSolarOrbitalDistance() * 0.02f)
				* distanceConstant;
		float sunIntensity = sunPower / (distToSun * distToSun);
		// radius of planet assumes all planets are of equal density
		float radius = dimProps.getGravitationalMultiplier() * EARTH_RADIUS;

		//float sunInput = getAlbedo() * sunIntensity * (float) Math.PI * radius * radius;

		float planetCrossArea = (float) Math.PI * radius * radius;
		stefArea = stefanBoltzConstant * 4 * planetCrossArea;

		sunInput = planetCrossArea * sunIntensity;
		// calculate amount of greenhouse gas required to keep the thing in equilibrium
		// g = (c * AoT^4 / Ein) ^ 2
		greenhouseFactor = greenhouseAbsorbanceConstant * 4
				* stefanBoltzConstant
				* (float) Math.pow(temperatureOffset + baseTemperature, 4)
				/ ((1 - getAlbedo()) * sunIntensity);
		greenhouseFactor *= greenhouseFactor;
		//}
		//}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		temperatureOffset = nbt.getFloat("Temperature");
		greenhouseFactor = nbt.getFloat("Greenhouse");
		leafOffset = nbt.getInteger("Trees");

	}

	@Override
	public void writeToNBT(NBTTagCompound nbt) {
		nbt.setFloat("Temperature", temperatureOffset);
		nbt.setFloat("Greenhouse", greenhouseFactor);
		nbt.setInteger("Trees", leafOffset);
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

	/** Returns the absolute temperature in kelvin */
	public float getAbsoluteTemperature() {
		return temperatureOffset + baseTemperature;
	}

	/** Return the temperature in degrees */
	public float getTemperature() {
		return temperatureOffset + baseTemperature - 273;
	}

	public static TFTWorldData get(World world) {
		MapStorage storage = world.perWorldStorage;
		TFTWorldData instance = (TFTWorldData) storage
				.loadData(TFTWorldData.class, DATA_NAME);

		if (instance == null) {
			instance = new TFTWorldData(world);
			storage.setData(DATA_NAME, instance);
		}
		return instance;
	}

	public void tick(WorldTickEvent event) {
		float energyIn = this.sunInput * (1 - getAlbedo());
		// P = AoT^4
		//temperatureOffset = 0;
		float energyOut = (float) ((float) Math.pow(temperatureOffset
				+ baseTemperature, 4)
				* stefArea * greenhouseAbsorbanceConstant / Math
				.max(greenhouseAbsorbanceConstant,
						(float) Math.sqrt(greenhouseFactor)));
		temperatureOffset = Math.max(-baseTemperature, temperatureOffset
				+ energyTemperatureConstant * (energyIn - energyOut));
		if (world.provider.dimensionId != -1 && world.provider.dimensionId != 1) {
			//	TFTechness2.logger.info("[" + dimProps.getName() + "]: "
			//		+ temperatureOffset);
		}
		//todo: something slightly more complex here such that breaking 1 leaf doesn't permanently upset the equilibrium
		int trees = Math.max(0, leafOffset - minLeaves);
		float treeAbsorbance = trees * leafAbsorbanceConstant;
		greenhouseFactor += baseGreenhouseEmission - treeAbsorbance;
		this.markDirty();
	}

	public void emitGreenhouse(float amt) {
		greenhouseFactor += amt;
	}

	public float getAlbedo() {

		float atmosAlbedo = dimProps.getAtmosphereDensity()
				* atmosAlbedoConstant;
		float waterInAtmosAlbedo = 0f;
		//float maxTemp =
		if (temperatureOffset > -30 && temperatureOffset < 100) {
			waterInAtmosAlbedo = 0.208f + temperatureOffset * waterInAtmosScale;
		}
		float groundAlbedo = Math.max(0.04f,
				Math.min(0.83f, 0.0789f - 0.001f * temperatureOffset));
		//first go through atmos, then water, then ground
		float inverseAlbedo = 1 - atmosAlbedo;
		inverseAlbedo *= 1 - waterInAtmosAlbedo;
		inverseAlbedo *= 1 - groundAlbedo;
		return Math.min(1f, Math.max(1 - inverseAlbedo, 0f));
	}

	public boolean isEarth() {
		return world.provider.dimensionId == 0;
	}

	public void makeLeaf() {
		leafOffset++;
	}

	public void breakLeaf() {
		leafOffset--;

	}

	public void makeTree(SaplingGrowTreeEvent event) {
		//TODO: you can do better than this
		leafOffset += 32;

	}
}
