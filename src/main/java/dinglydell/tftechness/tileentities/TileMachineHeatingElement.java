package dinglydell.tftechness.tileentities;

public class TileMachineHeatingElement extends TileMachineComponent {

	public TileMachineHeatingElement() {
		super();
	}

	@Override
	public void updateEntity() {

		super.updateEntity();
		if (!worldObj.isRemote) {
			//TFTechness2.logger.info(temperature);
			temperature += 5f;

		}
	}

}
