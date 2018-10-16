package com.kaszub.pdi.memsummary;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.pentaho.di.core.CheckResult;
import org.pentaho.di.core.CheckResultInterface;
import org.pentaho.di.core.Const;
import org.pentaho.di.core.annotations.Step;
import org.pentaho.di.core.util.Utils;
import org.pentaho.di.core.database.DatabaseMeta;
import org.pentaho.di.core.exception.KettleException;
import org.pentaho.di.core.exception.KettlePluginException;
import org.pentaho.di.core.exception.KettleXMLException;
import org.pentaho.di.core.injection.Injection;
import org.pentaho.di.core.injection.InjectionSupported;
import org.pentaho.di.core.row.RowMeta;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.core.row.value.ValueMetaBase;
import org.pentaho.di.core.row.value.ValueMetaFactory;
import org.pentaho.di.core.row.value.ValueMetaNone;
import org.pentaho.di.core.variables.VariableSpace;
import org.pentaho.di.core.xml.XMLHandler;
import org.pentaho.di.i18n.BaseMessages;
import org.pentaho.di.repository.ObjectId;
import org.pentaho.di.repository.Repository;
import org.pentaho.di.trans.Trans;
import org.pentaho.di.trans.TransMeta;
import org.pentaho.di.trans.step.BaseStepMeta;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.step.StepInterface;
import org.pentaho.di.trans.step.StepMeta;
import org.pentaho.di.trans.step.StepMetaInterface;
import org.pentaho.metastore.api.IMetaStore;
import org.w3c.dom.Node;

@Step(id = "KaszubMemorySummary", i18nPackageName = "com.kaszub.pdi.memsummary", image = "MGB.png", name = "Kaszub.MemSummary.Step.Name",
description = "Kaszub.MemSummary.Step.Description", categoryDescription = "Kaszub.MemSummary.Step.CategoryDescription")
@InjectionSupported(localizationPrefix = "MemorySummary.Injection.", groups = { "FIELDS", "AGGREGATES" })
public class MemorySummaryMeta extends BaseStepMeta implements StepMetaInterface {
	private static Class<?> PKG = MemorySummaryMeta.class; 
	public static final int TYPE_GROUP_NONE = 0;
	public static final int TYPE_GROUP_SUM = 1;
	public static final int TYPE_GROUP_AVERAGE = 2;
	public static final int TYPE_GROUP_MEDIAN = 3;
	public static final int TYPE_GROUP_PERCENTILE = 4;
	public static final int TYPE_GROUP_MIN = 5;
	public static final int TYPE_GROUP_MAX = 6;
	public static final int TYPE_GROUP_COUNT_ALL = 7;
	public static final int TYPE_GROUP_CONCAT_COMMA = 8;
	public static final int TYPE_GROUP_FIRST = 9;
	public static final int TYPE_GROUP_LAST = 10;
	public static final int TYPE_GROUP_FIRST_INCL_NULL = 11;
	public static final int TYPE_GROUP_LAST_INCL_NULL = 12;
	public static final int TYPE_GROUP_STANDARD_DEVIATION = 13;
	public static final int TYPE_GROUP_CONCAT_STRING = 14;
	public static final int TYPE_GROUP_COUNT_DISTINCT = 15;
	public static final int TYPE_GROUP_COUNT_ANY = 16;
	public static final String[] typeGroupCode = { "-", "SUM", "AVERAGE", "MEDIAN", "PERCENTILE", "MIN", "MAX",
			"COUNT_ALL", "CONCAT_COMMA", "FIRST", "LAST", "FIRST_INCL_NULL", "LAST_INCL_NULL", "STD_DEV",
			"CONCAT_STRING", "COUNT_DISTINCT", "COUNT_ANY", };

	public static final String[] typeGroupLongDesc = { "-",
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.SUM"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.AVERAGE"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.MEDIAN"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.PERCENTILE"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.MIN"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.MAX"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.CONCAT_ALL"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.CONCAT_COMMA"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.FIRST"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.LAST"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.FIRST_INCL_NULL"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.LAST_INCL_NULL"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.STANDARD_DEVIATION"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.CONCAT_STRING"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.COUNT_DISTINCT"),
			BaseMessages.getString(PKG, "MemorySummaryMeta.TypeGroupLongDesc.COUNT_ANY"), };

	@Injection(name = "GROUPFIELD", group = "FIELDS")
	/** Fields to group over */
	private String[][] groupField;

	@Injection(name = "AGGREGATEFIELD", group = "AGGREGATES")
	/** Name of aggregate field */
	private String[] aggregateField;

	@Injection(name = "SUBJECTFIELD", group = "AGGREGATES")
	/** Field name to group over */
	private String[] subjectField;

	@Injection(name = "AGGREGATETYPE", group = "AGGREGATES")
	/** Type of aggregate */
	private int[] aggregateType;

	@Injection(name = "VALUEFIELD", group = "AGGREGATES")
	/** Value to use as separator for ex */
	private String[] valueField;

	@Injection(name = "ALWAYSGIVINGBACKONEROW", group = "FIELDS")
	/**
	 * Flag to indicate that we always give back one row. Defaults to true for
	 * existing transformations.
	 */
	private boolean alwaysGivingBackOneRow;

	@Injection(name = "ADDNULLSTOCONCAT", group = "FIELDS")
	/**
	 * Flag to indicate that we include null values in the concatenation
	 * aggregation. Default is false
	 */
	private boolean addNullsToConcat;

	public MemorySummaryMeta() {
		super(); // allocate BaseStepMeta
	}

	/**
	 * @return Returns the aggregateField.
	 */
	public String[] getAggregateField() {
		return aggregateField;
	}

	/**
	 * @param aggregateField
	 *            The aggregateField to set.
	 */
	public void setAggregateField(String[] aggregateField) {
		this.aggregateField = aggregateField;
	}

	/**
	 * @return Returns the aggregateType.
	 */
	public int[] getAggregateType() {
		return aggregateType;
	}

	/**
	 * @param aggregateType
	 *            The aggregateType to set.
	 */
	public void setAggregateType(int[] aggregateType) {
		this.aggregateType = aggregateType;
	}

	/**
	 * @return Returns the groupField.
	 */
	public String[][] getGroupField() {
		return groupField;
	}

	/**
	 * @param groupField
	 *            The groupField to set.
	 */
	public void setGroupField(String[][] groupField) {
		this.groupField = groupField;
	}

	/**
	 * @return Returns the subjectField.
	 */
	public String[] getSubjectField() {
		return subjectField;
	}

	/**
	 * @param subjectField
	 *            The subjectField to set.
	 */
	public void setSubjectField(String[] subjectField) {
		this.subjectField = subjectField;
	}

	/**
	 * @return Returns the valueField.
	 */
	public String[] getValueField() {
		return valueField;
	}

	/**
	 * @param separatorField
	 *            The valueField to set.
	 */
	public void setValueField(String[] valueField) {
		this.valueField = valueField;
	}

	@Override
	public void loadXML(Node stepnode, List<DatabaseMeta> databases, IMetaStore metaStore) throws KettleXMLException {
		readData(stepnode);
	}

	public void allocate(int sizegroup, int nrfields) {
		groupField = new String[sizegroup][8];
		aggregateField = new String[nrfields];
		subjectField = new String[nrfields];
		aggregateType = new int[nrfields];
		valueField = new String[nrfields];
	}

	@Override
	public Object clone() {
		MemorySummaryMeta retval = (MemorySummaryMeta) super.clone();
		int nrFields = aggregateField.length;
		int nrGroupRows = groupField.length;

		retval.allocate(nrGroupRows, nrFields);
		System.arraycopy(groupField, 0, retval.groupField, 0, nrGroupRows);
		System.arraycopy(aggregateField, 0, retval.aggregateField, 0, nrFields);
		System.arraycopy(subjectField, 0, retval.subjectField, 0, nrFields);
		System.arraycopy(aggregateType, 0, retval.aggregateType, 0, nrFields);
		System.arraycopy(valueField, 0, retval.valueField, 0, nrFields);
		return retval;
	}

	public boolean containsGroupRows(){
		return getGroupField().length != 0;
	}

	public int numOfNonNullGroupFields(int index){
		int nonNullFields = 0;
		
		if (containsGroupRows())
			for (int i=0; i< getGroupField()[index].length; i++)
				if (getGroupField()[index][i] != null)
					nonNullFields++;

		return nonNullFields;
	}
	
	public int maxNumOfNonNullGroupFields(){
		int max = 0;
		
		for (int i=0; i< getGroupField().length; i++){
			int ret = numOfNonNullGroupFields(i);
			if (ret > max)
				max = ret;
		}

		return max;
	}
	
	private void readData(Node stepnode) throws KettleXMLException {
		try {
			Node groupn = XMLHandler.getSubNode(stepnode, "groups");
			Node fields = XMLHandler.getSubNode(stepnode, "fields");

			int sizegrouprows = XMLHandler.countNodes(groupn, "group");
			int nrfields = XMLHandler.countNodes(fields, "field");

			allocate(sizegrouprows, nrfields);

			for (int i = 0; i < sizegrouprows; i++) {
				Node gnode = XMLHandler.getSubNodeByNr(groupn, "group", i);
				int sizegroupcols = XMLHandler.countNodes(gnode, "field");
				for (int j = 0; j < sizegroupcols; j++) {
					Node fnode = XMLHandler.getSubNodeByNr(gnode, "field", j);
					groupField[i][j] = XMLHandler.getTagValue(fnode, "name");
				}
			}

			boolean hasNumberOfValues = false;
			for (int i = 0; i < nrfields; i++) {
				Node fnode = XMLHandler.getSubNodeByNr(fields, "field", i);
				aggregateField[i] = XMLHandler.getTagValue(fnode, "aggregate");
				subjectField[i] = XMLHandler.getTagValue(fnode, "subject");
				aggregateType[i] = getType(XMLHandler.getTagValue(fnode, "type"));

				if (aggregateType[i] == TYPE_GROUP_COUNT_ALL || aggregateType[i] == TYPE_GROUP_COUNT_DISTINCT
						|| aggregateType[i] == TYPE_GROUP_COUNT_ANY) {
					hasNumberOfValues = true;
				}

				valueField[i] = XMLHandler.getTagValue(fnode, "valuefield");
			}

			String giveBackRow = XMLHandler.getTagValue(stepnode, "give_back_row");
			if (Utils.isEmpty(giveBackRow)) {
				alwaysGivingBackOneRow = hasNumberOfValues;
			} else {
				alwaysGivingBackOneRow = "Y".equalsIgnoreCase(giveBackRow);
			}

			String addNulls = XMLHandler.getTagValue(stepnode, "add_nulls_concat");
			if (Utils.isEmpty(addNulls)) {
				addNullsToConcat = false;
			} else {
				addNullsToConcat = "Y".equalsIgnoreCase(addNulls);
			}

		} catch (Exception e) {
			throw new KettleXMLException(
					BaseMessages.getString(PKG, "MemorySummaryMeta.Exception.UnableToLoadStepInfoFromXML"), e);
		}
	}

	public static final int getType(String desc) {
		for (int i = 0; i < typeGroupCode.length; i++) {
			if (typeGroupCode[i].equalsIgnoreCase(desc)) {
				return i;
			}
		}
		for (int i = 0; i < typeGroupLongDesc.length; i++) {
			if (typeGroupLongDesc[i].equalsIgnoreCase(desc)) {
				return i;
			}
		}
		return 0;
	}

	public static final String getTypeDesc(int i) {
		if (i < 0 || i >= typeGroupCode.length) {
			return null;
		}
		return typeGroupCode[i];
	}

	public static final String getTypeDescLong(int i) {
		if (i < 0 || i >= typeGroupLongDesc.length) {
			return null;
		}
		return typeGroupLongDesc[i];
	}

	@Override
	public void setDefault() {
		int sizegroup = 0;
		int nrfields = 0;

		allocate(sizegroup, nrfields);
	}

	@Override
	public void getFields(RowMetaInterface r, String origin, RowMetaInterface[] info, StepMeta nextStep,
			VariableSpace space, Repository repository, IMetaStore metaStore) {
		// Check compatibility mode
		boolean compatibilityMode = ValueMetaBase.convertStringToBoolean(
				space.getVariable(Const.KETTLE_COMPATIBILITY_MEMORY_GROUP_BY_SUM_AVERAGE_RETURN_NUMBER_TYPE, "N"));

		// re-assemble a new row of metadata
		//
		RowMetaInterface fields = new RowMeta();

		Set<ValueMetaInterface> set = new HashSet<ValueMetaInterface>();

		// Add the grouping fields in the correct order...
		//
		for (int i = 0; i < groupField.length; i++) {
			for (int j = 0; j < groupField[i].length; j++) {
				ValueMetaInterface valueMeta = r.searchValueMeta(groupField[i][j]);
				if (valueMeta != null && !set.contains(valueMeta)) {
					valueMeta.setStorageType(ValueMetaInterface.STORAGE_TYPE_NORMAL);
					fields.addValueMeta(valueMeta);
					set.add(valueMeta);
				}
			}
		}

		// Re-add aggregates
		//
		for (int i = 0; i < subjectField.length; i++) {
			ValueMetaInterface subj = r.searchValueMeta(subjectField[i]);
			if (subj != null || aggregateType[i] == TYPE_GROUP_COUNT_ANY) {
				String value_name = aggregateField[i];
				int value_type = ValueMetaInterface.TYPE_NONE;
				int length = -1;
				int precision = -1;

				switch (aggregateType[i]) {
				case TYPE_GROUP_FIRST:
				case TYPE_GROUP_LAST:
				case TYPE_GROUP_FIRST_INCL_NULL:
				case TYPE_GROUP_LAST_INCL_NULL:
				case TYPE_GROUP_MIN:
				case TYPE_GROUP_MAX:
					value_type = subj.getType();
					break;
				case TYPE_GROUP_COUNT_DISTINCT:
				case TYPE_GROUP_COUNT_ALL:
				case TYPE_GROUP_COUNT_ANY:
					value_type = ValueMetaInterface.TYPE_INTEGER;
					break;
				case TYPE_GROUP_CONCAT_COMMA:
					value_type = ValueMetaInterface.TYPE_STRING;
					break;
				case TYPE_GROUP_SUM:
				case TYPE_GROUP_AVERAGE:
					if (!compatibilityMode && subj.isNumeric()) {
						value_type = subj.getType();
					} else {
						value_type = ValueMetaInterface.TYPE_NUMBER;
					}
					break;
				case TYPE_GROUP_MEDIAN:
				case TYPE_GROUP_PERCENTILE:
				case TYPE_GROUP_STANDARD_DEVIATION:
					value_type = ValueMetaInterface.TYPE_NUMBER;
					break;
				case TYPE_GROUP_CONCAT_STRING:
					value_type = ValueMetaInterface.TYPE_STRING;
					break;
				default:
					break;
				}

				if (aggregateType[i] == TYPE_GROUP_COUNT_ALL || aggregateType[i] == TYPE_GROUP_COUNT_DISTINCT
						|| aggregateType[i] == TYPE_GROUP_COUNT_ANY) {
					length = ValueMetaInterface.DEFAULT_INTEGER_LENGTH;
					precision = 0;
				} else if (aggregateType[i] == TYPE_GROUP_SUM && value_type != ValueMetaInterface.TYPE_INTEGER
						&& value_type != ValueMetaInterface.TYPE_NUMBER
						&& value_type != ValueMetaInterface.TYPE_BIGNUMBER) {
					// If it ain't numeric, we change it to Number
					//
					value_type = ValueMetaInterface.TYPE_NUMBER;
					precision = -1;
					length = -1;
				}

				if (value_type != ValueMetaInterface.TYPE_NONE) {
					ValueMetaInterface v;
					try {
						v = ValueMetaFactory.createValueMeta(value_name, value_type);
					} catch (KettlePluginException e) {
						log.logError(BaseMessages.getString(PKG, "MemorySummaryMeta.Exception.UnknownValueMetaType"),
								value_type, e);
						v = new ValueMetaNone(value_name);
					}
					v.setOrigin(origin);
					v.setLength(length, precision);
					fields.addValueMeta(v);
				}
			}
		}

		// Now that we have all the fields we want, we should clear the original
		// row and replace the values...
		//
		r.clear();
		r.addRowMeta(fields);
	}

	@Override
	public String getXML() {
		StringBuilder retval = new StringBuilder(500);

		retval.append("      ").append(XMLHandler.addTagValue("give_back_row", alwaysGivingBackOneRow));
		retval.append("      ").append(XMLHandler.addTagValue("add_nulls_concat", addNullsToConcat));

		retval.append("      <groups>").append(Const.CR);
		for (int i = 0; i < groupField.length; i++) {
			retval.append("        <group>").append(Const.CR);
			for (int j = 0; j < groupField[i].length; j++){
				retval.append("          <field>").append(Const.CR);
				retval.append("            ").append(XMLHandler.addTagValue("name", groupField[i][j]));
				retval.append("          </field>").append(Const.CR);
			}
			retval.append("        </group>").append(Const.CR);
		}
		retval.append("      </groups>").append(Const.CR);

		retval.append("      <fields>").append(Const.CR);
		for (int i = 0; i < subjectField.length; i++) {
			retval.append("        <field>").append(Const.CR);
			retval.append("          ").append(XMLHandler.addTagValue("aggregate", aggregateField[i]));
			retval.append("          ").append(XMLHandler.addTagValue("subject", subjectField[i]));
			retval.append("          ").append(XMLHandler.addTagValue("type", getTypeDesc(aggregateType[i])));
			retval.append("          ").append(XMLHandler.addTagValue("valuefield", valueField[i]));
			retval.append("        </field>").append(Const.CR);
		}
		retval.append("      </fields>").append(Const.CR);

		return retval.toString();
	}

	@Override
	//TODO: Fix for multiple groups
	public void readRep(Repository rep, IMetaStore metaStore, ObjectId id_step, List<DatabaseMeta> databases)
			throws KettleException {
		try {
			int groupsize = rep.countNrStepAttributes(id_step, "group_name");
			int nrvalues = rep.countNrStepAttributes(id_step, "aggregate_name");

			allocate(groupsize, nrvalues);

			for (int i = 0; i < groupsize; i++) {
				groupField[i][0] = rep.getStepAttributeString(id_step, i, "group_name");
			}

			boolean hasNumberOfValues = false;
			for (int i = 0; i < nrvalues; i++) {
				aggregateField[i] = rep.getStepAttributeString(id_step, i, "aggregate_name");
				subjectField[i] = rep.getStepAttributeString(id_step, i, "aggregate_subject");
				aggregateType[i] = getType(rep.getStepAttributeString(id_step, i, "aggregate_type"));

				if (aggregateType[i] == TYPE_GROUP_COUNT_ALL || aggregateType[i] == TYPE_GROUP_COUNT_DISTINCT
						|| aggregateType[i] == TYPE_GROUP_COUNT_ANY) {
					hasNumberOfValues = true;
				}
				valueField[i] = rep.getStepAttributeString(id_step, i, "aggregate_value_field");
			}

			alwaysGivingBackOneRow = rep.getStepAttributeBoolean(id_step, 0, "give_back_row", hasNumberOfValues);
			addNullsToConcat = rep.getStepAttributeBoolean(id_step, 0, "add_nulls_concat", hasNumberOfValues);
		} catch (Exception e) {
			throw new KettleException(BaseMessages.getString(PKG,
					"MemorySummaryMeta.Exception.UnexpectedErrorInReadingStepInfoFromRepository"), e);
		}
	}

	@Override
	//TODO: Fix for multiple groups
	public void saveRep(Repository rep, IMetaStore metaStore, ObjectId id_transformation, ObjectId id_step)
			throws KettleException {
		try {
			rep.saveStepAttribute(id_transformation, id_step, "give_back_row", alwaysGivingBackOneRow);
			rep.saveStepAttribute(id_transformation, id_step, "add_nulls_concat", addNullsToConcat);

			for (int i = 0; i < groupField.length; i++) {
				rep.saveStepAttribute(id_transformation, id_step, i, "group_name", groupField[i][0]);
			}

			for (int i = 0; i < subjectField.length; i++) {
				rep.saveStepAttribute(id_transformation, id_step, i, "aggregate_name", aggregateField[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "aggregate_subject", subjectField[i]);
				rep.saveStepAttribute(id_transformation, id_step, i, "aggregate_type", getTypeDesc(aggregateType[i]));
				rep.saveStepAttribute(id_transformation, id_step, i, "aggregate_value_field", valueField[i]);
			}
		} catch (Exception e) {
			throw new KettleException(
					BaseMessages.getString(PKG, "MemorySummaryMeta.Exception.UnableToSaveStepInfoToRepository")
							+ id_step,
					e);
		}
	}

	@Override
	public void check(List<CheckResultInterface> remarks, TransMeta transMeta, StepMeta stepMeta, RowMetaInterface prev,
			String[] input, String[] output, RowMetaInterface info, VariableSpace space, Repository repository,
			IMetaStore metaStore) {
		CheckResult cr;

		if (input.length > 0) {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_OK,
					BaseMessages.getString(PKG, "MemorySummaryMeta.CheckResult.ReceivingInfoOK"), stepMeta);
			remarks.add(cr);
		} else {
			cr = new CheckResult(CheckResultInterface.TYPE_RESULT_ERROR,
					BaseMessages.getString(PKG, "MemorySummaryMeta.CheckResult.NoInputError"), stepMeta);
			remarks.add(cr);
		}
	}

	@Override
	public StepInterface getStep(StepMeta stepMeta, StepDataInterface stepDataInterface, int cnr, TransMeta transMeta,
			Trans trans) {
		return new MemorySummary(stepMeta, stepDataInterface, cnr, transMeta, trans);
	}

	@Override
	public StepDataInterface getStepData() {
		return new MemorySummaryData();
	}

	/**
	 * @return the alwaysGivingBackOneRow
	 */
	public boolean isAlwaysGivingBackOneRow() {
		return alwaysGivingBackOneRow;
	}

	/**
	 * @return the addNullsToConcat
	 */
	public boolean isAddingNullValuesToConcatenation() {
		return addNullsToConcat;
	}

	/**
	 * @param alwaysGivingBackOneRow
	 *            the alwaysGivingBackOneRow to set
	 */
	public void setAlwaysGivingBackOneRow(boolean alwaysGivingBackOneRow) {
		this.alwaysGivingBackOneRow = alwaysGivingBackOneRow;
	}
	
	/**
	 * @param addNullsToConcat
	 *            the addNullsToConcat to set
	 */
	public void setAddNullValuesToConcatenation(boolean addNullsToConcat) {
		this.addNullsToConcat = addNullsToConcat;
	}

}
