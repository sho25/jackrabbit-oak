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
name|core
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
name|api
operator|.
name|PropertyState
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
name|Type
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

begin_comment
comment|/**  * Instances of this class represent trees that are inaccessible because  * of the respective content would potentially be internal (hidden).  *<p>  * Calls to any of the mutator methods on this class throws an  * {@code IllegalStateException}.  */
end_comment

begin_class
specifier|public
class|class
name|HiddenTree
implements|implements
name|Tree
block|{
specifier|private
specifier|final
name|Tree
name|parent
decl_stmt|;
specifier|private
specifier|final
name|String
name|name
decl_stmt|;
specifier|public
name|HiddenTree
parameter_list|(
name|Tree
name|parent
parameter_list|,
name|String
name|name
parameter_list|)
block|{
name|this
operator|.
name|parent
operator|=
name|parent
expr_stmt|;
name|this
operator|.
name|name
operator|=
name|name
expr_stmt|;
block|}
comment|//------------------------------------------------------------< Object>---
annotation|@
name|Override
specifier|public
name|String
name|toString
parameter_list|()
block|{
return|return
name|getPath
argument_list|()
operator|+
literal|": {}"
return|;
block|}
comment|//------------------------------------------------------------< Tree>---
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getName
parameter_list|()
block|{
return|return
name|name
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|isRoot
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|PathUtils
operator|.
name|concat
argument_list|(
name|parent
operator|.
name|getPath
argument_list|()
argument_list|,
name|name
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Status
name|getStatus
parameter_list|()
block|{
return|return
name|Status
operator|.
name|UNCHANGED
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|exists
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Tree
name|getParent
parameter_list|()
block|{
return|return
name|parent
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|PropertyState
name|getProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
annotation|@
name|CheckForNull
specifier|public
name|Status
name|getPropertyStatus
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
literal|null
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getPropertyCount
parameter_list|()
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|?
extends|extends
name|PropertyState
argument_list|>
name|getProperties
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Tree
name|getChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
operator|new
name|HiddenTree
argument_list|(
name|this
argument_list|,
name|checkNotNull
argument_list|(
name|name
argument_list|)
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|hasChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|long
name|getChildrenCount
parameter_list|(
name|long
name|max
parameter_list|)
block|{
return|return
literal|0
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Iterable
argument_list|<
name|Tree
argument_list|>
name|getChildren
parameter_list|()
block|{
return|return
name|Collections
operator|.
name|emptyList
argument_list|()
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|remove
parameter_list|()
block|{
return|return
literal|false
return|;
block|}
annotation|@
name|Nonnull
annotation|@
name|Override
specifier|public
name|Tree
name|addChild
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
throw|throw
name|nonExistingTree
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setOrderableChildren
parameter_list|(
name|boolean
name|enable
parameter_list|)
block|{
throw|throw
name|nonExistingTree
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|orderBefore
parameter_list|(
annotation|@
name|Nullable
name|String
name|name
parameter_list|)
block|{
throw|throw
name|nonExistingTree
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|setProperty
parameter_list|(
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
throw|throw
name|nonExistingTree
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|T
name|value
parameter_list|)
block|{
throw|throw
name|nonExistingTree
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
parameter_list|<
name|T
parameter_list|>
name|void
name|setProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|,
annotation|@
name|Nonnull
name|T
name|value
parameter_list|,
annotation|@
name|Nonnull
name|Type
argument_list|<
name|T
argument_list|>
name|type
parameter_list|)
block|{
throw|throw
name|nonExistingTree
argument_list|()
throw|;
block|}
annotation|@
name|Override
specifier|public
name|void
name|removeProperty
parameter_list|(
annotation|@
name|Nonnull
name|String
name|name
parameter_list|)
block|{
throw|throw
name|nonExistingTree
argument_list|()
throw|;
block|}
specifier|private
specifier|static
name|IllegalStateException
name|nonExistingTree
parameter_list|()
block|{
return|return
operator|new
name|IllegalStateException
argument_list|(
literal|"This tree does not exist"
argument_list|)
return|;
block|}
block|}
end_class

end_unit

