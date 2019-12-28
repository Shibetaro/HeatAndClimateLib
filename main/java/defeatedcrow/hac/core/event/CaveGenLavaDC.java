package defeatedcrow.hac.core.event;

import java.util.Random;

import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.WorldGenLakes;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.event.terraingen.DecorateBiomeEvent;
import net.minecraftforge.event.terraingen.PopulateChunkEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CaveGenLavaDC {

	private Random rand = new Random();

	@SubscribeEvent
	public void initLakeGen(PopulateChunkEvent.Populate event) {
		if (event.getType() == net.minecraftforge.event.terraingen.PopulateChunkEvent.Populate.EventType.LAVA) {
			if (CoreConfigDC.enableUnderLake) {
				BlockPos pos = new BlockPos(event.getChunkX() * 16, 0, event.getChunkZ() * 16);
				Biome biome = event.getWorld().getBiomeForCoordsBody(pos);
				if (biome.getRainfall() > 0.8F || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
					int x = rand.nextInt(16) + 8;
					int y = rand.nextInt(256);
					int z = rand.nextInt(16) + 8;
					BlockPos pos1 = pos.add(x, y, z);
					if (y < event.getWorld().getSeaLevel()) {
						(new WorldGenLakes(Blocks.MAGMA)).generate(event.getWorld(), rand, pos1);
						// DCLogger.debugLog("underlava changed: " + pos1.getX() + "," + pos1.getY() + "," +
						// pos1.getZ());
					}
					event.setResult(Result.DENY);
				}
			}
			if (CoreConfigDC.enableForestLake) {
				BlockPos pos = new BlockPos(event.getChunkX() * 16 + 8, 64, event.getChunkZ() * 16 + 8);
				Biome biome = event.getWorld().getBiomeForCoordsBody(pos);
				if (pos.getY() > 45 && (BiomeDictionary.hasType(biome, BiomeDictionary.Type.FOREST) || BiomeDictionary
						.hasType(biome, BiomeDictionary.Type.DENSE)) || BiomeDictionary
								.hasType(biome, BiomeDictionary.Type.CONIFEROUS)) {
					event.setResult(Result.DENY);
				}
			}
		}
	}

	@SubscribeEvent
	public void initFluid(DecorateBiomeEvent.Decorate event) {
		if (CoreConfigDC.enableUnderLake && event
				.getType() == net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LAKE_LAVA) {
			Biome biome = event.getWorld().getBiomeForCoordsBody(event.getChunkPos().getBlock(8, 8, 8));
			Random random = event.getWorld().rand;
			boolean flag = false;
			if (biome.getRainfall() > 0.8F || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
				// 降雨量が多いと溶岩ができない
				event.setResult(Result.DENY);
			}
		} else if (CoreConfigDC.enableUnderLake && event
				.getType() == net.minecraftforge.event.terraingen.DecorateBiomeEvent.Decorate.EventType.LAKE_WATER) {
			Biome biome = event.getWorld().getBiomeForCoordsBody(event.getChunkPos().getBlock(8, 8, 8));
			Random random = event.getWorld().rand;
			boolean flag = false;
			if (biome.getRainfall() <= 0.2F) {
				// 乾燥地帯には水の池ができない
				event.setResult(Result.DENY);
			}
		}
	}

}
