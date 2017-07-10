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
name|importer
package|;
end_package

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|TimeUnit
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
name|index
operator|.
name|AsyncIndexUpdate
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
name|NodeStore
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
name|stats
operator|.
name|Clock
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
comment|/**  * Lock implementation for clustered scenario. The locking is done  * by setting the lease time for the lane to distant future which  * prevent AsyncIndexUpdate from  running.  */
end_comment

begin_class
specifier|public
class|class
name|ClusterNodeStoreLock
implements|implements
name|AsyncIndexerLock
argument_list|<
name|ClusteredLockToken
argument_list|>
block|{
comment|/**      * Use a looong lease time to ensure that async indexer does not start      * in between the import process which can take some time      */
specifier|private
specifier|static
specifier|final
name|long
name|LOCK_TIMEOUT
init|=
name|TimeUnit
operator|.
name|DAYS
operator|.
name|toMillis
argument_list|(
literal|100
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Logger
name|log
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|getClass
argument_list|()
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|NodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|public
name|ClusterNodeStoreLock
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|)
block|{
name|this
argument_list|(
name|nodeStore
argument_list|,
name|Clock
operator|.
name|SIMPLE
argument_list|)
expr_stmt|;
block|}
specifier|public
name|ClusterNodeStoreLock
parameter_list|(
name|NodeStore
name|nodeStore
parameter_list|,
name|Clock
name|clock
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|nodeStore
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|ClusteredLockToken
name|lock
parameter_list|(
name|String
name|asyncIndexerLane
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|async
init|=
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
decl_stmt|;
name|String
name|leaseName
init|=
name|AsyncIndexUpdate
operator|.
name|leasify
argument_list|(
name|asyncIndexerLane
argument_list|)
decl_stmt|;
name|long
name|leaseEndTime
init|=
name|clock
operator|.
name|getTime
argument_list|()
operator|+
name|LOCK_TIMEOUT
decl_stmt|;
if|if
condition|(
name|async
operator|.
name|hasProperty
argument_list|(
name|leaseName
argument_list|)
condition|)
block|{
name|log
operator|.
name|info
argument_list|(
literal|"AsyncIndexer found to be running currently. Lease update would cause its"
operator|+
literal|"commit to fail. Such a failure should be ignored"
argument_list|)
expr_stmt|;
block|}
comment|//TODO Attempt few times if merge failure due to current running indexer cycle
name|async
operator|.
name|setProperty
argument_list|(
name|leaseName
argument_list|,
name|leaseEndTime
argument_list|)
expr_stmt|;
name|NodeStoreUtils
operator|.
name|mergeWithConcurrentCheck
argument_list|(
name|nodeStore
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Acquired the lock for async indexer lane [{}]"
argument_list|,
name|asyncIndexerLane
argument_list|)
expr_stmt|;
return|return
operator|new
name|ClusteredLockToken
argument_list|(
name|asyncIndexerLane
argument_list|,
name|leaseEndTime
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|unlock
parameter_list|(
name|ClusteredLockToken
name|token
parameter_list|)
throws|throws
name|CommitFailedException
block|{
name|String
name|leaseName
init|=
name|AsyncIndexUpdate
operator|.
name|leasify
argument_list|(
name|token
operator|.
name|laneName
argument_list|)
decl_stmt|;
name|NodeBuilder
name|builder
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|builder
argument_list|()
decl_stmt|;
name|NodeBuilder
name|async
init|=
name|builder
operator|.
name|child
argument_list|(
literal|":async"
argument_list|)
decl_stmt|;
name|async
operator|.
name|removeProperty
argument_list|(
name|leaseName
argument_list|)
expr_stmt|;
name|NodeStoreUtils
operator|.
name|mergeWithConcurrentCheck
argument_list|(
name|nodeStore
argument_list|,
name|builder
argument_list|)
expr_stmt|;
name|log
operator|.
name|info
argument_list|(
literal|"Remove the lock for async indexer lane [{}]"
argument_list|,
name|token
operator|.
name|laneName
argument_list|)
expr_stmt|;
block|}
specifier|public
name|boolean
name|isLocked
parameter_list|(
name|String
name|asyncIndexerLane
parameter_list|)
block|{
name|NodeState
name|async
init|=
name|nodeStore
operator|.
name|getRoot
argument_list|()
operator|.
name|getChildNode
argument_list|(
literal|":async"
argument_list|)
decl_stmt|;
name|String
name|leaseName
init|=
name|AsyncIndexUpdate
operator|.
name|leasify
argument_list|(
name|asyncIndexerLane
argument_list|)
decl_stmt|;
return|return
name|async
operator|.
name|hasProperty
argument_list|(
name|leaseName
argument_list|)
return|;
block|}
block|}
end_class

begin_class
class|class
name|ClusteredLockToken
implements|implements
name|AsyncIndexerLock
operator|.
name|LockToken
block|{
specifier|final
name|String
name|laneName
decl_stmt|;
specifier|final
name|long
name|timeout
decl_stmt|;
name|ClusteredLockToken
parameter_list|(
name|String
name|laneName
parameter_list|,
name|long
name|timeout
parameter_list|)
block|{
name|this
operator|.
name|laneName
operator|=
name|laneName
expr_stmt|;
name|this
operator|.
name|timeout
operator|=
name|timeout
expr_stmt|;
block|}
block|}
end_class

end_unit

