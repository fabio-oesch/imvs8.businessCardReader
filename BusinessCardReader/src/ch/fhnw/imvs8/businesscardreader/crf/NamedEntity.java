package ch.fhnw.imvs8.businesscardreader.crf;

/**
 * Represents an Entity tagged by the NEREngine class.
 * @author jon
 *
 */
public class NamedEntity {
	public final double confidence;
	public final String tag;
	public final String entity;
	
	public NamedEntity(String tag, String entity, double confidence) {
		this.tag = tag;
		this.entity = entity;
		this.confidence = confidence;
	}
}
