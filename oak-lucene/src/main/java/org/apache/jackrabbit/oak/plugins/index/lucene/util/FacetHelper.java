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
operator|.
name|util
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
name|HashMap
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
name|java
operator|.
name|util
operator|.
name|Map
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
operator|.
name|SecureFacetConfiguration
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
name|query
operator|.
name|QueryConstants
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
name|query
operator|.
name|QueryIndex
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
name|NodeBuilder
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
name|FacetResult
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
name|Facets
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
name|FacetsCollector
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
name|MultiFacets
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
name|DefaultSortedSetDocValuesReaderState
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
name|SortedSetDocValuesFacetCounts
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
name|search
operator|.
name|IndexSearcher
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
name|search
operator|.
name|Query
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
name|search
operator|.
name|Sort
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

begin_comment
comment|/**  *  */
end_comment

begin_class
specifier|public
class|class
name|FacetHelper
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOGGER
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|FacetHelper
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * IndexPaln Attribute name which refers to the name of the fields that should be used for facets.      */
specifier|public
specifier|static
specifier|final
name|String
name|ATTR_FACET_FIELDS
init|=
literal|"oak.facet.fields"
decl_stmt|;
specifier|private
name|FacetHelper
parameter_list|()
block|{     }
specifier|public
specifier|static
name|FacetsConfig
name|getFacetsConfig
parameter_list|(
name|NodeBuilder
name|definition
parameter_list|)
block|{
return|return
operator|new
name|NodeStateFacetsConfig
argument_list|(
name|definition
argument_list|)
return|;
block|}
specifier|public
specifier|static
name|Facets
name|getFacets
parameter_list|(
name|IndexSearcher
name|searcher
parameter_list|,
name|Query
name|query
parameter_list|,
name|QueryIndex
operator|.
name|IndexPlan
name|plan
parameter_list|,
name|SecureFacetConfiguration
name|secureFacetConfiguration
parameter_list|)
throws|throws
name|IOException
block|{
name|Facets
name|facets
init|=
literal|null
decl_stmt|;
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
name|List
argument_list|<
name|String
argument_list|>
name|facetFields
init|=
operator|(
name|List
argument_list|<
name|String
argument_list|>
operator|)
name|plan
operator|.
name|getAttribute
argument_list|(
name|ATTR_FACET_FIELDS
argument_list|)
decl_stmt|;
if|if
condition|(
name|facetFields
operator|!=
literal|null
operator|&&
name|facetFields
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|Map
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
name|facetsMap
init|=
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|Facets
argument_list|>
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|facetField
range|:
name|facetFields
control|)
block|{
name|FacetsCollector
name|facetsCollector
init|=
operator|new
name|FacetsCollector
argument_list|()
decl_stmt|;
try|try
block|{
name|DefaultSortedSetDocValuesReaderState
name|state
init|=
operator|new
name|DefaultSortedSetDocValuesReaderState
argument_list|(
name|searcher
operator|.
name|getIndexReader
argument_list|()
argument_list|,
name|FieldNames
operator|.
name|createFacetFieldName
argument_list|(
name|facetField
argument_list|)
argument_list|)
decl_stmt|;
name|FacetsCollector
operator|.
name|search
argument_list|(
name|searcher
argument_list|,
name|query
argument_list|,
literal|null
argument_list|,
literal|1
argument_list|,
name|Sort
operator|.
name|INDEXORDER
argument_list|,
name|facetsCollector
argument_list|)
expr_stmt|;
switch|switch
condition|(
name|secureFacetConfiguration
operator|.
name|getMode
argument_list|()
condition|)
block|{
case|case
name|INSECURE
case|:
name|facets
operator|=
operator|new
name|SortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|facetsCollector
argument_list|)
expr_stmt|;
break|break;
case|case
name|STATISTICAL
case|:
name|facets
operator|=
operator|new
name|StatisticalSortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|facetsCollector
argument_list|,
name|plan
operator|.
name|getFilter
argument_list|()
argument_list|,
name|secureFacetConfiguration
argument_list|)
expr_stmt|;
break|break;
case|case
name|SECURE
case|:
default|default:
name|facets
operator|=
operator|new
name|SecureSortedSetDocValuesFacetCounts
argument_list|(
name|state
argument_list|,
name|facetsCollector
argument_list|,
name|plan
operator|.
name|getFilter
argument_list|()
argument_list|)
expr_stmt|;
break|break;
block|}
name|facetsMap
operator|.
name|put
argument_list|(
name|facetField
argument_list|,
name|facets
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|iae
parameter_list|)
block|{
name|LOGGER
operator|.
name|warn
argument_list|(
literal|"facets for {} not yet indexed"
argument_list|,
name|facetField
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|facetsMap
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|facets
operator|=
operator|new
name|MultiFacets
argument_list|(
name|facetsMap
argument_list|,
name|NULL_FACETS
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|facets
return|;
block|}
specifier|public
specifier|static
name|String
name|parseFacetField
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
return|return
name|columnName
operator|.
name|substring
argument_list|(
name|QueryConstants
operator|.
name|REP_FACET
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|,
name|columnName
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
return|;
block|}
specifier|private
specifier|static
specifier|final
name|Facets
name|NULL_FACETS
init|=
operator|new
name|Facets
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|FacetResult
name|getTopChildren
parameter_list|(
name|int
name|topN
parameter_list|,
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|Number
name|getSpecificValue
parameter_list|(
name|String
name|dim
parameter_list|,
name|String
modifier|...
name|path
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|List
argument_list|<
name|FacetResult
argument_list|>
name|getAllDims
parameter_list|(
name|int
name|topN
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
block|}
decl_stmt|;
block|}
end_class

end_unit

