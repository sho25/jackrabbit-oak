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
name|ArrayList
import|;
end_import

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
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Deque
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|HashSet
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Iterator
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
name|java
operator|.
name|util
operator|.
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|AbstractIterator
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Queues
import|;
end_import

begin_import
import|import
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Sets
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
name|QueryEngineSettings
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
name|Cursors
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
name|SolrServerException
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
name|client
operator|.
name|solrj
operator|.
name|response
operator|.
name|SpellCheckResponse
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
name|apache
operator|.
name|solr
operator|.
name|common
operator|.
name|util
operator|.
name|NamedList
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
name|util
operator|.
name|SimpleOrderedMap
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
name|getAncestorPath
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
name|getDepth
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
name|getParentPath
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
name|TYPE
init|=
literal|"solr"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NATIVE_SOLR_QUERY
init|=
literal|"native*solr"
decl_stmt|;
specifier|static
specifier|final
name|String
name|NATIVE_LUCENE_QUERY
init|=
literal|"native*lucene"
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
parameter_list|,
name|NodeAggregator
name|aggregator
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
name|this
operator|.
name|aggregator
operator|=
name|aggregator
expr_stmt|;
block|}
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
argument_list|(
name|name
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|,
literal|null
argument_list|)
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
comment|// cost is inverse proportional to the number of matching restrictions, infinite if no restriction matches
name|double
name|cost
init|=
literal|10d
operator|/
name|getMatchingFilterRestrictions
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
literal|"Solr: cost for {} is {}"
argument_list|,
name|name
argument_list|,
name|cost
argument_list|)
expr_stmt|;
block|}
return|return
name|cost
return|;
block|}
name|int
name|getMatchingFilterRestrictions
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
name|int
name|match
init|=
literal|0
decl_stmt|;
comment|// full text expressions OR full text conditions defined
if|if
condition|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|!=
literal|null
operator|||
operator|(
name|filter
operator|.
name|getFulltextConditions
argument_list|()
operator|!=
literal|null
operator|&&
name|filter
operator|.
name|getFulltextConditions
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|)
condition|)
block|{
name|match
operator|++
expr_stmt|;
comment|// full text queries have usually a significant recall
block|}
comment|// path restriction defined AND path restrictions handled
if|if
condition|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|Filter
operator|.
name|PathRestriction
operator|.
name|NO_RESTRICTION
operator|.
name|equals
argument_list|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
argument_list|)
operator|&&
name|configuration
operator|.
name|useForPathRestrictions
argument_list|()
condition|)
block|{
name|match
operator|++
expr_stmt|;
block|}
comment|// primary type restriction defined AND primary type restriction handled
if|if
condition|(
name|filter
operator|.
name|getPrimaryTypes
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
name|configuration
operator|.
name|useForPrimaryTypes
argument_list|()
condition|)
block|{
name|match
operator|++
expr_stmt|;
block|}
comment|// property restriction OR native language property restriction defined AND property restriction handled
if|if
condition|(
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
operator|>
literal|0
operator|&&
operator|(
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|NATIVE_SOLR_QUERY
argument_list|)
operator|!=
literal|null
operator|||
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|NATIVE_LUCENE_QUERY
argument_list|)
operator|!=
literal|null
operator|||
name|configuration
operator|.
name|useForPropertyRestrictions
argument_list|()
operator|)
operator|&&
operator|!
name|hasIgnoredProperties
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
argument_list|,
name|configuration
argument_list|)
condition|)
block|{
name|match
operator|++
expr_stmt|;
block|}
return|return
name|match
return|;
block|}
specifier|private
specifier|static
name|boolean
name|hasIgnoredProperties
parameter_list|(
name|Collection
argument_list|<
name|Filter
operator|.
name|PropertyRestriction
argument_list|>
name|propertyRestrictions
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|)
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
return|return
literal|true
return|;
block|}
block|}
return|return
literal|false
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
name|FilterQueryParser
operator|.
name|getQuery
argument_list|(
name|filter
argument_list|,
name|configuration
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Get the set of relative paths of a full-text condition. For example, for      * the condition "contains(a/b, 'hello') and contains(c/d, 'world'), the set      * { "a", "c" } is returned. If there are no relative properties, then one      * entry is returned (the empty string). If there is no expression, then an      * empty set is returned.      *      * @param ft the full-text expression      * @return the set of relative paths (possibly empty)      */
specifier|private
specifier|static
name|Set
argument_list|<
name|String
argument_list|>
name|getRelativePaths
parameter_list|(
name|FullTextExpression
name|ft
parameter_list|)
block|{
specifier|final
name|HashSet
argument_list|<
name|String
argument_list|>
name|relPaths
init|=
operator|new
name|HashSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|ft
operator|.
name|accept
argument_list|(
operator|new
name|FullTextVisitor
operator|.
name|FullTextVisitorBase
argument_list|()
block|{
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
operator|==
literal|null
condition|)
block|{
name|relPaths
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|p
operator|.
name|startsWith
argument_list|(
literal|"../"
argument_list|)
operator|||
name|p
operator|.
name|startsWith
argument_list|(
literal|"./"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Relative parent is not supported:"
operator|+
name|p
argument_list|)
throw|;
block|}
elseif|else
if|if
condition|(
name|getDepth
argument_list|(
name|p
argument_list|)
operator|>
literal|1
condition|)
block|{
name|String
name|parent
init|=
name|getParentPath
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|relPaths
operator|.
name|add
argument_list|(
name|parent
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|relPaths
operator|.
name|add
argument_list|(
literal|""
argument_list|)
expr_stmt|;
block|}
return|return
literal|true
return|;
block|}
block|}
argument_list|)
expr_stmt|;
return|return
name|relPaths
return|;
block|}
annotation|@
name|Override
specifier|public
name|Cursor
name|query
parameter_list|(
specifier|final
name|Filter
name|filter
parameter_list|,
specifier|final
name|NodeState
name|root
parameter_list|)
block|{
name|Cursor
name|cursor
decl_stmt|;
try|try
block|{
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|relPaths
init|=
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
operator|!=
literal|null
condition|?
name|getRelativePaths
argument_list|(
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
argument_list|)
else|:
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
decl_stmt|;
specifier|final
name|String
name|parent
init|=
name|relPaths
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|?
literal|""
else|:
name|relPaths
operator|.
name|iterator
argument_list|()
operator|.
name|next
argument_list|()
decl_stmt|;
specifier|final
name|int
name|parentDepth
init|=
name|getDepth
argument_list|(
name|parent
argument_list|)
decl_stmt|;
name|cursor
operator|=
operator|new
name|SolrRowCursor
argument_list|(
operator|new
name|AbstractIterator
argument_list|<
name|SolrResultRow
argument_list|>
argument_list|()
block|{
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|seenPaths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Deque
argument_list|<
name|SolrResultRow
argument_list|>
name|queue
init|=
name|Queues
operator|.
name|newArrayDeque
argument_list|()
decl_stmt|;
specifier|private
name|SolrDocument
name|lastDoc
decl_stmt|;
specifier|private
name|int
name|offset
init|=
literal|0
decl_stmt|;
specifier|private
name|boolean
name|noDocs
init|=
literal|false
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|SolrResultRow
name|computeNext
parameter_list|()
block|{
if|if
condition|(
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
operator|||
name|loadDocs
argument_list|()
condition|)
block|{
return|return
name|queue
operator|.
name|remove
argument_list|()
return|;
block|}
return|return
name|endOfData
argument_list|()
return|;
block|}
specifier|private
name|SolrResultRow
name|convertToRow
parameter_list|(
name|SolrDocument
name|doc
parameter_list|)
block|{
name|String
name|path
init|=
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
decl_stmt|;
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"/"
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|parent
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|path
operator|=
name|getAncestorPath
argument_list|(
name|path
argument_list|,
name|parentDepth
argument_list|)
expr_stmt|;
comment|// avoid duplicate entries
if|if
condition|(
name|seenPaths
operator|.
name|contains
argument_list|(
name|path
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
name|seenPaths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
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
operator|new
name|SolrResultRow
argument_list|(
name|path
argument_list|,
name|score
argument_list|,
name|doc
argument_list|)
return|;
block|}
return|return
literal|null
return|;
block|}
comment|/**                  * Loads the Solr documents in batches                  * @return true if any document is loaded                  */
specifier|private
name|boolean
name|loadDocs
parameter_list|()
block|{
if|if
condition|(
name|noDocs
condition|)
block|{
return|return
literal|false
return|;
block|}
name|SolrDocument
name|lastDocToRecord
init|=
literal|null
decl_stmt|;
try|try
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
name|SolrQuery
name|query
init|=
name|FilterQueryParser
operator|.
name|getQuery
argument_list|(
name|filter
argument_list|,
name|configuration
argument_list|)
decl_stmt|;
if|if
condition|(
name|lastDoc
operator|!=
literal|null
condition|)
block|{
name|offset
operator|++
expr_stmt|;
name|int
name|newOffset
init|=
name|offset
operator|*
name|configuration
operator|.
name|getRows
argument_list|()
decl_stmt|;
name|query
operator|.
name|setParam
argument_list|(
literal|"start"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|newOffset
argument_list|)
argument_list|)
expr_stmt|;
block|}
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
name|SolrDocumentList
name|docs
init|=
name|queryResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|docs
operator|!=
literal|null
condition|)
block|{
name|onRetrievedDocs
argument_list|(
name|filter
argument_list|,
name|docs
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
literal|"getting docs {}"
argument_list|,
name|docs
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|SolrDocument
name|doc
range|:
name|docs
control|)
block|{
name|SolrResultRow
name|row
init|=
name|convertToRow
argument_list|(
name|doc
argument_list|)
decl_stmt|;
if|if
condition|(
name|row
operator|!=
literal|null
condition|)
block|{
name|queue
operator|.
name|add
argument_list|(
name|row
argument_list|)
expr_stmt|;
block|}
name|lastDocToRecord
operator|=
name|doc
expr_stmt|;
block|}
block|}
comment|// handle spellcheck
name|SpellCheckResponse
name|spellCheckResponse
init|=
name|queryResponse
operator|.
name|getSpellCheckResponse
argument_list|()
decl_stmt|;
if|if
condition|(
name|spellCheckResponse
operator|!=
literal|null
operator|&&
name|spellCheckResponse
operator|.
name|getSuggestions
argument_list|()
operator|!=
literal|null
operator|&&
name|spellCheckResponse
operator|.
name|getSuggestions
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|SolrDocument
name|fakeDoc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|SpellCheckResponse
operator|.
name|Suggestion
name|suggestion
range|:
name|spellCheckResponse
operator|.
name|getSuggestions
argument_list|()
control|)
block|{
name|fakeDoc
operator|.
name|addField
argument_list|(
name|QueryImpl
operator|.
name|REP_SPELLCHECK
argument_list|,
name|suggestion
operator|.
name|getAlternatives
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|queue
operator|.
name|add
argument_list|(
operator|new
name|SolrResultRow
argument_list|(
literal|"/"
argument_list|,
literal|1.0
argument_list|,
name|fakeDoc
argument_list|)
argument_list|)
expr_stmt|;
name|noDocs
operator|=
literal|true
expr_stmt|;
block|}
comment|// handle suggest
name|NamedList
argument_list|<
name|Object
argument_list|>
name|response
init|=
name|queryResponse
operator|.
name|getResponse
argument_list|()
decl_stmt|;
name|Map
name|suggest
init|=
operator|(
name|Map
operator|)
name|response
operator|.
name|get
argument_list|(
literal|"suggest"
argument_list|)
decl_stmt|;
if|if
condition|(
name|suggest
operator|!=
literal|null
condition|)
block|{
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|suggestEntries
init|=
name|suggest
operator|.
name|entrySet
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|suggestEntries
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|SolrDocument
name|fakeDoc
init|=
name|getSuggestions
argument_list|(
name|suggestEntries
argument_list|,
name|filter
argument_list|)
decl_stmt|;
name|queue
operator|.
name|add
argument_list|(
operator|new
name|SolrResultRow
argument_list|(
literal|"/"
argument_list|,
literal|1.0
argument_list|,
name|fakeDoc
argument_list|)
argument_list|)
expr_stmt|;
name|noDocs
operator|=
literal|true
expr_stmt|;
block|}
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
if|if
condition|(
name|log
operator|.
name|isWarnEnabled
argument_list|()
condition|)
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"query via {} failed."
argument_list|,
name|solrServer
argument_list|,
name|e
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|lastDocToRecord
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|lastDoc
operator|=
name|lastDocToRecord
expr_stmt|;
block|}
return|return
operator|!
name|queue
operator|.
name|isEmpty
argument_list|()
return|;
block|}
block|}
argument_list|,
name|filter
operator|.
name|getQueryEngineSettings
argument_list|()
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
name|SolrDocument
name|getSuggestions
parameter_list|(
name|Set
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|suggestEntries
parameter_list|,
name|Filter
name|filter
parameter_list|)
throws|throws
name|SolrServerException
block|{
name|Collection
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|retrievedSuggestions
init|=
operator|new
name|HashSet
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
name|SolrDocument
name|fakeDoc
init|=
operator|new
name|SolrDocument
argument_list|()
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|suggestor
range|:
name|suggestEntries
control|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|suggestionResponses
init|=
operator|(
operator|(
name|SimpleOrderedMap
operator|)
name|suggestor
operator|.
name|getValue
argument_list|()
operator|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|suggestionResponse
range|:
name|suggestionResponses
control|)
block|{
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|suggestionResults
init|=
operator|(
operator|(
name|SimpleOrderedMap
operator|)
name|suggestionResponse
operator|.
name|getValue
argument_list|()
operator|)
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|suggestionResult
range|:
name|suggestionResults
control|)
block|{
if|if
condition|(
literal|"suggestions"
operator|.
name|equals
argument_list|(
name|suggestionResult
operator|.
name|getKey
argument_list|()
argument_list|)
condition|)
block|{
name|ArrayList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
name|suggestions
init|=
operator|(
operator|(
name|ArrayList
argument_list|<
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
argument_list|>
operator|)
name|suggestionResult
operator|.
name|getValue
argument_list|()
operator|)
decl_stmt|;
if|if
condition|(
operator|!
name|suggestions
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
for|for
control|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|suggestion
range|:
name|suggestions
control|)
block|{
name|retrievedSuggestions
operator|.
name|add
argument_list|(
name|suggestion
argument_list|)
expr_stmt|;
block|}
block|}
block|}
block|}
block|}
block|}
comment|// ACL filter suggestions
for|for
control|(
name|SimpleOrderedMap
argument_list|<
name|Object
argument_list|>
name|suggestion
range|:
name|retrievedSuggestions
control|)
block|{
name|SolrQuery
name|solrQuery
init|=
operator|new
name|SolrQuery
argument_list|()
decl_stmt|;
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"q"
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|suggestion
operator|.
name|get
argument_list|(
literal|"term"
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
name|solrQuery
operator|.
name|setParam
argument_list|(
literal|"df"
argument_list|,
name|configuration
operator|.
name|getCatchAllField
argument_list|()
argument_list|)
expr_stmt|;
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
literal|"rows"
argument_list|,
literal|"100"
argument_list|)
expr_stmt|;
name|QueryResponse
name|suggestQueryResponse
init|=
name|solrServer
operator|.
name|query
argument_list|(
name|solrQuery
argument_list|)
decl_stmt|;
name|SolrDocumentList
name|results
init|=
name|suggestQueryResponse
operator|.
name|getResults
argument_list|()
decl_stmt|;
if|if
condition|(
name|results
operator|!=
literal|null
operator|&&
name|results
operator|.
name|getNumFound
argument_list|()
operator|>
literal|0
condition|)
block|{
for|for
control|(
name|SolrDocument
name|doc
range|:
name|results
control|)
block|{
if|if
condition|(
name|filter
operator|.
name|isAccessible
argument_list|(
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
argument_list|)
condition|)
block|{
name|fakeDoc
operator|.
name|addField
argument_list|(
name|QueryImpl
operator|.
name|REP_SUGGEST
argument_list|,
literal|"{term="
operator|+
name|suggestion
operator|.
name|get
argument_list|(
literal|"term"
argument_list|)
operator|+
literal|",weight="
operator|+
name|suggestion
operator|.
name|get
argument_list|(
literal|"weight"
argument_list|)
operator|+
literal|"}"
argument_list|)
expr_stmt|;
break|break;
block|}
block|}
block|}
block|}
return|return
name|fakeDoc
return|;
block|}
name|void
name|onRetrievedDocs
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|SolrDocumentList
name|docs
parameter_list|)
block|{
comment|// do nothing
block|}
specifier|static
name|boolean
name|isIgnoredProperty
parameter_list|(
name|String
name|propertyName
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|)
block|{
return|return
operator|!
operator|(
name|NATIVE_LUCENE_QUERY
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
operator|||
name|NATIVE_SOLR_QUERY
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
operator|)
operator|&&
operator|(
operator|!
name|configuration
operator|.
name|useForPropertyRestrictions
argument_list|()
comment|// Solr index not used for properties
operator|||
operator|(
name|configuration
operator|.
name|getUsedProperties
argument_list|()
operator|.
name|size
argument_list|()
operator|>
literal|0
operator|&&
operator|!
name|configuration
operator|.
name|getUsedProperties
argument_list|()
operator|.
name|contains
argument_list|(
name|propertyName
argument_list|)
operator|)
comment|// not explicitly contained in the used properties
operator|||
name|propertyName
operator|.
name|contains
argument_list|(
literal|"/"
argument_list|)
comment|// no child-level property restrictions
operator|||
literal|"rep:excerpt"
operator|.
name|equals
argument_list|(
name|propertyName
argument_list|)
comment|// rep:excerpt is handled by the query engine
operator|||
name|configuration
operator|.
name|getIgnoredProperties
argument_list|()
operator|.
name|contains
argument_list|(
name|propertyName
argument_list|)
operator|)
return|;
block|}
specifier|static
class|class
name|SolrResultRow
block|{
specifier|final
name|String
name|path
decl_stmt|;
specifier|final
name|double
name|score
decl_stmt|;
specifier|final
name|SolrDocument
name|doc
decl_stmt|;
name|SolrResultRow
parameter_list|(
name|String
name|path
parameter_list|,
name|double
name|score
parameter_list|)
block|{
name|this
argument_list|(
name|path
argument_list|,
name|score
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
name|SolrResultRow
parameter_list|(
name|String
name|path
parameter_list|,
name|double
name|score
parameter_list|,
name|SolrDocument
name|doc
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|score
operator|=
name|score
expr_stmt|;
name|this
operator|.
name|doc
operator|=
name|doc
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|String
operator|.
name|format
argument_list|(
literal|"%s (%1.2f)"
argument_list|,
name|path
argument_list|,
name|score
argument_list|)
return|;
block|}
block|}
comment|/**      * A cursor over Solr results. The result includes the path and the jcr:score pseudo-property as returned by Solr,      * plus, eventually, the returned stored values if {@link org.apache.solr.common.SolrDocument} is included in the      * {@link org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndex.SolrResultRow}.      */
specifier|static
class|class
name|SolrRowCursor
implements|implements
name|Cursor
block|{
specifier|private
specifier|final
name|Cursor
name|pathCursor
decl_stmt|;
name|SolrResultRow
name|currentRow
decl_stmt|;
name|SolrRowCursor
parameter_list|(
specifier|final
name|Iterator
argument_list|<
name|SolrResultRow
argument_list|>
name|it
parameter_list|,
name|QueryEngineSettings
name|settings
parameter_list|)
block|{
name|Iterator
argument_list|<
name|String
argument_list|>
name|pathIterator
init|=
operator|new
name|Iterator
argument_list|<
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|hasNext
parameter_list|()
block|{
return|return
name|it
operator|.
name|hasNext
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|next
parameter_list|()
block|{
name|currentRow
operator|=
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
return|return
name|currentRow
operator|.
name|path
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|remove
parameter_list|()
block|{
name|it
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
block|}
decl_stmt|;
name|pathCursor
operator|=
operator|new
name|Cursors
operator|.
name|PathCursor
argument_list|(
name|pathIterator
argument_list|,
literal|true
argument_list|,
name|settings
argument_list|)
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
name|pathCursor
operator|.
name|hasNext
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
name|pathCursor
operator|.
name|remove
argument_list|()
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|IndexRow
name|next
parameter_list|()
block|{
specifier|final
name|IndexRow
name|pathRow
init|=
name|pathCursor
operator|.
name|next
argument_list|()
decl_stmt|;
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
name|pathRow
operator|.
name|getPath
argument_list|()
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
comment|// overlay the score
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
return|return
name|PropertyValues
operator|.
name|newDouble
argument_list|(
name|currentRow
operator|.
name|score
argument_list|)
return|;
block|}
comment|// TODO : make inclusion of doc configurable
name|Collection
argument_list|<
name|Object
argument_list|>
name|fieldValues
init|=
name|currentRow
operator|.
name|doc
operator|.
name|getFieldValues
argument_list|(
name|columnName
argument_list|)
decl_stmt|;
return|return
name|currentRow
operator|.
name|doc
operator|!=
literal|null
condition|?
name|PropertyValues
operator|.
name|newString
argument_list|(
name|Iterables
operator|.
name|toString
argument_list|(
name|fieldValues
operator|!=
literal|null
condition|?
name|fieldValues
else|:
name|Collections
operator|.
name|emptyList
argument_list|()
argument_list|)
argument_list|)
else|:
literal|null
return|;
block|}
block|}
return|;
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

