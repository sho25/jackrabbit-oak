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
name|document
package|;
end_package

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
name|document
operator|.
name|util
operator|.
name|Utils
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
name|document
operator|.
name|Collection
operator|.
name|CLUSTER_NODES
import|;
end_import

begin_comment
comment|/**  * Implements the recovery handler on startup of a {@link DocumentNodeStore}.  */
end_comment

begin_class
class|class
name|RecoveryHandlerImpl
implements|implements
name|RecoveryHandler
block|{
specifier|private
specifier|static
specifier|final
name|Logger
name|LOG
init|=
name|LoggerFactory
operator|.
name|getLogger
argument_list|(
name|RecoveryHandlerImpl
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|int
name|COMMIT_VALUE_CACHE_SIZE
init|=
literal|10000
decl_stmt|;
comment|/**      * The timeout in milliseconds to wait for the recovery performed by      * another cluster node.      */
specifier|private
name|long
name|recoveryWaitTimeoutMS
init|=
name|Long
operator|.
name|getLong
argument_list|(
literal|"oak.recoveryWaitTimeoutMS"
argument_list|,
literal|60000
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
specifier|private
specifier|final
name|Clock
name|clock
decl_stmt|;
specifier|private
specifier|final
name|MissingLastRevSeeker
name|lastRevSeeker
decl_stmt|;
name|RecoveryHandlerImpl
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|Clock
name|clock
parameter_list|,
name|MissingLastRevSeeker
name|lastRevSeeker
parameter_list|)
block|{
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|clock
operator|=
name|clock
expr_stmt|;
name|this
operator|.
name|lastRevSeeker
operator|=
name|lastRevSeeker
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|recover
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
try|try
block|{
return|return
name|recoverInternal
argument_list|(
name|clusterId
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|DocumentStoreException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Recovery failed for cluster node {}"
argument_list|,
name|clusterId
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
block|}
specifier|private
name|boolean
name|recoverInternal
parameter_list|(
name|int
name|clusterId
parameter_list|)
throws|throws
name|DocumentStoreException
block|{
name|NodeDocument
name|root
init|=
name|Utils
operator|.
name|getRootDocument
argument_list|(
name|store
argument_list|)
decl_stmt|;
comment|// prepare a context for recovery
name|RevisionContext
name|context
init|=
operator|new
name|RecoveryContext
argument_list|(
name|root
argument_list|,
name|clock
argument_list|,
name|clusterId
argument_list|,
operator|new
name|CachingCommitValueResolver
argument_list|(
name|COMMIT_VALUE_CACHE_SIZE
argument_list|,
name|root
operator|::
name|getSweepRevisions
argument_list|)
argument_list|)
decl_stmt|;
name|LastRevRecoveryAgent
name|agent
init|=
operator|new
name|LastRevRecoveryAgent
argument_list|(
name|store
argument_list|,
name|context
argument_list|,
name|lastRevSeeker
argument_list|,
name|id
lambda|->
block|{}
argument_list|)
decl_stmt|;
name|long
name|timeout
init|=
name|context
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
operator|+
name|recoveryWaitTimeoutMS
decl_stmt|;
name|int
name|numRecovered
init|=
name|agent
operator|.
name|recover
argument_list|(
name|clusterId
argument_list|,
name|timeout
argument_list|)
decl_stmt|;
if|if
condition|(
name|numRecovered
operator|==
operator|-
literal|1
condition|)
block|{
name|ClusterNodeInfoDocument
name|doc
init|=
name|store
operator|.
name|find
argument_list|(
name|CLUSTER_NODES
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|clusterId
argument_list|)
argument_list|)
decl_stmt|;
name|String
name|otherId
init|=
literal|"n/a"
decl_stmt|;
if|if
condition|(
name|doc
operator|!=
literal|null
condition|)
block|{
name|otherId
operator|=
name|String
operator|.
name|valueOf
argument_list|(
name|doc
operator|.
name|get
argument_list|(
name|ClusterNodeInfo
operator|.
name|REV_RECOVERY_BY
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|String
name|msg
init|=
literal|"This cluster node ("
operator|+
name|clusterId
operator|+
literal|") requires "
operator|+
literal|"_lastRev recovery which is currently performed by "
operator|+
literal|"another cluster node ("
operator|+
name|otherId
operator|+
literal|"). Recovery is "
operator|+
literal|"still ongoing after "
operator|+
name|recoveryWaitTimeoutMS
operator|+
literal|" ms."
decl_stmt|;
name|LOG
operator|.
name|info
argument_list|(
name|msg
argument_list|)
expr_stmt|;
return|return
literal|false
return|;
block|}
return|return
literal|true
return|;
block|}
block|}
end_class

end_unit
