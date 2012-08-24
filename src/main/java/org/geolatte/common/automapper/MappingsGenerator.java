/*
 * This file is part of the GeoLatte project.
 *
 *     GeoLatte is free software: you can redistribute it and/or modify
 *     it under the terms of the GNU Lesser General Public License as published by
 *     the Free Software Foundation, either version 3 of the License, or
 *     (at your option) any later version.
 *
 *     GeoLatte is distributed in the hope that it will be useful,
 *     but WITHOUT ANY WARRANTY; without even the implied warranty of
 *     MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *     GNU Lesser General Public License for more details.
 *
 *     You should have received a copy of the GNU Lesser General Public License
 *     along with GeoLatte.  If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2010 - 2012 and Ownership of code is shared by:
 * Qmino bvba - Romeinsestraat 18 - 3001 Heverlee  (http://www.qmino.com)
 * Geovise bvba - Generaal Eisenhowerlei 9 - 2140 Antwerpen (http://www.geovise.com)
 */
package org.geolatte.common.automapper;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.io.OutputFormat;
import org.dom4j.io.XMLWriter;

import java.io.IOException;
import java.io.Writer;
import java.util.Map;

/**
 * This class creates a Hibernate mapping file for a list of tables.
 * 
 * @author Karel Maesen, Geovise BVBA (http://www.geovise.com/)
 */
public class MappingsGenerator {

	private Document mappingDoc;

	private String packageName;

	public MappingsGenerator(String packageName) {
		this.packageName = packageName;
	}

	public void write(Writer writer) throws IOException {
		OutputFormat format = OutputFormat.createPrettyPrint();
		XMLWriter xmlWriter = new XMLWriter(writer, format);
		xmlWriter.write(this.mappingDoc);
		xmlWriter.close();
	}

	public Document getMappingsDoc() {
		return this.mappingDoc;
	}

	public void load(Map<TableRef,ClassInfo> mapping) {

		this.mappingDoc = DocumentHelper.createDocument();
		this.mappingDoc.addDocType("hibernate-mapping",
				"-//Hibernate/Hibernate Mapping DTD 3.0//EN",
				"http://hibernate.sourceforge.net/hibernate-mapping-3.0.dtd");
		Element root = this.mappingDoc.addElement("hibernate-mapping");
    	root.addAttribute("package", this.packageName);
		for (Map.Entry<TableRef, ClassInfo> entry : mapping.entrySet()) {
			addTableElement(root, entry.getKey(), entry.getValue());
		}
	}

	private void addTableElement(Element root, TableRef tableRef, ClassInfo classInfo) {
		Element tableEl = root.addElement("class");
		tableEl.addAttribute("name", classInfo.getClassName());
		tableEl.addAttribute("table", classInfo.getTableName());
        if (tableRef.getCatalog() != null) {
            tableEl.addAttribute("catalog", tableRef.getCatalog());
        }
        if (tableRef.getSchema() != null) {
            tableEl.addAttribute("schema", tableRef.getSchema());
        }
//		addColElement(tableEl, classInfo.getIdAttribute());
		for (AttributeInfo ai : classInfo.getAttributes()) {
            addColElement(tableEl, ai);
		}

	}

	private void addColElement(Element tableEl, AttributeInfo ai) {
		Element colEl = null;
		if (ai.isIdentifier()) {
			colEl = tableEl.addElement("id");
		} else {
			colEl = tableEl.addElement("property");
		}
		colEl.addAttribute("name", ai.getFieldName());
		colEl.addAttribute("column", ai.getColumnName());
		colEl.addAttribute("type", ai.getHibernateType());
		return;
	}


}
