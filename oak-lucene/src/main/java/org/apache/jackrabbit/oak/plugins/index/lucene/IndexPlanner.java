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
name|lucene
operator|.
name|index
operator|.
name|IndexReader
import|;
end_import

begin_import
import|import static
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Lists
operator|.
name|newArrayListWithCapacity
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
import|import static
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
name|IndexPlan
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
name|spi
operator|.
name|query
operator|.
name|QueryIndex
operator|.
name|OrderEntry
import|;
end_import

begin_class
specifier|public
class|class
name|IndexPlanner
block|{
specifier|private
specifier|final
name|IndexDefinition
name|defn
decl_stmt|;
specifier|private
specifier|final
name|Filter
name|filter
decl_stmt|;
specifier|private
specifier|final
name|String
name|indexPath
decl_stmt|;
specifier|private
specifier|final
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
decl_stmt|;
specifier|private
name|IndexNode
name|indexNode
decl_stmt|;
specifier|public
name|IndexPlanner
parameter_list|(
name|IndexNode
name|indexNode
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|Filter
name|filter
parameter_list|,
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|sortOrder
parameter_list|)
block|{
name|this
operator|.
name|indexNode
operator|=
name|indexNode
expr_stmt|;
name|this
operator|.
name|indexPath
operator|=
name|indexPath
expr_stmt|;
name|this
operator|.
name|defn
operator|=
name|indexNode
operator|.
name|getDefinition
argument_list|()
expr_stmt|;
name|this
operator|.
name|filter
operator|=
name|filter
expr_stmt|;
name|this
operator|.
name|sortOrder
operator|=
name|sortOrder
expr_stmt|;
block|}
name|IndexPlan
name|getPlan
parameter_list|()
block|{
name|IndexPlan
operator|.
name|Builder
name|builder
init|=
name|getPlanBuilder
argument_list|()
decl_stmt|;
return|return
name|builder
operator|!=
literal|null
condition|?
name|builder
operator|.
name|build
argument_list|()
else|:
literal|null
return|;
block|}
specifier|private
name|IndexPlan
operator|.
name|Builder
name|getPlanBuilder
parameter_list|()
block|{
comment|//TODO Support native functions
name|FullTextExpression
name|ft
init|=
name|filter
operator|.
name|getFullTextConstraint
argument_list|()
decl_stmt|;
comment|//IndexPlanner is currently for property indexes only and does not
comment|//support full text indexes
if|if
condition|(
name|ft
operator|!=
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
if|if
condition|(
name|defn
operator|.
name|hasFunctionDefined
argument_list|()
operator|&&
name|filter
operator|.
name|getPropertyRestriction
argument_list|(
name|defn
operator|.
name|getFunctionName
argument_list|()
argument_list|)
operator|!=
literal|null
condition|)
block|{
comment|//If native function is handled by this index then ensure
comment|// that lowest cost if returned
return|return
name|defaultPlan
argument_list|()
operator|.
name|setEstimatedEntryCount
argument_list|(
literal|1
argument_list|)
return|;
block|}
name|List
argument_list|<
name|String
argument_list|>
name|indexedProps
init|=
name|newArrayListWithCapacity
argument_list|(
name|filter
operator|.
name|getPropertyRestrictions
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
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
comment|//Only those properties which are included and not tokenized
comment|//can be managed by lucene for property restrictions
if|if
condition|(
name|defn
operator|.
name|includeProperty
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
operator|&&
name|defn
operator|.
name|skipTokenization
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
condition|)
block|{
name|indexedProps
operator|.
name|add
argument_list|(
name|pr
operator|.
name|propertyName
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
operator|!
name|indexedProps
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
comment|//TODO Need a way to have better cost estimate to indicate that
comment|//this index can evaluate more propertyRestrictions natively (if more props are indexed)
comment|//For now we reduce cost per entry
name|IndexPlan
operator|.
name|Builder
name|plan
init|=
name|defaultPlan
argument_list|()
decl_stmt|;
if|if
condition|(
name|plan
operator|!=
literal|null
condition|)
block|{
return|return
name|plan
operator|.
name|setCostPerEntry
argument_list|(
literal|1.0
operator|/
name|indexedProps
operator|.
name|size
argument_list|()
argument_list|)
return|;
block|}
block|}
comment|//TODO Support for path restrictions
comment|//TODO support for NodeTypes
comment|//TODO Support for property existence queries
comment|//TODO support for nodeName queries
return|return
literal|null
return|;
block|}
specifier|private
name|IndexPlan
operator|.
name|Builder
name|defaultPlan
parameter_list|()
block|{
return|return
operator|new
name|IndexPlan
operator|.
name|Builder
argument_list|()
operator|.
name|setCostPerExecution
argument_list|(
literal|1
argument_list|)
comment|// we're local. Low-cost
operator|.
name|setCostPerEntry
argument_list|(
literal|1
argument_list|)
operator|.
name|setFulltextIndex
argument_list|(
name|defn
operator|.
name|isFullTextEnabled
argument_list|()
argument_list|)
operator|.
name|setIncludesNodeData
argument_list|(
literal|false
argument_list|)
comment|// we should not include node data
operator|.
name|setFilter
argument_list|(
name|filter
argument_list|)
operator|.
name|setPathPrefix
argument_list|(
name|getPathPrefix
argument_list|()
argument_list|)
operator|.
name|setSortOrder
argument_list|(
name|createSortOrder
argument_list|()
argument_list|)
operator|.
name|setDelayed
argument_list|(
literal|true
argument_list|)
comment|//Lucene is always async
operator|.
name|setAttribute
argument_list|(
name|LuceneIndex
operator|.
name|ATTR_INDEX_PATH
argument_list|,
name|indexPath
argument_list|)
operator|.
name|setEstimatedEntryCount
argument_list|(
name|getReader
argument_list|()
operator|.
name|numDocs
argument_list|()
argument_list|)
return|;
block|}
specifier|private
name|String
name|getPathPrefix
parameter_list|()
block|{
name|String
name|parentPath
init|=
name|PathUtils
operator|.
name|getAncestorPath
argument_list|(
name|indexPath
argument_list|,
literal|2
argument_list|)
decl_stmt|;
return|return
name|PathUtils
operator|.
name|denotesRoot
argument_list|(
name|parentPath
argument_list|)
condition|?
literal|""
else|:
name|parentPath
return|;
block|}
specifier|private
name|IndexReader
name|getReader
parameter_list|()
block|{
return|return
name|indexNode
operator|.
name|getSearcher
argument_list|()
operator|.
name|getIndexReader
argument_list|()
return|;
block|}
annotation|@
name|CheckForNull
specifier|private
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|createSortOrder
parameter_list|()
block|{
comment|//TODO Refine later once we make mixed indexes having both
comment|//full text  and property index
if|if
condition|(
name|defn
operator|.
name|isFullTextEnabled
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
if|if
condition|(
name|sortOrder
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|List
argument_list|<
name|OrderEntry
argument_list|>
name|orderEntries
init|=
name|newArrayListWithCapacity
argument_list|(
name|sortOrder
operator|.
name|size
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|OrderEntry
name|o
range|:
name|sortOrder
control|)
block|{
comment|//sorting can only be done for known/configured properties
comment|// and whose types are known
comment|//TODO Can sorting be done for array properties
if|if
condition|(
name|defn
operator|.
name|includeProperty
argument_list|(
name|o
operator|.
name|getPropertyName
argument_list|()
argument_list|)
operator|||
name|defn
operator|.
name|isOrdered
argument_list|(
name|o
operator|.
name|getPropertyName
argument_list|()
argument_list|)
operator|&&
name|o
operator|.
name|getPropertyType
argument_list|()
operator|!=
literal|null
operator|&&
operator|!
name|o
operator|.
name|getPropertyType
argument_list|()
operator|.
name|isArray
argument_list|()
condition|)
block|{
name|orderEntries
operator|.
name|add
argument_list|(
name|o
argument_list|)
expr_stmt|;
comment|//Lucene can manage any order desc/asc
block|}
block|}
return|return
name|orderEntries
return|;
block|}
block|}
end_class

end_unit

