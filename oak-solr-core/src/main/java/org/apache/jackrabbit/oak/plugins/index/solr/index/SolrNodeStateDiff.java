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
name|IOException
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
name|LinkedList
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
name|Nonnull
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
name|IndexEditor
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
name|NodeStateDiff
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
name|common
operator|.
name|SolrInputDocument
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
comment|/**  * A Solr based {@link IndexEditor}  */
end_comment

begin_class
class|class
name|SolrNodeStateDiff
implements|implements
name|NodeStateDiff
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
name|SolrNodeStateDiff
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|solrInputDocuments
decl_stmt|;
specifier|private
specifier|final
name|StringBuilder
name|deleteByIdQueryBuilder
decl_stmt|;
specifier|private
specifier|final
name|SolrServer
name|solrServer
decl_stmt|;
specifier|private
name|IOException
name|exception
decl_stmt|;
specifier|public
name|SolrNodeStateDiff
parameter_list|(
name|SolrServer
name|solrServer
parameter_list|)
block|{
name|this
operator|.
name|solrServer
operator|=
name|solrServer
expr_stmt|;
name|solrInputDocuments
operator|=
operator|new
name|LinkedList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
expr_stmt|;
name|deleteByIdQueryBuilder
operator|=
name|initializeDeleteQueryBuilder
argument_list|()
expr_stmt|;
block|}
specifier|public
name|void
name|postProcess
parameter_list|(
name|NodeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
throw|throw
name|exception
throw|;
block|}
try|try
block|{
comment|// handle adds
if|if
condition|(
name|solrInputDocuments
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
name|solrServer
operator|.
name|add
argument_list|(
name|solrInputDocuments
argument_list|)
expr_stmt|;
block|}
comment|// handle deletions
if|if
condition|(
name|deleteByIdQueryBuilder
operator|.
name|length
argument_list|()
operator|>
literal|12
condition|)
block|{
name|solrServer
operator|.
name|deleteByQuery
argument_list|(
name|deleteByIdQueryBuilder
operator|.
name|append
argument_list|(
literal|")"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|solrServer
operator|.
name|commit
argument_list|()
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
operator|new
name|StringBuilder
argument_list|(
literal|"added "
argument_list|)
operator|.
name|append
argument_list|(
name|solrInputDocuments
operator|.
name|size
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|" documents"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|// free structures
name|solrInputDocuments
operator|.
name|clear
argument_list|()
expr_stmt|;
name|deleteByIdQueryBuilder
operator|.
name|delete
argument_list|(
literal|4
argument_list|,
name|deleteByIdQueryBuilder
operator|.
name|length
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
try|try
block|{
if|if
condition|(
name|solrServer
operator|!=
literal|null
condition|)
block|{
name|solrServer
operator|.
name|rollback
argument_list|()
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e1
parameter_list|)
block|{
comment|// do nothing
block|}
throw|throw
operator|new
name|IOException
argument_list|(
name|e
argument_list|)
throw|;
block|}
block|}
specifier|private
name|Collection
argument_list|<
name|SolrInputDocument
argument_list|>
name|docsFromState
parameter_list|(
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|NodeState
name|state
parameter_list|)
block|{
name|List
argument_list|<
name|SolrInputDocument
argument_list|>
name|solrInputDocuments
init|=
operator|new
name|LinkedList
argument_list|<
name|SolrInputDocument
argument_list|>
argument_list|()
decl_stmt|;
name|SolrInputDocument
name|inputDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
comment|// TODO : make id field configurable
name|inputDocument
operator|.
name|addField
argument_list|(
literal|"path_exact"
argument_list|,
name|path
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|propertyState
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
comment|// TODO : enable selecting field from property type
if|if
condition|(
name|propertyState
operator|.
name|isArray
argument_list|()
condition|)
block|{
for|for
control|(
name|String
name|s
range|:
name|propertyState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|,
name|s
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
name|propertyState
operator|.
name|getName
argument_list|()
argument_list|,
name|propertyState
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRING
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
name|solrInputDocuments
operator|.
name|add
argument_list|(
name|inputDocument
argument_list|)
expr_stmt|;
for|for
control|(
name|ChildNodeEntry
name|childNodeEntry
range|:
name|state
operator|.
name|getChildNodeEntries
argument_list|()
control|)
block|{
name|solrInputDocuments
operator|.
name|addAll
argument_list|(
name|docsFromState
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|'/'
argument_list|)
operator|.
name|append
argument_list|(
name|childNodeEntry
operator|.
name|getName
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|,
name|childNodeEntry
operator|.
name|getNodeState
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|solrInputDocuments
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
comment|// TODO implement this
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
comment|// TODO implement this
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
comment|// TODO implement this
block|}
annotation|@
name|Override
specifier|public
name|void
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
return|return;
block|}
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|addSubtree
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|addSubtree
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
throws|throws
name|IOException
block|{
name|solrInputDocuments
operator|.
name|addAll
argument_list|(
name|docsFromState
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|void
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
return|return;
block|}
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|SolrNodeStateDiff
name|diff
init|=
operator|new
name|SolrNodeStateDiff
argument_list|(
name|solrServer
argument_list|)
decl_stmt|;
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
name|diff
operator|.
name|postProcess
argument_list|(
name|after
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|void
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
return|return;
block|}
if|if
condition|(
name|exception
operator|==
literal|null
condition|)
block|{
try|try
block|{
name|deleteSubtree
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|deleteSubtree
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO : handle cases where default operator is AND
for|for
control|(
name|SolrInputDocument
name|doc
range|:
name|docsFromState
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
control|)
block|{
name|deleteByIdQueryBuilder
operator|.
name|append
argument_list|(
name|doc
operator|.
name|getFieldValue
argument_list|(
literal|"path_exact"
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|StringBuilder
name|initializeDeleteQueryBuilder
parameter_list|()
block|{
return|return
operator|new
name|StringBuilder
argument_list|(
literal|"path_exact:("
argument_list|)
return|;
block|}
block|}
end_class

end_unit

