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
specifier|public
specifier|static
specifier|final
name|String
name|NATIVE_SOLR_QUERY
init|=
literal|"native*solr"
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
comment|// TODO : estimate no of returned values and 0 is not good for no restrictions
return|return
operator|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
operator|!=
literal|null
condition|?
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
operator|.
name|size
argument_list|()
operator|*
literal|0.1
else|:
literal|0
operator|)
operator|+
operator|(
name|filter
operator|.
name|getFulltextConditions
argument_list|()
operator|!=
literal|null
condition|?
name|filter
operator|.
name|getFulltextConditions
argument_list|()
operator|.
name|size
argument_list|()
operator|*
literal|0.01
else|:
literal|0
operator|)
operator|+
operator|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|!=
literal|null
condition|?
literal|0.2
else|:
literal|0
operator|)
return|;
comment|//
comment|//        FullTextExpression ft = filter.getFullTextConstraint();
comment|//        if (ft == null) {
comment|//            // TODO solr should only be triggered for full-text conditions
comment|//            // return Double.POSITIVE_INFINITY;
comment|//        }
comment|//        int cost = 10;
comment|//        Collection<PropertyRestriction> restrictions = filter.getPropertyRestrictions();
comment|//        if (restrictions != null) {
comment|//            cost /= 2;
comment|//        }
comment|//        if (filter.getPathRestriction() != null) {
comment|//            cost /= 2;
comment|//        }
comment|//        return cost;
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
for|for
control|(
name|String
name|pt
range|:
name|filter
operator|.
name|getPrimaryTypes
argument_list|()
control|)
block|{
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
expr_stmt|;
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
literal|" "
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
condition|)
block|{
name|queryBuilder
operator|.
name|append
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
expr_stmt|;
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
comment|// TODO : change this to be not hard coded
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"df"
argument_list|,
literal|"catch_all"
argument_list|)
expr_stmt|;
comment|// TODO : can we handle this better?
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
name|Integer
operator|.
name|MAX_VALUE
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

