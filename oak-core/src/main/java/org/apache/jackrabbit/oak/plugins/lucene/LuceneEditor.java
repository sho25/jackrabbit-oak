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
name|commons
operator|.
name|PathUtils
operator|.
name|elements
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
name|spi
operator|.
name|query
operator|.
name|IndexDefinition
operator|.
name|INDEX_DATA_CHILD_NAME
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
name|CoreValue
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
name|plugins
operator|.
name|memory
operator|.
name|LongValue
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
name|CommitHook
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
name|IndexDefinition
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
name|lucene
operator|.
name|analysis
operator|.
name|Analyzer
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
name|analysis
operator|.
name|standard
operator|.
name|StandardAnalyzer
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
name|store
operator|.
name|Directory
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
name|util
operator|.
name|Version
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
name|Tika
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

begin_comment
comment|/**  * This class updates a Lucene index when node content is changed.  */
end_comment

begin_class
class|class
name|LuceneEditor
implements|implements
name|CommitHook
implements|,
name|LuceneIndexConstants
block|{
specifier|private
specifier|static
specifier|final
name|Tika
name|TIKA
init|=
operator|new
name|Tika
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Version
name|VERSION
init|=
name|Version
operator|.
name|LUCENE_40
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|Analyzer
name|ANALYZER
init|=
operator|new
name|StandardAnalyzer
argument_list|(
name|VERSION
argument_list|)
decl_stmt|;
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
specifier|final
name|Iterable
argument_list|<
name|String
argument_list|>
name|path
decl_stmt|;
specifier|public
name|LuceneEditor
parameter_list|(
name|IndexDefinition
name|indexDefinition
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|elements
argument_list|(
name|indexDefinition
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
block|}
comment|/*      *       * If before is null, then the #processCommit call is treated as a full      * reindex call      */
annotation|@
name|Override
specifier|public
name|NodeState
name|processCommit
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
name|NodeBuilder
name|rootBuilder
init|=
name|after
operator|.
name|getBuilder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|rootBuilder
decl_stmt|;
for|for
control|(
name|String
name|name
range|:
name|path
control|)
block|{
name|builder
operator|=
name|builder
operator|.
name|getChildBuilder
argument_list|(
name|name
argument_list|)
expr_stmt|;
block|}
name|builder
operator|=
name|builder
operator|.
name|getChildBuilder
argument_list|(
name|INDEX_DATA_CHILD_NAME
argument_list|)
expr_stmt|;
name|Directory
name|directory
init|=
operator|new
name|ReadWriteOakDirectory
argument_list|(
name|builder
argument_list|)
decl_stmt|;
try|try
block|{
name|IndexWriter
name|writer
init|=
operator|new
name|IndexWriter
argument_list|(
name|directory
argument_list|,
name|config
argument_list|)
decl_stmt|;
try|try
block|{
name|LuceneDiff
name|diff
init|=
operator|new
name|LuceneDiff
argument_list|(
name|writer
argument_list|,
literal|"/"
argument_list|)
decl_stmt|;
if|if
condition|(
name|before
operator|!=
literal|null
condition|)
block|{
comment|// normal diff
name|after
operator|.
name|compareAgainstBaseState
argument_list|(
name|before
argument_list|,
name|diff
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// trigger re-indexing
name|diff
operator|.
name|childNodeDeleted
argument_list|(
literal|""
argument_list|,
name|after
argument_list|)
expr_stmt|;
name|diff
operator|.
name|childNodeAdded
argument_list|(
literal|""
argument_list|,
name|after
argument_list|)
expr_stmt|;
block|}
name|diff
operator|.
name|postProcess
argument_list|(
name|after
argument_list|)
expr_stmt|;
name|writer
operator|.
name|commit
argument_list|()
expr_stmt|;
name|builder
operator|.
name|setProperty
argument_list|(
name|INDEX_UPDATE
argument_list|,
operator|new
name|LongValue
argument_list|(
name|System
operator|.
name|currentTimeMillis
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|writer
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
return|return
name|rootBuilder
operator|.
name|getNodeState
argument_list|()
return|;
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
block|}
specifier|private
specifier|static
class|class
name|LuceneDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|IndexWriter
name|writer
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
specifier|private
name|boolean
name|modified
decl_stmt|;
specifier|private
name|IOException
name|exception
decl_stmt|;
specifier|public
name|LuceneDiff
parameter_list|(
name|IndexWriter
name|writer
parameter_list|,
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|writer
operator|=
name|writer
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|path
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
if|if
condition|(
name|modified
condition|)
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
block|}
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
name|modified
operator|=
literal|true
expr_stmt|;
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
name|modified
operator|=
literal|true
expr_stmt|;
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
name|modified
operator|=
literal|true
expr_stmt|;
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
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
name|addDocument
argument_list|(
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
name|addSubtree
argument_list|(
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
name|LuceneDiff
name|diff
init|=
operator|new
name|LuceneDiff
argument_list|(
name|writer
argument_list|,
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
name|concat
argument_list|(
name|path
argument_list|,
name|name
argument_list|)
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
name|deleteDocuments
argument_list|(
name|newPathTerm
argument_list|(
name|path
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
name|deleteSubtree
argument_list|(
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
for|for
control|(
name|CoreValue
name|value
range|:
name|property
operator|.
name|getValues
argument_list|()
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
name|value
argument_list|)
argument_list|)
argument_list|)
expr_stmt|;
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
name|CoreValue
name|value
parameter_list|)
block|{
name|String
name|string
decl_stmt|;
if|if
condition|(
name|value
operator|.
name|getType
argument_list|()
operator|!=
name|PropertyType
operator|.
name|BINARY
condition|)
block|{
name|string
operator|=
name|value
operator|.
name|getString
argument_list|()
expr_stmt|;
block|}
else|else
block|{
try|try
block|{
name|string
operator|=
name|TIKA
operator|.
name|parseToString
argument_list|(
name|value
operator|.
name|getNewStream
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
name|string
operator|=
literal|""
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|TikaException
name|e
parameter_list|)
block|{
name|string
operator|=
literal|""
expr_stmt|;
block|}
block|}
return|return
name|string
return|;
block|}
block|}
block|}
end_class

end_unit

