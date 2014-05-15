package ch.fhnw.imvs8.businesscardreader.crf;

/**
 * Represents an Entity labeled by the NEREngine class.
 * 
 * It also contains a list of all possible labels;
 * 
 * This object is Immutable
 * @author jon
 *
 */
public class NamedEntity {
	public static final String[] LABELS = {"FN", "LN","ST","PLZ","ORT","B-TM","I-TM","B-TW","I-TW","B-TF","I-TF","EMA","TIT","WEB","ORG","IDK"};
	
	private final double confidence;
	private final String label;
	private final String entity;
	
	/**
	 * 
	 * @param label
	 * @param entity
	 * @param confidence
	 */
	public NamedEntity(String label, String entity, double confidence) {
		this.label = label;
		this.entity = entity;
		this.confidence = confidence;
	}

	public double getConfidence() {
		return confidence;
	}

	public String getLabel() {
		return label;
	}

	public String getEntity() {
		return entity;
	}
	
	
}
