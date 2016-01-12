begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *     http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|solr
operator|.
name|query
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collection
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
name|JcrConstants
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|configuration
operator|.
name|OakSolrConfiguration
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
name|query
operator|.
name|QueryImpl
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextAnd
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextContains
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextExpression
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextOr
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextTerm
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
name|query
operator|.
name|fulltext
operator|.
name|FullTextVisitor
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
name|Filter
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
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrQuery
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
name|commons
operator|.
name|PathUtils
operator|.
name|getName
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
name|solr
operator|.
name|util
operator|.
name|SolrUtils
operator|.
name|getSortingField
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
name|solr
operator|.
name|util
operator|.
name|SolrUtils
operator|.
name|partialEscape
import|;
end_import

begin_comment
comment|/**  * the {@link org.apache.jackrabbit.oak.plugins.index.solr.query.FilterQueryParser} can parse {@link org.apache.jackrabbit.oak.spi.query.Filter}s  * and transform them into {@link org.apache.solr.client.solrj.SolrQuery}s and / or Solr query {@code String}s.  */
end_comment

begin_class
class|class
name|FilterQueryParser
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
name|FilterQueryParser
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
name|SolrQuery
name|getQuery
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|List
argument_list|<
name|QueryIndex
operator|.
name|OrderEntry
argument_list|>
name|sortOrder
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|)
block|{
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|setDefaults
argument_list|(
name|solrQuery
argument_list|,
name|configuration
argument_list|)
expr_stmt|;
name|StringBuilder
name|queryBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|FullTextExpression
name|ft
init|=
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
decl_stmt|;
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|parseFullTextExpression
argument_list|(
name|ft
argument_list|,
name|configuration
argument_list|)
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|filter
operator|.
name|getFulltextConditions
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|Collection
argument_list|<
name|String
argument_list|>
name|fulltextConditions
init|=
name|filter
operator|.
name|getFulltextConditions
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|fulltextCondition
range|:
name|fulltextConditions
control|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|fulltextCondition
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|sortOrder
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|QueryIndex
operator|.
name|OrderEntry
name|orderEntry
range|:
name|sortOrder
control|)
block|{
name|SolrQuery
operator|.
name|ORDER
name|order
decl_stmt|;
if|if
condition|(
name|QueryIndex
operator|.
name|OrderEntry
operator|.
name|Order
operator|.
name|ASCENDING
operator|.
name|equals
argument_list|(
name|orderEntry
operator|.
name|getOrder
argument_list|()
argument_list|)
condition|)
block|{
name|order
operator|=
name|SolrQuery
operator|.
name|ORDER
operator|.
name|asc
expr_stmt|;
block|}
else|else
block|{
name|order
operator|=
name|SolrQuery
operator|.
name|ORDER
operator|.
name|desc
expr_stmt|;
block|}
name|String
name|sortingField
decl_stmt|;
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_PATH
operator|.
name|equals
argument_list|(
name|orderEntry
operator|.
name|getPropertyName
argument_list|()
argument_list|)
condition|)
block|{
name|sortingField
operator|=
name|partialEscape
argument_list|(
name|configuration
operator|.
name|getPathField
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|JcrConstants
operator|.
name|JCR_SCORE
operator|.
name|equals
argument_list|(
name|orderEntry
operator|.
name|getPropertyName
argument_list|()
argument_list|)
condition|)
block|{
name|sortingField
operator|=
literal|"score"
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|orderEntry
operator|.
name|getPropertyName
argument_list|()
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"cannot sort on relative properties, ignoring {} clause"
argument_list|,
name|orderEntry
argument_list|)
expr_stmt|;
continue|continue;
comment|// sorting by relative properties not supported until index time aggregation is supported
block|}
name|sortingField
operator|=
name|partialEscape
argument_list|(
name|getSortingField
argument_list|(
name|orderEntry
operator|.
name|getPropertyType
argument_list|()
operator|.
name|tag
argument_list|()
argument_list|,
name|orderEntry
operator|.
name|getPropertyName
argument_list|()
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|solrQuery
operator|.
name|addOrUpdateSort
argument_list|(
name|sortingField
argument_list|,
name|order
argument_list|)
expr_stmt|;
block|}
block|}
name|Collection
argument_list|<
name|Filter
operator|.
name|PropertyRestriction
argument_list|>
name|propertyRestrictions
init|=
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
decl_stmt|;
if|if
condition|(
name|propertyRestrictions
operator|!=
literal|null
operator|&&
operator|!
name|propertyRestrictions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|Filter
operator|.
name|PropertyRestriction
name|pr
range|:
name|propertyRestrictions
control|)
block|{
if|if
condition|(
name|pr
operator|.
name|isNullRestriction
argument_list|()
condition|)
block|{
comment|// can not use full "x is null"
continue|continue;
block|}
comment|// facets
if|if
condition|(
name|QueryImpl
operator|.
name|REP_FACET
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
condition|)
block|{
name|solrQuery
operator|.
name|setFacetMinCount
argument_list|(
literal|1
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setFacet
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|pr
operator|.
name|first
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
decl_stmt|;
name|solrQuery
operator|.
name|addFacetField
argument_list|(
name|value
operator|.
name|substring
argument_list|(
name|QueryImpl
operator|.
name|REP_FACET
operator|.
name|length
argument_list|()
operator|+
literal|1
argument_list|,
name|value
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
operator|+
literal|"_facet"
argument_list|)
expr_stmt|;
block|}
comment|// native query support
if|if
condition|(
name|SolrQueryIndex
operator|.
name|NATIVE_SOLR_QUERY
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
operator|||
name|SolrQueryIndex
operator|.
name|NATIVE_LUCENE_QUERY
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
condition|)
block|{
name|String
name|nativeQueryString
init|=
name|String
operator|.
name|valueOf
argument_list|(
name|pr
operator|.
name|first
operator|.
name|getValue
argument_list|(
name|pr
operator|.
name|first
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|isSupportedHttpRequest
argument_list|(
name|nativeQueryString
argument_list|)
condition|)
block|{
comment|// pass through the native HTTP Solr request
name|String
name|requestHandlerString
init|=
name|nativeQueryString
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|nativeQueryString
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
literal|"select"
operator|.
name|equals
argument_list|(
name|requestHandlerString
argument_list|)
condition|)
block|{
if|if
condition|(
name|requestHandlerString
operator|.
name|charAt
argument_list|(
literal|0
argument_list|)
operator|!=
literal|'/'
condition|)
block|{
name|requestHandlerString
operator|=
literal|"/"
operator|+
name|requestHandlerString
expr_stmt|;
block|}
name|solrQuery
operator|.
name|setRequestHandler
argument_list|(
name|requestHandlerString
argument_list|)
expr_stmt|;
block|}
name|String
name|parameterString
init|=
name|nativeQueryString
operator|.
name|substring
argument_list|(
name|nativeQueryString
operator|.
name|indexOf
argument_list|(
literal|'?'
argument_list|)
operator|+
literal|1
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|param
range|:
name|parameterString
operator|.
name|split
argument_list|(
literal|"&"
argument_list|)
control|)
block|{
name|String
index|[]
name|kv
init|=
name|param
operator|.
name|split
argument_list|(
literal|"="
argument_list|)
decl_stmt|;
if|if
condition|(
name|kv
operator|.
name|length
operator|!=
literal|2
condition|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"Unparsable native HTTP Solr query"
argument_list|)
throw|;
block|}
else|else
block|{
comment|// more like this
if|if
condition|(
literal|"/mlt"
operator|.
name|equals
argument_list|(
name|requestHandlerString
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"stream.body"
operator|.
name|equals
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|kv
index|[
literal|0
index|]
operator|=
literal|"q"
expr_stmt|;
name|String
name|mltFlString
init|=
literal|"mlt.fl="
decl_stmt|;
name|int
name|mltFlIndex
init|=
name|parameterString
operator|.
name|indexOf
argument_list|(
name|mltFlString
argument_list|)
decl_stmt|;
if|if
condition|(
name|mltFlIndex
operator|>
operator|-
literal|1
condition|)
block|{
name|int
name|beginIndex
init|=
name|mltFlIndex
operator|+
name|mltFlString
operator|.
name|length
argument_list|()
decl_stmt|;
name|int
name|endIndex
init|=
name|parameterString
operator|.
name|indexOf
argument_list|(
literal|'&'
argument_list|,
name|beginIndex
argument_list|)
decl_stmt|;
name|String
name|fields
decl_stmt|;
if|if
condition|(
name|endIndex
operator|>
name|beginIndex
condition|)
block|{
name|fields
operator|=
name|parameterString
operator|.
name|substring
argument_list|(
name|beginIndex
argument_list|,
name|endIndex
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|fields
operator|=
name|parameterString
operator|.
name|substring
argument_list|(
name|beginIndex
argument_list|)
expr_stmt|;
block|}
name|kv
index|[
literal|1
index|]
operator|=
literal|"_query_:\"{!dismax qf="
operator|+
name|fields
operator|+
literal|" q.op=OR}"
operator|+
name|kv
index|[
literal|1
index|]
operator|+
literal|"\""
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|"mlt.fl"
operator|.
name|equals
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
operator|&&
literal|":path"
operator|.
name|equals
argument_list|(
name|kv
index|[
literal|1
index|]
argument_list|)
condition|)
block|{
comment|// rep:similar passes the path of the node to find similar documents for in the :path
comment|// but needs its indexed content to find similar documents
name|kv
index|[
literal|1
index|]
operator|=
name|configuration
operator|.
name|getCatchAllField
argument_list|()
expr_stmt|;
block|}
block|}
if|if
condition|(
literal|"/spellcheck"
operator|.
name|equals
argument_list|(
name|requestHandlerString
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"term"
operator|.
name|equals
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|kv
index|[
literal|0
index|]
operator|=
literal|"spellcheck.q"
expr_stmt|;
block|}
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"spellcheck"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
literal|"/suggest"
operator|.
name|equals
argument_list|(
name|requestHandlerString
argument_list|)
condition|)
block|{
if|if
condition|(
literal|"term"
operator|.
name|equals
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|)
condition|)
block|{
name|kv
index|[
literal|0
index|]
operator|=
literal|"suggest.q"
expr_stmt|;
block|}
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"suggest"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
name|solrQuery
operator|.
name|setParam
argument_list|(
name|kv
index|[
literal|0
index|]
argument_list|,
name|kv
index|[
literal|1
index|]
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|solrQuery
return|;
block|}
else|else
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|nativeQueryString
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|SolrQueryIndex
operator|.
name|isIgnoredProperty
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|,
name|configuration
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|String
name|first
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pr
operator|.
name|first
operator|!=
literal|null
condition|)
block|{
name|first
operator|=
name|partialEscape
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|pr
operator|.
name|first
operator|.
name|getValue
argument_list|(
name|pr
operator|.
name|first
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|String
name|last
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|pr
operator|.
name|last
operator|!=
literal|null
condition|)
block|{
name|last
operator|=
name|partialEscape
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|pr
operator|.
name|last
operator|.
name|getValue
argument_list|(
name|pr
operator|.
name|last
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
argument_list|)
operator|.
name|toString
argument_list|()
expr_stmt|;
block|}
name|String
name|prField
init|=
name|configuration
operator|.
name|getFieldForPropertyRestriction
argument_list|(
name|pr
argument_list|)
decl_stmt|;
name|CharSequence
name|fieldName
init|=
name|partialEscape
argument_list|(
name|prField
operator|!=
literal|null
condition|?
name|prField
else|:
name|pr
operator|.
name|propertyName
argument_list|)
decl_stmt|;
if|if
condition|(
literal|"jcr\\:path"
operator|.
name|equals
argument_list|(
name|fieldName
operator|.
name|toString
argument_list|()
argument_list|)
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|configuration
operator|.
name|getPathField
argument_list|()
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
name|pr
operator|.
name|first
operator|!=
literal|null
operator|&&
name|pr
operator|.
name|last
operator|!=
literal|null
operator|&&
name|pr
operator|.
name|first
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|last
argument_list|)
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
name|first
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|first
operator|==
literal|null
operator|&&
name|pr
operator|.
name|last
operator|==
literal|null
condition|)
block|{
if|if
condition|(
operator|!
name|queryBuilder
operator|.
name|toString
argument_list|()
operator|.
name|contains
argument_list|(
name|fieldName
operator|+
literal|":"
argument_list|)
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
block|}
block|}
elseif|else
if|if
condition|(
operator|(
name|pr
operator|.
name|first
operator|!=
literal|null
operator|&&
name|pr
operator|.
name|last
operator|==
literal|null
operator|)
operator|||
operator|(
name|pr
operator|.
name|last
operator|!=
literal|null
operator|&&
name|pr
operator|.
name|first
operator|==
literal|null
operator|)
operator|||
operator|(
operator|!
name|pr
operator|.
name|first
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|last
argument_list|)
operator|)
condition|)
block|{
comment|// TODO : need to check if this works for all field types (most likely not!)
name|queryBuilder
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
name|createRangeQuery
argument_list|(
name|first
argument_list|,
name|last
argument_list|,
name|pr
operator|.
name|firstIncluding
argument_list|,
name|pr
operator|.
name|lastIncluding
argument_list|)
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|pr
operator|.
name|isLike
condition|)
block|{
comment|// TODO : the current parameter substitution is not expected to work well
name|queryBuilder
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
name|partialEscape
argument_list|(
name|String
operator|.
name|valueOf
argument_list|(
name|pr
operator|.
name|first
operator|.
name|getValue
argument_list|(
name|pr
operator|.
name|first
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
operator|.
name|replace
argument_list|(
literal|'%'
argument_list|,
literal|'*'
argument_list|)
operator|.
name|replace
argument_list|(
literal|'_'
argument_list|,
literal|'?'
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
literal|"[unexpected!] not handled case"
argument_list|)
throw|;
block|}
block|}
block|}
name|queryBuilder
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|configuration
operator|.
name|useForPrimaryTypes
argument_list|()
condition|)
block|{
name|String
index|[]
name|pts
init|=
name|filter
operator|.
name|getPrimaryTypes
argument_list|()
operator|.
name|toArray
argument_list|(
operator|new
name|String
index|[
name|filter
operator|.
name|getPrimaryTypes
argument_list|()
operator|.
name|size
argument_list|()
index|]
argument_list|)
decl_stmt|;
name|StringBuilder
name|ptQueryBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
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
name|pts
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|String
name|pt
init|=
name|pts
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
name|ptQueryBuilder
operator|.
name|append
argument_list|(
literal|"("
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|<
name|pts
operator|.
name|length
condition|)
block|{
name|ptQueryBuilder
operator|.
name|append
argument_list|(
literal|"OR "
argument_list|)
expr_stmt|;
block|}
name|ptQueryBuilder
operator|.
name|append
argument_list|(
literal|"jcr\\:primaryType"
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|partialEscape
argument_list|(
name|pt
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
if|if
condition|(
name|i
operator|==
name|pts
operator|.
name|length
operator|-
literal|1
condition|)
block|{
name|ptQueryBuilder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
expr_stmt|;
name|ptQueryBuilder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
block|}
name|solrQuery
operator|.
name|addFilterQuery
argument_list|(
name|ptQueryBuilder
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|.
name|getQueryStatement
argument_list|()
operator|!=
literal|null
operator|&&
name|filter
operator|.
name|getQueryStatement
argument_list|()
operator|.
name|contains
argument_list|(
name|QueryImpl
operator|.
name|REP_EXCERPT
argument_list|)
condition|)
block|{
if|if
condition|(
operator|!
name|solrQuery
operator|.
name|getHighlight
argument_list|()
condition|)
block|{
comment|// enable highlighting
name|solrQuery
operator|.
name|setHighlight
argument_list|(
literal|true
argument_list|)
expr_stmt|;
comment|// defaults
name|solrQuery
operator|.
name|set
argument_list|(
literal|"hl.fl"
argument_list|,
literal|"*"
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|set
argument_list|(
literal|"hl.encoder"
argument_list|,
literal|"html"
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|set
argument_list|(
literal|"hl.mergeContiguous"
argument_list|,
literal|true
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setHighlightSimplePre
argument_list|(
literal|"<strong>"
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setHighlightSimplePost
argument_list|(
literal|"</strong>"
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|configuration
operator|.
name|useForPathRestrictions
argument_list|()
condition|)
block|{
name|Filter
operator|.
name|PathRestriction
name|pathRestriction
init|=
name|filter
operator|.
name|getPathRestriction
argument_list|()
decl_stmt|;
if|if
condition|(
name|pathRestriction
operator|!=
literal|null
condition|)
block|{
name|String
name|path
init|=
name|purgePath
argument_list|(
name|filter
argument_list|)
decl_stmt|;
name|String
name|fieldName
init|=
name|configuration
operator|.
name|getFieldForPathRestriction
argument_list|(
name|pathRestriction
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|pathRestriction
operator|.
name|equals
argument_list|(
name|Filter
operator|.
name|PathRestriction
operator|.
name|ALL_CHILDREN
argument_list|)
condition|)
block|{
name|solrQuery
operator|.
name|addFilterQuery
argument_list|(
name|fieldName
operator|+
literal|':'
operator|+
name|path
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|fieldName
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|queryBuilder
operator|.
name|append
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
if|if
condition|(
name|configuration
operator|.
name|collapseJcrContentNodes
argument_list|()
condition|)
block|{
name|solrQuery
operator|.
name|addFilterQuery
argument_list|(
literal|"{!collapse field="
operator|+
name|configuration
operator|.
name|getCollapsedPathField
argument_list|()
operator|+
literal|" min="
operator|+
name|configuration
operator|.
name|getPathDepthField
argument_list|()
operator|+
literal|" hint=top_fc nullPolicy=expand}"
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|queryBuilder
operator|.
name|length
argument_list|()
operator|==
literal|0
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
literal|"*:*"
argument_list|)
expr_stmt|;
block|}
name|String
name|escapedQuery
init|=
name|queryBuilder
operator|.
name|toString
argument_list|()
decl_stmt|;
name|solrQuery
operator|.
name|setQuery
argument_list|(
name|escapedQuery
argument_list|)
expr_stmt|;
if|if
condition|(
name|log
operator|.
name|isDebugEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"JCR query {} has been converted to Solr query {}"
argument_list|,
name|filter
operator|.
name|getQueryStatement
argument_list|()
argument_list|,
name|solrQuery
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|solrQuery
return|;
block|}
specifier|private
specifier|static
name|String
name|parseFullTextExpression
parameter_list|(
name|FullTextExpression
name|ft
parameter_list|,
specifier|final
name|OakSolrConfiguration
name|configuration
parameter_list|)
block|{
specifier|final
name|StringBuilder
name|fullTextString
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|ft
operator|.
name|accept
argument_list|(
operator|new
name|FullTextVisitor
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextOr
name|or
parameter_list|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|or
operator|.
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|<
name|or
operator|.
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|" OR "
argument_list|)
expr_stmt|;
block|}
name|FullTextExpression
name|e
init|=
name|or
operator|.
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|orTerm
init|=
name|parseFullTextExpression
argument_list|(
name|e
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
name|orTerm
argument_list|)
expr_stmt|;
block|}
name|fullTextString
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextContains
name|contains
parameter_list|)
block|{
return|return
name|contains
operator|.
name|getBase
argument_list|()
operator|.
name|accept
argument_list|(
name|this
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextAnd
name|and
parameter_list|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|and
operator|.
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
operator|&&
name|i
operator|<
name|and
operator|.
name|list
operator|.
name|size
argument_list|()
condition|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|" AND "
argument_list|)
expr_stmt|;
block|}
name|FullTextExpression
name|e
init|=
name|and
operator|.
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|String
name|andTerm
init|=
name|parseFullTextExpression
argument_list|(
name|e
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
name|andTerm
argument_list|)
expr_stmt|;
block|}
name|fullTextString
operator|.
name|append
argument_list|(
literal|')'
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|visit
parameter_list|(
name|FullTextTerm
name|term
parameter_list|)
block|{
if|if
condition|(
name|term
operator|.
name|isNot
argument_list|()
condition|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
block|}
name|String
name|p
init|=
name|term
operator|.
name|getPropertyName
argument_list|()
decl_stmt|;
if|if
condition|(
name|p
operator|!=
literal|null
operator|&&
name|p
operator|.
name|indexOf
argument_list|(
literal|'/'
argument_list|)
operator|>=
literal|0
condition|)
block|{
name|p
operator|=
name|getName
argument_list|(
name|p
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|p
operator|==
literal|null
operator|||
literal|"*"
operator|.
name|equals
argument_list|(
name|p
argument_list|)
condition|)
block|{
name|p
operator|=
name|configuration
operator|.
name|getCatchAllField
argument_list|()
expr_stmt|;
block|}
name|fullTextString
operator|.
name|append
argument_list|(
name|partialEscape
argument_list|(
name|p
argument_list|)
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|termText
init|=
name|term
operator|.
name|getText
argument_list|()
decl_stmt|;
if|if
condition|(
name|termText
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
operator|>
literal|0
condition|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
name|fullTextString
operator|.
name|append
argument_list|(
name|termText
operator|.
name|replace
argument_list|(
literal|"/"
argument_list|,
literal|"\\/"
argument_list|)
operator|.
name|replace
argument_list|(
literal|":"
argument_list|,
literal|"\\:"
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|termText
operator|.
name|indexOf
argument_list|(
literal|' '
argument_list|)
operator|>
literal|0
condition|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|'"'
argument_list|)
expr_stmt|;
block|}
name|String
name|boost
init|=
name|term
operator|.
name|getBoost
argument_list|()
decl_stmt|;
if|if
condition|(
name|boost
operator|!=
literal|null
condition|)
block|{
name|fullTextString
operator|.
name|append
argument_list|(
literal|'^'
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
name|boost
argument_list|)
expr_stmt|;
block|}
name|fullTextString
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|fullTextString
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isSupportedHttpRequest
parameter_list|(
name|String
name|nativeQueryString
parameter_list|)
block|{
comment|// the query string starts with ${supported-handler.selector}?
return|return
name|nativeQueryString
operator|.
name|matches
argument_list|(
literal|"(suggest|spellcheck|mlt|query|select|get)\\\\?.*"
argument_list|)
return|;
block|}
specifier|private
specifier|static
name|void
name|setDefaults
parameter_list|(
name|SolrQuery
name|solrQuery
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|)
block|{
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"q.op"
argument_list|,
literal|"AND"
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"fl"
argument_list|,
name|configuration
operator|.
name|getPathField
argument_list|()
operator|+
literal|" score"
argument_list|)
expr_stmt|;
name|String
name|catchAllField
init|=
name|configuration
operator|.
name|getCatchAllField
argument_list|()
decl_stmt|;
if|if
condition|(
name|catchAllField
operator|!=
literal|null
operator|&&
name|catchAllField
operator|.
name|length
argument_list|()
operator|>
literal|0
condition|)
block|{
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"df"
argument_list|,
name|catchAllField
argument_list|)
expr_stmt|;
block|}
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"rows"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|configuration
operator|.
name|getRows
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|String
name|createRangeQuery
parameter_list|(
name|String
name|first
parameter_list|,
name|String
name|last
parameter_list|,
name|boolean
name|firstIncluding
parameter_list|,
name|boolean
name|lastIncluding
parameter_list|)
block|{
comment|// TODO : handle inclusion / exclusion of bounds
return|return
literal|"["
operator|+
operator|(
name|first
operator|!=
literal|null
condition|?
name|first
else|:
literal|"*"
operator|)
operator|+
literal|" TO "
operator|+
operator|(
name|last
operator|!=
literal|null
condition|?
name|last
else|:
literal|"*"
operator|)
operator|+
literal|"]"
return|;
block|}
specifier|private
specifier|static
name|String
name|purgePath
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|partialEscape
argument_list|(
name|filter
operator|.
name|getPath
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

