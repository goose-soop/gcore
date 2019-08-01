package com.guillaumevdn.gcore.lib.data.mysql;

import java.util.ArrayList;
import java.util.List;

public class Query {

	// base
	private String query = "";
	private List<String> stringParams = new ArrayList<String>();

	public Query() {
	}

	public Query(String query, String... stringParams) {
		add(query, stringParams);
	}

	// get
	public String getQuery() {
		return query;
	}

	public List<String> getStringParams() {
		return stringParams;
	}

	public boolean isEmpty() {
		return query.isEmpty();
	}

	// methods
	public void add(String query, String... stringParams) {
		this.query += query;
		if (stringParams != null) {
			for (String stringParam : stringParams) {
				this.stringParams.add(stringParam);
			}
		}
	}

	public void add(Query query) {
		this.query += query.query;
		this.stringParams.addAll(query.stringParams);
	}

}
