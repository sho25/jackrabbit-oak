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
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|newFileStore
import|;
end_import

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
name|plugins
operator|.
name|segment
operator|.
name|file
operator|.
name|FileStore
operator|.
name|ReadOnlyStore
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
name|Stopwatch
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
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
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
name|FileStore
name|restore
init|=
operator|new
name|ReadOnlyStore
argument_list|(
name|source
argument_list|)
decl_stmt|;
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|FileStore
name|store
init|=
name|newFileStore
argument_list|(
name|destination
argument_list|)
operator|.
name|create
argument_list|()
decl_stmt|;
name|SegmentNodeState
name|current
init|=
name|store
operator|.
name|getHead
argument_list|()
decl_stmt|;
try|try
block|{
name|Compactor
name|compactor
init|=
operator|new
name|Compactor
argument_list|(
name|store
operator|.
name|getTracker
argument_list|()
argument_list|)
decl_stmt|;
name|compactor
operator|.
name|setDeepCheckLargeBinaries
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|SegmentNodeState
name|after
init|=
name|compactor
operator|.
name|compact
argument_list|(
name|current
argument_list|,
name|restore
operator|.
name|getHead
argument_list|()
argument_list|,
name|current
argument_list|)
decl_stmt|;
name|store
operator|.
name|setHead
argument_list|(
name|current
argument_list|,
name|after
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
name|store
operator|.
name|close
argument_list|()
expr_stmt|;
block|}
name|watch
operator|.
name|stop
argument_list|()
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Restore finished in {}."
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
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
block|{
name|log
operator|.
name|warn
argument_list|(
literal|"Restore not available as an online operation."
argument_list|)
expr_stmt|;
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

