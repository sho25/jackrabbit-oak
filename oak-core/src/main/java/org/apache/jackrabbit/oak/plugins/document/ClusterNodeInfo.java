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
name|checkNotNull
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
name|Document
operator|.
name|ID
import|;
end_import

begin_import
import|import
name|java
operator|.
name|lang
operator|.
name|management
operator|.
name|ManagementFactory
import|;
end_import

begin_import
import|import
name|java
operator|.
name|net
operator|.
name|NetworkInterface
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|ArrayList
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Collections
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Enumeration
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
name|UUID
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
name|StringUtils
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
name|apache
operator|.
name|jackrabbit
operator|.
name|oak
operator|.
name|util
operator|.
name|OakVersion
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
comment|/**  * Information about a cluster node.  */
end_comment

begin_class
specifier|public
class|class
name|ClusterNodeInfo
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
name|ClusterNodeInfo
operator|.
name|class
argument_list|)
decl_stmt|;
comment|/**      * The prefix for random (non-reusable) keys.      */
specifier|private
specifier|static
specifier|final
name|String
name|RANDOM_PREFIX
init|=
literal|"random:"
decl_stmt|;
comment|/**      * The machine id.      */
specifier|private
specifier|static
specifier|final
name|String
name|MACHINE_ID_KEY
init|=
literal|"machine"
decl_stmt|;
comment|/**      * The Oak version.      */
specifier|private
specifier|static
specifier|final
name|String
name|OAK_VERSION_KEY
init|=
literal|"oakVersion"
decl_stmt|;
comment|/**      * The unique instance id within this machine (the current working directory      * if not set).      */
specifier|private
specifier|static
specifier|final
name|String
name|INSTANCE_ID_KEY
init|=
literal|"instance"
decl_stmt|;
comment|/**      * The end of the lease.      */
specifier|public
specifier|static
specifier|final
name|String
name|LEASE_END_KEY
init|=
literal|"leaseEnd"
decl_stmt|;
comment|/**      * The state of the cluster. On proper shutdown the state should be cleared.      *      * @see org.apache.jackrabbit.oak.plugins.document.ClusterNodeInfo.ClusterNodeState      */
specifier|public
specifier|static
specifier|final
name|String
name|STATE
init|=
literal|"state"
decl_stmt|;
specifier|public
specifier|static
enum|enum
name|ClusterNodeState
block|{
name|NONE
block|,
comment|/**          * Indicates that cluster node is currently active          */
name|ACTIVE
block|;
specifier|static
name|ClusterNodeState
name|fromString
parameter_list|(
name|String
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
name|NONE
return|;
block|}
return|return
name|valueOf
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
comment|/**      * Flag to indicate whether the _lastRev recovery is in progress.      *      * @see RecoverLockState      */
specifier|public
specifier|static
specifier|final
name|String
name|REV_RECOVERY_LOCK
init|=
literal|"recoveryLock"
decl_stmt|;
specifier|public
specifier|static
enum|enum
name|RecoverLockState
block|{
name|NONE
block|,
comment|/**          * _lastRev recovery in progress          */
name|ACQUIRED
block|;
specifier|static
name|RecoverLockState
name|fromString
parameter_list|(
name|String
name|state
parameter_list|)
block|{
if|if
condition|(
name|state
operator|==
literal|null
condition|)
block|{
return|return
name|NONE
return|;
block|}
return|return
name|valueOf
argument_list|(
name|state
argument_list|)
return|;
block|}
block|}
comment|/**      * Additional info, such as the process id, for support.      */
specifier|private
specifier|static
specifier|final
name|String
name|INFO_KEY
init|=
literal|"info"
decl_stmt|;
comment|/**      * The read/write mode key. Specifies the read/write preference to be used with      * DocumentStore      */
specifier|private
specifier|static
specifier|final
name|String
name|READ_WRITE_MODE_KEY
init|=
literal|"readWriteMode"
decl_stmt|;
comment|/**      * The unique machine id (the MAC address if available).      */
specifier|private
specifier|static
specifier|final
name|String
name|MACHINE_ID
init|=
name|getMachineId
argument_list|()
decl_stmt|;
comment|/**      * The process id (if available).      */
specifier|private
specifier|static
specifier|final
name|long
name|PROCESS_ID
init|=
name|getProcessId
argument_list|()
decl_stmt|;
comment|/**      * The current working directory.      */
specifier|private
specifier|static
specifier|final
name|String
name|WORKING_DIR
init|=
name|System
operator|.
name|getProperty
argument_list|(
literal|"user.dir"
argument_list|,
literal|""
argument_list|)
decl_stmt|;
comment|/**      *<b>Only Used For Testing</b>      */
specifier|private
specifier|static
name|Clock
name|clock
init|=
name|Clock
operator|.
name|SIMPLE
decl_stmt|;
specifier|public
specifier|static
specifier|final
name|int
name|DEFAULT_LEASE_DURATION_MILLIS
init|=
literal|1000
operator|*
literal|60
decl_stmt|;
comment|/**      * The number of milliseconds for a lease (1 minute by default, and      * initially).      */
specifier|private
name|long
name|leaseTime
init|=
name|DEFAULT_LEASE_DURATION_MILLIS
decl_stmt|;
comment|/**      * The assigned cluster id.      */
specifier|private
specifier|final
name|int
name|id
decl_stmt|;
comment|/**      * The machine id.      */
specifier|private
specifier|final
name|String
name|machineId
decl_stmt|;
comment|/**      * The instance id.      */
specifier|private
specifier|final
name|String
name|instanceId
decl_stmt|;
comment|/**      * The document store that is used to renew the lease.      */
specifier|private
specifier|final
name|DocumentStore
name|store
decl_stmt|;
comment|/**      * The time (in milliseconds UTC) where this instance was started.      */
specifier|private
specifier|final
name|long
name|startTime
decl_stmt|;
comment|/**      * A unique id.      */
specifier|private
specifier|final
name|String
name|uuid
init|=
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
decl_stmt|;
comment|/**      * The time (in milliseconds UTC) where the lease of this instance ends.      */
specifier|private
specifier|volatile
name|long
name|leaseEndTime
decl_stmt|;
comment|/**      * The read/write mode.      */
specifier|private
name|String
name|readWriteMode
decl_stmt|;
comment|/**      * The state of the cluter node.      */
specifier|private
name|ClusterNodeState
name|state
decl_stmt|;
comment|/**      * Whether or not the OAK-2739/leaseCheck failed and thus a System.exit was already triggered      * (is used to avoid calling System.exit a hundred times when it then happens)      */
specifier|private
specifier|volatile
name|boolean
name|systemExitTriggered
decl_stmt|;
comment|/**      * OAK-2739: for development it would be useful to be able to disable the      * lease check - hence there's a system property that does that:      * oak.documentMK.disableLeaseCheck      */
specifier|private
specifier|final
name|boolean
name|leaseCheckDisabled
decl_stmt|;
comment|/**      * Tracks the fact whether the lease has *ever* been renewed by this instance      * or has just be read from the document store at initialization time.      */
specifier|private
name|boolean
name|renewed
decl_stmt|;
comment|/**      * The revLock value of the cluster;      */
specifier|private
name|RecoverLockState
name|revRecoveryLock
decl_stmt|;
comment|/**      * In memory flag indicating that this ClusterNode is entry is new and is being added to      * DocumentStore for the first time      *      * If false then it indicates that a previous entry for current node existed and that is being      * reused      */
specifier|private
name|boolean
name|newEntry
decl_stmt|;
specifier|private
name|ClusterNodeInfo
parameter_list|(
name|int
name|id
parameter_list|,
name|DocumentStore
name|store
parameter_list|,
name|String
name|machineId
parameter_list|,
name|String
name|instanceId
parameter_list|,
name|ClusterNodeState
name|state
parameter_list|,
name|RecoverLockState
name|revRecoveryLock
parameter_list|,
name|Long
name|leaseEnd
parameter_list|,
name|boolean
name|newEntry
parameter_list|)
block|{
name|this
operator|.
name|id
operator|=
name|id
expr_stmt|;
name|this
operator|.
name|startTime
operator|=
name|getCurrentTime
argument_list|()
expr_stmt|;
if|if
condition|(
name|leaseEnd
operator|==
literal|null
condition|)
block|{
name|this
operator|.
name|leaseEndTime
operator|=
name|startTime
expr_stmt|;
block|}
else|else
block|{
name|this
operator|.
name|leaseEndTime
operator|=
name|leaseEnd
expr_stmt|;
block|}
name|this
operator|.
name|renewed
operator|=
literal|false
expr_stmt|;
comment|// will be updated once we renew it the first time
name|this
operator|.
name|store
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|machineId
operator|=
name|machineId
expr_stmt|;
name|this
operator|.
name|instanceId
operator|=
name|instanceId
expr_stmt|;
name|this
operator|.
name|state
operator|=
name|state
expr_stmt|;
name|this
operator|.
name|revRecoveryLock
operator|=
name|revRecoveryLock
expr_stmt|;
name|this
operator|.
name|newEntry
operator|=
name|newEntry
expr_stmt|;
name|this
operator|.
name|leaseCheckDisabled
operator|=
name|Boolean
operator|.
name|valueOf
argument_list|(
name|System
operator|.
name|getProperty
argument_list|(
literal|"oak.documentMK.disableLeaseCheck"
argument_list|,
literal|"false"
argument_list|)
argument_list|)
expr_stmt|;
block|}
specifier|public
name|int
name|getId
parameter_list|()
block|{
return|return
name|id
return|;
block|}
comment|/**      * Create a cluster node info instance for the store, with the      *       * @param store the document store (for the lease)      * @return the cluster node info      */
specifier|public
specifier|static
name|ClusterNodeInfo
name|getInstance
parameter_list|(
name|DocumentStore
name|store
parameter_list|)
block|{
return|return
name|getInstance
argument_list|(
name|store
argument_list|,
name|MACHINE_ID
argument_list|,
name|WORKING_DIR
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Create a cluster node info instance for the store.      *       * @param store the document store (for the lease)      * @param machineId the machine id (null for MAC address)      * @param instanceId the instance id (null for current working directory)      * @return the cluster node info      */
specifier|public
specifier|static
name|ClusterNodeInfo
name|getInstance
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|String
name|machineId
parameter_list|,
name|String
name|instanceId
parameter_list|)
block|{
return|return
name|getInstance
argument_list|(
name|store
argument_list|,
name|machineId
argument_list|,
name|instanceId
argument_list|,
literal|true
argument_list|)
return|;
block|}
comment|/**      * Create a cluster node info instance for the store.      *      * @param store the document store (for the lease)      * @param machineId the machine id (null for MAC address)      * @param instanceId the instance id (null for current working directory)      * @param updateLease whether to update the lease      * @return the cluster node info      */
specifier|public
specifier|static
name|ClusterNodeInfo
name|getInstance
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|String
name|machineId
parameter_list|,
name|String
name|instanceId
parameter_list|,
name|boolean
name|updateLease
parameter_list|)
block|{
if|if
condition|(
name|machineId
operator|==
literal|null
condition|)
block|{
name|machineId
operator|=
name|MACHINE_ID
expr_stmt|;
block|}
if|if
condition|(
name|instanceId
operator|==
literal|null
condition|)
block|{
name|instanceId
operator|=
name|WORKING_DIR
expr_stmt|;
block|}
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
literal|10
condition|;
name|i
operator|++
control|)
block|{
name|ClusterNodeInfo
name|clusterNode
init|=
name|createInstance
argument_list|(
name|store
argument_list|,
name|machineId
argument_list|,
name|instanceId
argument_list|)
decl_stmt|;
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
literal|""
operator|+
name|clusterNode
operator|.
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|update
operator|.
name|set
argument_list|(
name|ID
argument_list|,
name|String
operator|.
name|valueOf
argument_list|(
name|clusterNode
operator|.
name|id
argument_list|)
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|MACHINE_ID_KEY
argument_list|,
name|clusterNode
operator|.
name|machineId
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|INSTANCE_ID_KEY
argument_list|,
name|clusterNode
operator|.
name|instanceId
argument_list|)
expr_stmt|;
if|if
condition|(
name|updateLease
condition|)
block|{
name|update
operator|.
name|set
argument_list|(
name|LEASE_END_KEY
argument_list|,
name|getCurrentTime
argument_list|()
operator|+
name|clusterNode
operator|.
name|leaseTime
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|update
operator|.
name|set
argument_list|(
name|LEASE_END_KEY
argument_list|,
name|clusterNode
operator|.
name|leaseEndTime
argument_list|)
expr_stmt|;
block|}
name|update
operator|.
name|set
argument_list|(
name|INFO_KEY
argument_list|,
name|clusterNode
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|STATE
argument_list|,
name|clusterNode
operator|.
name|state
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|REV_RECOVERY_LOCK
argument_list|,
name|clusterNode
operator|.
name|revRecoveryLock
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|OAK_VERSION_KEY
argument_list|,
name|OakVersion
operator|.
name|getVersion
argument_list|()
argument_list|)
expr_stmt|;
specifier|final
name|boolean
name|success
decl_stmt|;
if|if
condition|(
name|clusterNode
operator|.
name|newEntry
condition|)
block|{
comment|//For new entry do a create. This ensures that if two nodes create
comment|//entry with same id then only one would succeed
name|success
operator|=
name|store
operator|.
name|create
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|Collections
operator|.
name|singletonList
argument_list|(
name|update
argument_list|)
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// No expiration of earlier cluster info, so update
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
expr_stmt|;
name|success
operator|=
literal|true
expr_stmt|;
block|}
if|if
condition|(
name|success
condition|)
block|{
return|return
name|clusterNode
return|;
block|}
block|}
throw|throw
operator|new
name|DocumentStoreException
argument_list|(
literal|"Could not get cluster node info"
argument_list|)
throw|;
block|}
specifier|private
specifier|static
name|ClusterNodeInfo
name|createInstance
parameter_list|(
name|DocumentStore
name|store
parameter_list|,
name|String
name|machineId
parameter_list|,
name|String
name|instanceId
parameter_list|)
block|{
name|long
name|now
init|=
name|getCurrentTime
argument_list|()
decl_stmt|;
name|List
argument_list|<
name|ClusterNodeInfoDocument
argument_list|>
name|list
init|=
name|ClusterNodeInfoDocument
operator|.
name|all
argument_list|(
name|store
argument_list|)
decl_stmt|;
name|int
name|clusterNodeId
init|=
literal|0
decl_stmt|;
name|int
name|maxId
init|=
literal|0
decl_stmt|;
name|ClusterNodeState
name|state
init|=
name|ClusterNodeState
operator|.
name|NONE
decl_stmt|;
name|Long
name|prevLeaseEnd
init|=
literal|null
decl_stmt|;
name|boolean
name|newEntry
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Document
name|doc
range|:
name|list
control|)
block|{
name|String
name|key
init|=
name|doc
operator|.
name|getId
argument_list|()
decl_stmt|;
name|int
name|id
decl_stmt|;
try|try
block|{
name|id
operator|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
comment|// not an integer - ignore
continue|continue;
block|}
name|maxId
operator|=
name|Math
operator|.
name|max
argument_list|(
name|maxId
argument_list|,
name|id
argument_list|)
expr_stmt|;
name|Long
name|leaseEnd
init|=
operator|(
name|Long
operator|)
name|doc
operator|.
name|get
argument_list|(
name|LEASE_END_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|leaseEnd
operator|!=
literal|null
operator|&&
name|leaseEnd
operator|>
name|now
condition|)
block|{
continue|continue;
block|}
name|String
name|mId
init|=
literal|""
operator|+
name|doc
operator|.
name|get
argument_list|(
name|MACHINE_ID_KEY
argument_list|)
decl_stmt|;
name|String
name|iId
init|=
literal|""
operator|+
name|doc
operator|.
name|get
argument_list|(
name|INSTANCE_ID_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|machineId
operator|.
name|startsWith
argument_list|(
name|RANDOM_PREFIX
argument_list|)
condition|)
block|{
comment|// remove expired entries with random keys
name|store
operator|.
name|remove
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|key
argument_list|)
expr_stmt|;
continue|continue;
block|}
if|if
condition|(
operator|!
name|mId
operator|.
name|equals
argument_list|(
name|machineId
argument_list|)
operator|||
operator|!
name|iId
operator|.
name|equals
argument_list|(
name|instanceId
argument_list|)
condition|)
block|{
comment|// a different machine or instance
continue|continue;
block|}
comment|//and cluster node which matches current machine identity but
comment|//not being used
if|if
condition|(
name|clusterNodeId
operator|==
literal|0
operator|||
name|id
operator|<
name|clusterNodeId
condition|)
block|{
comment|// if there are multiple, use the smallest value
name|clusterNodeId
operator|=
name|id
expr_stmt|;
name|state
operator|=
name|ClusterNodeState
operator|.
name|fromString
argument_list|(
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
name|STATE
argument_list|)
argument_list|)
expr_stmt|;
name|prevLeaseEnd
operator|=
name|leaseEnd
expr_stmt|;
block|}
block|}
comment|//No existing entry with matching signature found so
comment|//create a new entry
if|if
condition|(
name|clusterNodeId
operator|==
literal|0
condition|)
block|{
name|clusterNodeId
operator|=
name|maxId
operator|+
literal|1
expr_stmt|;
name|newEntry
operator|=
literal|true
expr_stmt|;
block|}
comment|// Do not expire entries and stick on the earlier state, and leaseEnd so,
comment|// that _lastRev recovery if needed is done.
return|return
operator|new
name|ClusterNodeInfo
argument_list|(
name|clusterNodeId
argument_list|,
name|store
argument_list|,
name|machineId
argument_list|,
name|instanceId
argument_list|,
name|state
argument_list|,
name|RecoverLockState
operator|.
name|NONE
argument_list|,
name|prevLeaseEnd
argument_list|,
name|newEntry
argument_list|)
return|;
block|}
specifier|public
name|void
name|performLeaseCheck
parameter_list|()
block|{
if|if
condition|(
name|leaseCheckDisabled
operator|||
operator|!
name|renewed
condition|)
block|{
comment|// if leaseCheckDisabled is set we never do the check, so return fast
comment|// the 'renewed' flag indicates if this instance *ever* renewed the lease after startup
comment|// until that is not set, we cannot do the lease check (otherwise startup wouldn't work)
return|return;
block|}
specifier|final
name|long
name|now
init|=
name|getCurrentTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|<
name|leaseEndTime
condition|)
block|{
comment|// then all is good
return|return;
block|}
comment|// OAK-2739 : when the lease is not current, we must stop
comment|// the instance immediately to avoid any cluster inconsistency
specifier|final
name|String
name|errorMsg
init|=
literal|"performLeaseCheck: this instance failed to update the lease in time "
operator|+
literal|"(leaseEndTime: "
operator|+
name|leaseEndTime
operator|+
literal|", now: "
operator|+
name|now
operator|+
literal|", leaseTime: "
operator|+
name|leaseTime
operator|+
literal|") "
operator|+
literal|"and is thus no longer eligible for taking part in the cluster. Shutting down NOW!"
decl_stmt|;
name|LOG
operator|.
name|error
argument_list|(
name|errorMsg
argument_list|)
expr_stmt|;
comment|// now here comes the thing: we should a) call System.exit in a separate thread
comment|// to avoid any deadlock when calling from eg within the shutdown hook
comment|// AND b) we should not call system.exit hundred times.
comment|// so for b) we use 'systemExitTriggered' to avoid calling it over and over
comment|// BUT it doesn't have to be 100% ensured that system.exit is called only once.
comment|// it is fine if it gets called once, twice - but just not hundred times.
comment|// which is a long way of saying: volatile is fine here - and the 'if' too
if|if
condition|(
operator|!
name|systemExitTriggered
condition|)
block|{
name|systemExitTriggered
operator|=
literal|true
expr_stmt|;
specifier|final
name|Runnable
name|r
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
name|System
operator|.
name|exit
argument_list|(
operator|-
literal|1
argument_list|)
expr_stmt|;
block|}
block|}
decl_stmt|;
specifier|final
name|Thread
name|th
init|=
operator|new
name|Thread
argument_list|(
name|r
argument_list|,
literal|"FailedLeaseCheckShutdown-Thread"
argument_list|)
decl_stmt|;
name|th
operator|.
name|setDaemon
argument_list|(
literal|true
argument_list|)
expr_stmt|;
name|th
operator|.
name|start
argument_list|()
expr_stmt|;
block|}
throw|throw
operator|new
name|AssertionError
argument_list|(
name|errorMsg
argument_list|)
throw|;
block|}
comment|/**      * Renew the cluster id lease. This method needs to be called once in a while,      * to ensure the same cluster id is not re-used by a different instance.      * The lease is only renewed when half of the lease time passed. That is,      * with a lease time of 60 seconds, the lease is renewed every 30 seconds.      *      * @return {@code true} if the lease was renewed; {@code false} otherwise.      */
specifier|public
name|boolean
name|renewLease
parameter_list|()
block|{
name|long
name|now
init|=
name|getCurrentTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|+
name|leaseTime
operator|/
literal|2
operator|<
name|leaseEndTime
condition|)
block|{
return|return
literal|false
return|;
block|}
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
literal|""
operator|+
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|leaseEndTime
operator|=
name|now
operator|+
name|leaseTime
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|LEASE_END_KEY
argument_list|,
name|leaseEndTime
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|STATE
argument_list|,
name|ClusterNodeState
operator|.
name|ACTIVE
operator|.
name|name
argument_list|()
argument_list|)
expr_stmt|;
name|ClusterNodeInfoDocument
name|doc
init|=
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
decl_stmt|;
name|String
name|mode
init|=
operator|(
name|String
operator|)
name|doc
operator|.
name|get
argument_list|(
name|READ_WRITE_MODE_KEY
argument_list|)
decl_stmt|;
if|if
condition|(
name|mode
operator|!=
literal|null
operator|&&
operator|!
name|mode
operator|.
name|equals
argument_list|(
name|readWriteMode
argument_list|)
condition|)
block|{
name|readWriteMode
operator|=
name|mode
expr_stmt|;
name|store
operator|.
name|setReadWriteMode
argument_list|(
name|mode
argument_list|)
expr_stmt|;
block|}
name|renewed
operator|=
literal|true
expr_stmt|;
return|return
literal|true
return|;
block|}
specifier|public
name|void
name|setLeaseTime
parameter_list|(
name|long
name|leaseTime
parameter_list|)
block|{
name|this
operator|.
name|leaseTime
operator|=
name|leaseTime
expr_stmt|;
block|}
specifier|public
name|long
name|getLeaseTime
parameter_list|()
block|{
return|return
name|leaseTime
return|;
block|}
specifier|public
name|void
name|dispose
parameter_list|()
block|{
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
literal|""
operator|+
name|id
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|update
operator|.
name|set
argument_list|(
name|LEASE_END_KEY
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|STATE
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|update
operator|.
name|set
argument_list|(
name|REV_RECOVERY_LOCK
argument_list|,
literal|null
argument_list|)
expr_stmt|;
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
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
literal|"id: "
operator|+
name|id
operator|+
literal|",\n"
operator|+
literal|"startTime: "
operator|+
name|startTime
operator|+
literal|",\n"
operator|+
literal|"machineId: "
operator|+
name|machineId
operator|+
literal|",\n"
operator|+
literal|"instanceId: "
operator|+
name|instanceId
operator|+
literal|",\n"
operator|+
literal|"pid: "
operator|+
name|PROCESS_ID
operator|+
literal|",\n"
operator|+
literal|"uuid: "
operator|+
name|uuid
operator|+
literal|",\n"
operator|+
literal|"readWriteMode: "
operator|+
name|readWriteMode
operator|+
literal|",\n"
operator|+
literal|"state: "
operator|+
name|state
operator|+
literal|",\n"
operator|+
literal|"revLock: "
operator|+
name|revRecoveryLock
return|;
block|}
comment|/**      * Specify a custom clock to be used for determining current time.      *      *<b>Only Used For Testing</b>      */
specifier|static
name|void
name|setClock
parameter_list|(
name|Clock
name|c
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|c
argument_list|)
expr_stmt|;
name|clock
operator|=
name|c
expr_stmt|;
block|}
comment|/**      * Resets the clock to the default      */
specifier|static
name|void
name|resetClockToDefault
parameter_list|()
block|{
name|clock
operator|=
name|Clock
operator|.
name|SIMPLE
expr_stmt|;
block|}
specifier|private
specifier|static
name|long
name|getProcessId
parameter_list|()
block|{
try|try
block|{
name|String
name|name
init|=
name|ManagementFactory
operator|.
name|getRuntimeMXBean
argument_list|()
operator|.
name|getName
argument_list|()
decl_stmt|;
return|return
name|Long
operator|.
name|parseLong
argument_list|(
name|name
operator|.
name|substring
argument_list|(
literal|0
argument_list|,
name|name
operator|.
name|indexOf
argument_list|(
literal|'@'
argument_list|)
argument_list|)
argument_list|)
return|;
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Could not get process id"
argument_list|,
name|e
argument_list|)
expr_stmt|;
return|return
literal|0
return|;
block|}
block|}
comment|/**      * Calculate the unique machine id. This is the lowest MAC address if      * available. As an alternative, a randomly generated UUID is used.      *       * @return the unique id      */
specifier|private
specifier|static
name|String
name|getMachineId
parameter_list|()
block|{
name|Exception
name|exception
init|=
literal|null
decl_stmt|;
try|try
block|{
name|ArrayList
argument_list|<
name|String
argument_list|>
name|list
init|=
operator|new
name|ArrayList
argument_list|<
name|String
argument_list|>
argument_list|()
decl_stmt|;
name|Enumeration
argument_list|<
name|NetworkInterface
argument_list|>
name|e
init|=
name|NetworkInterface
operator|.
name|getNetworkInterfaces
argument_list|()
decl_stmt|;
while|while
condition|(
name|e
operator|.
name|hasMoreElements
argument_list|()
condition|)
block|{
name|NetworkInterface
name|ni
init|=
name|e
operator|.
name|nextElement
argument_list|()
decl_stmt|;
try|try
block|{
name|byte
index|[]
name|mac
init|=
name|ni
operator|.
name|getHardwareAddress
argument_list|()
decl_stmt|;
if|if
condition|(
name|mac
operator|!=
literal|null
condition|)
block|{
name|String
name|x
init|=
name|StringUtils
operator|.
name|convertBytesToHex
argument_list|(
name|mac
argument_list|)
decl_stmt|;
name|list
operator|.
name|add
argument_list|(
name|x
argument_list|)
expr_stmt|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e2
parameter_list|)
block|{
name|exception
operator|=
name|e2
expr_stmt|;
block|}
block|}
if|if
condition|(
name|list
operator|.
name|size
argument_list|()
operator|>
literal|0
condition|)
block|{
comment|// use the lowest value, such that if the order changes,
comment|// the same one is used
name|Collections
operator|.
name|sort
argument_list|(
name|list
argument_list|)
expr_stmt|;
return|return
literal|"mac:"
operator|+
name|list
operator|.
name|get
argument_list|(
literal|0
argument_list|)
return|;
block|}
block|}
catch|catch
parameter_list|(
name|Exception
name|e
parameter_list|)
block|{
name|exception
operator|=
name|e
expr_stmt|;
block|}
if|if
condition|(
name|exception
operator|!=
literal|null
condition|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Error getting the machine id; using a UUID"
argument_list|,
name|exception
argument_list|)
expr_stmt|;
block|}
return|return
name|RANDOM_PREFIX
operator|+
name|UUID
operator|.
name|randomUUID
argument_list|()
operator|.
name|toString
argument_list|()
return|;
block|}
specifier|private
specifier|static
name|long
name|getCurrentTime
parameter_list|()
block|{
return|return
name|clock
operator|.
name|getTime
argument_list|()
return|;
block|}
block|}
end_class

end_unit

