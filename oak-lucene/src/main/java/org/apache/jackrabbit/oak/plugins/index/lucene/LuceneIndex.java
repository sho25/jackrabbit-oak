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
name|lucene
package|;
end_package

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|jackrabbit
operator|.
name|JcrConstants
operator|.
name|JCR_MIXINTYPES
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
name|JcrConstants
operator|.
name|JCR_PRIMARYTYPE
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
name|IndexConstants
operator|.
name|INDEX_DEFINITIONS_NAME
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
name|IndexConstants
operator|.
name|TYPE_PROPERTY_NAME
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
name|IndexUtils
operator|.
name|getString
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
name|FieldNames
operator|.
name|PATH
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
name|FieldNames
operator|.
name|PATH_SELECTOR
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
name|LuceneIndexConstants
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|LuceneIndexConstants
operator|.
name|TYPE_LUCENE
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
name|TermFactory
operator|.
name|newPathTerm
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
name|query
operator|.
name|Query
operator|.
name|JCR_PATH
import|;
end_import

begin_import
import|import static
name|org
operator|.
name|apache
operator|.
name|lucene
operator|.
name|search
operator|.
name|BooleanClause
operator|.
name|Occur
operator|.
name|SHOULD
import|;
end_import

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
name|Filter
operator|.
name|PropertyRestriction
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
name|ChildNodeEntry
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
name|jackrabbit
operator|.
name|oak
operator|.
name|spi
operator|.
name|state
operator|.
name|ReadOnlyBuilder
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
name|DirectoryReader
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
name|IndexReader
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
name|MultiFields
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
name|Term
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
name|BooleanClause
operator|.
name|Occur
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
name|BooleanQuery
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
name|MatchAllDocsQuery
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
name|PrefixQuery
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
name|ScoreDoc
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
name|TermQuery
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
name|TermRangeQuery
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
name|TopDocs
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
name|WildcardQuery
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
name|store
operator|.
name|Directory
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
comment|/**  * Provides a QueryIndex that does lookups against a Lucene-based index  *   *<p>  * To define a lucene index on a subtree you have to add an<code>oak:index<code> node.  *   * Under it follows the index definition node that:  *<ul>  *<li>must be of type<code>oak:queryIndexDefinition</code></li>  *<li>must have the<code>type</code> property set to<b><code>lucene</code></b></li>  *</ul>  *</p>  *   *<p>  * Note:<code>reindex<code> is a property that when set to<code>true</code>, triggers a full content reindex.  *</p>  *   *<pre>  *<code>  * {  *     NodeBuilder index = root.child("oak:index");  *     index.child("lucene")  *         .setProperty("jcr:primaryType", "oak:queryIndexDefinition", Type.NAME)  *         .setProperty("type", "lucene")  *         .setProperty("reindex", "true");  * }  *</code>  *</pre>  *   * @see QueryIndex  *   */
end_comment

begin_class
specifier|public
class|class
name|LuceneIndex
implements|implements
name|FulltextQueryIndex
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|LuceneIndex
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
name|LuceneIndex
parameter_list|()
block|{     }
annotation|@
name|Override
specifier|public
name|String
name|getIndexName
parameter_list|()
block|{
return|return
literal|"lucene"
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
comment|// TODO: proper cost calculation
name|NodeState
name|index
init|=
name|getIndexDataNode
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
comment|// unusable index
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
if|if
condition|(
operator|!
name|filter
operator|.
name|getFulltextConditions
argument_list|()
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
literal|0.5
return|;
block|}
comment|// no fulltext, don't use this index
return|return
name|Double
operator|.
name|POSITIVE_INFINITY
return|;
block|}
specifier|private
specifier|static
name|NodeState
name|getIndexDataNode
parameter_list|(
name|NodeState
name|node
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|node
operator|.
name|getChildNode
argument_list|(
name|INDEX_DEFINITIONS_NAME
argument_list|)
decl_stmt|;
for|for
control|(
name|ChildNodeEntry
name|entry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|NodeState
name|ns
init|=
name|entry
operator|.
name|getNodeState
argument_list|()
decl_stmt|;
if|if
condition|(
name|TYPE_LUCENE
operator|.
name|equals
argument_list|(
name|getString
argument_list|(
name|ns
argument_list|,
name|TYPE_PROPERTY_NAME
argument_list|)
argument_list|)
condition|)
block|{
if|if
condition|(
name|ns
operator|.
name|hasChildNode
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
condition|)
block|{
return|return
name|ns
operator|.
name|getChildNode
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
return|;
block|}
comment|// unusable index (not initialized yet)
return|return
literal|null
return|;
block|}
block|}
return|return
literal|null
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
name|root
parameter_list|)
block|{
return|return
name|getQuery
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
literal|null
argument_list|)
operator|.
name|toString
argument_list|()
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
name|NodeState
name|index
init|=
name|getIndexDataNode
argument_list|(
name|root
argument_list|)
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|null
condition|)
block|{
return|return
name|Cursors
operator|.
name|newPathCursor
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
name|Directory
name|directory
init|=
operator|new
name|ReadOnlyOakDirectory
argument_list|(
operator|new
name|ReadOnlyBuilder
argument_list|(
name|index
argument_list|)
argument_list|)
decl_stmt|;
name|long
name|s
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
try|try
block|{
try|try
block|{
name|IndexReader
name|reader
init|=
name|DirectoryReader
operator|.
name|open
argument_list|(
name|directory
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexSearcher
name|searcher
init|=
operator|new
name|IndexSearcher
argument_list|(
name|reader
argument_list|)
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Query
name|query
init|=
name|getQuery
argument_list|(
name|filter
argument_list|,
name|root
argument_list|,
name|reader
argument_list|)
decl_stmt|;
if|if
condition|(
name|query
operator|!=
literal|null
condition|)
block|{
name|TopDocs
name|docs
init|=
name|searcher
operator|.
name|search
argument_list|(
name|query
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|)
decl_stmt|;
for|for
control|(
name|ScoreDoc
name|doc
range|:
name|docs
operator|.
name|scoreDocs
control|)
block|{
name|String
name|path
init|=
name|reader
operator|.
name|document
argument_list|(
name|doc
operator|.
name|doc
argument_list|,
name|PATH_SELECTOR
argument_list|)
operator|.
name|get
argument_list|(
name|PATH
argument_list|)
decl_stmt|;
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
name|paths
operator|.
name|add
argument_list|(
literal|"/"
argument_list|)
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|path
operator|!=
literal|null
condition|)
block|{
name|paths
operator|.
name|add
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
block|}
block|}
name|LOG
operator|.
name|debug
argument_list|(
literal|"query via {} took {} ms."
argument_list|,
name|this
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|s
argument_list|)
expr_stmt|;
return|return
name|Cursors
operator|.
name|newPathCursor
argument_list|(
name|paths
argument_list|)
return|;
block|}
finally|finally
block|{
name|reader
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
finally|finally
block|{
name|directory
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|e
operator|.
name|printStackTrace
argument_list|()
expr_stmt|;
return|return
name|Cursors
operator|.
name|newPathCursor
argument_list|(
name|Collections
operator|.
expr|<
name|String
operator|>
name|emptySet
argument_list|()
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|Query
name|getQuery
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
name|List
argument_list|<
name|Query
argument_list|>
name|qs
init|=
operator|new
name|ArrayList
argument_list|<
name|Query
argument_list|>
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|filter
operator|.
name|matchesAllTypes
argument_list|()
condition|)
block|{
name|addNodeTypeConstraints
argument_list|(
name|qs
argument_list|,
name|filter
argument_list|)
expr_stmt|;
block|}
name|String
name|path
init|=
name|filter
operator|.
name|getPath
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|filter
operator|.
name|getPathRestriction
argument_list|()
condition|)
block|{
case|case
name|ALL_CHILDREN
case|:
if|if
condition|(
literal|"/"
operator|.
name|equals
argument_list|(
name|path
argument_list|)
condition|)
block|{
break|break;
block|}
if|if
condition|(
operator|!
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|+=
literal|"/"
expr_stmt|;
block|}
name|qs
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|DIRECT_CHILDREN
case|:
comment|// FIXME OAK-420
if|if
condition|(
operator|!
name|path
operator|.
name|endsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|+=
literal|"/"
expr_stmt|;
block|}
name|qs
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|EXACT
case|:
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|PARENT
case|:
if|if
condition|(
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|path
argument_list|)
condition|)
block|{
comment|// there's no parent of the root node
return|return
literal|null
return|;
block|}
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|newPathTerm
argument_list|(
name|PathUtils
operator|.
name|getParentPath
argument_list|(
name|path
argument_list|)
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
break|break;
case|case
name|NO_RESTRICTION
case|:
break|break;
block|}
for|for
control|(
name|PropertyRestriction
name|pr
range|:
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
control|)
block|{
name|String
name|name
init|=
name|pr
operator|.
name|propertyName
decl_stmt|;
if|if
condition|(
name|name
operator|.
name|contains
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
comment|// lucene cannot handle child-level property restrictions
continue|continue;
block|}
name|String
name|first
init|=
literal|null
decl_stmt|;
name|String
name|last
init|=
literal|null
decl_stmt|;
name|boolean
name|isLike
init|=
name|pr
operator|.
name|isLike
decl_stmt|;
comment|// TODO what to do with escaped tokens?
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
expr_stmt|;
name|first
operator|=
name|first
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
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
name|pr
operator|.
name|last
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
expr_stmt|;
name|last
operator|=
name|last
operator|.
name|replace
argument_list|(
literal|"\\"
argument_list|,
literal|""
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|isLike
condition|)
block|{
name|first
operator|=
name|first
operator|.
name|replace
argument_list|(
literal|'%'
argument_list|,
name|WildcardQuery
operator|.
name|WILDCARD_STRING
argument_list|)
expr_stmt|;
name|first
operator|=
name|first
operator|.
name|replace
argument_list|(
literal|'_'
argument_list|,
name|WildcardQuery
operator|.
name|WILDCARD_CHAR
argument_list|)
expr_stmt|;
name|int
name|indexOfWS
init|=
name|first
operator|.
name|indexOf
argument_list|(
name|WildcardQuery
operator|.
name|WILDCARD_STRING
argument_list|)
decl_stmt|;
name|int
name|indexOfWC
init|=
name|first
operator|.
name|indexOf
argument_list|(
name|WildcardQuery
operator|.
name|WILDCARD_CHAR
argument_list|)
decl_stmt|;
name|int
name|len
init|=
name|first
operator|.
name|length
argument_list|()
decl_stmt|;
if|if
condition|(
name|indexOfWS
operator|==
name|len
operator|||
name|indexOfWC
operator|==
name|len
condition|)
block|{
comment|// remove trailing "*" for prefixquery
name|first
operator|=
name|first
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|first
operator|.
name|length
argument_list|()
operator|-
literal|1
argument_list|)
expr_stmt|;
if|if
condition|(
name|JCR_PATH
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|qs
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
name|newPathTerm
argument_list|(
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|qs
operator|.
name|add
argument_list|(
operator|new
name|PrefixQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|JCR_PATH
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|qs
operator|.
name|add
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
name|newPathTerm
argument_list|(
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|qs
operator|.
name|add
argument_list|(
operator|new
name|WildcardQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
continue|continue;
block|}
if|if
condition|(
name|first
operator|!=
literal|null
operator|&&
name|first
operator|.
name|equals
argument_list|(
name|last
argument_list|)
operator|&&
name|pr
operator|.
name|firstIncluding
operator|&&
name|pr
operator|.
name|lastIncluding
condition|)
block|{
if|if
condition|(
name|JCR_PATH
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
name|newPathTerm
argument_list|(
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
if|if
condition|(
literal|"*"
operator|.
name|equals
argument_list|(
name|name
argument_list|)
condition|)
block|{
name|addReferenceConstraint
argument_list|(
name|first
argument_list|,
name|qs
argument_list|,
name|reader
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|name
argument_list|,
name|first
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
continue|continue;
block|}
name|qs
operator|.
name|add
argument_list|(
name|TermRangeQuery
operator|.
name|newStringRange
argument_list|(
name|name
argument_list|,
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
if|if
condition|(
name|qs
operator|.
name|size
argument_list|()
operator|==
literal|0
condition|)
block|{
return|return
operator|new
name|MatchAllDocsQuery
argument_list|()
return|;
block|}
if|if
condition|(
name|qs
operator|.
name|size
argument_list|()
operator|==
literal|1
condition|)
block|{
return|return
name|qs
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|Query
name|q
range|:
name|qs
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
name|q
argument_list|,
name|Occur
operator|.
name|MUST
argument_list|)
expr_stmt|;
block|}
return|return
name|bq
return|;
block|}
specifier|private
specifier|static
name|void
name|addReferenceConstraint
parameter_list|(
name|String
name|uuid
parameter_list|,
name|List
argument_list|<
name|Query
argument_list|>
name|qs
parameter_list|,
name|IndexReader
name|reader
parameter_list|)
block|{
if|if
condition|(
name|reader
operator|==
literal|null
condition|)
block|{
comment|// getPlan call
name|qs
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
literal|"*"
argument_list|,
name|uuid
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
return|return;
block|}
comment|// reference query
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
name|Collection
argument_list|<
name|String
argument_list|>
name|fields
init|=
name|MultiFields
operator|.
name|getIndexedFields
argument_list|(
name|reader
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|f
range|:
name|fields
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|f
argument_list|,
name|uuid
argument_list|)
argument_list|)
argument_list|,
name|Occur
operator|.
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|qs
operator|.
name|add
argument_list|(
name|bq
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
name|void
name|addNodeTypeConstraints
parameter_list|(
name|List
argument_list|<
name|Query
argument_list|>
name|qs
parameter_list|,
name|Filter
name|filter
parameter_list|)
block|{
name|BooleanQuery
name|bq
init|=
operator|new
name|BooleanQuery
argument_list|()
decl_stmt|;
for|for
control|(
name|String
name|type
range|:
name|filter
operator|.
name|getPrimaryTypes
argument_list|()
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|,
name|type
argument_list|)
argument_list|)
argument_list|,
name|SHOULD
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|type
range|:
name|filter
operator|.
name|getMixinTypes
argument_list|()
control|)
block|{
name|bq
operator|.
name|add
argument_list|(
operator|new
name|TermQuery
argument_list|(
operator|new
name|Term
argument_list|(
name|JCR_MIXINTYPES
argument_list|,
name|type
argument_list|)
argument_list|)
argument_list|,
name|SHOULD
argument_list|)
expr_stmt|;
block|}
name|qs
operator|.
name|add
argument_list|(
name|bq
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

