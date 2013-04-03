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
name|index
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|Closeable
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
name|HashMap
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
name|api
operator|.
name|CommitFailedException
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
name|plugins
operator|.
name|index
operator|.
name|IndexHook
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
name|plugins
operator|.
name|index
operator|.
name|solr
operator|.
name|query
operator|.
name|SolrQueryIndex
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
name|commit
operator|.
name|Editor
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
name|commit
operator|.
name|EditorHook
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
name|commit
operator|.
name|EditorProvider
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
name|NodeStateUtils
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
name|commons
operator|.
name|PathUtils
operator|.
name|concat
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
name|memory
operator|.
name|EmptyNodeState
operator|.
name|EMPTY_NODE
import|;
end_import

begin_comment
comment|/**  * {@link IndexHook} implementation that is responsible for keeping the  * {@link org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndex} up to date  *<p/>  * This handles index updates by keeping a {@link Map} of<code>String</code>  * and {@link SolrIndexUpdate} for each path.  *  * @see org.apache.jackrabbit.oak.plugins.index.solr.query.SolrQueryIndex  * @see SolrIndexHook  */
end_comment

begin_class
specifier|public
class|class
name|SolrIndexDiff
implements|implements
name|IndexHook
implements|,
name|Closeable
block|{
specifier|private
specifier|final
name|SolrIndexDiff
name|parent
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|node
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|private
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|SolrIndexUpdate
argument_list|>
name|updates
decl_stmt|;
specifier|private
name|SolrServer
name|solrServer
decl_stmt|;
specifier|private
name|OakSolrConfiguration
name|configuration
decl_stmt|;
comment|/**      * the root editor in charge of applying the updates      */
specifier|private
specifier|final
name|boolean
name|isRoot
decl_stmt|;
specifier|private
name|SolrIndexDiff
parameter_list|(
name|SolrIndexDiff
name|parent
parameter_list|,
name|NodeBuilder
name|node
parameter_list|,
name|SolrServer
name|solrServer
parameter_list|,
name|String
name|name
parameter_list|,
name|String
name|path
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|SolrIndexUpdate
argument_list|>
name|updates
parameter_list|,
name|OakSolrConfiguration
name|configuration
parameter_list|,
name|boolean
name|isRoot
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|node
operator|=
name|node
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|updates
operator|=
name|updates
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
name|isRoot
operator|=
name|isRoot
expr_stmt|;
block|}
specifier|private
name|SolrIndexDiff
parameter_list|(
name|SolrIndexDiff
name|parent
parameter_list|,
name|SolrServer
name|solrServer
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
argument_list|(
name|parent
argument_list|,
name|getChildNode
argument_list|(
name|parent
operator|.
name|node
argument_list|,
name|name
argument_list|)
argument_list|,
name|solrServer
argument_list|,
name|name
argument_list|,
literal|null
argument_list|,
name|parent
operator|.
name|updates
argument_list|,
name|parent
operator|.
name|configuration
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|SolrIndexDiff
parameter_list|(
name|NodeBuilder
name|root
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
literal|null
argument_list|,
name|root
argument_list|,
name|solrServer
argument_list|,
literal|null
argument_list|,
literal|"/"
argument_list|,
operator|new
name|HashMap
argument_list|<
name|String
argument_list|,
name|SolrIndexUpdate
argument_list|>
argument_list|()
argument_list|,
name|configuration
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|enter
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
comment|// TODO : test properly on PDFs
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
operator|.
name|hasChildNode
argument_list|(
literal|"oak:index"
argument_list|)
condition|)
block|{
name|NodeBuilder
name|index
init|=
name|node
operator|.
name|child
argument_list|(
literal|"oak:index"
argument_list|)
decl_stmt|;
for|for
control|(
name|String
name|indexName
range|:
name|index
operator|.
name|getChildNodeNames
argument_list|()
control|)
block|{
name|NodeBuilder
name|child
init|=
name|index
operator|.
name|child
argument_list|(
name|indexName
argument_list|)
decl_stmt|;
if|if
condition|(
name|isIndexNode
argument_list|(
name|child
argument_list|)
operator|&&
operator|!
name|this
operator|.
name|updates
operator|.
name|containsKey
argument_list|(
name|getPath
argument_list|()
argument_list|)
condition|)
block|{
name|this
operator|.
name|updates
operator|.
name|put
argument_list|(
name|getPath
argument_list|()
argument_list|,
operator|new
name|SolrIndexUpdate
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|child
argument_list|,
name|configuration
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
block|}
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|name
operator|!=
literal|null
operator|&&
operator|!
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|insert
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
specifier|static
name|NodeBuilder
name|getChildNode
parameter_list|(
name|NodeBuilder
name|node
parameter_list|,
name|String
name|name
parameter_list|)
block|{
if|if
condition|(
name|node
operator|!=
literal|null
operator|&&
name|node
operator|.
name|hasChildNode
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|node
operator|.
name|child
argument_list|(
name|name
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
if|if
condition|(
name|path
operator|==
literal|null
condition|)
block|{
name|path
operator|=
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
expr_stmt|;
block|}
return|return
name|path
return|;
block|}
specifier|private
specifier|static
name|boolean
name|isIndexNode
parameter_list|(
name|NodeBuilder
name|node
parameter_list|)
block|{
name|PropertyState
name|ps
init|=
name|node
operator|.
name|getProperty
argument_list|(
name|JCR_PRIMARYTYPE
argument_list|)
decl_stmt|;
name|boolean
name|isNodeType
init|=
name|ps
operator|!=
literal|null
operator|&&
operator|!
name|ps
operator|.
name|isArray
argument_list|()
operator|&&
name|ps
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|.
name|equals
argument_list|(
literal|"oak:queryIndexDefinition"
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|isNodeType
condition|)
block|{
return|return
literal|false
return|;
block|}
name|PropertyState
name|type
init|=
name|node
operator|.
name|getProperty
argument_list|(
literal|"type"
argument_list|)
decl_stmt|;
name|boolean
name|isIndexType
init|=
name|type
operator|!=
literal|null
operator|&&
operator|!
name|type
operator|.
name|isArray
argument_list|()
operator|&&
name|type
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
operator|.
name|equals
argument_list|(
name|SolrQueryIndex
operator|.
name|TYPE
argument_list|)
decl_stmt|;
return|return
name|isIndexType
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|insert
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|insert
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|insert
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|node
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|insert
argument_list|(
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|,
operator|new
name|ReadOnlyBuilder
argument_list|(
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|EMPTY_NODE
argument_list|,
name|after
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
return|return
operator|new
name|SolrIndexDiff
argument_list|(
name|this
argument_list|,
name|solrServer
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|Editor
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
literal|null
return|;
block|}
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|remove
argument_list|(
name|concat
argument_list|(
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|leave
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
operator|!
name|isRoot
condition|)
block|{
return|return;
block|}
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|apply
argument_list|(
name|solrServer
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|reindex
parameter_list|(
name|NodeBuilder
name|state
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|boolean
name|reindex
init|=
literal|false
decl_stmt|;
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
if|if
condition|(
name|update
operator|.
name|getAndResetReindexFlag
argument_list|()
condition|)
block|{
name|reindex
operator|=
literal|true
expr_stmt|;
block|}
block|}
if|if
condition|(
name|reindex
condition|)
block|{
name|EditorProvider
name|provider
init|=
operator|new
name|EditorProvider
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|Editor
name|getRootEditor
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeBuilder
name|builder
parameter_list|)
block|{
return|return
operator|new
name|SolrIndexDiff
argument_list|(
name|node
argument_list|,
name|solrServer
argument_list|,
name|configuration
argument_list|)
return|;
block|}
block|}
decl_stmt|;
name|EditorHook
name|eh
init|=
operator|new
name|EditorHook
argument_list|(
name|provider
argument_list|)
decl_stmt|;
name|eh
operator|.
name|processCommit
argument_list|(
name|EMPTY_NODE
argument_list|,
name|state
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
for|for
control|(
name|SolrIndexUpdate
name|update
range|:
name|updates
operator|.
name|values
argument_list|()
control|)
block|{
name|update
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|updates
operator|.
name|clear
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

