package defeatedcrow.hac.core.climate.recipe;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidContainerRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import defeatedcrow.hac.api.climate.ClimateAPI;
import defeatedcrow.hac.api.climate.DCAirflow;
import defeatedcrow.hac.api.climate.DCHeatTier;
import defeatedcrow.hac.api.climate.DCHumidity;
import defeatedcrow.hac.api.climate.IClimate;
import defeatedcrow.hac.api.recipe.IFluidRecipe;
import defeatedcrow.hac.api.recipe.IRecipePanel;
import defeatedcrow.hac.core.util.DCUtil;

public class FluidCraftRecipe implements IFluidRecipe {

	private final Object[] input;
	private final FluidStack inputF;
	private ArrayList<Object> processedInput;
	private final ItemStack output;
	private final FluidStack outputF;
	private final ItemStack secondary;
	private final float chance;
	private final boolean needCooling;
	private List<DCHeatTier> heat = new ArrayList<DCHeatTier>();
	private List<DCHumidity> hum = new ArrayList<DCHumidity>();
	private List<DCAirflow> air = new ArrayList<DCAirflow>();
	private String type = "";

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
			heat.add(t);
			if (t.getID() < 9) {
				if (t.getID() == 3) {
					heat.add(t.addTier(1));
					heat.add(t.addTier(-1));
				} else if (t.getID() == 1 || t.getID() == 2) {
					heat.add(t.addTier(-1));
				} else if (t.getID() > 0) {
					heat.add(t.addTier(1));
				}
			}
		}
		if (h != null)
			hum.add(h);
		if (a != null)
			air.add(a);
		processedInput = new ArrayList<Object>();
		if (inputs != null) {
			for (int i = 0; i < inputs.length; i++) {
				if (inputs[i] instanceof String) {
					processedInput.add(OreDictionary.getOres((String) inputs[i]));
				} else if (inputs[i] instanceof ItemStack) {
					processedInput.add(((ItemStack) inputs[i]).copy());
				} else if (inputs[i] instanceof Item) {
					processedInput.add(new ItemStack((Item) inputs[i], 1, 0));
				} else if (inputs[i] instanceof Block) {
					processedInput.add(new ItemStack((Block) inputs[i], 1, 0));
				} else {
					throw new IllegalArgumentException("Unknown Object passed to recipe!");
				}
			}
		}
	}

	@Override
	public Object[] getInput() {
		return input;
	}

	@Override
	public ItemStack getOutput() {
		return output.copy();
	}

	@Override
	public ItemStack getSecondary() {
		if (this.secondary != null) {
			return this.secondary.copy();
		} else {
			return null;
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
	public List<ItemStack> getContainerItems(List<ItemStack> items) {
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (int i = 0; i < items.size(); i++) {
			ItemStack next = items.get(i);
			ItemStack cont = null;
			if (next != null) {
				cont = next.getItem().getContainerItem(next);
				if (cont != null) {
					list.add(cont);
				} else {
					cont = FluidContainerRegistry.drainFluidContainer(next);
					if (cont != null) {
						list.add(cont);
					}
				}
			}
		}

		return list;
	}

	@Override
	public List<Object> getProcessedInput() {
		return processedInput;
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
			b1 = true;
		} else if (fluid != null) {
			b1 = (inputF.getFluid() == fluid.getFluid()) && (inputF.amount <= fluid.amount);
		}

		if (b1) {
			// DCLogger.debugLog("1: fluid match");
			ArrayList<Object> required = new ArrayList<Object>(this.processedInput);
			if (required.isEmpty())
				return true;

			for (int x = 0; x < items.size(); x++) {
				ItemStack slot = items.get(x);

				if (slot != null) {
					boolean inRecipe = false;
					Iterator<Object> req = required.iterator();

					if (slot.getItem() instanceof IRecipePanel) {
						inRecipe = true;
						continue;
					}

					while (req.hasNext()) {
						boolean match = false;

						Object next = req.next();

						if (next instanceof ItemStack) {
							match = DCUtil.isSameItem((ItemStack) next, slot);
						} else if (next instanceof ArrayList) {
							Iterator<ItemStack> itr = ((ArrayList<ItemStack>) next).iterator();
							while (itr.hasNext() && !match) {
								match = DCUtil.isSameItem(itr.next(), slot);
							}
						}

						if (match) {
							inRecipe = true;
							required.remove(next);
							break;
						}
					}

					if (!inRecipe) {
						return false;
					}
				}
			}
			// if (required.isEmpty())
			// DCLogger.debugLog("2: item match");
			return required.isEmpty();
		} else {
			return false;
		}
	}

	@Override
	public boolean matchOutput(List<ItemStack> items, FluidStack fluid, int slotsize) {
		boolean b1 = false;
		if (this.outputF == null) {
			b1 = true;
		} else if (fluid != null) {
			b1 = outputF.getFluid() == fluid.getFluid();
		}

		if (b1) {
			if (items != null && !items.isEmpty()) {
				boolean b2 = false;
				boolean b3 = false;
				for (ItemStack get : items) {
					if (getOutput() == null || DCUtil.isSameItem(getOutput(), get)) {
						b2 = true;
					} else if (getSecondary() == null || DCUtil.isSameItem(getSecondary(), get)) {
						b3 = true;
					}
				}
				if (items.size() < slotsize - 1) {
					return true;
				} else if (items.size() == slotsize - 1) {
					return b2 || b3;
				} else {
					return b2 && b3;
				}
			} else {
				return true;
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
		// DCLogger.debugLog("3: clm match");
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
}
