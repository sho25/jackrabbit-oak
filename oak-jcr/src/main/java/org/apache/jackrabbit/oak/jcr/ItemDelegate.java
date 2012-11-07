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
name|jcr
package|;
end_package

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
name|jcr
operator|.
name|InvalidItemStateException
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
name|api
operator|.
name|Tree
operator|.
name|Status
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
name|api
operator|.
name|TreeLocation
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
name|PathUtils
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
comment|/**  * Abstract base class for {@link NodeDelegate} and {@link PropertyDelegate}  */
end_comment

begin_class
specifier|public
specifier|abstract
class|class
name|ItemDelegate
block|{
specifier|protected
specifier|final
name|SessionDelegate
name|sessionDelegate
decl_stmt|;
comment|/** The underlying {@link org.apache.jackrabbit.oak.api.TreeLocation} of this item. */
specifier|private
name|TreeLocation
name|location
decl_stmt|;
comment|/**      * Revision on which this item is based. The underlying state of the item      * is re-resolved whenever the revision of the session does not match this      * revision.      */
specifier|private
name|int
name|revision
decl_stmt|;
name|ItemDelegate
parameter_list|(
name|SessionDelegate
name|sessionDelegate
parameter_list|,
name|TreeLocation
name|location
parameter_list|)
block|{
name|this
operator|.
name|sessionDelegate
operator|=
name|checkNotNull
argument_list|(
name|sessionDelegate
argument_list|)
expr_stmt|;
name|this
operator|.
name|location
operator|=
name|checkNotNull
argument_list|(
name|location
argument_list|)
expr_stmt|;
name|this
operator|.
name|revision
operator|=
name|sessionDelegate
operator|.
name|getRevision
argument_list|()
expr_stmt|;
block|}
comment|/**      * Get the name of this item      * @return oak name of this item      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getName
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|PathUtils
operator|.
name|getName
argument_list|(
name|getPath
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Get the path of this item      * @return oak path of this item      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getPath
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|getLocation
argument_list|()
operator|.
name|getPath
argument_list|()
return|;
comment|// never null
block|}
comment|/**      * Get the parent of this item or {@code null}.      * @return  parent of this item or {@code null} for root or if the parent      * is not accessible.      */
annotation|@
name|CheckForNull
specifier|public
name|NodeDelegate
name|getParent
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
return|return
name|NodeDelegate
operator|.
name|create
argument_list|(
name|sessionDelegate
argument_list|,
name|getLocation
argument_list|()
operator|.
name|getParent
argument_list|()
argument_list|)
return|;
block|}
comment|/**      * Determine whether this item is stale      * @return  {@code true} iff stale      */
specifier|public
name|boolean
name|isStale
parameter_list|()
block|{
name|Status
name|status
init|=
name|getLocationOrNull
argument_list|()
operator|.
name|getStatus
argument_list|()
decl_stmt|;
return|return
name|status
operator|==
name|Status
operator|.
name|REMOVED
operator|||
name|status
operator|==
literal|null
return|;
block|}
comment|/**      * Get the status of this item      * @return  {@link Status} of this item      */
annotation|@
name|Nonnull
specifier|public
name|Status
name|getStatus
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|Status
name|status
init|=
name|getLocation
argument_list|()
operator|.
name|getStatus
argument_list|()
decl_stmt|;
if|if
condition|(
name|status
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|()
throw|;
block|}
return|return
name|status
return|;
block|}
comment|/**      * Get the session delegate with which this item is associated      * @return  {@link SessionDelegate} to which this item belongs      */
annotation|@
name|Nonnull
specifier|public
specifier|final
name|SessionDelegate
name|getSessionDelegate
parameter_list|()
block|{
return|return
name|sessionDelegate
return|;
block|}
comment|/**      * The underlying {@link org.apache.jackrabbit.oak.api.TreeLocation} of this item.      * @return  tree location of the underlying item      * @throws InvalidItemStateException if the location points to a stale item      */
annotation|@
name|Nonnull
specifier|public
name|TreeLocation
name|getLocation
parameter_list|()
throws|throws
name|InvalidItemStateException
block|{
name|TreeLocation
name|location
init|=
name|getLocationOrNull
argument_list|()
decl_stmt|;
if|if
condition|(
name|location
operator|==
name|TreeLocation
operator|.
name|NULL
condition|)
block|{
throw|throw
operator|new
name|InvalidItemStateException
argument_list|(
literal|"Item is stale"
argument_list|)
throw|;
block|}
return|return
name|location
return|;
block|}
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
comment|// don't disturb the state: avoid resolving location
return|return
name|getClass
argument_list|()
operator|.
name|getSimpleName
argument_list|()
operator|+
literal|'['
operator|+
name|location
operator|.
name|getPath
argument_list|()
operator|+
literal|']'
return|;
block|}
comment|//------------------------------------------------------------< private>---
comment|/**      * The underlying {@link org.apache.jackrabbit.oak.api.TreeLocation} of this item.      * The location is only re-resolved when the revision of this item does not match      * the revision of the session.      * @return  tree location of the underlying item or {@code null} if stale.      */
annotation|@
name|CheckForNull
specifier|private
specifier|synchronized
name|TreeLocation
name|getLocationOrNull
parameter_list|()
block|{
if|if
condition|(
name|location
operator|!=
name|TreeLocation
operator|.
name|NULL
operator|&&
name|sessionDelegate
operator|.
name|getRevision
argument_list|()
operator|!=
name|revision
condition|)
block|{
name|location
operator|=
name|sessionDelegate
operator|.
name|getLocation
argument_list|(
name|location
operator|.
name|getPath
argument_list|()
argument_list|)
expr_stmt|;
name|revision
operator|=
name|sessionDelegate
operator|.
name|getRevision
argument_list|()
expr_stmt|;
block|}
return|return
name|location
return|;
block|}
block|}
end_class

end_unit

