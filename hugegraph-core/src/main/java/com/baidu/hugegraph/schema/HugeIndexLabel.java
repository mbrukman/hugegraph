package com.baidu.hugegraph.schema;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.baidu.hugegraph.HugeException;
import com.baidu.hugegraph.backend.tx.SchemaTransaction;
import com.baidu.hugegraph.type.HugeTypes;
import com.baidu.hugegraph.type.define.IndexType;
import com.baidu.hugegraph.type.schema.IndexLabel;
import com.baidu.hugegraph.util.StringUtil;

/**
 * Created by liningrui on 2017/4/21.
 */
public class HugeIndexLabel extends IndexLabel {

    private String indexName;
    private HugeTypes baseType;
    private String baseValue;
    private IndexType indexType;
    private Set<String> indexFields;

    public HugeIndexLabel(String indexName, HugeTypes baseType, String name, SchemaTransaction transaction) {
        super(name, transaction);
        this.indexName = indexName;
        this.baseType = baseType;
        this.baseValue = name;
        this.indexFields = new HashSet<>();
    }

    @Override
    public String indexName() {
        return indexName;
    }

    @Override
    public HugeTypes baseType() {
        return this.baseType;
    }

    @Override
    public String baseValue() {
        return baseValue;
    }

    @Override
    public IndexType indexType() {
        return this.indexType;
    }

    public void indexType(IndexType indexType) {
        this.indexType = indexType;
    }

    @Override
    public Set<String> indexFields() {
        return this.indexFields;
    }

    @Override
    public IndexLabel by(String... indexFields) {
        for (String indexFiled : indexFields) {
            this.indexFields.add(indexFiled);
        }
        return this;
    }

    @Override
    public IndexLabel secondary() {
        this.indexType = IndexType.SECONDARY;
        return this;
    }

    @Override
    public IndexLabel range() {
        this.indexType = IndexType.RANGE;
        return this;
    }

    @Override
    public IndexLabel search() {
        this.indexType = IndexType.SEARCH;
        return this;
    }

    @Override
    public String schema() {
        String schema = "";
        schema = ".index(\"" + this.indexName + "\")"
                + StringUtil.descSchema("by", this.indexFields)
                + "." + this.indexType.string() + "()";
        return schema;
    }

    @Override
    public void create() {
        if (this.transaction.getIndexLabel(this.name) != null) {
            throw new HugeException("The indexLabel:" + this.name + " has exised.");
        }

        // TODO: need to check param.
        this.transaction.addIndexLabel(this);
        this.commit();
    }

    @Override
    public void remove() {

    }
}