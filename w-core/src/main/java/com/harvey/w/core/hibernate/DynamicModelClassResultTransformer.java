package com.harvey.w.core.hibernate;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.sql.Blob;
import java.sql.Clob;
import java.util.List;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hibernate.transform.ResultTransformer;

import com.harvey.w.core.dao.utils.EntityUtils;
import com.harvey.w.core.model.DynamicModelClass;
import com.harvey.w.core.utils.BeanUtils;

public class DynamicModelClassResultTransformer implements ResultTransformer {

	/**
     * 
     */
	private static final long serialVersionUID = 1L;

	public static ResultTransformer getInstance() {
		return new DynamicModelClassResultTransformer();
	}

	private String[] fields;

	@Override
	@SuppressWarnings("rawtypes")
	public List transformList(List paramList) {
		return paramList;
	}

	private void initialFields(String[] aliases) {
		fields = new String[aliases.length];
		for (int i = 0; i < fields.length; i++) {
			String aliase = aliases[i].toLowerCase();
			if ("UUID__".equals(aliase)) {
				if (ArrayUtils.contains(aliases, "uuid")) {
					aliase = "uuid__";
				} else {
					aliase = "uuid";
				}
			} else {
				aliase = EntityUtils.toPascalCase(aliase, false);
			}
			fields[i] = aliase;
		}
	}

	@Override
	public Object transformTuple(Object[] tuple, String[] aliases) {
		if (fields == null || aliases.length != fields.length) {
			initialFields(aliases);
		}
		DynamicModelClass result = new DynamicModelClass();
		for (int i = 0; i < aliases.length; i++) {
			result.put(fields[i], convert(tuple[i]));
		}
		return result;
	}

	private Object convert(Object value) {
		if (value == null || BeanUtils.isSimpleValueType(value.getClass())) {
			return value;
		}
		if (value instanceof Clob) {
			return getClobValue((Clob) value);
		}
		if (value instanceof Reader) {
			return getReaderValue((Reader) value);
		}
		if (value instanceof InputStream) {
			return getInputStream((InputStream) value);
		}
		if (value instanceof Blob) {
			Blob blob = (Blob) value;
			return getBlobValue(blob);
		}
		return value;
	}

	private String getReaderValue(Reader reader) {
		try {
			return IOUtils.toString(reader);
		} catch (Exception e) {
			throw new RuntimeException("Unable to access lob stream", e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] getInputStream(InputStream stream) {
		try {
			return IOUtils.toByteArray(stream);
		} catch (Exception e) {
			throw new RuntimeException("Unable to access lob stream", e);
		} finally {
			try {
				stream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private byte[] getBlobValue(Blob value) {
		try {
			return getInputStream(value.getBinaryStream());
		} catch (Exception e) {
			throw new RuntimeException("Unable to access lob stream", e);
		}
	}

	private String getClobValue(Clob value) {
		try {
			Reader reader = value.getCharacterStream();
			return getReaderValue(reader);
		} catch (Exception e) {
			throw new RuntimeException("Unable to access lob stream", e);
		}
	}

}
