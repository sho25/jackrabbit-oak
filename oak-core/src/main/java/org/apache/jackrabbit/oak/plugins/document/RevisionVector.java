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
name|Arrays
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
name|Iterator
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
name|Set
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|AbstractIterator
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
name|Iterators
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
name|PeekingIterator
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
name|Sets
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
name|primitives
operator|.
name|Ints
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
name|cache
operator|.
name|CacheValue
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|collect
operator|.
name|Iterables
operator|.
name|toArray
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
name|collect
operator|.
name|Iterators
operator|.
name|peekingIterator
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
name|collect
operator|.
name|Lists
operator|.
name|newArrayListWithCapacity
import|;
end_import

begin_import
import|import static
name|java
operator|.
name|util
operator|.
name|Arrays
operator|.
name|sort
import|;
end_import

begin_comment
comment|/**  * A vector of revisions. Instances of this class are immutable and methods  * like {@link #update(Revision)} create a new instance as needed.  *  * This class implements {@link Comparable}. While  * {@link #compareTo(RevisionVector)} provides a total order of revision  * vector instances, this order is unrelated to when changes are visible in  * a DocumentNodeStore cluster. Do not use this method to determine whether  * a given revision vector happened before or after another!  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|RevisionVector
implements|implements
name|Iterable
argument_list|<
name|Revision
argument_list|>
implements|,
name|Comparable
argument_list|<
name|RevisionVector
argument_list|>
implements|,
name|CacheValue
block|{
specifier|private
specifier|final
specifier|static
name|RevisionVector
name|EMPTY
init|=
operator|new
name|RevisionVector
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|Revision
index|[]
name|revisions
decl_stmt|;
comment|//lazily initialized
specifier|private
name|int
name|hash
decl_stmt|;
specifier|private
name|RevisionVector
parameter_list|(
annotation|@
name|Nonnull
name|Revision
index|[]
name|revisions
parameter_list|,
name|boolean
name|checkUniqueClusterIds
parameter_list|,
name|boolean
name|sort
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|revisions
argument_list|)
expr_stmt|;
if|if
condition|(
name|checkUniqueClusterIds
condition|)
block|{
name|checkUniqueClusterIds
argument_list|(
name|revisions
argument_list|)
expr_stmt|;
block|}
if|if
condition|(
name|sort
condition|)
block|{
name|sort
argument_list|(
name|revisions
argument_list|,
name|RevisionComparator
operator|.
name|INSTANCE
argument_list|)
expr_stmt|;
block|}
name|this
operator|.
name|revisions
operator|=
name|revisions
expr_stmt|;
block|}
specifier|public
name|RevisionVector
parameter_list|(
annotation|@
name|Nonnull
name|Revision
modifier|...
name|revisions
parameter_list|)
block|{
name|this
argument_list|(
name|Arrays
operator|.
name|copyOf
argument_list|(
name|revisions
argument_list|,
name|revisions
operator|.
name|length
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RevisionVector
parameter_list|(
annotation|@
name|Nonnull
name|Iterable
argument_list|<
name|Revision
argument_list|>
name|revisions
parameter_list|)
block|{
name|this
argument_list|(
name|toArray
argument_list|(
name|revisions
argument_list|,
name|Revision
operator|.
name|class
argument_list|)
argument_list|,
literal|true
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
specifier|public
name|RevisionVector
parameter_list|(
annotation|@
name|Nonnull
name|Set
argument_list|<
name|Revision
argument_list|>
name|revisions
parameter_list|)
block|{
name|this
argument_list|(
name|toArray
argument_list|(
name|revisions
argument_list|,
name|Revision
operator|.
name|class
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|true
argument_list|)
expr_stmt|;
block|}
comment|/**      * Creates a new revision vector with based on this vector and the given      * {@code revision}. If this vector contains a revision with the same      * clusterId as {@code revision}, the returned vector will have the      * revision updated with the given one. Otherwise the returned vector will      * have all elements of this vector plus the given {@code revision}.      *      * @param revision the revision set to use for the new vector.      * @return the resulting revision vector.      */
specifier|public
name|RevisionVector
name|update
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|revision
argument_list|)
expr_stmt|;
name|Revision
name|existing
init|=
literal|null
decl_stmt|;
name|int
name|i
decl_stmt|;
for|for
control|(
name|i
operator|=
literal|0
init|;
name|i
operator|<
name|revisions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Revision
name|r
init|=
name|revisions
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|==
name|revision
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
name|existing
operator|=
name|r
expr_stmt|;
break|break;
block|}
block|}
name|Revision
index|[]
name|newRevisions
decl_stmt|;
name|boolean
name|sort
decl_stmt|;
if|if
condition|(
name|existing
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|revision
operator|.
name|equals
argument_list|(
name|existing
argument_list|)
condition|)
block|{
return|return
name|this
return|;
block|}
else|else
block|{
name|newRevisions
operator|=
name|Arrays
operator|.
name|copyOf
argument_list|(
name|revisions
argument_list|,
name|revisions
operator|.
name|length
argument_list|)
expr_stmt|;
name|newRevisions
index|[
name|i
index|]
operator|=
name|revision
expr_stmt|;
name|sort
operator|=
literal|false
expr_stmt|;
block|}
block|}
else|else
block|{
name|newRevisions
operator|=
operator|new
name|Revision
index|[
name|revisions
operator|.
name|length
operator|+
literal|1
index|]
expr_stmt|;
name|System
operator|.
name|arraycopy
argument_list|(
name|revisions
argument_list|,
literal|0
argument_list|,
name|newRevisions
argument_list|,
literal|0
argument_list|,
name|revisions
operator|.
name|length
argument_list|)
expr_stmt|;
name|newRevisions
index|[
name|revisions
operator|.
name|length
index|]
operator|=
name|revision
expr_stmt|;
name|sort
operator|=
literal|true
expr_stmt|;
block|}
return|return
operator|new
name|RevisionVector
argument_list|(
name|newRevisions
argument_list|,
literal|false
argument_list|,
name|sort
argument_list|)
return|;
block|}
comment|/**      * Returns a RevisionVector without the revision element with the given      * {@code clusterId}.      *      * @param clusterId the clusterId of the revision to remove.      * @return RevisionVector without the revision element.      */
specifier|public
name|RevisionVector
name|remove
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
if|if
condition|(
name|revisions
operator|.
name|length
operator|==
literal|0
condition|)
block|{
return|return
name|this
return|;
block|}
name|boolean
name|found
init|=
literal|false
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|==
name|clusterId
condition|)
block|{
name|found
operator|=
literal|true
expr_stmt|;
break|break;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|>
name|clusterId
condition|)
block|{
break|break;
block|}
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
return|return
name|this
return|;
block|}
name|Revision
index|[]
name|revs
init|=
operator|new
name|Revision
index|[
name|revisions
operator|.
name|length
operator|-
literal|1
index|]
decl_stmt|;
name|int
name|idx
init|=
literal|0
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|!=
name|clusterId
condition|)
block|{
name|revs
index|[
name|idx
operator|++
index|]
operator|=
name|r
expr_stmt|;
block|}
block|}
return|return
operator|new
name|RevisionVector
argument_list|(
name|revs
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Calculates the parallel minimum of this and the given {@code vector}.      *      * @param vector the other vector.      * @return the parallel minimum of the two.      */
specifier|public
name|RevisionVector
name|pmin
parameter_list|(
annotation|@
name|Nonnull
name|RevisionVector
name|vector
parameter_list|)
block|{
comment|// optimize single revision case
if|if
condition|(
name|revisions
operator|.
name|length
operator|==
literal|1
operator|&&
name|vector
operator|.
name|revisions
operator|.
name|length
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|revisions
index|[
literal|0
index|]
operator|.
name|getClusterId
argument_list|()
operator|==
name|vector
operator|.
name|revisions
index|[
literal|0
index|]
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
return|return
name|revisions
index|[
literal|0
index|]
operator|.
name|compareRevisionTime
argument_list|(
name|vector
operator|.
name|revisions
index|[
literal|0
index|]
argument_list|)
operator|<
literal|0
condition|?
name|this
else|:
name|vector
return|;
block|}
else|else
block|{
return|return
name|EMPTY
return|;
block|}
block|}
name|int
name|capacity
init|=
name|Math
operator|.
name|min
argument_list|(
name|revisions
operator|.
name|length
argument_list|,
name|vector
operator|.
name|revisions
operator|.
name|length
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Revision
argument_list|>
name|pmin
init|=
name|newArrayListWithCapacity
argument_list|(
name|capacity
argument_list|)
decl_stmt|;
name|PeekingIterator
argument_list|<
name|Revision
argument_list|>
name|it
init|=
name|peekingIterator
argument_list|(
name|vector
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
name|Revision
name|other
init|=
name|peekRevision
argument_list|(
name|it
argument_list|,
name|r
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|other
operator|.
name|getClusterId
argument_list|()
operator|==
name|r
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
name|pmin
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|min
argument_list|(
name|r
argument_list|,
name|other
argument_list|)
argument_list|)
expr_stmt|;
block|}
block|}
else|else
block|{
break|break;
block|}
block|}
return|return
operator|new
name|RevisionVector
argument_list|(
name|toArray
argument_list|(
name|pmin
argument_list|,
name|Revision
operator|.
name|class
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Calculates the parallel maximum of this and the given {@code vector}.      *      * @param vector the other vector.      * @return the parallel maximum of the two.      */
specifier|public
name|RevisionVector
name|pmax
parameter_list|(
annotation|@
name|Nonnull
name|RevisionVector
name|vector
parameter_list|)
block|{
comment|// optimize single revision case
if|if
condition|(
name|revisions
operator|.
name|length
operator|==
literal|1
operator|&&
name|vector
operator|.
name|revisions
operator|.
name|length
operator|==
literal|1
condition|)
block|{
if|if
condition|(
name|revisions
index|[
literal|0
index|]
operator|.
name|getClusterId
argument_list|()
operator|==
name|vector
operator|.
name|revisions
index|[
literal|0
index|]
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
return|return
name|revisions
index|[
literal|0
index|]
operator|.
name|compareRevisionTime
argument_list|(
name|vector
operator|.
name|revisions
index|[
literal|0
index|]
argument_list|)
operator|>
literal|0
condition|?
name|this
else|:
name|vector
return|;
block|}
else|else
block|{
return|return
operator|new
name|RevisionVector
argument_list|(
name|revisions
index|[
literal|0
index|]
argument_list|,
name|vector
operator|.
name|revisions
index|[
literal|0
index|]
argument_list|)
return|;
block|}
block|}
name|int
name|capacity
init|=
name|Math
operator|.
name|max
argument_list|(
name|revisions
operator|.
name|length
argument_list|,
name|vector
operator|.
name|revisions
operator|.
name|length
argument_list|)
decl_stmt|;
name|List
argument_list|<
name|Revision
argument_list|>
name|pmax
init|=
name|newArrayListWithCapacity
argument_list|(
name|capacity
argument_list|)
decl_stmt|;
name|PeekingIterator
argument_list|<
name|Revision
argument_list|>
name|it
init|=
name|peekingIterator
argument_list|(
name|vector
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|it
operator|.
name|peek
argument_list|()
operator|.
name|getClusterId
argument_list|()
operator|<
name|r
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
name|pmax
operator|.
name|add
argument_list|(
name|it
operator|.
name|next
argument_list|()
argument_list|)
expr_stmt|;
block|}
name|Revision
name|other
init|=
name|peekRevision
argument_list|(
name|it
argument_list|,
name|r
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|other
operator|!=
literal|null
operator|&&
name|other
operator|.
name|getClusterId
argument_list|()
operator|==
name|r
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
name|pmax
operator|.
name|add
argument_list|(
name|Utils
operator|.
name|max
argument_list|(
name|r
argument_list|,
name|other
argument_list|)
argument_list|)
expr_stmt|;
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
else|else
block|{
comment|// other does not have a revision with r.clusterId
name|pmax
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
comment|// add remaining
name|Iterators
operator|.
name|addAll
argument_list|(
name|pmax
argument_list|,
name|it
argument_list|)
expr_stmt|;
return|return
operator|new
name|RevisionVector
argument_list|(
name|toArray
argument_list|(
name|pmax
argument_list|,
name|Revision
operator|.
name|class
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Returns the difference of this and the other vector. The returned vector      * contains all revisions of this vector that are not contained in the      * other vector.      *      * @param vector the other vector.      * @return the difference of the two vectors.      */
specifier|public
name|RevisionVector
name|difference
parameter_list|(
name|RevisionVector
name|vector
parameter_list|)
block|{
name|List
argument_list|<
name|Revision
argument_list|>
name|diff
init|=
name|newArrayListWithCapacity
argument_list|(
name|revisions
operator|.
name|length
argument_list|)
decl_stmt|;
name|PeekingIterator
argument_list|<
name|Revision
argument_list|>
name|it
init|=
name|peekingIterator
argument_list|(
name|vector
operator|.
name|iterator
argument_list|()
argument_list|)
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
name|Revision
name|other
init|=
name|peekRevision
argument_list|(
name|it
argument_list|,
name|r
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
operator|!
name|r
operator|.
name|equals
argument_list|(
name|other
argument_list|)
condition|)
block|{
name|diff
operator|.
name|add
argument_list|(
name|r
argument_list|)
expr_stmt|;
block|}
block|}
return|return
operator|new
name|RevisionVector
argument_list|(
name|toArray
argument_list|(
name|diff
argument_list|,
name|Revision
operator|.
name|class
argument_list|)
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Returns {@code true} if the given revision is newer than the revision      * element with the same clusterId in the vector. The given revision is      * also considered newer if there is no revision element with the same      * clusterId in this vector.      *      * @param revision the revision to check.      * @return {@code true} if considered newer, {@code false} otherwise.      */
specifier|public
name|boolean
name|isRevisionNewer
parameter_list|(
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|revision
argument_list|)
expr_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|==
name|revision
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
return|return
name|r
operator|.
name|compareRevisionTime
argument_list|(
name|revision
argument_list|)
operator|<
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|>
name|revision
operator|.
name|getClusterId
argument_list|()
condition|)
block|{
comment|// revisions are sorted by clusterId ascending
break|break;
block|}
block|}
return|return
literal|true
return|;
block|}
comment|/**      * @return {@code true} if any of the revisions in this vector is a branch      *          revision, {@code false} otherwise.      */
specifier|public
name|boolean
name|isBranch
parameter_list|()
block|{
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
if|if
condition|(
name|r
operator|.
name|isBranch
argument_list|()
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
comment|/**      * @return the first branch revision in this vector.      * @throws IllegalStateException if this vector does not contain a branch      *          revision.      */
annotation|@
name|Nonnull
specifier|public
name|Revision
name|getBranchRevision
parameter_list|()
block|{
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
if|if
condition|(
name|r
operator|.
name|isBranch
argument_list|()
condition|)
block|{
return|return
name|r
return|;
block|}
block|}
throw|throw
operator|new
name|IllegalStateException
argument_list|(
literal|"This vector does not contain a branch revision: "
operator|+
name|this
argument_list|)
throw|;
block|}
comment|/**      * Returns the revision element with the given clusterId or {@code null}      * if there is no such revision in this vector.      *      * @param clusterId a clusterId.      * @return the revision element with the given clusterId or {@code null}      *      if none exists.      */
specifier|public
name|Revision
name|getRevision
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
name|int
name|cmp
init|=
name|Ints
operator|.
name|compare
argument_list|(
name|r
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|clusterId
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|==
literal|0
condition|)
block|{
return|return
name|r
return|;
block|}
elseif|else
if|if
condition|(
name|cmp
operator|>
literal|0
condition|)
block|{
break|break;
block|}
block|}
return|return
literal|null
return|;
block|}
comment|/**      * Returns a string representation of this revision vector, which can be      * parsed again by {@link #fromString(String)}.      *      * @return a string representation of this revision vector.      */
specifier|public
name|String
name|asString
parameter_list|()
block|{
name|int
name|len
init|=
name|revisions
operator|.
name|length
decl_stmt|;
if|if
condition|(
name|len
operator|==
literal|0
condition|)
block|{
return|return
literal|""
return|;
block|}
name|StringBuilder
name|sb
init|=
operator|new
name|StringBuilder
argument_list|(
name|len
operator|*
name|Revision
operator|.
name|REV_STRING_APPROX_SIZE
operator|+
name|len
operator|-
literal|1
argument_list|)
decl_stmt|;
return|return
name|toStringBuilder
argument_list|(
name|sb
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
comment|/**      * Appends the string representation of this revision vector to the passed      * {@code StringBuilder}. The string representation is the same as returned      * by {@link #asString()}.      *      * @param sb the {@code StringBuilder} this revision vector is appended to.      * @return the passed {@code StringBuilder} object.      */
specifier|public
name|StringBuilder
name|toStringBuilder
parameter_list|(
name|StringBuilder
name|sb
parameter_list|)
block|{
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|revisions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
if|if
condition|(
name|i
operator|>
literal|0
condition|)
block|{
name|sb
operator|.
name|append
argument_list|(
literal|','
argument_list|)
expr_stmt|;
block|}
name|revisions
index|[
name|i
index|]
operator|.
name|toStringBuilder
argument_list|(
name|sb
argument_list|)
expr_stmt|;
block|}
return|return
name|sb
return|;
block|}
comment|/**      * Creates a revision vector from a string representation as returned by      * {@link #asString()}.      *      * @param s the string representation of a revision vector.      * @return the revision vector.      * @throws IllegalArgumentException if the string is malformed      */
specifier|public
specifier|static
name|RevisionVector
name|fromString
parameter_list|(
name|String
name|s
parameter_list|)
block|{
name|String
index|[]
name|list
init|=
name|s
operator|.
name|split
argument_list|(
literal|","
argument_list|)
decl_stmt|;
name|Revision
index|[]
name|revisions
init|=
operator|new
name|Revision
index|[
name|list
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|list
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|revisions
index|[
name|i
index|]
operator|=
name|Revision
operator|.
name|fromString
argument_list|(
name|list
index|[
name|i
index|]
argument_list|)
expr_stmt|;
block|}
return|return
operator|new
name|RevisionVector
argument_list|(
name|revisions
argument_list|)
return|;
block|}
comment|/**      * Returns a revision vector where all revision elements are turned into      * trunk revisions.      *      * @return a trunk representation of this revision vector.      */
specifier|public
name|RevisionVector
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
name|Revision
index|[]
name|revs
init|=
operator|new
name|Revision
index|[
name|revisions
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|revisions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|revs
index|[
name|i
index|]
operator|=
name|revisions
index|[
name|i
index|]
operator|.
name|asTrunkRevision
argument_list|()
expr_stmt|;
block|}
return|return
operator|new
name|RevisionVector
argument_list|(
name|revs
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * A clone of this revision vector with the revision for the given      * clusterId set to a branch revision.      *      * @param clusterId the clusterId of the revision to be turned into a branch      *                  revision.      * @return the revision vector with the branch revision.      * @throws IllegalArgumentException if there is no revision element with the      *      given clusterId.      */
specifier|public
name|RevisionVector
name|asBranchRevision
parameter_list|(
name|int
name|clusterId
parameter_list|)
block|{
name|boolean
name|found
init|=
literal|false
decl_stmt|;
name|Revision
index|[]
name|revs
init|=
operator|new
name|Revision
index|[
name|revisions
operator|.
name|length
index|]
decl_stmt|;
for|for
control|(
name|int
name|i
init|=
literal|0
init|;
name|i
operator|<
name|revisions
operator|.
name|length
condition|;
name|i
operator|++
control|)
block|{
name|Revision
name|r
init|=
name|revisions
index|[
name|i
index|]
decl_stmt|;
if|if
condition|(
name|r
operator|.
name|getClusterId
argument_list|()
operator|==
name|clusterId
condition|)
block|{
name|r
operator|=
name|r
operator|.
name|asBranchRevision
argument_list|()
expr_stmt|;
name|found
operator|=
literal|true
expr_stmt|;
block|}
name|revs
index|[
name|i
index|]
operator|=
name|r
expr_stmt|;
block|}
if|if
condition|(
operator|!
name|found
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"RevisionVector ["
operator|+
name|asString
argument_list|()
operator|+
literal|"] does not have a revision for clusterId "
operator|+
name|clusterId
argument_list|)
throw|;
block|}
return|return
operator|new
name|RevisionVector
argument_list|(
name|revs
argument_list|,
literal|false
argument_list|,
literal|false
argument_list|)
return|;
block|}
comment|/**      * Returns the dimensions of this revision vector. That is, the number of      * revision elements in this vector.      *      * @return the number of revision elements in this vector.      */
specifier|public
name|int
name|getDimensions
parameter_list|()
block|{
return|return
name|revisions
operator|.
name|length
return|;
block|}
comment|//------------------------< CacheValue>------------------------------------
annotation|@
name|Override
specifier|public
name|int
name|getMemory
parameter_list|()
block|{
return|return
literal|32
comment|// shallow size
operator|+
name|revisions
operator|.
name|length
operator|*
operator|(
name|Revision
operator|.
name|SHALLOW_MEMORY_USAGE
operator|+
literal|4
operator|)
return|;
block|}
comment|//------------------------< Comparable>------------------------------------
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|Nonnull
name|RevisionVector
name|other
parameter_list|)
block|{
name|Iterator
argument_list|<
name|Revision
argument_list|>
name|it
init|=
name|other
operator|.
name|iterator
argument_list|()
decl_stmt|;
for|for
control|(
name|Revision
name|r
range|:
name|revisions
control|)
block|{
if|if
condition|(
operator|!
name|it
operator|.
name|hasNext
argument_list|()
condition|)
block|{
return|return
literal|1
return|;
block|}
name|Revision
name|otherRev
init|=
name|it
operator|.
name|next
argument_list|()
decl_stmt|;
name|int
name|cmp
init|=
operator|-
name|Ints
operator|.
name|compare
argument_list|(
name|r
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|otherRev
operator|.
name|getClusterId
argument_list|()
argument_list|)
decl_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
name|cmp
operator|=
name|r
operator|.
name|compareTo
argument_list|(
name|otherRev
argument_list|)
expr_stmt|;
if|if
condition|(
name|cmp
operator|!=
literal|0
condition|)
block|{
return|return
name|cmp
return|;
block|}
block|}
return|return
name|it
operator|.
name|hasNext
argument_list|()
condition|?
operator|-
literal|1
else|:
literal|0
return|;
block|}
comment|//-------------------------< Iterable>-------------------------------------
annotation|@
name|Override
specifier|public
name|Iterator
argument_list|<
name|Revision
argument_list|>
name|iterator
parameter_list|()
block|{
return|return
operator|new
name|AbstractIterator
argument_list|<
name|Revision
argument_list|>
argument_list|()
block|{
name|int
name|i
init|=
literal|0
decl_stmt|;
annotation|@
name|Override
specifier|protected
name|Revision
name|computeNext
parameter_list|()
block|{
if|if
condition|(
name|i
operator|>=
name|revisions
operator|.
name|length
condition|)
block|{
return|return
name|endOfData
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|revisions
index|[
name|i
operator|++
index|]
return|;
block|}
block|}
block|}
return|;
block|}
comment|//--------------------------< Object>--------------------------------------
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|asString
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|o
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|o
condition|)
block|{
return|return
literal|true
return|;
block|}
if|if
condition|(
name|o
operator|==
literal|null
operator|||
name|getClass
argument_list|()
operator|!=
name|o
operator|.
name|getClass
argument_list|()
condition|)
block|{
return|return
literal|false
return|;
block|}
name|RevisionVector
name|other
init|=
operator|(
name|RevisionVector
operator|)
name|o
decl_stmt|;
return|return
name|Arrays
operator|.
name|equals
argument_list|(
name|revisions
argument_list|,
name|other
operator|.
name|revisions
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
if|if
condition|(
name|hash
operator|==
literal|0
condition|)
block|{
name|hash
operator|=
name|Arrays
operator|.
name|hashCode
argument_list|(
name|revisions
argument_list|)
expr_stmt|;
block|}
return|return
name|hash
return|;
block|}
comment|//-------------------------< internal>-------------------------------------
annotation|@
name|CheckForNull
specifier|private
name|Revision
name|peekRevision
parameter_list|(
name|PeekingIterator
argument_list|<
name|Revision
argument_list|>
name|it
parameter_list|,
name|int
name|minClusterId
parameter_list|)
block|{
while|while
condition|(
name|it
operator|.
name|hasNext
argument_list|()
operator|&&
name|it
operator|.
name|peek
argument_list|()
operator|.
name|getClusterId
argument_list|()
operator|<
name|minClusterId
condition|)
block|{
name|it
operator|.
name|next
argument_list|()
expr_stmt|;
block|}
return|return
name|it
operator|.
name|hasNext
argument_list|()
condition|?
name|it
operator|.
name|peek
argument_list|()
else|:
literal|null
return|;
block|}
specifier|private
specifier|static
name|void
name|checkUniqueClusterIds
parameter_list|(
name|Revision
index|[]
name|revisions
parameter_list|)
throws|throws
name|IllegalArgumentException
block|{
if|if
condition|(
name|revisions
operator|.
name|length
operator|<
literal|2
condition|)
block|{
return|return;
block|}
name|Set
argument_list|<
name|Integer
argument_list|>
name|known
init|=
name|Sets
operator|.
name|newHashSetWithExpectedSize
argument_list|(
name|revisions
operator|.
name|length
argument_list|)
decl_stmt|;
for|for
control|(
name|Revision
name|revision
range|:
name|revisions
control|)
block|{
if|if
condition|(
operator|!
name|known
operator|.
name|add
argument_list|(
name|revision
operator|.
name|getClusterId
argument_list|()
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Multiple revisions with clusterId "
operator|+
name|revision
operator|.
name|getClusterId
argument_list|()
argument_list|)
throw|;
block|}
block|}
block|}
comment|/**      * Compares revisions according to their clusterId.      */
specifier|private
specifier|static
specifier|final
class|class
name|RevisionComparator
implements|implements
name|Comparator
argument_list|<
name|Revision
argument_list|>
block|{
specifier|private
specifier|static
specifier|final
name|Comparator
argument_list|<
name|Revision
argument_list|>
name|INSTANCE
init|=
operator|new
name|RevisionComparator
argument_list|()
decl_stmt|;
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
return|return
name|Ints
operator|.
name|compare
argument_list|(
name|o1
operator|.
name|getClusterId
argument_list|()
argument_list|,
name|o2
operator|.
name|getClusterId
argument_list|()
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

