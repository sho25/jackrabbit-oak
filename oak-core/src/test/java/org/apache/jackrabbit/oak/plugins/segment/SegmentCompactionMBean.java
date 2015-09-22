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
name|segment
package|;
end_package

begin_comment
comment|/**  * MBean for monitoring and interacting with the {@link SegmentCompactionIT}  * longevity test.  */
end_comment

begin_interface
specifier|public
interface|interface
name|SegmentCompactionMBean
block|{
comment|/**      * Stop the test.      */
name|void
name|stop
parameter_list|()
function_decl|;
comment|/**      * Set the core pool size of the scheduler used to execute concurrent      * operations.      * @param corePoolSize      */
name|void
name|setCorePoolSize
parameter_list|(
name|int
name|corePoolSize
parameter_list|)
function_decl|;
comment|/**      * @return the core pool size of the scheduler used to execute concurrent      * operations.      */
name|int
name|getCorePoolSize
parameter_list|()
function_decl|;
comment|/**      * Set the compaction interval      * @param minutes  number of minutes to wait between compaction cycles.      */
name|void
name|setCompactionInterval
parameter_list|(
name|int
name|minutes
parameter_list|)
function_decl|;
comment|/**      * @return  the compaction interval in minutes.      */
name|int
name|getCompactionInterval
parameter_list|()
function_decl|;
comment|/**      * @return  Time stamp from when compaction last ran.      */
name|String
name|getLastCompaction
parameter_list|()
function_decl|;
comment|/**      * Determine whether to compaction should run exclusively wrt. concurrent writers.      * @param value  run compaction exclusively iff {@code true}      */
name|void
name|setUseCompactionLock
parameter_list|(
name|boolean
name|value
parameter_list|)
function_decl|;
comment|/**      * @return  Compaction runs exclusively wrt. concurrent writers iff {@code true}      */
name|boolean
name|getUseCompactionLock
parameter_list|()
function_decl|;
comment|/**      * Time to wait for the commit lock for committing the compacted head.      * @param seconds  number of seconds to wait      * @see SegmentNodeStore#locked(java.util.concurrent.Callable, long, java.util.concurrent.TimeUnit)      */
name|void
name|setLockWaitTime
parameter_list|(
name|int
name|seconds
parameter_list|)
function_decl|;
comment|/**      * Time to wait for the commit lock for committing the compacted head.      * @return  number of seconds      * @see SegmentNodeStore#locked(java.util.concurrent.Callable, long, java.util.concurrent.TimeUnit)      */
name|int
name|getLockWaitTime
parameter_list|()
function_decl|;
comment|/**      * Set the maximal number of concurrent readers      * @param count      */
name|void
name|setMaxReaders
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * @return  maximal number of concurrent readers      */
name|int
name|getMaxReaders
parameter_list|()
function_decl|;
comment|/**      * Set the maximal number of concurrent writers      * @param count      */
name|void
name|setMaxWriters
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * @return  maximal number of concurrent writers      */
name|int
name|getMaxWriters
parameter_list|()
function_decl|;
comment|/**      * Set the maximal size of the store      * @param size  size in bytes      */
name|void
name|setMaxStoreSize
parameter_list|(
name|long
name|size
parameter_list|)
function_decl|;
comment|/**      * @return  maximal size of the store in bytes      */
name|long
name|getMaxStoreSize
parameter_list|()
function_decl|;
comment|/**      * Set the maximal size of string properties      * @param size  size in bytes      */
name|void
name|setMaxStringSize
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
comment|/**      * @return  maximal size of string properties in bytes      */
name|int
name|getMaxStringSize
parameter_list|()
function_decl|;
comment|/**      * Set the maximal size of binary properties      * @param size  size in bytes      */
name|void
name|setMaxBlobSize
parameter_list|(
name|int
name|size
parameter_list|)
function_decl|;
comment|/**      * @return  maximal size of binary properties in bytes      */
name|int
name|getMaxBlobSize
parameter_list|()
function_decl|;
comment|/**      * Set the maximal number of held references      * @param count  maximal number of references      */
name|void
name|setMaxReferences
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * @return  maximal number of held references      */
name|int
name|getMaxReferences
parameter_list|()
function_decl|;
comment|/**      * Maximal number of write operations per scheduled writer      * @param count  maximal number of operations      */
name|void
name|setMaxWriteOps
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * @return  maximal number of operations      */
name|int
name|getMaxWriteOps
parameter_list|()
function_decl|;
comment|/**      * Set the maximal number of child node of a node      * @param count  maximal number of child nodes      */
name|void
name|setMaxNodeCount
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * @return  Maximal number of child nodes of a node      */
name|int
name|getMaxNodeCount
parameter_list|()
function_decl|;
comment|/**      * Set the maximal number of properties of a node      * @param count  maximal number of properties      */
name|void
name|setMaxPropertyCount
parameter_list|(
name|int
name|count
parameter_list|)
function_decl|;
comment|/**      * @return  Maximal number of properties of a node      */
name|int
name|getMaxPropertyCount
parameter_list|()
function_decl|;
comment|/**      * Set the ration of remove node operations wrt. all other operations.      * @param ratio  ratio of node remove operations      */
name|void
name|setNodeRemoveRatio
parameter_list|(
name|int
name|ratio
parameter_list|)
function_decl|;
comment|/**      * @return  Ratio of node remove operations      */
name|int
name|getNodeRemoveRatio
parameter_list|()
function_decl|;
comment|/**      * Set the ration of remove property operations wrt. all other operations.      * @param ratio  ratio of property remove operations      */
name|void
name|setPropertyRemoveRatio
parameter_list|(
name|int
name|ratio
parameter_list|)
function_decl|;
comment|/**      * @return  Ratio of property remove operations      */
name|int
name|getPropertyRemoveRatio
parameter_list|()
function_decl|;
comment|/**      * Set the ration of add node operations wrt. all other operations.      * @param ratio  ratio of node add operations      */
name|void
name|setNodeAddRatio
parameter_list|(
name|int
name|ratio
parameter_list|)
function_decl|;
comment|/**      * @return  Ratio of node add operations      */
name|int
name|getNodeAddRatio
parameter_list|()
function_decl|;
comment|/**      * Set the ration of add string property operations wrt. all other operations.      * @param ratio  ratio of string property add operations      */
name|void
name|setAddStringRatio
parameter_list|(
name|int
name|ratio
parameter_list|)
function_decl|;
comment|/**      * @return  Ratio of string property add operations      */
name|int
name|getAddStringRatio
parameter_list|()
function_decl|;
comment|/**      * Set the ration of add binary property operations wrt. all other operations.      * @param ratio  ratio of binary property add operations      */
name|void
name|setAddBinaryRatio
parameter_list|(
name|int
name|ratio
parameter_list|)
function_decl|;
comment|/**      * @return  Ratio of binary property add operations      */
name|int
name|getAddBinaryRatio
parameter_list|()
function_decl|;
comment|/**      * Add a reference to the current root or release a held reference.      * @param set  add a reference if {@code true}, otherwise release any held reference      */
name|void
name|setRootReference
parameter_list|(
name|boolean
name|set
parameter_list|)
function_decl|;
comment|/**      * @return  {@code true} if currently a root reference is being held. {@code false} otherwise.      */
name|boolean
name|getRootReference
parameter_list|()
function_decl|;
comment|/**      * Determine whether the compaction map is persisted or in memory      * @return  {@code true} if persisted, {@code false} otherwise      */
name|boolean
name|getPersistCompactionMap
parameter_list|()
function_decl|;
comment|/**      * @return  actual number of concurrent readers      */
name|int
name|getReaderCount
parameter_list|()
function_decl|;
comment|/**      * @return  actual number of concurrent writers      */
name|int
name|getWriterCount
parameter_list|()
function_decl|;
comment|/**      * @return actual number of held references (not including any root reference)      */
name|int
name|getReferenceCount
parameter_list|()
function_decl|;
comment|/**      * @return  current size of the {@link org.apache.jackrabbit.oak.plugins.segment.file.FileStore}      */
name|long
name|getFileStoreSize
parameter_list|()
function_decl|;
comment|/**      * @return  current weight of the compaction map      */
name|long
name|getCompactionMapWeight
parameter_list|()
function_decl|;
comment|/**      * @return  number of record referenced by the keys in this map.      */
name|long
name|getRecordCount
parameter_list|()
function_decl|;
comment|/**      * @return  number of segments referenced by the keys in this map.      */
name|long
name|getSegmentCount
parameter_list|()
function_decl|;
comment|/**      * @return  current depth of the compaction map      */
name|int
name|getCompactionMapDepth
parameter_list|()
function_decl|;
comment|/**      * @return  last error      */
name|String
name|getLastError
parameter_list|()
function_decl|;
comment|/**      * @return  Number of commits      */
name|long
name|getCommitCount
parameter_list|()
function_decl|;
block|}
end_interface

end_unit

