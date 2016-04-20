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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Preconditions
operator|.
name|checkArgument
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
name|SegmentNodeStore
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
name|tooling
operator|.
name|BasicReadOnlyBlobStore
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
name|FileStoreBackup
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
name|FileStoreBackup
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|boolean
name|USE_FAKE_BLOBSTORE
init|=
name|Boolean
operator|.
name|getBoolean
argument_list|(
literal|"oak.backup.UseFakeBlobStore"
argument_list|)
decl_stmt|;
specifier|public
specifier|static
name|void
name|backup
parameter_list|(
name|NodeStore
name|store
parameter_list|,
name|File
name|destination
parameter_list|)
throws|throws
name|IOException
block|{
name|checkArgument
argument_list|(
name|store
operator|instanceof
name|SegmentNodeStore
argument_list|)
expr_stmt|;
name|Stopwatch
name|watch
init|=
name|Stopwatch
operator|.
name|createStarted
argument_list|()
decl_stmt|;
name|NodeState
name|current
init|=
operator|(
operator|(
name|SegmentNodeStore
operator|)
name|store
operator|)
operator|.
name|getSuperRoot
argument_list|()
decl_stmt|;
name|FileStore
operator|.
name|Builder
name|builder
init|=
name|FileStore
operator|.
name|builder
argument_list|(
name|destination
argument_list|)
operator|.
name|withDefaultMemoryMapping
argument_list|()
decl_stmt|;
if|if
condition|(
name|USE_FAKE_BLOBSTORE
condition|)
block|{
name|builder
operator|.
name|withBlobStore
argument_list|(
operator|new
name|BasicReadOnlyBlobStore
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|FileStore
name|backup
init|=
name|builder
operator|.
name|build
argument_list|()
decl_stmt|;
try|try
block|{
name|SegmentNodeState
name|state
init|=
name|backup
operator|.
name|getHead
argument_list|()
decl_stmt|;
comment|// This is allows us to decouple and fix problems for online compaction independent
comment|// of backup / restore.
comment|//            Compactor compactor = new Compactor(backup.getTracker());
comment|//            compactor.setDeepCheckLargeBinaries(true);
comment|//            compactor.setContentEqualityCheck(true);
comment|//            SegmentNodeState after = compactor.compact(state, current, state);
comment|//            backup.setHead(state, after);
block|}
finally|finally
block|{
name|backup
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
literal|"Backup finished in {}."
argument_list|,
name|watch
argument_list|)
expr_stmt|;
block|}
block|}
end_class

end_unit

