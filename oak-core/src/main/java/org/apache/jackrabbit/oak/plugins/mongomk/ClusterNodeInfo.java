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
name|mongomk
package|;
end_package

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
name|Map
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
name|mk
operator|.
name|api
operator|.
name|MicroKernelException
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
name|mk
operator|.
name|util
operator|.
name|StringUtils
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
name|MongoDocumentStore
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
comment|/**      * The cluster node id.      */
specifier|private
specifier|static
specifier|final
name|String
name|ID_KEY
init|=
literal|"_id"
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
specifier|private
specifier|static
specifier|final
name|String
name|LEASE_END_KEY
init|=
literal|"leaseEnd"
decl_stmt|;
comment|/**      * Additional info, such as the process id, for support.      */
specifier|private
specifier|static
specifier|final
name|String
name|INFO_KEY
init|=
literal|"info"
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
comment|/**      * The number of milliseconds for a lease (1 minute by default, and      * initially).      */
specifier|private
name|long
name|leaseTime
init|=
literal|1000
operator|*
literal|60
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
name|long
name|leaseEndTime
decl_stmt|;
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
name|System
operator|.
name|currentTimeMillis
argument_list|()
expr_stmt|;
name|this
operator|.
name|leaseEndTime
operator|=
name|startTime
expr_stmt|;
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
comment|/**      * Create a cluster node info instance for the store, with the       *       * @param store the document store (for the lease)      * @return the cluster node info      */
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
literal|null
argument_list|,
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
name|ID_KEY
argument_list|,
literal|""
operator|+
name|clusterNode
operator|.
name|id
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
name|update
operator|.
name|set
argument_list|(
name|LEASE_END_KEY
argument_list|,
name|System
operator|.
name|currentTimeMillis
argument_list|()
operator|+
name|clusterNode
operator|.
name|leaseTime
argument_list|)
expr_stmt|;
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
name|boolean
name|success
init|=
name|store
operator|.
name|create
argument_list|(
name|DocumentStore
operator|.
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
decl_stmt|;
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
name|MicroKernelException
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
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
comment|// keys between "0" and "a" includes all possible numbers
name|List
argument_list|<
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
argument_list|>
name|list
init|=
name|store
operator|.
name|query
argument_list|(
name|DocumentStore
operator|.
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
literal|"0"
argument_list|,
literal|"a"
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
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
for|for
control|(
name|Map
argument_list|<
name|String
argument_list|,
name|Object
argument_list|>
name|doc
range|:
name|list
control|)
block|{
name|String
name|key
init|=
literal|""
operator|+
name|doc
operator|.
name|get
argument_list|(
name|ID_KEY
argument_list|)
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
name|DocumentStore
operator|.
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
comment|// remove expired matching entries
name|store
operator|.
name|remove
argument_list|(
name|DocumentStore
operator|.
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|key
argument_list|)
expr_stmt|;
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
block|}
block|}
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
block|}
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
argument_list|)
return|;
block|}
comment|/**      * Renew the cluster id lease. This method needs to be called once in a while,      * to ensure the same cluster id is not re-used by a different instance.      *       * @param nextCheckMillis the millisecond offset      */
specifier|public
name|void
name|renewLease
parameter_list|(
name|long
name|nextCheckMillis
parameter_list|)
block|{
name|long
name|now
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|now
operator|+
name|nextCheckMillis
operator|+
name|nextCheckMillis
operator|<
name|leaseEndTime
condition|)
block|{
return|return;
block|}
name|UpdateOp
name|update
init|=
operator|new
name|UpdateOp
argument_list|(
literal|null
argument_list|,
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
name|store
operator|.
name|createOrUpdate
argument_list|(
name|DocumentStore
operator|.
name|Collection
operator|.
name|CLUSTER_NODES
argument_list|,
name|update
argument_list|)
expr_stmt|;
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
literal|null
argument_list|,
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
name|store
operator|.
name|createOrUpdate
argument_list|(
name|DocumentStore
operator|.
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
literal|"\n"
return|;
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
name|LOG
operator|.
name|error
argument_list|(
literal|"Error calculating the machine id"
argument_list|,
name|e
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
block|}
end_class

end_unit

