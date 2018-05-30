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
name|Set
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
name|solr
operator|.
name|util
operator|.
name|SolrIndexInitializer
operator|.
name|isSolrIndexNode
import|;
end_import

begin_comment
comment|/**  * Lookup for Solr indexes to be used for a given {@link Filter}.  */
end_comment

begin_class
class|class
name|SolrIndexLookup
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
name|SolrIndexLookup
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeState
name|root
decl_stmt|;
name|SolrIndexLookup
parameter_list|(
name|NodeState
name|root
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|root
expr_stmt|;
block|}
name|Collection
argument_list|<
name|String
argument_list|>
name|collectIndexNodePaths
parameter_list|(
name|Filter
name|filter
parameter_list|)
block|{
return|return
name|collectIndexNodePaths
argument_list|(
name|filter
argument_list|,
literal|true
argument_list|)
return|;
block|}
specifier|private
name|Collection
argument_list|<
name|String
argument_list|>
name|collectIndexNodePaths
parameter_list|(
name|Filter
name|filter
parameter_list|,
name|boolean
name|recurse
parameter_list|)
block|{
name|Set
argument_list|<
name|String
argument_list|>
name|paths
init|=
name|Sets
operator|.
name|newHashSet
argument_list|()
decl_stmt|;
name|collectIndexNodePaths
argument_list|(
name|root
argument_list|,
literal|"/"
argument_list|,
name|paths
argument_list|)
expr_stmt|;
if|if
condition|(
name|recurse
condition|)
block|{
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|NodeState
name|nodeState
init|=
name|root
decl_stmt|;
for|for
control|(
name|String
name|element
range|:
name|PathUtils
operator|.
name|elements
argument_list|(
name|filter
operator|.
name|getPath
argument_list|()
argument_list|)
control|)
block|{
name|nodeState
operator|=
name|nodeState
operator|.
name|getChildNode
argument_list|(
name|element
argument_list|)
expr_stmt|;
name|collectIndexNodePaths
argument_list|(
name|nodeState
argument_list|,
name|sb
operator|.
name|append
argument_list|(
literal|"/"
argument_list|)
operator|.
name|append
argument_list|(
name|element
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|paths
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|paths
return|;
block|}
specifier|private
specifier|static
name|void
name|collectIndexNodePaths
parameter_list|(
name|NodeState
name|nodeState
parameter_list|,
name|String
name|parentPath
parameter_list|,
name|Collection
argument_list|<
name|String
argument_list|>
name|paths
parameter_list|)
block|{
name|NodeState
name|state
init|=
name|nodeState
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
if|if
condition|(
name|isSolrIndexNode
argument_list|(
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
condition|)
block|{
name|String
name|indexNodePath
init|=
name|createIndexNodePath
argument_list|(
name|parentPath
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
decl_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"found Solr index node at {}"
argument_list|,
name|indexNodePath
argument_list|)
expr_stmt|;
name|paths
operator|.
name|add
argument_list|(
name|indexNodePath
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|String
name|createIndexNodePath
parameter_list|(
name|String
name|parentPath
parameter_list|,
name|String
name|name
parameter_list|)
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parentPath
argument_list|,
name|INDEX_DEFINITIONS_NAME
argument_list|,
name|name
argument_list|)
return|;
block|}
block|}
end_class

end_unit

