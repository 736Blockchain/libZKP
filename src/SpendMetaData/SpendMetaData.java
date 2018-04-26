package SpendMetaData;

public class SpendMetaData {

	byte accumulatorId;
	byte txHash;
	//修改-加了两个this.
	public SpendMetaData(byte accumulatorId, byte txHash) {
		
		this.accumulatorId = accumulatorId;
		this.txHash = txHash;
		
	}
	
}
