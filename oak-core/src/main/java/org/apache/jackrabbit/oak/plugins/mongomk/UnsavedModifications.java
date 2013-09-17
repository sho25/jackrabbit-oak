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
return|return
name|map
operator|.
name|put
argument_list|(
name|checkNotNull
argument_list|(
name|path
argument_list|)
argument_list|,
name|checkNotNull
argument_list|(
name|revision
argument_list|)
argument_list|)
return|;
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
name|Revision
name|r
init|=
name|other
operator|.
name|map
operator|.
name|putIfAbsent
argument_list|(
name|entry
operator|.
name|getKey
argument_list|()
argument_list|,
name|mergeCommit
argument_list|)
decl_stmt|;
if|if
condition|(
name|r
operator|!=
literal|null
condition|)
block|{
if|if
condition|(
name|r
operator|.
name|compareRevisionTime
argument_list|(
name|mergeCommit
argument_list|)
operator|<
literal|0
condition|)
block|{
name|other
operator|.
name|map
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
block|}
block|}
block|}
end_class

end_unit

