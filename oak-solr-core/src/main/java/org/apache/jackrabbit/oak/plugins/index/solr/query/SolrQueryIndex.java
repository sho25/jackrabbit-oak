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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|PropertyValue
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
name|aggregate
operator|.
name|NodeAggregator
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
name|Cursor
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
name|IndexRow
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
name|PropertyValues
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
name|query
operator|.
name|QueryIndex
operator|.
name|FulltextQueryIndex
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
name|apache
operator|.
name|solr
operator|.
name|client
operator|.
name|solrj
operator|.
name|SolrServer
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
name|response
operator|.
name|QueryResponse
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
name|common
operator|.
name|SolrDocument
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
name|common
operator|.
name|SolrDocumentList
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

begin_comment
comment|/**  * A Solr based {@link QueryIndex}  */
end_comment

begin_class
specifier|public
class|class
name|SolrQueryIndex
implements|implements
name|FulltextQueryIndex
block|{
specifier|private
specifier|static
specifier|final
name|String
name|NATIVE_SOLR_QUERY
init|=
literal|"native*solr"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|NATIVE_LUCENE_QUERY
init|=
literal|"native*lucene"
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|String
name|TYPE
init|=
literal|"solr"
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|SolrQueryIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
specifier|final
name|SolrServer
name|solrServer
decl_stmt|;
specifier|private
specifier|final
name|OakSolrConfiguration
name|configuration
decl_stmt|;
specifier|private
specifier|final
name|NodeAggregator
name|aggregator
decl_stmt|;
specifier|public
name|SolrQueryIndex
parameter_list|(
name|String
name|name
parameter_list|,
name|SolrServer
name|solrServer
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|)
block|{
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|solrServer
operator|=
name|solrServer
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
comment|// TODO this index should support aggregation in the same way as the Lucene index
name|this
operator|.
name|aggregator
operator|=
literal|null
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|double
name|getCost
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
if|if
condition|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|==
literal|null
operator|&&
name|filter
operator|.
name|getFulltextConditions
argument_list|()
operator|==
literal|null
operator|||
operator|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
operator|!=
literal|null
operator|&&
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
operator|.
name|size
argument_list|()
operator|==
literal|1
operator|&&
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|JcrConstants
operator|.
name|JCR_UUID
argument_list|)
operator|!=
literal|null
operator|)
condition|)
block|{
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
name|int
name|cost
init|=
literal|10
decl_stmt|;
name|Collection
argument_list|<
name|Filter
operator|.
name|PropertyRestriction
argument_list|>
name|restrictions
init|=
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
decl_stmt|;
if|if
condition|(
name|restrictions
operator|!=
literal|null
condition|)
block|{
name|cost
operator|/=
literal|5
expr_stmt|;
block|}
if|if
condition|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|cost
operator|/=
literal|2
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|getPlan
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|nodeState
parameter_list|)
block|{
return|return
name|getQuery
argument_list|(
name|filter
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
name|SolrQuery
name|getQuery
parameter_list|(
name|Filter
name|filter
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
argument_list|)
expr_stmt|;
name|StringBuilder
name|queryBuilder
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
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
comment|// native query support
if|if
condition|(
name|NATIVE_SOLR_QUERY
operator|.
name|equals
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
operator|||
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
comment|// every other restriction is not considered
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
name|pr
operator|.
name|propertyName
operator|.
name|contains
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// cannot handle child-level property restrictions
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
name|queryBuilder
operator|.
name|append
argument_list|(
literal|'*'
argument_list|)
expr_stmt|;
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
name|queryBuilder
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
name|queryBuilder
operator|.
name|append
argument_list|(
literal|"OR "
argument_list|)
expr_stmt|;
block|}
name|queryBuilder
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
name|queryBuilder
operator|.
name|append
argument_list|(
literal|")"
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
block|}
if|if
condition|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|!=
literal|null
condition|)
block|{
name|queryBuilder
operator|.
name|append
argument_list|(
name|getFullTextQuery
argument_list|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
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
name|queryBuilder
operator|.
name|append
argument_list|(
literal|' '
argument_list|)
expr_stmt|;
block|}
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
name|String
name|getFullTextQuery
parameter_list|(
name|FullTextExpression
name|ft
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
name|getFullTextQuery
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
name|orTerm
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|')'
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
name|getFullTextQuery
argument_list|(
name|e
argument_list|)
decl_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|'('
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
name|andTerm
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|')'
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
name|p
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|fullTextString
operator|.
name|append
argument_list|(
name|partialEscape
argument_list|(
name|term
operator|.
name|getText
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
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
literal|"(mlt|query|select|get)\\\\?.*"
argument_list|)
return|;
block|}
specifier|private
name|void
name|setDefaults
parameter_list|(
name|SolrQuery
name|solrQuery
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
literal|"* score"
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
comment|// TODO : can we handle this better? e.g. with deep paging support?
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"rows"
argument_list|,
literal|"100000"
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
comment|// partially borrowed from SolrPluginUtils#partialEscape
specifier|private
specifier|static
name|CharSequence
name|partialEscape
parameter_list|(
name|CharSequence
name|s
parameter_list|)
block|{
name|StringBuilder
name|sb
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
name|s
operator|.
name|length
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|char
name|c
init|=
name|s
operator|.
name|charAt
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|c
operator|==
literal|'\\'
operator|||
name|c
operator|==
literal|'!'
operator|||
name|c
operator|==
literal|'('
operator|||
name|c
operator|==
literal|')'
operator|||
name|c
operator|==
literal|':'
operator|||
name|c
operator|==
literal|'^'
operator|||
name|c
operator|==
literal|'['
operator|||
name|c
operator|==
literal|']'
operator|||
name|c
operator|==
literal|'/'
operator|||
name|c
operator|==
literal|'{'
operator|||
name|c
operator|==
literal|'}'
operator|||
name|c
operator|==
literal|'~'
operator|||
name|c
operator|==
literal|'*'
operator|||
name|c
operator|==
literal|'?'
operator|||
name|c
operator|==
literal|'-'
operator|||
name|c
operator|==
literal|' '
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'\\'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
name|c
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|)
block|{
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
literal|"converting filter {}"
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
name|Cursor
name|cursor
decl_stmt|;
try|try
block|{
name|SolrQuery
name|query
init|=
name|getQuery
argument_list|(
name|filter
argument_list|)
decl_stmt|;
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
literal|"sending query {}"
argument_list|,
name|query
argument_list|)
expr_stmt|;
block|}
name|QueryResponse
name|queryResponse
init|=
name|solrServer
operator|.
name|query
argument_list|(
name|query
argument_list|)
decl_stmt|;
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
literal|"getting response {}"
argument_list|,
name|queryResponse
argument_list|)
expr_stmt|;
block|}
name|cursor
operator|=
operator|new
name|SolrCursor
argument_list|(
name|queryResponse
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|RuntimeException
argument_list|(
name|e
argument_list|)
throw|;
block|}
return|return
name|cursor
return|;
block|}
specifier|private
class|class
name|SolrCursor
implements|implements
name|Cursor
block|{
specifier|private
specifier|final
name|SolrDocumentList
name|results
decl_stmt|;
specifier|private
name|int
name|i
decl_stmt|;
specifier|public
name|SolrCursor
parameter_list|(
name|QueryResponse
name|queryResponse
parameter_list|)
block|{
name|this
operator|.
name|results
operator|=
name|queryResponse
operator|.
name|getResults
argument_list|()
expr_stmt|;
name|i
operator|=
literal|0
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|results
operator|!=
literal|null
operator|&&
name|i
operator|<
name|results
operator|.
name|size
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|results
operator|.
name|remove
argument_list|(
name|i
argument_list|)
expr_stmt|;
block|}
specifier|public
name|IndexRow
name|next
parameter_list|()
block|{
if|if
condition|(
name|i
operator|<
name|results
operator|.
name|size
argument_list|()
condition|)
block|{
specifier|final
name|SolrDocument
name|doc
init|=
name|results
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
name|i
operator|++
expr_stmt|;
return|return
operator|new
name|IndexRow
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|String
operator|.
name|valueOf
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
name|configuration
operator|.
name|getPathField
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|PropertyValue
name|getValue
parameter_list|(
name|String
name|columnName
parameter_list|)
block|{
if|if
condition|(
name|QueryImpl
operator|.
name|JCR_SCORE
operator|.
name|equals
argument_list|(
name|columnName
argument_list|)
condition|)
block|{
name|float
name|score
init|=
literal|0f
decl_stmt|;
name|Object
name|scoreObj
init|=
name|doc
operator|.
name|get
argument_list|(
literal|"score"
argument_list|)
decl_stmt|;
if|if
condition|(
name|scoreObj
operator|!=
literal|null
condition|)
block|{
name|score
operator|=
operator|(
name|Float
operator|)
name|scoreObj
expr_stmt|;
block|}
return|return
name|PropertyValues
operator|.
name|newDouble
argument_list|(
operator|(
name|double
operator|)
name|score
argument_list|)
return|;
block|}
name|Object
name|o
init|=
name|doc
operator|.
name|getFieldValue
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
return|return
name|o
operator|==
literal|null
condition|?
literal|null
else|:
name|PropertyValues
operator|.
name|newString
argument_list|(
name|o
operator|.
name|toString
argument_list|()
argument_list|)
return|;
block|}
block|}
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|NodeAggregator
name|getNodeAggregator
parameter_list|()
block|{
return|return
name|aggregator
return|;
block|}
block|}
end_class

end_unit

