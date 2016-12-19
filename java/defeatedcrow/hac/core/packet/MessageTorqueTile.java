package defeatedcrow.hac.core.packet;

import io.netty.buffer.ByteBuf;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

//トルクTileの回転加速度の同期用
public class MessageTorqueTile implements IMessage {

	public int x;
	public int y;
	public int z;
	public float torque;

	public MessageTorqueTile() {
	}

	public MessageTorqueTile(BlockPos pos, float par1) {
		this.x = pos.getX();
		this.y = pos.getY();
		this.z = pos.getZ();
		this.torque = par1;
	}

	// read
	@Override
	public void fromBytes(ByteBuf buf) {
		this.x = buf.readInt();
		this.y = buf.readInt();
		this.z = buf.readInt();
		this.torque = buf.readFloat();
	}

	// write
	@Override
	public void toBytes(ByteBuf buf) {
		buf.writeInt(x);
		buf.writeInt(y);
		buf.writeInt(z);
		buf.writeFloat(this.torque);
	}
}
