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
name|search
operator|.
name|spi
operator|.
name|query
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
name|concurrent
operator|.
name|Semaphore
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|atomic
operator|.
name|AtomicInteger
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReadWriteLock
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|concurrent
operator|.
name|locks
operator|.
name|ReentrantReadWriteLock
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
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
name|PerfLogger
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
name|search
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
name|plugins
operator|.
name|index
operator|.
name|search
operator|.
name|IndexNode
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
name|search
operator|.
name|ReaderRefreshPolicy
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
name|checkState
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
name|ASYNC_PROPERTY_NAME
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
name|IndexUtils
operator|.
name|getAsyncLaneName
import|;
end_import

begin_class
specifier|public
specifier|abstract
class|class
name|IndexNodeManager
block|{
comment|/**      * Name of the hidden node under which information about the checkpoints      * seen and indexed by each async indexer is kept.      */
specifier|private
specifier|static
specifier|final
name|String
name|ASYNC
init|=
literal|":async"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|AtomicInteger
name|SEARCHER_ID_COUNTER
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|PerfLogger
name|PERF_LOGGER
init|=
operator|new
name|PerfLogger
argument_list|(
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|IndexNodeManager
operator|.
name|class
operator|.
name|getName
argument_list|()
operator|+
literal|".perf"
argument_list|)
argument_list|)
decl_stmt|;
specifier|protected
specifier|abstract
name|IndexNodeManager
name|open
parameter_list|(
name|String
name|indexPath
parameter_list|,
name|NodeState
name|root
parameter_list|,
name|NodeState
name|defnNodeState
parameter_list|)
function_decl|;
specifier|protected
specifier|abstract
name|void
name|releaseResources
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|IndexNode
name|getIndexNode
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|ReaderRefreshPolicy
name|getReaderRefreshPolicy
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|void
name|refreshReaders
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|String
name|getName
parameter_list|()
function_decl|;
specifier|protected
specifier|abstract
name|IndexDefinition
name|getDefinition
parameter_list|()
function_decl|;
specifier|static
name|boolean
name|hasAsyncIndexerRun
parameter_list|(
name|NodeState
name|root
parameter_list|,
name|String
name|indexPath
parameter_list|,
name|NodeState
name|defnNodeState
parameter_list|)
block|{
name|boolean
name|hasAsyncNode
init|=
name|root
operator|.
name|hasChildNode
argument_list|(
name|ASYNC
argument_list|)
decl_stmt|;
name|String
name|asyncLaneName
init|=
name|getAsyncLaneName
argument_list|(
name|defnNodeState
argument_list|,
name|indexPath
argument_list|,
name|defnNodeState
operator|.
name|getProperty
argument_list|(
name|ASYNC_PROPERTY_NAME
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|asyncLaneName
operator|!=
literal|null
condition|)
block|{
return|return
name|hasAsyncNode
operator|&&
name|root
operator|.
name|getChildNode
argument_list|(
name|ASYNC
argument_list|)
operator|.
name|hasProperty
argument_list|(
name|asyncLaneName
argument_list|)
return|;
block|}
else|else
block|{
comment|// useful only for tests - basically non-async index defs which don't rely on /:async
comment|// hence either readers are there (and this method doesn't come into play during open)
comment|// OR there is no cycle (where we return false correctly)
return|return
literal|false
return|;
block|}
block|}
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
name|IndexNodeManager
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|ReadWriteLock
name|lock
init|=
operator|new
name|ReentrantReadWriteLock
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Semaphore
name|refreshLock
init|=
operator|new
name|Semaphore
argument_list|(
literal|1
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|Runnable
name|refreshCallback
init|=
operator|new
name|Runnable
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|run
parameter_list|()
block|{
if|if
condition|(
name|refreshLock
operator|.
name|tryAcquire
argument_list|()
condition|)
block|{
try|try
block|{
name|refreshReaders
argument_list|()
expr_stmt|;
block|}
finally|finally
block|{
name|refreshLock
operator|.
name|release
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
decl_stmt|;
specifier|private
name|boolean
name|closed
init|=
literal|false
decl_stmt|;
annotation|@
name|CheckForNull
name|IndexNode
name|acquire
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
if|if
condition|(
name|closed
condition|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
return|return
literal|null
return|;
block|}
else|else
block|{
name|boolean
name|success
init|=
literal|false
decl_stmt|;
try|try
block|{
name|getReaderRefreshPolicy
argument_list|()
operator|.
name|refreshOnReadIfRequired
argument_list|(
name|refreshCallback
argument_list|)
expr_stmt|;
name|IndexNode
name|indexNode
init|=
name|getIndexNode
argument_list|()
decl_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
return|return
name|indexNode
return|;
block|}
finally|finally
block|{
if|if
condition|(
operator|!
name|success
condition|)
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
block|}
block|}
block|}
specifier|private
name|void
name|release
parameter_list|()
block|{
name|lock
operator|.
name|readLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|void
name|close
parameter_list|()
throws|throws
name|IOException
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|lock
argument_list|()
expr_stmt|;
try|try
block|{
name|checkState
argument_list|(
operator|!
name|closed
argument_list|)
expr_stmt|;
name|closed
operator|=
literal|true
expr_stmt|;
block|}
finally|finally
block|{
name|lock
operator|.
name|writeLock
argument_list|()
operator|.
name|unlock
argument_list|()
expr_stmt|;
block|}
name|releaseResources
argument_list|()
expr_stmt|;
block|}
block|}
end_class

end_unit

