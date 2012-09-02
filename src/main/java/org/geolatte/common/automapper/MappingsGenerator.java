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

/**
 * This class creates a Hibernate mapping file for a list of tables.
 * <p/>
 * <p>This class is not thread-safe</p>
 *
 * @author Karel Maesen, Geovise BVBA (http://www.geovise.com/)
 */
class MappingsGenerator {

    final private DatabaseMapping databaseMapping;
    final private Document mappingDoc;

    MappingsGenerator(DatabaseMapping dbMapping) {
        databaseMapping = dbMapping;
        mappingDoc = buildDocument();

    }

    public Document getMappingsDocument() {
        return this.mappingDoc;
    }

    private Document buildDocument() {
        Document doc = initializeDocument();
        Element root = addRoot(doc);
        addTables(root);
        return doc;
    }

    private Document initializeDocument() {
        Document doc = DocumentHelper.createDocument();
        doc.addDocType("hibernate-mapping",
                "-//Hibernate/Hibernate Mapping DTD 3.0//EN",
                "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd");
        return doc;
    }

    private Element addRoot(Document doc) {
        Element root = doc.addElement("hibernate-mapping");
        root.addAttribute("package", databaseMapping.getPackageName());
        return root;
    }

    private void addTables(Element root) {
        for (TableRef tableRef : databaseMapping.getMappedTables()) {
            addTableElement(root, tableRef, databaseMapping);
        }
    }

    private void addTableElement(Element root, TableRef tableRef, DatabaseMapping databaseMapping) {
        TableMapping tableMapping = databaseMapping.getTableMapping(tableRef);
        Element tableEl = createTableElement(root, tableRef, tableMapping);
        ColumnMetaData idColumnMetaData = addIdentifierPropertyElement(tableMapping, tableEl);
        for (ColumnMetaData ai : tableMapping.getMappedColumns()) {
            if (ai.equals(idColumnMetaData)) continue;
            ColumnMapping cMapping = tableMapping.getColumnMapping(ai);
            addPropertyElement("property", tableEl, ai, cMapping);
        }
    }

    private Element createTableElement(Element root, TableRef tableRef, TableMapping tableMapping) {
        Element tableEl = root.addElement("class");
        tableEl.addAttribute("name", tableMapping.getSimpleName());
        tableEl.addAttribute("table", tableRef.getTableName());
        if (tableRef.getCatalog() != null) {
            tableEl.addAttribute("catalog", tableRef.getCatalog());
        }
        if (tableRef.getSchema() != null) {
            tableEl.addAttribute("schema", tableRef.getSchema());
        }
        return tableEl;
    }

    private ColumnMetaData addIdentifierPropertyElement(TableMapping tableMapping, Element tableEl) {
        ColumnMetaData idColumnMetaData = tableMapping.getIdentifierColumn();
        addPropertyElement("id", tableEl, idColumnMetaData, tableMapping.getColumnMapping(idColumnMetaData));
        return idColumnMetaData;
    }

    private void addPropertyElement(String type, Element tableEl, ColumnMetaData ai, ColumnMapping cMapping) {
        Element colEl = tableEl.addElement(type);
        colEl.addAttribute("name", cMapping.getPropertyName());
        colEl.addAttribute("type", cMapping.getHibernateType());
        colEl.addAttribute("column", ai.getColumnName());
    }

}
