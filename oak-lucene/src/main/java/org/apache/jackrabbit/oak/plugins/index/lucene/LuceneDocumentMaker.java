begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one  * or more contributor license agreements.  See the NOTICE file  * distributed with this work for additional information  * regarding copyright ownership.  The ASF licenses this file  * to you under the Apache License, Version 2.0 (the  * "License"); you may not use this file except in compliance  * with the License.  You may obtain a copy of the License at  *  *   http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing,  * software distributed under the License is distributed on an  * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY  * KIND, either express or implied.  See the License for the  * specific language governing permissions and limitations  * under the License.  */
end_comment

begin_package
package|package
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|IOException
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|List
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Blob
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|PropertyState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|api
operator|.
name|Type
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|commons
operator|.
name|PathUtils
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|util
operator|.
name|FacetsConfigProvider
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|Aggregate
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|FieldNames
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|IndexDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|IndexFormatVersion
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|PropertyDefinition
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|spi
operator|.
name|binary
operator|.
name|FulltextBinaryTextExtractor
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|spi
operator|.
name|editor
operator|.
name|FulltextDocumentMaker
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|NodeState
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Document
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DoubleDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|DoubleField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|Field
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|LongField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|NumericDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|SortedDocValuesField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|document
operator|.
name|StringField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|FacetsConfig
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|facet
operator|.
name|sortedset
operator|.
name|SortedSetDocValuesFacetField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|index
operator|.
name|IndexableField
import|;
end_import

begin_import
import|import
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|util
operator|.
name|BytesRef
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|Logger
import|;
end_import

begin_import
import|import
name|org
operator|.
name|slf4j
operator|.
name|LoggerFactory
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newAncestorsField
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newDepthField
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newFulltextField
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newPathField
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|plugins
operator|.
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newPropertyField
import|;
end_import

begin_class
specifier|public
class|class
name|LuceneDocumentMaker
extends|extends
name|FulltextDocumentMaker
argument_list|<
name|Document
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneDocumentMaker
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|FacetsConfigProvider
name|facetsConfigProvider
decl_stmt|;
specifier|private
specifier|final
name|IndexAugmentorFactory
name|augmentorFactory
decl_stmt|;
specifier|public
name|LuceneDocumentMaker
parameter_list|(
name|IndexDefinition
name|definition
parameter_list|,
name|IndexDefinition
operator|.
name|IndexingRule
name|indexingRule
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
argument_list|(
literal|null
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|,
name|definition
argument_list|,
name|indexingRule
argument_list|,
name|path
argument_list|)
expr_stmt|;
block|}
specifier|public
name|LuceneDocumentMaker
parameter_list|(
annotation|@
name|Nullable
name|FulltextBinaryTextExtractor
name|textExtractor
parameter_list|,
annotation|@
name|Nullable
name|FacetsConfigProvider
name|facetsConfigProvider
parameter_list|,
annotation|@
name|Nullable
name|IndexAugmentorFactory
name|augmentorFactory
parameter_list|,
name|IndexDefinition
name|definition
parameter_list|,
name|IndexDefinition
operator|.
name|IndexingRule
name|indexingRule
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|super
argument_list|(
name|textExtractor
argument_list|,
name|definition
argument_list|,
name|indexingRule
argument_list|,
name|path
argument_list|)
expr_stmt|;
name|this
operator|.
name|facetsConfigProvider
operator|=
name|facetsConfigProvider
expr_stmt|;
name|this
operator|.
name|augmentorFactory
operator|=
name|augmentorFactory
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexAnalyzedProperty
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|pname
parameter_list|,
name|String
name|value
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|)
block|{
name|String
name|analyzedPropName
init|=
name|constructAnalyzedPropertyName
argument_list|(
name|pname
argument_list|)
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newPropertyField
argument_list|(
name|analyzedPropName
argument_list|,
name|value
argument_list|,
operator|!
name|pd
operator|.
name|skipTokenization
argument_list|(
name|pname
argument_list|)
argument_list|,
name|pd
operator|.
name|stored
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexSuggestValue
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|FieldFactory
operator|.
name|newSuggestField
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexSpellcheckValue
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newPropertyField
argument_list|(
name|FieldNames
operator|.
name|SPELLCHECK
argument_list|,
name|value
argument_list|,
literal|true
argument_list|,
literal|false
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexFulltextValue
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexAncestors
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newAncestorsField
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newDepthField
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|indexTypedProperty
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|String
name|pname
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|)
block|{
name|int
name|tag
init|=
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
decl_stmt|;
name|boolean
name|fieldAdded
init|=
literal|false
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|property
operator|.
name|count
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|Field
name|f
decl_stmt|;
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|LONG
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|LongField
argument_list|(
name|pname
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|,
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DATE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|String
name|date
init|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|,
name|i
argument_list|)
decl_stmt|;
name|f
operator|=
operator|new
name|LongField
argument_list|(
name|pname
argument_list|,
name|FieldFactory
operator|.
name|dateToLong
argument_list|(
name|date
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DOUBLE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|DoubleField
argument_list|(
name|pname
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|,
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|BOOLEAN
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|StringField
argument_list|(
name|pname
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|,
name|i
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|f
operator|=
operator|new
name|StringField
argument_list|(
name|pname
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|,
name|i
argument_list|)
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|includePropertyValue
argument_list|(
name|property
argument_list|,
name|i
argument_list|,
name|pd
argument_list|)
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fieldAdded
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|fieldAdded
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexNotNullProperty
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|FieldNames
operator|.
name|NOT_NULL_PROPS
argument_list|,
name|pd
operator|.
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexNullProperty
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|FieldNames
operator|.
name|NULL_PROPS
argument_list|,
name|pd
operator|.
name|name
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
name|String
name|constructAnalyzedPropertyName
parameter_list|(
name|String
name|pname
parameter_list|)
block|{
if|if
condition|(
name|definition
operator|.
name|getVersion
argument_list|()
operator|.
name|isAtLeast
argument_list|(
name|IndexFormatVersion
operator|.
name|V2
argument_list|)
condition|)
block|{
return|return
name|FieldNames
operator|.
name|createAnalyzedFieldName
argument_list|(
name|pname
argument_list|)
return|;
block|}
return|return
name|pname
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|addBinary
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|path
parameter_list|,
name|List
argument_list|<
name|String
argument_list|>
name|binaryValues
parameter_list|)
block|{
name|boolean
name|added
init|=
literal|false
decl_stmt|;
for|for
control|(
name|String
name|binaryValue
range|:
name|binaryValues
control|)
block|{
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|path
argument_list|,
name|binaryValue
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|doc
operator|.
name|add
argument_list|(
name|newFulltextField
argument_list|(
name|binaryValue
argument_list|,
literal|true
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|added
operator|=
literal|true
expr_stmt|;
block|}
return|return
name|added
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|indexFacetProperty
parameter_list|(
name|Document
name|doc
parameter_list|,
name|int
name|tag
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|String
name|pname
parameter_list|)
block|{
name|String
name|facetFieldName
init|=
name|FieldNames
operator|.
name|createFacetFieldName
argument_list|(
name|pname
argument_list|)
decl_stmt|;
name|getFacetsConfig
argument_list|()
operator|.
name|setIndexFieldName
argument_list|(
name|pname
argument_list|,
name|facetFieldName
argument_list|)
expr_stmt|;
name|boolean
name|fieldAdded
init|=
literal|false
decl_stmt|;
try|try
block|{
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|STRINGS
operator|.
name|tag
argument_list|()
operator|&&
name|property
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|getFacetsConfig
argument_list|()
operator|.
name|setMultiValued
argument_list|(
name|pname
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|Iterable
argument_list|<
name|String
argument_list|>
name|values
init|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|value
range|:
name|values
control|)
block|{
if|if
condition|(
name|value
operator|!=
literal|null
operator|&&
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
name|pname
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|fieldAdded
operator|=
literal|true
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|STRING
operator|.
name|tag
argument_list|()
condition|)
block|{
name|String
name|value
init|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|SortedSetDocValuesFacetField
argument_list|(
name|pname
argument_list|,
name|value
argument_list|)
argument_list|)
expr_stmt|;
name|fieldAdded
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Throwable
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Ignoring facet property. Could not convert property {} of type {} to type {} for path {}"
argument_list|,
name|getIndexName
argument_list|()
argument_list|,
name|pname
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|tag
argument_list|,
literal|false
argument_list|)
argument_list|,
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldAdded
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexAggregateValue
parameter_list|(
name|Document
name|doc
parameter_list|,
name|Aggregate
operator|.
name|NodeIncludeResult
name|result
parameter_list|,
name|String
name|value
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|)
block|{
name|Field
name|field
init|=
name|result
operator|.
name|isRelativeNode
argument_list|()
condition|?
name|newFulltextField
argument_list|(
name|result
operator|.
name|rootIncludePath
argument_list|,
name|value
argument_list|)
else|:
name|newFulltextField
argument_list|(
name|value
argument_list|)
decl_stmt|;
if|if
condition|(
name|pd
operator|!=
literal|null
condition|)
block|{
name|field
operator|.
name|setBoost
argument_list|(
name|pd
operator|.
name|boost
argument_list|)
expr_stmt|;
block|}
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|Document
name|initDoc
parameter_list|()
block|{
name|Document
name|doc
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|doc
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|augmentCustomFields
parameter_list|(
specifier|final
name|String
name|path
parameter_list|,
specifier|final
name|Document
name|doc
parameter_list|,
specifier|final
name|NodeState
name|document
parameter_list|)
block|{
name|boolean
name|dirty
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|augmentorFactory
operator|!=
literal|null
condition|)
block|{
name|Iterable
argument_list|<
name|Field
argument_list|>
name|augmentedFields
init|=
name|augmentorFactory
operator|.
name|getIndexFieldProvider
argument_list|(
name|indexingRule
operator|.
name|getNodeTypeName
argument_list|()
argument_list|)
operator|.
name|getAugmentedFields
argument_list|(
name|path
argument_list|,
name|document
argument_list|,
name|definition
operator|.
name|getDefinitionNodeState
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Field
name|field
range|:
name|augmentedFields
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|field
argument_list|)
expr_stmt|;
name|dirty
operator|=
literal|true
expr_stmt|;
block|}
block|}
return|return
name|dirty
return|;
block|}
annotation|@
name|Override
specifier|protected
name|Document
name|finalizeDoc
parameter_list|(
name|Document
name|doc
parameter_list|,
name|boolean
name|dirty
parameter_list|,
name|boolean
name|facet
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|facet
operator|&&
name|isFacetingEnabled
argument_list|()
condition|)
block|{
name|doc
operator|=
name|getFacetsConfig
argument_list|()
operator|.
name|build
argument_list|(
name|doc
argument_list|)
expr_stmt|;
block|}
name|List
argument_list|<
name|IndexableField
argument_list|>
name|fields
init|=
name|doc
operator|.
name|getFields
argument_list|()
decl_stmt|;
comment|// because of LUCENE-5833 we have to merge the suggest fields into a single one
name|Field
name|suggestField
init|=
literal|null
decl_stmt|;
for|for
control|(
name|IndexableField
name|f
range|:
name|fields
control|)
block|{
if|if
condition|(
name|FieldNames
operator|.
name|SUGGEST
operator|.
name|equals
argument_list|(
name|f
operator|.
name|name
argument_list|()
argument_list|)
condition|)
block|{
if|if
condition|(
name|suggestField
operator|==
literal|null
condition|)
block|{
name|suggestField
operator|=
name|FieldFactory
operator|.
name|newSuggestField
argument_list|(
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|suggestField
operator|=
name|FieldFactory
operator|.
name|newSuggestField
argument_list|(
name|suggestField
operator|.
name|stringValue
argument_list|()
argument_list|,
name|f
operator|.
name|stringValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|doc
operator|.
name|removeFields
argument_list|(
name|FieldNames
operator|.
name|SUGGEST
argument_list|)
expr_stmt|;
if|if
condition|(
name|suggestField
operator|!=
literal|null
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|suggestField
argument_list|)
expr_stmt|;
block|}
return|return
name|doc
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|isFacetingEnabled
parameter_list|()
block|{
return|return
name|facetsConfigProvider
operator|!=
literal|null
return|;
block|}
annotation|@
name|Override
specifier|protected
name|boolean
name|indexTypeOrderedFields
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|pname
parameter_list|,
name|int
name|tag
parameter_list|,
name|PropertyState
name|property
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|)
block|{
name|String
name|name
init|=
name|FieldNames
operator|.
name|createDocValFieldName
argument_list|(
name|pname
argument_list|)
decl_stmt|;
name|boolean
name|fieldAdded
init|=
literal|false
decl_stmt|;
name|Field
name|f
init|=
literal|null
decl_stmt|;
try|try
block|{
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|LONG
operator|.
name|tag
argument_list|()
condition|)
block|{
comment|//TODO Distinguish fields which need to be used for search and for sort
comment|//If a field is only used for Sort then it can be stored with less precision
name|f
operator|=
operator|new
name|NumericDocValuesField
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|LONG
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DATE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|String
name|date
init|=
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DATE
argument_list|)
decl_stmt|;
name|f
operator|=
operator|new
name|NumericDocValuesField
argument_list|(
name|name
argument_list|,
name|FieldFactory
operator|.
name|dateToLong
argument_list|(
name|date
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|DOUBLE
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|DoubleDocValuesField
argument_list|(
name|name
argument_list|,
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|DOUBLE
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|BOOLEAN
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|SortedDocValuesField
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|tag
operator|==
name|Type
operator|.
name|STRING
operator|.
name|tag
argument_list|()
condition|)
block|{
name|f
operator|=
operator|new
name|SortedDocValuesField
argument_list|(
name|name
argument_list|,
operator|new
name|BytesRef
argument_list|(
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|f
operator|!=
literal|null
operator|&&
name|includePropertyValue
argument_list|(
name|property
argument_list|,
literal|0
argument_list|,
name|pd
argument_list|)
condition|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
name|fieldAdded
operator|=
literal|true
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"[{}] Ignoring ordered property. Could not convert property {} of type {} to type {} for path {}"
argument_list|,
name|getIndexName
argument_list|()
argument_list|,
name|pname
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|,
literal|false
argument_list|)
argument_list|,
name|Type
operator|.
name|fromTag
argument_list|(
name|tag
argument_list|,
literal|false
argument_list|)
argument_list|,
name|path
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
return|return
name|fieldAdded
return|;
block|}
specifier|private
name|FacetsConfig
name|getFacetsConfig
parameter_list|()
block|{
return|return
name|facetsConfigProvider
operator|.
name|getFacetsConfig
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexNodeName
parameter_list|(
name|Document
name|doc
parameter_list|,
name|String
name|value
parameter_list|)
block|{
name|doc
operator|.
name|add
argument_list|(
operator|new
name|StringField
argument_list|(
name|FieldNames
operator|.
name|NODE_NAME
argument_list|,
name|value
argument_list|,
name|Field
operator|.
name|Store
operator|.
name|NO
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexSimilarityStrings
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|,
name|String
name|value
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Field
name|f
range|:
name|FieldFactory
operator|.
name|newSimilarityFields
argument_list|(
name|pd
operator|.
name|name
argument_list|,
name|value
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pd
operator|.
name|similarityRerank
condition|)
block|{
for|for
control|(
name|Field
name|f
range|:
name|FieldFactory
operator|.
name|newBinSimilarityFields
argument_list|(
name|pd
operator|.
name|name
argument_list|,
name|value
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|protected
name|void
name|indexSimilarityBinaries
parameter_list|(
name|Document
name|doc
parameter_list|,
name|PropertyDefinition
name|pd
parameter_list|,
name|Blob
name|blob
parameter_list|)
throws|throws
name|IOException
block|{
for|for
control|(
name|Field
name|f
range|:
name|FieldFactory
operator|.
name|newSimilarityFields
argument_list|(
name|pd
operator|.
name|name
argument_list|,
name|blob
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|pd
operator|.
name|similarityRerank
condition|)
block|{
for|for
control|(
name|Field
name|f
range|:
name|FieldFactory
operator|.
name|newBinSimilarityFields
argument_list|(
name|pd
operator|.
name|name
argument_list|,
name|blob
argument_list|)
control|)
block|{
name|doc
operator|.
name|add
argument_list|(
name|f
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
end_class

end_unit

