begin_unit|revision:0.9.5;language:Java;cregit-version:0.0.1
begin_comment
comment|/*  * Licensed to the Apache Software Foundation (ASF) under one or more  * contributor license agreements.  See the NOTICE file distributed with  * this work for additional information regarding copyright ownership.  * The ASF licenses this file to You under the Apache License, Version 2.0  * (the "License"); you may not use this file except in compliance with  * the License.  You may obtain a copy of the License at  *  *      http://www.apache.org/licenses/LICENSE-2.0  *  * Unless required by applicable law or agreed to in writing, software  * distributed under the License is distributed on an "AS IS" BASIS,  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.  * See the License for the specific language governing permissions and  * limitations under the License.  */
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
name|java
operator|.
name|util
operator|.
name|TreeMap
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|TreeSet
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
name|OakSolrUtils
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
import|;
end_import

begin_comment
comment|/**  * A Solr index update  */
end_comment

begin_class
specifier|public
class|class
name|SolrIndexUpdate
implements|implements
name|Closeable
block|{
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
specifier|final
name|NodeBuilder
name|index
decl_stmt|;
specifier|private
name|OakSolrConfiguration
name|configuration
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
name|insert
init|=
operator|new
name|TreeMap
argument_list|<
name|String
argument_list|,
name|NodeState
argument_list|>
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Set
argument_list|<
name|String
argument_list|>
name|remove
init|=
operator|new
name|TreeSet
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
specifier|public
name|SolrIndexUpdate
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|index
parameter_list|,
name|OakSolrConfiguration
name|configuration
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
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|configuration
operator|=
name|configuration
expr_stmt|;
block|}
specifier|public
name|void
name|insert
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|value
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|path
operator|.
name|substring
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|insert
operator|.
name|containsKey
argument_list|(
name|key
argument_list|)
condition|)
block|{
if|if
condition|(
literal|""
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
name|key
operator|=
literal|"/"
expr_stmt|;
block|}
if|if
condition|(
name|value
operator|!=
literal|null
condition|)
block|{
name|insert
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|public
name|void
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
block|{
name|Preconditions
operator|.
name|checkArgument
argument_list|(
name|path
operator|.
name|startsWith
argument_list|(
name|this
operator|.
name|path
argument_list|)
argument_list|)
expr_stmt|;
name|remove
operator|.
name|add
argument_list|(
name|path
operator|.
name|substring
argument_list|(
name|this
operator|.
name|path
operator|.
name|length
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|boolean
name|getAndResetReindexFlag
parameter_list|()
block|{
name|boolean
name|reindex
init|=
name|index
operator|.
name|getProperty
argument_list|(
literal|"reindex"
argument_list|)
operator|!=
literal|null
operator|&&
name|index
operator|.
name|getProperty
argument_list|(
literal|"reindex"
argument_list|)
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BOOLEAN
argument_list|)
decl_stmt|;
name|index
operator|.
name|setProperty
argument_list|(
literal|"reindex"
argument_list|,
literal|false
argument_list|)
expr_stmt|;
return|return
name|reindex
return|;
block|}
specifier|public
name|void
name|apply
parameter_list|(
name|SolrServer
name|solrServer
parameter_list|)
throws|throws
name|CommitFailedException
block|{
if|if
condition|(
name|remove
operator|.
name|isEmpty
argument_list|()
operator|&&
name|insert
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return;
block|}
try|try
block|{
for|for
control|(
name|String
name|p
range|:
name|remove
control|)
block|{
name|deleteSubtreeWriter
argument_list|(
name|solrServer
argument_list|,
name|p
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|String
name|p
range|:
name|insert
operator|.
name|keySet
argument_list|()
control|)
block|{
name|NodeState
name|ns
init|=
name|insert
operator|.
name|get
argument_list|(
name|p
argument_list|)
decl_stmt|;
name|addSubtreeWriter
argument_list|(
name|solrServer
argument_list|,
name|p
argument_list|,
name|ns
argument_list|)
expr_stmt|;
block|}
name|OakSolrUtils
operator|.
name|commitByPolicy
argument_list|(
name|solrServer
argument_list|,
name|configuration
operator|.
name|getCommitPolicy
argument_list|()
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Failed to update the full text search index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
catch|catch
parameter_list|(
name|SolrServerException
name|e
parameter_list|)
block|{
throw|throw
operator|new
name|CommitFailedException
argument_list|(
literal|"Failed to update the full text search index"
argument_list|,
name|e
argument_list|)
throw|;
block|}
finally|finally
block|{
name|remove
operator|.
name|clear
argument_list|()
expr_stmt|;
name|insert
operator|.
name|clear
argument_list|()
expr_stmt|;
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
name|docFromState
argument_list|(
name|path
argument_list|,
name|state
argument_list|)
decl_stmt|;
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
specifier|private
name|SolrInputDocument
name|docFromState
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|SolrInputDocument
name|inputDocument
init|=
operator|new
name|SolrInputDocument
argument_list|()
decl_stmt|;
name|inputDocument
operator|.
name|addField
argument_list|(
name|configuration
operator|.
name|getPathField
argument_list|()
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
comment|// try to get the field to use for this property from configuration
name|String
name|fieldName
init|=
name|configuration
operator|.
name|getFieldNameFor
argument_list|(
name|propertyState
operator|.
name|getType
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|fieldName
operator|!=
literal|null
condition|)
block|{
name|inputDocument
operator|.
name|addField
argument_list|(
name|fieldName
argument_list|,
name|propertyState
operator|.
name|getValue
argument_list|(
name|propertyState
operator|.
name|getType
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// or fallback to adding propertyName:stringValue(s)
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
block|}
return|return
name|inputDocument
return|;
block|}
specifier|private
name|void
name|deleteSubtreeWriter
parameter_list|(
name|SolrServer
name|solrServer
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
comment|// TODO verify the removal of the entire sub-hierarchy
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"/"
operator|+
name|path
expr_stmt|;
block|}
name|path
operator|=
name|path
operator|.
name|replace
argument_list|(
literal|"/"
argument_list|,
literal|"\\/"
argument_list|)
expr_stmt|;
name|solrServer
operator|.
name|deleteByQuery
argument_list|(
operator|new
name|StringBuilder
argument_list|(
name|configuration
operator|.
name|getPathField
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|':'
argument_list|)
operator|.
name|append
argument_list|(
name|path
argument_list|)
operator|.
name|append
argument_list|(
literal|"*"
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
specifier|private
name|void
name|addSubtreeWriter
parameter_list|(
name|SolrServer
name|solrServer
parameter_list|,
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|)
throws|throws
name|IOException
throws|,
name|SolrServerException
block|{
if|if
condition|(
operator|!
name|path
operator|.
name|startsWith
argument_list|(
literal|"/"
argument_list|)
condition|)
block|{
name|path
operator|=
literal|"/"
operator|+
name|path
expr_stmt|;
block|}
name|solrServer
operator|.
name|add
argument_list|(
name|docFromState
argument_list|(
name|path
argument_list|,
name|state
argument_list|)
argument_list|)
expr_stmt|;
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
name|remove
operator|.
name|clear
argument_list|()
expr_stmt|;
name|insert
operator|.
name|clear
argument_list|()
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
literal|"SolrIndexUpdate [path="
operator|+
name|path
operator|+
literal|", insert="
operator|+
name|insert
operator|+
literal|", remove="
operator|+
name|remove
operator|+
literal|"]"
return|;
block|}
block|}
end_class

end_unit

