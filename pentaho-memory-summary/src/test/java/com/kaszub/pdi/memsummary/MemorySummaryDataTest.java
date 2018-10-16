package com.kaszub.pdi.memsummary;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.runners.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import org.pentaho.di.core.row.RowMetaInterface;
import org.pentaho.di.core.row.ValueMetaInterface;

import com.kaszub.pdi.memsummary.MemorySummaryData;

import java.util.HashMap;

import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.when;
import static org.junit.Assert.assertEquals;

/**
 * Created by bmorrise on 2/11/16.
 */
@RunWith(MockitoJUnitRunner.class)
public class MemorySummaryDataTest {

	private MemorySummaryData data = new MemorySummaryData();

	@Mock
	private RowMetaInterface groupMeta;
	@Mock
	private ValueMetaInterface valueMeta;

	@Before
	public void setUp() throws Exception {
		data.groupMeta = new RowMetaInterface[]{groupMeta};
		when(groupMeta.size()).thenReturn(1);
		when(groupMeta.getValueMeta(anyInt())).thenReturn(valueMeta);
		when(valueMeta.convertToNormalStorageType(anyObject())).then(new Answer<Object>() {
			@Override
			public Object answer(InvocationOnMock invocation) throws Throwable {
				Object argument = invocation.getArguments()[0];
				return new String((byte[]) argument);
			}
		});
	}

	@Test
	public void hashEntryTest() {
		HashMap<MemorySummaryData.HashEntry, String> map = new HashMap<>();

		byte[] byteValue1 = "key".getBytes();
		Object[] groupData1 = new Object[1];
		groupData1[0] = byteValue1;

		MemorySummaryData.HashEntry hashEntry1 = data.getHashEntry(groupData1, data.groupMeta[0]);
		map.put(hashEntry1, "value");

		byte[] byteValue2 = "key".getBytes();
		Object[] groupData2 = new Object[1];
		groupData2[0] = byteValue2;

		MemorySummaryData.HashEntry hashEntry2 = data.getHashEntry(groupData2, data.groupMeta[0]);

		String value = map.get(hashEntry2);

		assertEquals("value", value);
	}

}
