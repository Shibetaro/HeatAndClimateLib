package defeatedcrow.hac.core.event;

import defeatedcrow.hac.config.CoreConfigDC;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.ChunkPrimer;
import net.minecraft.world.gen.MapGenRavine;
import net.minecraftforge.common.BiomeDictionary;

public class MapGenRavineDC extends MapGenRavine {
	protected static final IBlockState BLK_WATER = Blocks.FLOWING_WATER.getDefaultState();

	private boolean isExceptionBiome(net.minecraft.world.biome.Biome biome) {
		if (biome == net.minecraft.init.Biomes.BEACH)
			return true;
		if (biome == net.minecraft.init.Biomes.DESERT)
			return true;
		if (biome == net.minecraft.init.Biomes.MUSHROOM_ISLAND)
			return true;
		if (biome == net.minecraft.init.Biomes.MUSHROOM_ISLAND_SHORE)
			return true;
		return false;
	}

	@Override
	protected boolean isOceanBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ) {
		return y > 40 && super.isOceanBlock(data, x, y, z, chunkX, chunkZ);
	}

	@Override
	protected void digBlock(ChunkPrimer data, int x, int y, int z, int chunkX, int chunkZ, boolean foundTop) {
		net.minecraft.world.biome.Biome biome = world.getBiome(new BlockPos(x + chunkX * 16, 0, z + chunkZ * 16));
		IBlockState state = data.getBlockState(x, y, z);
		IBlockState top = isExceptionBiome(biome) ? Blocks.GRASS.getDefaultState() : biome.topBlock;
		IBlockState filler = isExceptionBiome(biome) ? Blocks.DIRT.getDefaultState() : biome.fillerBlock;

		if (state.getBlock() == Blocks.STONE || state.getBlock() == top.getBlock() || state.getBlock() == filler
				.getBlock()) {
			if (y - 1 < 10) {
				if (biome.getRainfall() >= 0.85F || BiomeDictionary.hasType(biome, BiomeDictionary.Type.WET)) {
					data.setBlockState(x, y, z, BLK_WATER);
				} else {
					data.setBlockState(x, y, z, FLOWING_LAVA);
				}
			} else {
				if (BiomeDictionary.hasType(biome, BiomeDictionary.Type.OCEAN)) {
					if (y < 40 && CoreConfigDC.enableSubmergedCave) {
						data.setBlockState(x, y, z, BLK_WATER);
					} else if (state.getMaterial() != Material.WATER) {
						data.setBlockState(x, y, z, AIR);
					}
				} else {
					data.setBlockState(x, y, z, AIR);
				}

				if (foundTop && data.getBlockState(x, y - 1, z).getBlock() == filler.getBlock()) {
					data.setBlockState(x, y - 1, z, top.getBlock().getDefaultState());
				}
			}
		}
	}
}
