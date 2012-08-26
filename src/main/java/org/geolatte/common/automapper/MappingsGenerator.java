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
 *
 * @author Karel Maesen, Geovise BVBA (http://www.geovise.com/)
 */
class MappingsGenerator {

    private Document mappingDoc;

    public MappingsGenerator() {
    }

    public Document getMappingsDoc() {
        return this.mappingDoc;
    }

    public void load(AutoMapper autoMapper) {

        this.mappingDoc = DocumentHelper.createDocument();
        this.mappingDoc.addDocType("hibernate-mapping",
                "-//Hibernate/Hibernate Mapping DTD 3.0//EN",
                "http://www.hibernate.org/dtd/hibernate-mapping-3.0.dtd");
        Element root = this.mappingDoc.addElement("hibernate-mapping");
        root.addAttribute("package", autoMapper.getPackageName());
        for (TableRef tableRef : autoMapper.getMappedTables()) {
            addTableElement(root, tableRef, autoMapper);
        }
    }

    private void addTableElement(Element root, TableRef tableRef, AutoMapper autoMapper) {
        Element tableEl = root.addElement("class");
        TableMapping tableMapping = autoMapper.getMappedClass(tableRef);
        tableEl.addAttribute("name", tableMapping.getSimpleName());
        tableEl.addAttribute("table", tableRef.getTableName());
        if (tableRef.getCatalog() != null) {
            tableEl.addAttribute("catalog", tableRef.getCatalog());
        }
        if (tableRef.getSchema() != null) {
            tableEl.addAttribute("schema", tableRef.getSchema());
        }

        Attribute idAttribute = tableMapping.getIdentifer();
        addElement("id", tableEl, idAttribute, tableMapping);
        for (Attribute ai : tableMapping.getMappedAttributes()) {
            if (ai.equals(idAttribute)) continue;
            addElement("property", tableEl, ai, tableMapping);
        }

    }

    private void addElement(String type, Element tableEl, Attribute ai, TableMapping tableMapping) {
        Element colEl = tableEl.addElement(type);
        colEl.addAttribute("name", tableMapping.getPropertyName(ai));
        colEl.addAttribute("type", tableMapping.getHibernateType(ai));
        colEl.addAttribute("column", ai.getColumnName());
    }

}
