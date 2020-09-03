package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IFluidRecipe;
import defeatedcrow.hac.api.recipe.IRecipePanel;
import defeatedcrow.hac.core.fluid.DCFluidUtil;
import defeatedcrow.hac.core.fluid.FluidDictionaryDC;
import defeatedcrow.hac.core.util.DCUtil;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;

public class FluidCraftRecipe implements IFluidRecipe {

	private final Object[] input;
	private final FluidStack inputF;
	private ArrayList<Object> processedInput = new ArrayList<Object>();
	private ArrayList<Object> inputList = new ArrayList<Object>();
	private final ItemStack output;
	private final FluidStack outputF;
	private final ItemStack secondary;
	private final float chance;
	private final boolean needCooling;
	private List<DCHeatTier> heat = new ArrayList<DCHeatTier>();
	private List<DCHumidity> hum = new ArrayList<DCHumidity>();
	private List<DCAirflow> air = new ArrayList<DCAirflow>();
	private String type = "";
	private static final ArrayList<Object> EMPTY = new ArrayList<Object>();
	private final int count;

	public FluidCraftRecipe(ItemStack o, ItemStack s, FluidStack oF, DCHeatTier t, DCHumidity h, DCAirflow a, float c,
			boolean cooling, FluidStack iF, Object... inputs) {
		input = inputs;
		inputF = iF;
		output = o;
		outputF = oF;
		secondary = s;
		chance = c;
		needCooling = cooling;
		if (t != null) {
			heat.add(t);
			if (t.getID() < DCHeatTier.INFERNO.getID()) {
				if (t.getID() == DCHeatTier.NORMAL.getID() || t.getID() == DCHeatTier.WARM.getID()) {
					heat.add(t.addTier(1));
					heat.add(t.addTier(-1));
				} else if (t.getID() > 0 && t.getID() < DCHeatTier.NORMAL.getID()) {
					heat.add(t.addTier(-1));
				} else if (t.getID() > DCHeatTier.WARM.getID()) {
					heat.add(t.addTier(1));
				}
			}
		}
		if (h != null)
			hum.add(h);
		if (a != null)
			air.add(a);
		if (inputs != null && inputs.length > 0) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] instanceof String) {
					List<ItemStack> ret = new ArrayList<ItemStack>();
					ret.addAll(OreDictionary.getOres((String) inputs[i]));
					processedInput.add(ret);
					inputList.add(inputs[i]);
				} else if (inputs[i] instanceof ItemStack) {
					if (!DCUtil.isEmpty((ItemStack) inputs[i])) {
						ItemStack ret = ((ItemStack) inputs[i]).copy();
						processedInput.add(ret);
						inputList.add(ret);
					}
				} else if (inputs[i] instanceof Item) {
					ItemStack ret = new ItemStack((Item) inputs[i], 1, 0);
					processedInput.add(ret);
					inputList.add(ret);
				} else if (inputs[i] instanceof Block) {
					ItemStack ret = new ItemStack((Block) inputs[i], 1, 0);
					processedInput.add(ret);
					inputList.add(ret);
				} else {
					throw new IllegalArgumentException("Unknown Object passed to recipe!");
				}
			}
			int i1 = iF == null ? 0 : 1;
			i1 += processedInput.size();
			count = i1;
		} else {
			count = iF == null ? 0 : 1;
		}
	}

	@Override
	public Object[] getInput() {
		return input;
	}

	@Override
	public ItemStack getOutput() {
		return DCUtil.isEmpty(output) ? ItemStack.EMPTY : output.copy();
	}

	@Override
	public ItemStack getSecondary() {
		if (!DCUtil.isEmpty(secondary)) {
			return this.secondary.copy();
		} else {
			List<ItemStack> ret = getContainerItems(processedInput);
			if (ret != null && !ret.isEmpty()) {
				return ret.get(0);
			}
			return ItemStack.EMPTY;
		}
	}

	@Override
	public float getSecondaryChance() {
		return chance;
	}

	@Override
	public FluidStack getInputFluid() {
		return this.inputF;
	}

	@Override
	public FluidStack getOutputFluid() {
		return this.outputF;
	}

	@Override
	public List<ItemStack> getContainerItems(List<Object> items) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < items.size(); i++) {
			if (items.get(i) instanceof ItemStack) {
				ItemStack next = (ItemStack) items.get(i);
				ItemStack cont = ItemStack.EMPTY;
				if (!DCUtil.isEmpty(next)) {
					cont = next.getItem().getContainerItem(next);
					if (!DCUtil.isEmpty(cont)) {
						list.add(cont);
					} else {
						cont = DCFluidUtil.getEmptyCont(next);
						if (!DCUtil.isEmpty(cont)) {
							list.add(cont);
						}
					}
				}
			}
		}

		return list;
	}

	@Override
	public List<Object> getProcessedInput() {
		if (processedInput == null || this.processedInput.isEmpty()) {
			return EMPTY;
		} else {
			return new ArrayList<Object>(this.processedInput);
		}
	}

	@Override
	public int getRecipeSize() {
		if (input != null)
			return input.length;
		return 0;
	}

	@Override
	public boolean matches(List<ItemStack> items, FluidStack fluid) {
		boolean b1 = false;
		if (this.inputF == null) {
			if (fluid == null)
				b1 = true;
		} else if (fluid != null) {
			if (inputF.getFluid() == fluid.getFluid() || FluidDictionaryDC.matchFluid(fluid.getFluid(), inputF
					.getFluid())) {
				b1 = inputF.amount <= fluid.amount;
			}
		}

		if (b1) {
			// DCLogger.debugInfoLog("1: fluid match");
			ArrayList<Object> required = new ArrayList<Object>(this.inputList);

			if (required.isEmpty()) {
				for (int x = 0; x < items.size(); x++) {
					ItemStack slot = items.get(x);
					if (!DCUtil.isEmpty(slot)) {
						// DCLogger.debugInfoLog("fail");
						return false;
					}
				}
				return true;
			}

			for (int x = 0; x < items.size(); x++) {
				ItemStack slot = items.get(x);
				if (DCUtil.isEmpty(slot) || slot.getItem() instanceof IRecipePanel || required.isEmpty()) {
					continue;
				}

				boolean inRecipe = false;
				Iterator<Object> req = required.iterator();

				while (req.hasNext()) {
					boolean match = false;

					Object next = req.next();
					if (next == null) {
						continue;
					}

					if (next instanceof ItemStack) {
						// DCLogger.debugInfoLog("target: item");
						match = DCUtil.isSameItem((ItemStack) next, slot, false);
					} else if (next instanceof String) {
						// DCLogger.debugInfoLog("target: string " + "[" + (String) next + "]");
						match = DCUtil.matchDicName((String) next, slot);
					}

					if (match) {
						// DCLogger.debugInfoLog("match");
						inRecipe = true;
						required.remove(next);
						break;
					}
				}

				req = null;

				if (!inRecipe) {
					// DCLogger.debugInfoLog("fail");
					return false;
				}
			}

			// if (required.isEmpty()) {
			// DCLogger.debugInfoLog("2: item match");
			// } else {
			// DCLogger.debugInfoLog("fail " + required.toString());
			// }
			return required.isEmpty();
		} else {
			return false;
		}
	}

	@Override
	public boolean matchOutput(List<ItemStack> items, FluidStack fluid, int slotsize) {
		boolean b1 = false;
		if (this.outputF == null || fluid == null) {
			b1 = true;
		} else {
			b1 = outputF.getFluid() == fluid.getFluid();
		}

		if (b1) {
			if (items != null && !items.isEmpty()) {
				int i1 = -2;
				int i2 = -2;
				if (DCUtil.isEmpty(getOutput()))
					i1 = -1;
				if (DCUtil.isEmpty(getSecondary()))
					i2 = -1;
				for (int i = 0; i < items.size(); i++) {
					ItemStack get = items.get(i);
					if (i1 == -2 && DCUtil.canInsert(getOutput(), get)) {
						i1 = i;
						continue;
					}
					if (i2 == -2 && DCUtil.canInsert(getSecondary(), get)) {
						i2 = i;
					}
				}
				if (i1 == -1 && i2 == -1) {
					return true;
				} else
					return i1 > -2 && i2 > -2 && i1 != i2;
			} else {
				return DCUtil.isEmpty(getOutput()) && DCUtil.isEmpty(getSecondary());
			}
		}
		return false;
	}

	@Override
	public boolean matchClimate(int code) {
		IClimate clm = ClimateAPI.register.getClimateFromInt(code);
		return matchClimate(clm);
	}

	@Override
	public boolean isNeedCooling() {
		return this.needCooling;
	}

	@Override
	public boolean matchClimate(IClimate climate) {
		boolean t = requiredHeat().isEmpty() || requiredHeat().contains(climate.getHeat());
		boolean h = requiredHum().isEmpty() || requiredHum().contains(climate.getHumidity());
		boolean a = requiredAir().isEmpty() || requiredAir().contains(climate.getAirflow());
		// if (t && h && a)
		// DCLogger.debugInfoLog("3: clm match");
		return t && h && a;
	}

	@Override
	public boolean additionalRequire(World world, BlockPos pos) {
		if (isNeedCooling()) {
			return ClimateAPI.calculator.getCold(world, pos, 1, false).getID() <= 0;
		}
		return true;
	}

	@Override
	public int hasPlaceableOutput() {
		return 0;
	}

	@Override
	public List<DCHeatTier> requiredHeat() {
		return heat;
	}

	@Override
	public List<DCHumidity> requiredHum() {
		return hum;
	}

	@Override
	public List<DCAirflow> requiredAir() {
		return air;
	}

	@Override
	public String additionalString() {
		return type;
	}

	@Override
	public int recipeCoincidence() {
		return count;
	}

	@Override
	public DCHeatTier getMaxHeat() {
		DCHeatTier ret = DCHeatTier.ABSOLUTE;
		for (DCHeatTier h : heat) {
			if (h.getTier() > ret.getTier()) {
				ret = h;
			}
		}
		return ret;
	}

	@Override
	public DCHeatTier getMinHeat() {
		DCHeatTier ret = DCHeatTier.INFERNO;
		for (DCHeatTier h : heat) {
			if (h.getTier() < ret.getTier()) {
				ret = h;
			}
		}
		return ret;
	}
}
