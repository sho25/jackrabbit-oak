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
name|util
operator|.
name|Collection
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
name|Map
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
name|com
operator|.
name|google
operator|.
name|common
operator|.
name|base
operator|.
name|Function
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
name|Predicate
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
name|Iterables
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
comment|/**  * Keeps track of when nodes where last modified. To be persisted later by  * a background thread.  */
end_comment

begin_class
class|class
name|UnsavedModifications
block|{
specifier|private
specifier|final
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|map
init|=
operator|new
name|ConcurrentHashMap
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
argument_list|()
decl_stmt|;
comment|/**      * Puts a revision for the given path. The revision for the given path is      * only put if there is no modification present for the revision or if the      * current modification revision is older than the passed revision.      *      * @param path the path of the modified node.      * @param revision the revision of the modification.      * @return the previously set revision for the given path or null if there      *          was none or the current revision is newer.      */
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|put
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
annotation|@
name|Nonnull
name|Revision
name|revision
parameter_list|)
block|{
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
name|checkNotNull
argument_list|(
name|revision
argument_list|)
expr_stmt|;
for|for
control|(
init|;
condition|;
control|)
block|{
name|Revision
name|previous
init|=
name|map
operator|.
name|get
argument_list|(
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|previous
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
name|path
argument_list|,
name|revision
argument_list|)
operator|==
literal|null
condition|)
block|{
return|return
literal|null
return|;
block|}
block|}
else|else
block|{
if|if
condition|(
name|previous
operator|.
name|compareRevisionTime
argument_list|(
name|revision
argument_list|)
operator|<
literal|0
condition|)
block|{
if|if
condition|(
name|map
operator|.
name|replace
argument_list|(
name|path
argument_list|,
name|previous
argument_list|,
name|revision
argument_list|)
condition|)
block|{
return|return
name|previous
return|;
block|}
block|}
else|else
block|{
comment|// revision is earlier, do not update
return|return
literal|null
return|;
block|}
block|}
block|}
block|}
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|get
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|map
operator|.
name|get
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|CheckForNull
specifier|public
name|Revision
name|remove
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|map
operator|.
name|remove
argument_list|(
name|path
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|public
name|Collection
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|()
block|{
return|return
name|map
operator|.
name|keySet
argument_list|()
return|;
block|}
comment|/**      * Applies all modifications from this instance to the<code>other</code>.      * A modification is only applied if there is no modification in other      * for a given path or if the other modification is earlier than the      * merge commit revision.      *      * @param other the other<code>UnsavedModifications</code>.      * @param mergeCommit the merge commit revision.      */
specifier|public
name|void
name|applyTo
parameter_list|(
name|UnsavedModifications
name|other
parameter_list|,
name|Revision
name|mergeCommit
parameter_list|)
block|{
for|for
control|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|entry
range|:
name|map
operator|.
name|entrySet
argument_list|()
control|)
block|{
name|other
operator|.
name|put
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|mergeCommit
argument_list|)
expr_stmt|;
block|}
block|}
comment|/**      * Returns all paths of nodes with modifications at the start revision      * (inclusive) or later.      *      * @param start the start revision (inclusive).      * @return matching paths with pending modifications.      */
annotation|@
name|Nonnull
specifier|public
name|Iterable
argument_list|<
name|String
argument_list|>
name|getPaths
parameter_list|(
annotation|@
name|Nonnull
specifier|final
name|Revision
name|start
parameter_list|)
block|{
if|if
condition|(
name|map
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
else|else
block|{
return|return
name|Iterables
operator|.
name|transform
argument_list|(
name|Iterables
operator|.
name|filter
argument_list|(
name|map
operator|.
name|entrySet
argument_list|()
argument_list|,
operator|new
name|Predicate
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|boolean
name|apply
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|start
operator|.
name|compareRevisionTime
argument_list|(
name|input
operator|.
name|getValue
argument_list|()
argument_list|)
operator|<
literal|1
return|;
block|}
block|}
argument_list|)
argument_list|,
operator|new
name|Function
argument_list|<
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
argument_list|,
name|String
argument_list|>
argument_list|()
block|{
annotation|@
name|Override
specifier|public
name|String
name|apply
parameter_list|(
name|Map
operator|.
name|Entry
argument_list|<
name|String
argument_list|,
name|Revision
argument_list|>
name|input
parameter_list|)
block|{
return|return
name|input
operator|.
name|getKey
argument_list|()
return|;
block|}
block|}
argument_list|)
return|;
block|}
block|}
block|}
end_class

end_unit

