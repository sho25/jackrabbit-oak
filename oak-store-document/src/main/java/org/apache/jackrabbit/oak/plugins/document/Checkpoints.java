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
name|document
package|;
end_package

begin_import
import|import
name|java
operator|.
name|math
operator|.
name|BigInteger
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
name|LinkedHashMap
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
name|SortedMap
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
name|javax
operator|.
name|annotation
operator|.
name|CheckForNull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nonnull
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|annotation
operator|.
name|Nullable
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
name|json
operator|.
name|JsopBuilder
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
name|json
operator|.
name|JsopReader
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
name|json
operator|.
name|JsopTokenizer
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
name|json
operator|.
name|JsopWriter
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
name|collect
operator|.
name|Maps
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
name|checkNotNull
import|;
end_import

begin_comment
comment|/**  * Checkpoints provide details around which revision are to be kept. These  * are stored in Settings collection.  */
end_comment

begin_class
class|class
name|Checkpoints
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
name|Checkpoints
operator|.
name|class
argument_list|)
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|ID
init|=
literal|"checkpoint"
decl_stmt|;
comment|/**      * Property name to store all checkpoint data. The data is either stored as      * Revision => expiryTime or Revision => JSON with expiryTime and info.      */
specifier|private
specifier|static
specifier|final
name|String
name|PROP_CHECKPOINT
init|=
literal|"data"
decl_stmt|;
comment|/**      * Number of create calls after which old expired checkpoints entries would      * be removed      */
specifier|static
specifier|final
name|int
name|CLEANUP_INTERVAL
init|=
literal|100
decl_stmt|;
specifier|private
specifier|final
name|DocumentNodeStore
name|nodeStore
decl_stmt|;
specifier|private
specifier|final
name|DocumentStore
name|store
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
name|AtomicInteger
name|createCounter
init|=
operator|new
name|AtomicInteger
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Object
name|cleanupLock
init|=
operator|new
name|Object
argument_list|()
decl_stmt|;
name|Checkpoints
parameter_list|(
name|DocumentNodeStore
name|store
parameter_list|)
block|{
name|this
operator|.
name|nodeStore
operator|=
name|store
expr_stmt|;
name|this
operator|.
name|store
operator|=
name|store
operator|.
name|getDocumentStore
argument_list|()
expr_stmt|;
name|createIfNotExist
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Revision
name|create
parameter_list|(
name|long
name|lifetimeInMillis
parameter_list|,
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
parameter_list|)
block|{
comment|// create a unique dummy commit we can use as checkpoint revision
name|Revision
name|r
init|=
name|nodeStore
operator|.
name|commitQueue
operator|.
name|createRevision
argument_list|()
decl_stmt|;
specifier|final
name|RevisionVector
index|[]
name|rv
init|=
operator|new
name|RevisionVector
index|[
literal|1
index|]
decl_stmt|;
name|nodeStore
operator|.
name|commitQueue
operator|.
name|done
argument_list|(
name|r
argument_list|,
operator|new
name|CommitQueue
operator|.
name|Callback
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|void
name|headOfQueue
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|rv
index|[
literal|0
index|]
operator|=
name|nodeStore
operator|.
name|getHeadRevision
argument_list|()
expr_stmt|;
block|}
block|}
argument_list|)
expr_stmt|;
name|createCounter
operator|.
name|getAndIncrement
argument_list|()
expr_stmt|;
name|performCleanupIfRequired
argument_list|()
expr_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|long
name|endTime
init|=
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|nodeStore
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
argument_list|)
operator|.
name|add
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|lifetimeInMillis
argument_list|)
argument_list|)
operator|.
name|min
argument_list|(
name|BigInteger
operator|.
name|valueOf
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|)
argument_list|)
operator|.
name|longValue
argument_list|()
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
name|PROP_CHECKPOINT
argument_list|,
name|r
argument_list|,
operator|new
name|Info
argument_list|(
name|endTime
argument_list|,
name|rv
index|[
literal|0
index|]
argument_list|,
name|info
argument_list|)
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|op
argument_list|)
expr_stmt|;
return|return
name|r
return|;
block|}
specifier|public
name|void
name|release
parameter_list|(
name|String
name|checkpoint
parameter_list|)
block|{
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|op
operator|.
name|removeMapEntry
argument_list|(
name|PROP_CHECKPOINT
argument_list|,
name|Revision
operator|.
name|fromString
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
expr_stmt|;
name|store
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
comment|/**      * Returns the oldest valid checkpoint registered.      *      *<p>It also performs cleanup of expired checkpoint      *      * @return oldest valid checkpoint registered. Might return null if no valid      * checkpoint found      */
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|getOldestRevisionToKeep
parameter_list|()
block|{
comment|//Get uncached doc
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|Info
argument_list|>
name|checkpoints
init|=
name|getCheckpoints
argument_list|()
decl_stmt|;
if|if
condition|(
name|checkpoints
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
name|log
operator|.
name|debug
argument_list|(
literal|"No checkpoint registered so far"
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
specifier|final
name|long
name|currentTime
init|=
name|nodeStore
operator|.
name|getClock
argument_list|()
operator|.
name|getTime
argument_list|()
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|Revision
name|lastAliveRevision
init|=
literal|null
decl_stmt|;
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|Info
argument_list|>
name|e
range|:
name|checkpoints
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|long
name|expiryTime
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getExpiryTime
argument_list|()
decl_stmt|;
if|if
condition|(
name|currentTime
operator|>
name|expiryTime
condition|)
block|{
name|op
operator|.
name|removeMapEntry
argument_list|(
name|PROP_CHECKPOINT
argument_list|,
name|e
operator|.
name|getKey
argument_list|()
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|Revision
name|cpRev
init|=
name|e
operator|.
name|getKey
argument_list|()
decl_stmt|;
name|RevisionVector
name|rv
init|=
name|e
operator|.
name|getValue
argument_list|()
operator|.
name|getCheckpoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|rv
operator|!=
literal|null
condition|)
block|{
name|cpRev
operator|=
name|rv
operator|.
name|getRevision
argument_list|(
name|cpRev
operator|.
name|getClusterId
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|lastAliveRevision
operator|=
name|Utils
operator|.
name|min
argument_list|(
name|lastAliveRevision
argument_list|,
name|cpRev
argument_list|)
expr_stmt|;
block|}
block|}
if|if
condition|(
name|op
operator|.
name|hasChanges
argument_list|()
condition|)
block|{
name|store
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|op
argument_list|)
expr_stmt|;
name|log
operator|.
name|debug
argument_list|(
literal|"Purged {} expired checkpoints"
argument_list|,
name|op
operator|.
name|getChanges
argument_list|()
operator|.
name|size
argument_list|()
argument_list|)
expr_stmt|;
block|}
return|return
name|lastAliveRevision
return|;
block|}
annotation|@
name|SuppressWarnings
argument_list|(
literal|"unchecked"
argument_list|)
annotation|@
name|Nonnull
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|Info
argument_list|>
name|getCheckpoints
parameter_list|()
block|{
name|Document
name|cdoc
init|=
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|ID
argument_list|,
literal|0
argument_list|)
decl_stmt|;
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|data
init|=
literal|null
decl_stmt|;
if|if
condition|(
name|cdoc
operator|!=
literal|null
condition|)
block|{
name|data
operator|=
operator|(
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
operator|)
name|cdoc
operator|.
name|get
argument_list|(
name|PROP_CHECKPOINT
argument_list|)
expr_stmt|;
block|}
name|SortedMap
argument_list|<
name|Revision
argument_list|,
name|Info
argument_list|>
name|checkpoints
init|=
name|Maps
operator|.
name|newTreeMap
argument_list|(
name|StableRevisionComparator
operator|.
name|REVERSE
argument_list|)
decl_stmt|;
if|if
condition|(
name|data
operator|!=
literal|null
condition|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|Revision
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|data
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|checkpoints
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|Info
operator|.
name|fromString
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|checkpoints
return|;
block|}
comment|/**      * Retrieves the head revision for the given {@code checkpoint}.      *      * @param checkpoint the checkpoint reference.      * @return the head revision associated with the checkpoint or {@code null}      *      if there is no such checkpoint.      * @throws IllegalArgumentException if the checkpoint is malformed.      */
annotation|@
name|CheckForNull
name|RevisionVector
name|retrieve
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
name|Revision
name|r
decl_stmt|;
try|try
block|{
name|r
operator|=
name|Revision
operator|.
name|fromString
argument_list|(
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Malformed checkpoint reference: {}"
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
return|return
literal|null
return|;
block|}
name|Info
name|info
init|=
name|getCheckpoints
argument_list|()
operator|.
name|get
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
name|RevisionVector
name|rv
init|=
name|info
operator|.
name|getCheckpoint
argument_list|()
decl_stmt|;
if|if
condition|(
name|rv
operator|==
literal|null
condition|)
block|{
name|rv
operator|=
name|expand
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
return|return
name|rv
return|;
block|}
name|void
name|setInfoProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|checkpoint
parameter_list|,
annotation|@
name|Nonnull
name|String
name|key
parameter_list|,
annotation|@
name|Nullable
name|String
name|value
parameter_list|)
block|{
name|Revision
name|r
init|=
name|Revision
operator|.
name|fromString
argument_list|(
name|checkNotNull
argument_list|(
name|checkpoint
argument_list|)
argument_list|)
decl_stmt|;
name|Info
name|info
init|=
name|getCheckpoints
argument_list|()
operator|.
name|get
argument_list|(
name|r
argument_list|)
decl_stmt|;
if|if
condition|(
name|info
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"No such checkpoint: "
operator|+
name|checkpoint
argument_list|)
throw|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|metadata
init|=
operator|new
name|LinkedHashMap
argument_list|<>
argument_list|(
name|info
operator|.
name|get
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|value
operator|==
literal|null
condition|)
block|{
name|metadata
operator|.
name|remove
argument_list|(
name|key
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|metadata
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
name|Info
name|newInfo
init|=
operator|new
name|Info
argument_list|(
name|info
operator|.
name|getExpiryTime
argument_list|()
argument_list|,
name|info
operator|.
name|getCheckpoint
argument_list|()
argument_list|,
name|metadata
argument_list|)
decl_stmt|;
name|UpdateOp
name|op
init|=
operator|new
name|UpdateOp
argument_list|(
name|Checkpoints
operator|.
name|ID
argument_list|,
literal|false
argument_list|)
decl_stmt|;
name|op
operator|.
name|setMapEntry
argument_list|(
name|PROP_CHECKPOINT
argument_list|,
name|r
argument_list|,
name|newInfo
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
name|store
operator|.
name|findAndUpdate
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|op
argument_list|)
expr_stmt|;
block|}
name|int
name|size
parameter_list|()
block|{
return|return
name|getCheckpoints
argument_list|()
operator|.
name|size
argument_list|()
return|;
block|}
comment|/**      * Triggers collection of expired checkpoints createCounter exceeds certain size      */
specifier|private
name|void
name|performCleanupIfRequired
parameter_list|()
block|{
if|if
condition|(
name|createCounter
operator|.
name|get
argument_list|()
operator|>
name|CLEANUP_INTERVAL
condition|)
block|{
synchronized|synchronized
init|(
name|cleanupLock
init|)
block|{
name|getOldestRevisionToKeep
argument_list|()
expr_stmt|;
name|createCounter
operator|.
name|set
argument_list|(
literal|0
argument_list|)
expr_stmt|;
block|}
block|}
block|}
specifier|private
name|void
name|createIfNotExist
parameter_list|()
block|{
if|if
condition|(
name|store
operator|.
name|find
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|ID
argument_list|)
operator|==
literal|null
condition|)
block|{
name|UpdateOp
name|updateOp
init|=
operator|new
name|UpdateOp
argument_list|(
name|ID
argument_list|,
literal|true
argument_list|)
decl_stmt|;
name|store
operator|.
name|createOrUpdate
argument_list|(
name|Collection
operator|.
name|SETTINGS
argument_list|,
name|updateOp
argument_list|)
expr_stmt|;
block|}
block|}
specifier|private
name|RevisionVector
name|expand
parameter_list|(
name|Revision
name|checkpoint
parameter_list|)
block|{
name|LOG
operator|.
name|warn
argument_list|(
literal|"Expanding {} single revision checkpoint into a "
operator|+
literal|"RevisionVector. Please make sure all cluster nodes run "
operator|+
literal|"with the same Oak version."
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
comment|// best effort conversion
name|Map
argument_list|<
name|Integer
argument_list|,
name|Revision
argument_list|>
name|revs
init|=
name|Maps
operator|.
name|newHashMap
argument_list|()
decl_stmt|;
name|RevisionVector
name|head
init|=
name|nodeStore
operator|.
name|getHeadRevision
argument_list|()
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|head
control|)
block|{
name|int
name|cId
init|=
name|r
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
if|if
condition|(
name|cId
operator|==
name|checkpoint
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
name|revs
operator|.
name|put
argument_list|(
name|cId
argument_list|,
name|checkpoint
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|revs
operator|.
name|put
argument_list|(
name|cId
argument_list|,
operator|new
name|Revision
argument_list|(
name|checkpoint
operator|.
name|getTimestamp
argument_list|()
argument_list|,
literal|0
argument_list|,
name|cId
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
return|return
name|head
operator|.
name|pmin
argument_list|(
operator|new
name|RevisionVector
argument_list|(
name|revs
operator|.
name|values
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
specifier|static
specifier|final
class|class
name|Info
block|{
specifier|private
specifier|static
specifier|final
name|String
name|EXPIRES
init|=
literal|"expires"
decl_stmt|;
specifier|private
specifier|static
specifier|final
name|String
name|REVISION_VECTOR
init|=
literal|"rv"
decl_stmt|;
specifier|private
specifier|final
name|long
name|expiryTime
decl_stmt|;
specifier|private
specifier|final
name|RevisionVector
name|checkpoint
decl_stmt|;
specifier|private
specifier|final
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
decl_stmt|;
specifier|private
name|Info
parameter_list|(
name|long
name|expiryTime
parameter_list|,
annotation|@
name|Nullable
name|RevisionVector
name|checkpoint
parameter_list|,
annotation|@
name|Nonnull
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|info
parameter_list|)
block|{
name|this
operator|.
name|expiryTime
operator|=
name|expiryTime
expr_stmt|;
name|this
operator|.
name|checkpoint
operator|=
name|checkpoint
expr_stmt|;
name|this
operator|.
name|info
operator|=
name|Collections
operator|.
name|unmodifiableMap
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
specifier|static
name|Info
name|fromString
parameter_list|(
name|String
name|info
parameter_list|)
block|{
name|long
name|expiryTime
decl_stmt|;
name|RevisionVector
name|rv
init|=
literal|null
decl_stmt|;
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|map
decl_stmt|;
if|if
condition|(
name|info
operator|.
name|startsWith
argument_list|(
literal|"{"
argument_list|)
condition|)
block|{
name|map
operator|=
name|Maps
operator|.
name|newHashMap
argument_list|()
expr_stmt|;
name|JsopReader
name|reader
init|=
operator|new
name|JsopTokenizer
argument_list|(
name|info
argument_list|)
decl_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|'{'
argument_list|)
expr_stmt|;
name|String
name|key
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
if|if
condition|(
operator|!
name|EXPIRES
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"First entry in the "
operator|+
literal|"checkpoint info must be the expires date: "
operator|+
name|info
argument_list|)
throw|;
block|}
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|expiryTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|reader
operator|.
name|readString
argument_list|()
argument_list|)
expr_stmt|;
while|while
condition|(
name|reader
operator|.
name|matches
argument_list|(
literal|','
argument_list|)
condition|)
block|{
name|key
operator|=
name|reader
operator|.
name|readString
argument_list|()
expr_stmt|;
name|reader
operator|.
name|read
argument_list|(
literal|':'
argument_list|)
expr_stmt|;
name|String
name|value
init|=
name|reader
operator|.
name|readString
argument_list|()
decl_stmt|;
comment|// second entry is potentially checkpoint revision vector
if|if
condition|(
name|rv
operator|==
literal|null
operator|&&
name|map
operator|.
name|isEmpty
argument_list|()
operator|&&
name|REVISION_VECTOR
operator|.
name|equals
argument_list|(
name|key
argument_list|)
condition|)
block|{
comment|// try to read checkpoint
try|try
block|{
name|rv
operator|=
name|RevisionVector
operator|.
name|fromString
argument_list|(
name|value
argument_list|)
expr_stmt|;
block|}
catch|catch
parameter_list|(
name|IllegalArgumentException
name|e
parameter_list|)
block|{
comment|// not a revision vector, read as regular info entry
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
name|map
operator|.
name|put
argument_list|(
name|key
argument_list|,
name|value
argument_list|)
expr_stmt|;
block|}
block|}
name|reader
operator|.
name|read
argument_list|(
literal|'}'
argument_list|)
expr_stmt|;
name|reader
operator|.
name|read
argument_list|(
name|JsopReader
operator|.
name|END
argument_list|)
expr_stmt|;
block|}
else|else
block|{
comment|// old format
name|map
operator|=
name|Collections
operator|.
name|emptyMap
argument_list|()
expr_stmt|;
name|expiryTime
operator|=
name|Long
operator|.
name|parseLong
argument_list|(
name|info
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|Info
argument_list|(
name|expiryTime
argument_list|,
name|rv
argument_list|,
name|map
argument_list|)
return|;
block|}
name|Map
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|get
parameter_list|()
block|{
return|return
name|info
return|;
block|}
name|long
name|getExpiryTime
parameter_list|()
block|{
return|return
name|expiryTime
return|;
block|}
comment|/**          * The revision vector associated with this checkpoint or {@code null}          * if this checkpoint was created with a version of Oak, which did not          * yet support revision vectors.          *          * @return the revision vector checkpoint or {@code null}.          */
annotation|@
name|CheckForNull
name|RevisionVector
name|getCheckpoint
parameter_list|()
block|{
return|return
name|checkpoint
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|JsopWriter
name|writer
init|=
operator|new
name|JsopBuilder
argument_list|()
decl_stmt|;
name|writer
operator|.
name|object
argument_list|()
expr_stmt|;
name|writer
operator|.
name|key
argument_list|(
name|EXPIRES
argument_list|)
operator|.
name|value
argument_list|(
name|Long
operator|.
name|toString
argument_list|(
name|expiryTime
argument_list|)
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkpoint
operator|!=
literal|null
condition|)
block|{
name|writer
operator|.
name|key
argument_list|(
name|REVISION_VECTOR
argument_list|)
operator|.
name|value
argument_list|(
name|checkpoint
operator|.
name|toString
argument_list|()
argument_list|)
expr_stmt|;
block|}
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|String
argument_list|>
name|entry
range|:
name|info
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|writer
operator|.
name|key
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|)
operator|.
name|value
argument_list|(
name|entry
operator|.
name|getValue
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|writer
operator|.
name|endObject
argument_list|()
expr_stmt|;
return|return
name|writer
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit
