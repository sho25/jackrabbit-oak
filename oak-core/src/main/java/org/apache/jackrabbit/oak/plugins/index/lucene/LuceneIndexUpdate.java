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
name|index
operator|.
name|lucene
operator|.
name|FieldFactory
operator|.
name|newPathField
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
name|FieldFactory
operator|.
name|newPropertyField
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
name|jcr
operator|.
name|PropertyType
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
name|Blob
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
name|lucene
operator|.
name|document
operator|.
name|Document
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
name|IndexWriter
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
name|IndexWriterConfig
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
name|tika
operator|.
name|exception
operator|.
name|TikaException
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

begin_class
class|class
name|LuceneIndexUpdate
implements|implements
name|Closeable
implements|,
name|LuceneIndexConstants
block|{
specifier|private
specifier|static
name|IndexWriterConfig
name|getIndexWriterConfig
parameter_list|()
block|{
comment|// FIXME: Hack needed to make Lucene work in an OSGi environment
name|Thread
name|thread
init|=
name|Thread
operator|.
name|currentThread
argument_list|()
decl_stmt|;
name|ClassLoader
name|loader
init|=
name|thread
operator|.
name|getContextClassLoader
argument_list|()
decl_stmt|;
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|IndexWriterConfig
operator|.
name|class
operator|.
name|getClassLoader
argument_list|()
argument_list|)
expr_stmt|;
try|try
block|{
return|return
operator|new
name|IndexWriterConfig
argument_list|(
name|VERSION
argument_list|,
name|ANALYZER
argument_list|)
return|;
block|}
finally|finally
block|{
name|thread
operator|.
name|setContextClassLoader
argument_list|(
name|loader
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
specifier|final
name|IndexWriterConfig
name|config
init|=
name|getIndexWriterConfig
argument_list|()
decl_stmt|;
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
name|LuceneIndexUpdate
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeBuilder
name|index
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
if|if
condition|(
operator|!
name|insert
operator|.
name|containsKey
argument_list|(
name|path
argument_list|)
condition|)
block|{
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
comment|// null value can come from a deleted node, followed by a deleted
comment|// property event which would trigger an update on the previously
comment|// deleted node
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
name|REINDEX_PROPERTY_NAME
argument_list|)
operator|!=
literal|null
operator|&&
name|index
operator|.
name|getProperty
argument_list|(
name|REINDEX_PROPERTY_NAME
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
name|REINDEX_PROPERTY_NAME
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
parameter_list|()
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
name|IndexWriter
name|writer
init|=
literal|null
decl_stmt|;
try|try
block|{
name|writer
operator|=
operator|new
name|IndexWriter
argument_list|(
operator|new
name|ReadWriteOakDirectory
argument_list|(
name|index
operator|.
name|child
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
argument_list|)
argument_list|,
name|config
argument_list|)
expr_stmt|;
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
name|writer
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
name|writer
argument_list|,
name|p
argument_list|,
name|ns
argument_list|)
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
if|if
condition|(
name|writer
operator|!=
literal|null
condition|)
block|{
try|try
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{
comment|//
block|}
block|}
block|}
block|}
specifier|private
name|void
name|deleteSubtreeWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|path
parameter_list|)
throws|throws
name|IOException
block|{
comment|// TODO verify the removal of the entire sub-hierarchy
name|writer
operator|.
name|deleteDocuments
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
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
name|writer
operator|.
name|deleteDocuments
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
block|}
specifier|private
name|void
name|addSubtreeWriter
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|)
throws|throws
name|IOException
block|{
name|writer
operator|.
name|updateDocument
argument_list|(
name|newPathTerm
argument_list|(
name|path
argument_list|)
argument_list|,
name|makeDocument
argument_list|(
name|path
argument_list|,
name|state
argument_list|)
argument_list|)
expr_stmt|;
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
name|NodeStateUtils
operator|.
name|isHidden
argument_list|(
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
continue|continue;
block|}
name|addSubtreeWriter
argument_list|(
name|writer
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|entry
operator|.
name|getName
argument_list|()
argument_list|)
argument_list|,
name|entry
operator|.
name|getNodeState
argument_list|()
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|Document
name|makeDocument
parameter_list|(
name|String
name|path
parameter_list|,
name|NodeState
name|state
parameter_list|)
block|{
name|Document
name|document
init|=
operator|new
name|Document
argument_list|()
decl_stmt|;
name|document
operator|.
name|add
argument_list|(
name|newPathField
argument_list|(
name|path
argument_list|)
argument_list|)
expr_stmt|;
for|for
control|(
name|PropertyState
name|property
range|:
name|state
operator|.
name|getProperties
argument_list|()
control|)
block|{
name|String
name|pname
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
switch|switch
condition|(
name|property
operator|.
name|getType
argument_list|()
operator|.
name|tag
argument_list|()
condition|)
block|{
case|case
name|PropertyType
operator|.
name|BINARY
case|:
for|for
control|(
name|Blob
name|v
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|BINARIES
argument_list|)
control|)
block|{
name|document
operator|.
name|add
argument_list|(
name|newPropertyField
argument_list|(
name|pname
argument_list|,
name|parseStringValue
argument_list|(
name|v
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
default|default:
for|for
control|(
name|String
name|v
range|:
name|property
operator|.
name|getValue
argument_list|(
name|Type
operator|.
name|STRINGS
argument_list|)
control|)
block|{
name|document
operator|.
name|add
argument_list|(
name|newPropertyField
argument_list|(
name|pname
argument_list|,
name|v
argument_list|)
argument_list|)
expr_stmt|;
block|}
break|break;
block|}
block|}
return|return
name|document
return|;
block|}
specifier|private
specifier|static
name|String
name|parseStringValue
parameter_list|(
name|Blob
name|v
parameter_list|)
block|{
try|try
block|{
return|return
name|TIKA
operator|.
name|parseToString
argument_list|(
name|v
operator|.
name|getNewStream
argument_list|()
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|IOException
name|e
parameter_list|)
block|{         }
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{         }
return|return
literal|""
return|;
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
block|}
end_class

end_unit

