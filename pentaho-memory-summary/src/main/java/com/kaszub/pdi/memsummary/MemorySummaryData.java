package com.kaszub.pdi.memsummary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.pentaho.di.core.exception.KettleValueException;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;
import org.pentaho.di.trans.step.BaseStepData;
import org.pentaho.di.trans.step.StepDataInterface;
import org.pentaho.di.trans.steps.memgroupby.Aggregate;

public class MemorySummaryData extends BaseStepData implements StepDataInterface {
	public class HashEntry {
		private Object[] groupData;
		private RowMetaInterface meta;
		private int positionsHashCode = -1;

		public HashEntry(Object[] groupData, RowMetaInterface meta){
			this(groupData, meta, 0);
		}

		public HashEntry(Object[] groupData, RowMetaInterface meta, int positionsHashCode) {
			this.groupData = groupData;
			this.meta = meta;
			this.positionsHashCode = positionsHashCode;
		}

		public Object[] getGroupData() {
			return groupData;
		}

		public RowMetaInterface getMeta() {
			return meta;
		}

		public boolean equals(Object obj) {
			HashEntry entry = (HashEntry) obj;

			try {
				return meta.compare(groupData, entry.groupData) == 0;
			} catch (KettleValueException e) {
				throw new RuntimeException(e);
			}
		}

		public int hashCode() {
			try {
				int hash = 17;
				int dataHashCode = meta.hashCode(getHashValue());
			    hash = hash * 31 + dataHashCode;

			    if (positionsHashCode > -1)
					hash = hash * 31 + positionsHashCode;
			    
				return hash;
			} catch (KettleValueException e) {
				throw new RuntimeException(e);
			}
		}

		private Object[] getHashValue() throws KettleValueException {
			Object[] groupDataHash = new Object[groupData.length];
			for (int i = 0; i < groupData.length; i++) {
				ValueMetaInterface valueMeta = meta.getValueMeta(i);
				groupDataHash[i] = valueMeta.convertToNormalStorageType(groupData[i]);
			}
			return groupDataHash;
		}
	}

	public HashMap<HashEntry, Aggregate> map;

	public RowMetaInterface aggMeta;
	public RowMetaInterface groupMeta[];
	public RowMetaInterface entryMeta;

	public RowMetaInterface groupAggMeta[]; // for speed: groupMeta+aggMeta

	public List<List<Integer>> groupnrs;
	public List<Integer> metaGroupHashes = new ArrayList<Integer>();
	public int[] subjectnrs;

	public boolean firstRead;

	public Object[] groupResult;

	public boolean hasOutput;

	public RowMetaInterface inputRowMeta;
	public RowMetaInterface outputRowMeta;

	public ValueMetaInterface valueMetaInteger;
	public ValueMetaInterface valueMetaNumber;

	public boolean newBatch;

	public MemorySummaryData() {
		super();
	}

	public HashEntry getHashEntry(Object[] groupData, RowMetaInterface meta) {
		return getHashEntry(groupData, meta, 0);
	}
	public HashEntry getHashEntry(Object[] groupData, RowMetaInterface meta, int positionsHashCode) {
		return new HashEntry(groupData, meta, positionsHashCode);
	}
	public HashEntry getHashEntry(Object[] groupData, RowMetaInterface meta, List<Integer> positions) {
		return new HashEntry(groupData, meta, positions.hashCode());
	}

	/**
	 * Method responsible for clearing out memory hogs
	 */
	public void clear() {
		map = new HashMap<MemorySummaryData.HashEntry, Aggregate>();
	}
}
