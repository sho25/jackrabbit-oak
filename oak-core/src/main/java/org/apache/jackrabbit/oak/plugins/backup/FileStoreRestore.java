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
name|backup
package|;
end_package

begin_import
import|import
name|java
operator|.
name|io
operator|.
name|File
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
name|plugins
operator|.
name|segment
operator|.
name|Compactor
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
name|segment
operator|.
name|SegmentNodeBuilder
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
name|segment
operator|.
name|SegmentNodeState
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
name|segment
operator|.
name|SegmentStore
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
name|segment
operator|.
name|file
operator|.
name|FileStore
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
name|CommitInfo
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
name|EmptyHook
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
name|NodeStore
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

begin_class
specifier|public
class|class
name|FileStoreRestore
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
name|FileStoreRestore
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|static
name|int
name|MAX_FILE_SIZE
init|=
literal|256
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|JOURNAL_FILE_NAME
init|=
literal|"journal.log"
decl_stmt|;
specifier|public
specifier|static
name|void
name|restore
parameter_list|(
name|File
name|source
parameter_list|,
name|NodeStore
name|store
parameter_list|)
throws|throws
name|IOException
throws|,
name|CommitFailedException
block|{
comment|// 1. verify that this is an actual filestore
if|if
condition|(
operator|!
name|validFileStore
argument_list|(
name|source
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IOException
argument_list|(
literal|"Folder "
operator|+
name|source
operator|+
literal|" is not a valid FileStore directory"
argument_list|)
throw|;
block|}
comment|// 2. init filestore
name|FileStore
name|restore
init|=
operator|new
name|FileStore
argument_list|(
name|source
argument_list|,
name|MAX_FILE_SIZE
argument_list|,
literal|false
argument_list|)
decl_stmt|;
try|try
block|{
name|SegmentNodeState
name|state
init|=
name|restore
operator|.
name|getHead
argument_list|()
decl_stmt|;
name|restore
argument_list|(
name|state
operator|.
name|getChildNode
argument_list|(
literal|"root"
argument_list|)
argument_list|,
name|store
argument_list|,
name|restore
argument_list|)
expr_stmt|;
block|}
finally|finally
block|{
name|restore
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
block|}
specifier|private
specifier|static
name|void
name|restore
parameter_list|(
name|NodeState
name|source
parameter_list|,
name|NodeStore
name|store
parameter_list|,
name|SegmentStore
name|restore
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|long
name|s
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
name|NodeState
name|current
init|=
name|store
operator|.
name|getRoot
argument_list|()
decl_stmt|;
name|RestoreCompactor
name|compactor
init|=
operator|new
name|RestoreCompactor
argument_list|(
name|restore
argument_list|)
decl_stmt|;
name|SegmentNodeBuilder
name|builder
init|=
name|compactor
operator|.
name|process
argument_list|(
name|current
argument_list|,
name|source
argument_list|,
name|current
argument_list|)
decl_stmt|;
name|store
operator|.
name|merge
argument_list|(
name|builder
argument_list|,
name|EmptyHook
operator|.
name|INSTANCE
argument_list|,
name|CommitInfo
operator|.
name|EMPTY
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Restore finished in {} ms."
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|-
name|s
argument_list|)
expr_stmt|;
block|}
specifier|private
specifier|static
class|class
name|RestoreCompactor
extends|extends
name|Compactor
block|{
specifier|public
name|RestoreCompactor
parameter_list|(
name|SegmentStore
name|store
parameter_list|)
block|{
name|super
argument_list|(
name|store
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|protected
name|SegmentNodeBuilder
name|process
parameter_list|(
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|,
name|NodeState
name|onto
parameter_list|)
block|{
return|return
name|super
operator|.
name|process
argument_list|(
name|before
argument_list|,
name|after
argument_list|,
name|onto
argument_list|)
return|;
block|}
block|}
specifier|private
specifier|static
name|boolean
name|validFileStore
parameter_list|(
name|File
name|source
parameter_list|)
block|{
if|if
condition|(
name|source
operator|==
literal|null
operator|||
operator|!
name|source
operator|.
name|isDirectory
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
for|for
control|(
name|String
name|f
range|:
name|source
operator|.
name|list
argument_list|()
control|)
block|{
if|if
condition|(
name|JOURNAL_FILE_NAME
operator|.
name|equals
argument_list|(
name|f
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
block|}
end_class

end_unit

