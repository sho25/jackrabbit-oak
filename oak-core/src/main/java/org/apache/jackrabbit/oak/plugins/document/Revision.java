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
name|Comparator
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
name|TreeSet
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
name|ConcurrentHashMap
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
name|ConcurrentMap
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
comment|/**  * A revision.  */
end_comment

begin_class
specifier|public
class|class
name|Revision
block|{
specifier|private
specifier|static
specifier|volatile
name|long
name|lastTimestamp
decl_stmt|;
specifier|private
specifier|static
specifier|volatile
name|long
name|lastRevisionTimestamp
decl_stmt|;
specifier|private
specifier|static
specifier|volatile
name|int
name|lastRevisionCount
decl_stmt|;
comment|/**      * The timestamp in milliseconds since 1970 (unlike in seconds as in      * MongoDB). The timestamp is local to the machine that generated the      * revision, such that timestamps of revisions can only be compared if the      * machine id is the same.      */
specifier|private
specifier|final
name|long
name|timestamp
decl_stmt|;
comment|/**      * An incrementing counter, for commits that occur within the same      * millisecond.      */
specifier|private
specifier|final
name|int
name|counter
decl_stmt|;
comment|/**      * The cluster id (the MongoDB machine id).      */
specifier|private
specifier|final
name|int
name|clusterId
decl_stmt|;
comment|/**      * Whether this is a branch revision.      */
specifier|private
specifier|final
name|boolean
name|branch
decl_stmt|;
comment|/** Only set for testing */
specifier|private
specifier|static
name|Clock
name|clock
decl_stmt|;
comment|/**      *<b>      * Only to be used for testing.      * Do Not Use Otherwise      *</b>      *       * @param c - the clock      */
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
name|lastTimestamp
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
name|lastRevisionTimestamp
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
specifier|public
name|Revision
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|int
name|counter
parameter_list|,
name|int
name|clusterId
parameter_list|)
block|{
name|this
argument_list|(
name|timestamp
argument_list|,
name|counter
argument_list|,
name|clusterId
argument_list|,
literal|false
argument_list|)
expr_stmt|;
block|}
specifier|public
name|Revision
parameter_list|(
name|long
name|timestamp
parameter_list|,
name|int
name|counter
parameter_list|,
name|int
name|clusterId
parameter_list|,
name|boolean
name|branch
parameter_list|)
block|{
name|this
operator|.
name|timestamp
operator|=
name|timestamp
expr_stmt|;
name|this
operator|.
name|counter
operator|=
name|counter
expr_stmt|;
name|this
operator|.
name|clusterId
operator|=
name|clusterId
expr_stmt|;
name|this
operator|.
name|branch
operator|=
name|branch
expr_stmt|;
block|}
comment|/**      * Compare the time part of two revisions. If they contain the same time,      * the counter is compared.      *<p>      * This method requires that both revisions are from the same cluster node.      *      * @param other the other revision      * @return -1 if this revision occurred earlier, 1 if later, 0 if equal      * @throws IllegalArgumentException if the cluster ids don't match      */
name|int
name|compareRevisionTime
parameter_list|(
name|Revision
name|other
parameter_list|)
block|{
if|if
condition|(
name|clusterId
operator|!=
name|other
operator|.
name|clusterId
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Trying to compare revisions of different cluster ids: "
operator|+
name|this
operator|+
literal|" and "
operator|+
name|other
argument_list|)
throw|;
block|}
name|int
name|comp
init|=
name|timestamp
operator|<
name|other
operator|.
name|timestamp
condition|?
operator|-
literal|1
else|:
name|timestamp
operator|>
name|other
operator|.
name|timestamp
condition|?
literal|1
else|:
literal|0
decl_stmt|;
if|if
condition|(
name|comp
operator|==
literal|0
condition|)
block|{
name|comp
operator|=
name|counter
operator|<
name|other
operator|.
name|counter
condition|?
operator|-
literal|1
else|:
name|counter
operator|>
name|other
operator|.
name|counter
condition|?
literal|1
else|:
literal|0
expr_stmt|;
block|}
return|return
name|comp
return|;
block|}
comment|/**      * Compare the time part of two revisions. If they contain the same time,      * the counter is compared. If the counter is the same, the cluster ids are      * compared.      *      * @param other the other revision      * @return -1 if this revision occurred earlier, 1 if later, 0 if equal      */
name|int
name|compareRevisionTimeThenClusterId
parameter_list|(
name|Revision
name|other
parameter_list|)
block|{
name|int
name|comp
init|=
name|timestamp
operator|<
name|other
operator|.
name|timestamp
condition|?
operator|-
literal|1
else|:
name|timestamp
operator|>
name|other
operator|.
name|timestamp
condition|?
literal|1
else|:
literal|0
decl_stmt|;
if|if
condition|(
name|comp
operator|==
literal|0
condition|)
block|{
name|comp
operator|=
name|counter
operator|<
name|other
operator|.
name|counter
condition|?
operator|-
literal|1
else|:
name|counter
operator|>
name|other
operator|.
name|counter
condition|?
literal|1
else|:
literal|0
expr_stmt|;
block|}
if|if
condition|(
name|comp
operator|==
literal|0
condition|)
block|{
name|comp
operator|=
name|compareClusterId
argument_list|(
name|other
argument_list|)
expr_stmt|;
block|}
return|return
name|comp
return|;
block|}
comment|/**      * Compare the cluster node ids of both revisions.      *      * @param other the other revision      * @return -1 if this revision occurred earlier, 1 if later, 0 if equal      */
name|int
name|compareClusterId
parameter_list|(
name|Revision
name|other
parameter_list|)
block|{
return|return
name|clusterId
operator|<
name|other
operator|.
name|clusterId
condition|?
operator|-
literal|1
else|:
name|clusterId
operator|>
name|other
operator|.
name|clusterId
condition|?
literal|1
else|:
literal|0
return|;
block|}
comment|/**      * Create a simple revision id. The format is similar to MongoDB ObjectId.      *      * @param clusterId the unique machineId + processId      * @return the unique revision id      */
specifier|static
name|Revision
name|newRevision
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|long
name|timestamp
init|=
name|getCurrentTimestamp
argument_list|()
decl_stmt|;
name|int
name|c
decl_stmt|;
synchronized|synchronized
init|(
name|Revision
operator|.
name|class
init|)
block|{
comment|// need to check again, because threads
comment|// could arrive inside the synchronized block
comment|// out of order
if|if
condition|(
name|timestamp
operator|<
name|lastRevisionTimestamp
condition|)
block|{
name|timestamp
operator|=
name|lastRevisionTimestamp
expr_stmt|;
block|}
if|if
condition|(
name|timestamp
operator|==
name|lastRevisionTimestamp
condition|)
block|{
name|c
operator|=
operator|++
name|lastRevisionCount
expr_stmt|;
block|}
else|else
block|{
name|lastRevisionTimestamp
operator|=
name|timestamp
expr_stmt|;
name|lastRevisionCount
operator|=
name|c
operator|=
literal|0
expr_stmt|;
block|}
block|}
return|return
operator|new
name|Revision
argument_list|(
name|timestamp
argument_list|,
name|c
argument_list|,
name|clusterId
argument_list|)
return|;
block|}
comment|/**      * Get the timestamp value of the current date and time. Within the same      * process, the returned value is never smaller than a previously returned      * value, even if the system time was changed.      *      * @return the timestamp      */
specifier|public
specifier|static
name|long
name|getCurrentTimestamp
parameter_list|()
block|{
name|long
name|timestamp
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
if|if
condition|(
name|clock
operator|!=
literal|null
condition|)
block|{
name|timestamp
operator|=
name|clock
operator|.
name|getTime
argument_list|()
expr_stmt|;
block|}
if|if
condition|(
name|timestamp
operator|<
name|lastTimestamp
condition|)
block|{
comment|// protect against decreases in the system time,
comment|// time machines, and other fluctuations in the time continuum
name|timestamp
operator|=
name|lastTimestamp
expr_stmt|;
block|}
elseif|else
if|if
condition|(
name|timestamp
operator|>
name|lastTimestamp
condition|)
block|{
name|lastTimestamp
operator|=
name|timestamp
expr_stmt|;
block|}
return|return
name|timestamp
return|;
block|}
comment|/**      * Get the timestamp difference between two revisions (r1 - r2) in      * milliseconds.      *      * @param r1 the first revision      * @param r2 the second revision      * @return the difference in milliseconds      */
specifier|public
specifier|static
name|long
name|getTimestampDifference
parameter_list|(
name|Revision
name|r1
parameter_list|,
name|Revision
name|r2
parameter_list|)
block|{
return|return
name|r1
operator|.
name|getTimestamp
argument_list|()
operator|-
name|r2
operator|.
name|getTimestamp
argument_list|()
return|;
block|}
specifier|public
specifier|static
name|Revision
name|fromString
parameter_list|(
name|String
name|rev
parameter_list|)
block|{
name|boolean
name|isBranch
init|=
literal|false
decl_stmt|;
if|if
condition|(
name|rev
operator|.
name|startsWith
argument_list|(
literal|"b"
argument_list|)
condition|)
block|{
name|isBranch
operator|=
literal|true
expr_stmt|;
name|rev
operator|=
name|rev
operator|.
name|substring
argument_list|(
literal|1
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|rev
operator|.
name|startsWith
argument_list|(
literal|"r"
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|rev
argument_list|)
throw|;
block|}
name|int
name|idxCount
init|=
name|rev
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxCount
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|rev
argument_list|)
throw|;
block|}
name|int
name|idxClusterId
init|=
name|rev
operator|.
name|indexOf
argument_list|(
literal|'-'
argument_list|,
name|idxCount
operator|+
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|idxClusterId
operator|<
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
name|rev
argument_list|)
throw|;
block|}
name|String
name|t
init|=
name|rev
operator|.
name|substring
argument_list|(
literal|1
argument_list|,
name|idxCount
argument_list|)
decl_stmt|;
name|long
name|timestamp
init|=
name|Long
operator|.
name|parseLong
argument_list|(
name|t
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|t
operator|=
name|rev
operator|.
name|substring
argument_list|(
name|idxCount
operator|+
literal|1
argument_list|,
name|idxClusterId
argument_list|)
expr_stmt|;
name|int
name|c
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
argument_list|,
literal|16
argument_list|)
decl_stmt|;
name|t
operator|=
name|rev
operator|.
name|substring
argument_list|(
name|idxClusterId
operator|+
literal|1
argument_list|)
expr_stmt|;
name|int
name|clusterId
init|=
name|Integer
operator|.
name|parseInt
argument_list|(
name|t
argument_list|,
literal|16
argument_list|)
decl_stmt|;
return|return
operator|new
name|Revision
argument_list|(
name|timestamp
argument_list|,
name|c
argument_list|,
name|clusterId
argument_list|,
name|isBranch
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|toStringBuilder
argument_list|(
operator|new
name|StringBuilder
argument_list|()
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Appends the string representation of this revision to the given      * StringBuilder.      *      * @param sb a StringBuilder.      * @return the StringBuilder instance passed to this method.      */
specifier|public
name|StringBuilder
name|toStringBuilder
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
if|if
condition|(
name|branch
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|'b'
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'r'
argument_list|)
expr_stmt|;
name|sb
operator|.
name|append
argument_list|(
name|Long
operator|.
name|toHexString
argument_list|(
name|timestamp
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|<
literal|10
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|counter
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|counter
argument_list|)
argument_list|)
expr_stmt|;
block|}
name|sb
operator|.
name|append
argument_list|(
literal|'-'
argument_list|)
expr_stmt|;
if|if
condition|(
name|clusterId
operator|<
literal|10
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
block|}
else|else
block|{
name|sb
operator|.
name|append
argument_list|(
name|Integer
operator|.
name|toHexString
argument_list|(
name|clusterId
argument_list|)
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
specifier|public
name|String
name|toReadableString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|"revision: \""
argument_list|)
operator|.
name|append
argument_list|(
name|toString
argument_list|()
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|", clusterId: "
argument_list|)
operator|.
name|append
argument_list|(
name|clusterId
argument_list|)
expr_stmt|;
name|buff
operator|.
name|append
argument_list|(
literal|", time: \""
argument_list|)
operator|.
name|append
argument_list|(
name|Utils
operator|.
name|timestampToString
argument_list|(
name|timestamp
argument_list|)
argument_list|)
operator|.
name|append
argument_list|(
literal|"\""
argument_list|)
expr_stmt|;
if|if
condition|(
name|counter
operator|>
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", counter: "
argument_list|)
operator|.
name|append
argument_list|(
name|counter
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|branch
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|", branch: true"
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Get the timestamp in milliseconds since 1970.      *      * @return the timestamp      */
specifier|public
name|long
name|getTimestamp
parameter_list|()
block|{
return|return
name|timestamp
return|;
block|}
specifier|public
name|int
name|getCounter
parameter_list|()
block|{
return|return
name|counter
return|;
block|}
comment|/**      * @return<code>true</code> if this is a branch revision, otherwise      *<code>false</code>.      */
specifier|public
name|boolean
name|isBranch
parameter_list|()
block|{
return|return
name|branch
return|;
block|}
comment|/**      * Returns a revision with the same timestamp, counter and clusterId as this      * revision and the branch flag set to<code>true</code>.      *      * @return branch revision with this timestamp, counter and clusterId.      */
specifier|public
name|Revision
name|asBranchRevision
parameter_list|()
block|{
if|if
condition|(
name|isBranch
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
operator|new
name|Revision
argument_list|(
name|timestamp
argument_list|,
name|counter
argument_list|,
name|clusterId
argument_list|,
literal|true
argument_list|)
return|;
block|}
block|}
comment|/**      * Returns a revision with the same timestamp, counter and clusterId as this      * revision and the branch flag set to<code>false</code>.      *      * @return trunkrevision with this timestamp, counter and clusterId.      */
specifier|public
name|Revision
name|asTrunkRevision
parameter_list|()
block|{
if|if
condition|(
operator|!
name|isBranch
argument_list|()
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
return|return
operator|new
name|Revision
argument_list|(
name|timestamp
argument_list|,
name|counter
argument_list|,
name|clusterId
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
call|(
name|int
call|)
argument_list|(
name|timestamp
operator|>>>
literal|32
argument_list|)
operator|^
operator|(
name|int
operator|)
name|timestamp
operator|^
name|counter
operator|^
name|clusterId
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|.
name|getClass
argument_list|()
operator|!=
name|this
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|Revision
name|r
init|=
operator|(
name|Revision
operator|)
name|other
decl_stmt|;
return|return
name|r
operator|.
name|timestamp
operator|==
name|this
operator|.
name|timestamp
operator|&&
name|r
operator|.
name|counter
operator|==
name|this
operator|.
name|counter
operator|&&
name|r
operator|.
name|clusterId
operator|==
name|this
operator|.
name|clusterId
operator|&&
name|r
operator|.
name|branch
operator|==
name|this
operator|.
name|branch
return|;
block|}
specifier|public
name|boolean
name|equalsIgnoreBranch
parameter_list|(
name|Revision
name|other
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|other
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|other
operator|==
literal|null
condition|)
block|{
return|return
literal|false
return|;
block|}
return|return
name|other
operator|.
name|timestamp
operator|==
name|this
operator|.
name|timestamp
operator|&&
name|other
operator|.
name|counter
operator|==
name|this
operator|.
name|counter
operator|&&
name|other
operator|.
name|clusterId
operator|==
name|this
operator|.
name|clusterId
return|;
block|}
specifier|public
name|int
name|getClusterId
parameter_list|()
block|{
return|return
name|clusterId
return|;
block|}
comment|/**      * Revision ranges allow to compare revisions ids of different cluster instances. A      * range tells when a list of revisions from a certain cluster instance was seen by      * the current process.      */
specifier|static
class|class
name|RevisionRange
block|{
comment|/**          * The newest revision for the given cluster instance and time.          */
name|Revision
name|revision
decl_stmt|;
comment|/**          * The (local) revision; the time when this revision was seen by this          * cluster instance.          */
name|Revision
name|seenAt
decl_stmt|;
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|revision
operator|+
literal|":"
operator|+
name|seenAt
return|;
block|}
block|}
comment|/**      * A facility that is able to compare revisions of different cluster instances.      * It contains a map of revision ranges.      */
specifier|public
specifier|static
class|class
name|RevisionComparator
implements|implements
name|Comparator
argument_list|<
name|Revision
argument_list|>
block|{
specifier|static
specifier|final
name|Revision
name|NEWEST
init|=
operator|new
name|Revision
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|,
literal|0
argument_list|)
decl_stmt|;
specifier|static
specifier|final
name|Revision
name|FUTURE
init|=
operator|new
name|Revision
argument_list|(
name|Long
operator|.
name|MAX_VALUE
argument_list|,
name|Integer
operator|.
name|MAX_VALUE
argument_list|,
literal|0
argument_list|)
decl_stmt|;
comment|/**          * The map of cluster instances to lists of revision ranges.          */
specifier|private
specifier|final
name|ConcurrentMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|RevisionRange
argument_list|>
argument_list|>
name|map
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|Integer
argument_list|,
name|List
argument_list|<
name|RevisionRange
argument_list|>
argument_list|>
argument_list|()
decl_stmt|;
comment|/**          * When comparing revisions that occurred before, the timestamp is ignored.          */
specifier|private
name|long
name|oldestTimestamp
decl_stmt|;
comment|/**          * The cluster node id of the current cluster node. Revisions          * from this cluster node that are newer than the newest range          * (new local revisions)          * are considered to be the newest revisions overall.          */
specifier|private
specifier|final
name|int
name|currentClusterNodeId
decl_stmt|;
name|RevisionComparator
parameter_list|(
name|int
name|currentClusterNodId
parameter_list|)
block|{
name|this
operator|.
name|currentClusterNodeId
operator|=
name|currentClusterNodId
expr_stmt|;
block|}
comment|/**          * Forget the order of older revisions. After calling this method, when comparing          * revisions that happened before the given value, the timestamp order is used          * (time dilation is ignored for older events).          *          * @param timestamp the time in milliseconds (see {@link #getCurrentTimestamp})          */
specifier|public
name|void
name|purge
parameter_list|(
name|long
name|timestamp
parameter_list|)
block|{
name|oldestTimestamp
operator|=
name|timestamp
expr_stmt|;
for|for
control|(
name|int
name|clusterId
range|:
name|map
operator|.
name|keySet
argument_list|()
control|)
block|{
while|while
condition|(
literal|true
condition|)
block|{
name|List
argument_list|<
name|RevisionRange
argument_list|>
name|list
init|=
name|map
operator|.
name|get
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RevisionRange
argument_list|>
name|newList
init|=
name|purge
argument_list|(
name|list
argument_list|)
decl_stmt|;
if|if
condition|(
name|newList
operator|==
literal|null
condition|)
block|{
comment|// retry if removing was not successful
if|if
condition|(
name|map
operator|.
name|remove
argument_list|(
name|clusterId
argument_list|,
name|list
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
elseif|else
if|if
condition|(
name|newList
operator|==
name|list
condition|)
block|{
comment|// no change
break|break;
block|}
else|else
block|{
comment|// retry if replacing was not successful
if|if
condition|(
name|map
operator|.
name|replace
argument_list|(
name|clusterId
argument_list|,
name|list
argument_list|,
name|newList
argument_list|)
condition|)
block|{
break|break;
block|}
block|}
block|}
block|}
block|}
specifier|private
name|List
argument_list|<
name|RevisionRange
argument_list|>
name|purge
parameter_list|(
name|List
argument_list|<
name|RevisionRange
argument_list|>
name|list
parameter_list|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
for|for
control|(
init|;
name|i
operator|<
name|list
operator|.
name|size
argument_list|()
condition|;
name|i
operator|++
control|)
block|{
name|RevisionRange
name|r
init|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|seenAt
operator|.
name|getTimestamp
argument_list|()
operator|>
name|oldestTimestamp
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
name|i
operator|>
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
return|return
literal|null
return|;
block|}
elseif|else
if|if
condition|(
name|i
operator|==
literal|0
condition|)
block|{
return|return
name|list
return|;
block|}
return|return
operator|new
name|ArrayList
argument_list|<
name|RevisionRange
argument_list|>
argument_list|(
name|list
operator|.
name|subList
argument_list|(
name|i
argument_list|,
name|list
operator|.
name|size
argument_list|()
argument_list|)
argument_list|)
return|;
block|}
comment|/**          * Add the revision to the top of the queue for the given cluster node.          * If an entry for this timestamp already exists, it is replaced.          *          * @param r the revision          * @param seenAt the (local) revision where this revision was seen here          */
specifier|public
name|void
name|add
parameter_list|(
name|Revision
name|r
parameter_list|,
name|Revision
name|seenAt
parameter_list|)
block|{
name|int
name|clusterId
init|=
name|r
operator|.
name|getClusterId
argument_list|()
decl_stmt|;
while|while
condition|(
literal|true
condition|)
block|{
name|List
argument_list|<
name|RevisionRange
argument_list|>
name|list
init|=
name|map
operator|.
name|get
argument_list|(
name|clusterId
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|RevisionRange
argument_list|>
name|newList
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
name|newList
operator|=
operator|new
name|ArrayList
argument_list|<
name|RevisionRange
argument_list|>
argument_list|()
expr_stmt|;
block|}
else|else
block|{
name|RevisionRange
name|last
init|=
name|list
operator|.
name|get
argument_list|(
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
argument_list|)
decl_stmt|;
if|if
condition|(
name|last
operator|.
name|seenAt
operator|.
name|equals
argument_list|(
name|seenAt
argument_list|)
condition|)
block|{
comment|// replace existing
if|if
condition|(
name|r
operator|.
name|compareRevisionTime
argument_list|(
name|last
operator|.
name|revision
argument_list|)
operator|>
literal|0
condition|)
block|{
comment|// but only if newer
name|last
operator|.
name|revision
operator|=
name|r
expr_stmt|;
block|}
return|return;
block|}
if|if
condition|(
name|last
operator|.
name|revision
operator|.
name|compareRevisionTime
argument_list|(
name|r
argument_list|)
operator|>
literal|0
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Can not add an earlier revision: "
operator|+
name|last
operator|.
name|revision
operator|+
literal|"> "
operator|+
name|r
operator|+
literal|"; current cluster node is "
operator|+
name|currentClusterNodeId
argument_list|)
throw|;
block|}
name|newList
operator|=
operator|new
name|ArrayList
argument_list|<
name|RevisionRange
argument_list|>
argument_list|(
name|list
argument_list|)
expr_stmt|;
block|}
name|RevisionRange
name|range
init|=
operator|new
name|RevisionRange
argument_list|()
decl_stmt|;
name|range
operator|.
name|seenAt
operator|=
name|seenAt
expr_stmt|;
name|range
operator|.
name|revision
operator|=
name|r
expr_stmt|;
name|newList
operator|.
name|add
argument_list|(
name|range
argument_list|)
expr_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|map
operator|.
name|putIfAbsent
argument_list|(
name|clusterId
argument_list|,
name|newList
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return;
block|}
block|}
else|else
block|{
if|if
condition|(
name|map
operator|.
name|replace
argument_list|(
name|clusterId
argument_list|,
name|list
argument_list|,
name|newList
argument_list|)
condition|)
block|{
return|return;
block|}
block|}
block|}
block|}
annotation|@
name|Override
specifier|public
name|int
name|compare
parameter_list|(
name|Revision
name|o1
parameter_list|,
name|Revision
name|o2
parameter_list|)
block|{
if|if
condition|(
name|o1
operator|.
name|getClusterId
argument_list|()
operator|==
name|o2
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
return|return
name|o1
operator|.
name|compareRevisionTime
argument_list|(
name|o2
argument_list|)
return|;
block|}
name|Revision
name|range1
init|=
name|getRevisionSeen
argument_list|(
name|o1
argument_list|)
decl_stmt|;
name|Revision
name|range2
init|=
name|getRevisionSeen
argument_list|(
name|o2
argument_list|)
decl_stmt|;
if|if
condition|(
name|range1
operator|==
name|FUTURE
operator|&&
name|range2
operator|==
name|FUTURE
condition|)
block|{
return|return
name|o1
operator|.
name|compareRevisionTimeThenClusterId
argument_list|(
name|o2
argument_list|)
return|;
block|}
if|if
condition|(
name|range1
operator|==
literal|null
operator|&&
name|range2
operator|==
literal|null
condition|)
block|{
return|return
name|o1
operator|.
name|compareRevisionTimeThenClusterId
argument_list|(
name|o2
argument_list|)
return|;
block|}
if|if
condition|(
name|range1
operator|==
literal|null
condition|)
block|{
return|return
operator|-
literal|1
return|;
block|}
elseif|else
if|if
condition|(
name|range2
operator|==
literal|null
condition|)
block|{
return|return
literal|1
return|;
block|}
name|int
name|comp
init|=
name|range1
operator|.
name|compareRevisionTimeThenClusterId
argument_list|(
name|range2
argument_list|)
decl_stmt|;
if|if
condition|(
name|comp
operator|!=
literal|0
condition|)
block|{
return|return
name|comp
return|;
block|}
return|return
name|Integer
operator|.
name|signum
argument_list|(
name|o1
operator|.
name|getClusterId
argument_list|()
operator|-
name|o2
operator|.
name|getClusterId
argument_list|()
argument_list|)
return|;
block|}
comment|/**          * Get the seen-at revision from the revision range.          *<p>          *<ul>          *<li>          *         {@code null} if the revision is older than the earliest range          *         and the revision timestamp is less than or equal the time          *         of the last {@link #purge(long)} (see also          *         {@link #oldestTimestamp}).          *</li>          *<li>          *         if the revision is newer than the lower bound of the newest          *         range, then {@link #NEWEST} is returned for a local cluster          *         revision and {@link #FUTURE} for a foreign cluster revision.          *</li>          *<li>          *         if the revision matches the lower seen-at bound of a range,          *         then this seen-at revision is returned.          *</li>          *<li>          *         otherwise the lower bound seen-at revision of next higher          *         range is returned.          *</li>          *</ul>          *          * Below is a graph for a revision comparison example as seen from one          * cluster node with some known revision ranges. Revision ranges less          * than or equal r2-0-0 have been purged and there are known ranges for          * cluster node 1 (this cluster node) and cluster node 2 (some other          * cluster node).          *<pre>          *     View from cluster node 1:          *          *                purge    r3-0-1    r5-0-2    r7-0-1          *                  ˅         ˅         ˅         ˅          *     ---+---------+---------+---------+---------+---------          *     r1-0-0    r2-0-0    r3-0-0    r4-0-0    r5-0-0          *          *            ^          *         r1-0-1 -> null (1)          *          *                      ^          *                   r4-0-2 -> r4-0-0 (2)          *          *                            ^          *                         r3-0-1 -> r3-0-0 (3)          *          *                                           ^          *                                        r6-0-2 -> FUTURE (4)          *          *                                                       ^          *                                                    r9-0-1 -> NEWEST (5)          *</pre>          *<ol>          *<li>older than earliest range and purge time</li>          *<li>seen-at of next higher range</li>          *<li>seen-at of matching lower bound of range</li>          *<li>foreign revision is newer than most recent range</li>          *<li>local revision is newer than most recent range</li>          *</ol>          * This gives the following revision ordering:          *<pre>          * r1-0-1< r3-0-1< r-4-0-2< r9-0-1< r6-0-2          *</pre>          *          * @param r the revision          * @return the seen-at revision or {@code null} if the revision is older          *          than the earliest range and purge time.          */
name|Revision
name|getRevisionSeen
parameter_list|(
name|Revision
name|r
parameter_list|)
block|{
name|List
argument_list|<
name|RevisionRange
argument_list|>
name|list
init|=
name|map
operator|.
name|get
argument_list|(
name|r
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|list
operator|==
literal|null
condition|)
block|{
if|if
condition|(
name|r
operator|.
name|getTimestamp
argument_list|()
operator|<=
name|oldestTimestamp
condition|)
block|{
comment|// old revision with already purged range
return|return
literal|null
return|;
block|}
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|!=
name|currentClusterNodeId
condition|)
block|{
comment|// this is from a cluster node we did not see yet
comment|// see also OAK-1170
return|return
name|FUTURE
return|;
block|}
return|return
literal|null
return|;
block|}
comment|// search from latest backward
comment|// (binary search could be used, but we expect most queries
comment|// at the end of the list)
name|RevisionRange
name|range
init|=
literal|null
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
init|;
name|i
operator|>=
literal|0
condition|;
name|i
operator|--
control|)
block|{
name|range
operator|=
name|list
operator|.
name|get
argument_list|(
name|i
argument_list|)
expr_stmt|;
name|int
name|compare
init|=
name|r
operator|.
name|compareRevisionTime
argument_list|(
name|range
operator|.
name|revision
argument_list|)
decl_stmt|;
if|if
condition|(
name|compare
operator|==
literal|0
condition|)
block|{
return|return
name|range
operator|.
name|seenAt
return|;
block|}
elseif|else
if|if
condition|(
name|compare
operator|>
literal|0
condition|)
block|{
if|if
condition|(
name|i
operator|==
name|list
operator|.
name|size
argument_list|()
operator|-
literal|1
condition|)
block|{
comment|// newer than the newest range
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|==
name|currentClusterNodeId
condition|)
block|{
comment|// newer than all others, except for FUTURE
return|return
name|NEWEST
return|;
block|}
else|else
block|{
comment|// happens in the future (not visible yet)
return|return
name|FUTURE
return|;
block|}
block|}
else|else
block|{
comment|// there is a newer range
return|return
name|list
operator|.
name|get
argument_list|(
name|i
operator|+
literal|1
argument_list|)
operator|.
name|seenAt
return|;
block|}
block|}
block|}
if|if
condition|(
name|range
operator|!=
literal|null
operator|&&
name|r
operator|.
name|getTimestamp
argument_list|()
operator|>
name|oldestTimestamp
condition|)
block|{
comment|// revision is older than earliest range and after purge
comment|// timestamp. return seen-at revision of earliest range.
return|return
name|range
operator|.
name|seenAt
return|;
block|}
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
name|StringBuilder
name|buff
init|=
operator|new
name|StringBuilder
argument_list|()
decl_stmt|;
for|for
control|(
name|int
name|clusterId
range|:
operator|new
name|TreeSet
argument_list|<
name|Integer
argument_list|>
argument_list|(
name|map
operator|.
name|keySet
argument_list|()
argument_list|)
control|)
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
name|buff
operator|.
name|append
argument_list|(
name|clusterId
argument_list|)
operator|.
name|append
argument_list|(
literal|":"
argument_list|)
expr_stmt|;
for|for
control|(
name|RevisionRange
name|r
range|:
name|map
operator|.
name|get
argument_list|(
name|clusterId
argument_list|)
control|)
block|{
if|if
condition|(
name|i
operator|++
operator|%
literal|4
operator|==
literal|0
condition|)
block|{
name|buff
operator|.
name|append
argument_list|(
literal|'\n'
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|" "
argument_list|)
operator|.
name|append
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
name|buff
operator|.
name|append
argument_list|(
literal|"\n"
argument_list|)
expr_stmt|;
block|}
return|return
name|buff
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
block|}
end_class

end_unit

