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
name|security
operator|.
name|authorization
operator|.
name|permission
package|;
end_package

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
name|Objects
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
name|commons
operator|.
name|PathUtils
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|permission
operator|.
name|PermissionConstants
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
name|spi
operator|.
name|security
operator|.
name|authorization
operator|.
name|restriction
operator|.
name|RestrictionPattern
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
name|spi
operator|.
name|security
operator|.
name|privilege
operator|.
name|PrivilegeBits
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
name|util
operator|.
name|Text
import|;
end_import

begin_class
specifier|final
class|class
name|PermissionEntry
implements|implements
name|Comparable
argument_list|<
name|PermissionEntry
argument_list|>
implements|,
name|PermissionConstants
block|{
comment|/**      * flag controls if this is an allow or deny entry      */
specifier|final
name|boolean
name|isAllow
decl_stmt|;
comment|/**      * the privilege bits      */
specifier|final
name|PrivilegeBits
name|privilegeBits
decl_stmt|;
comment|/**      * the index (order) of the original ACE in the ACL.      */
specifier|private
specifier|final
name|int
name|index
decl_stmt|;
comment|/**      * the access controlled (node) path      */
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
comment|/**      * the restriction pattern for this entry      */
specifier|final
name|RestrictionPattern
name|restriction
decl_stmt|;
name|PermissionEntry
parameter_list|(
annotation|@
name|Nonnull
name|String
name|path
parameter_list|,
name|boolean
name|isAllow
parameter_list|,
name|int
name|index
parameter_list|,
annotation|@
name|Nonnull
name|PrivilegeBits
name|privilegeBits
parameter_list|,
annotation|@
name|Nonnull
name|RestrictionPattern
name|restriction
parameter_list|)
block|{
name|this
operator|.
name|path
operator|=
name|path
expr_stmt|;
name|this
operator|.
name|isAllow
operator|=
name|isAllow
expr_stmt|;
name|this
operator|.
name|index
operator|=
name|index
expr_stmt|;
name|this
operator|.
name|privilegeBits
operator|=
name|privilegeBits
expr_stmt|;
name|this
operator|.
name|restriction
operator|=
name|restriction
expr_stmt|;
block|}
name|boolean
name|matches
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|,
annotation|@
name|Nullable
name|PropertyState
name|property
parameter_list|)
block|{
return|return
name|restriction
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
name|restriction
operator|.
name|matches
argument_list|(
name|tree
argument_list|,
name|property
argument_list|)
return|;
block|}
name|boolean
name|matches
parameter_list|(
annotation|@
name|Nonnull
name|String
name|treePath
parameter_list|)
block|{
return|return
name|restriction
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
name|restriction
operator|.
name|matches
argument_list|(
name|treePath
argument_list|)
return|;
block|}
name|boolean
name|matches
parameter_list|()
block|{
return|return
name|restriction
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
name|restriction
operator|.
name|matches
argument_list|()
return|;
block|}
name|boolean
name|matchesParent
parameter_list|(
annotation|@
name|Nonnull
name|String
name|parentPath
parameter_list|)
block|{
return|return
name|Text
operator|.
name|isDescendantOrEqual
argument_list|(
name|path
argument_list|,
name|parentPath
argument_list|)
operator|&&
operator|(
name|restriction
operator|==
name|RestrictionPattern
operator|.
name|EMPTY
operator|||
name|restriction
operator|.
name|matches
argument_list|(
name|parentPath
argument_list|)
operator|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|compareTo
parameter_list|(
annotation|@
name|Nonnull
name|PermissionEntry
name|pe
parameter_list|)
block|{
if|if
condition|(
name|Objects
operator|.
name|equal
argument_list|(
name|path
argument_list|,
name|pe
operator|.
name|path
argument_list|)
condition|)
block|{
comment|// reverse order
if|if
condition|(
name|index
operator|==
name|pe
operator|.
name|index
condition|)
block|{
return|return
literal|0
return|;
block|}
elseif|else
if|if
condition|(
name|index
operator|<
name|pe
operator|.
name|index
condition|)
block|{
return|return
literal|1
return|;
block|}
else|else
block|{
return|return
operator|-
literal|1
return|;
block|}
block|}
else|else
block|{
specifier|final
name|int
name|depth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|path
argument_list|)
decl_stmt|;
specifier|final
name|int
name|otherDepth
init|=
name|PathUtils
operator|.
name|getDepth
argument_list|(
name|pe
operator|.
name|path
argument_list|)
decl_stmt|;
if|if
condition|(
name|depth
operator|==
name|otherDepth
condition|)
block|{
return|return
name|path
operator|.
name|compareTo
argument_list|(
name|pe
operator|.
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
operator|(
name|depth
operator|<
name|otherDepth
operator|)
condition|?
literal|1
else|:
operator|-
literal|1
return|;
block|}
block|}
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
operator|instanceof
name|PermissionEntry
condition|)
block|{
name|PermissionEntry
name|that
init|=
operator|(
name|PermissionEntry
operator|)
name|o
decl_stmt|;
return|return
name|index
operator|==
name|that
operator|.
name|index
operator|&&
name|isAllow
operator|==
name|that
operator|.
name|isAllow
operator|&&
name|privilegeBits
operator|.
name|equals
argument_list|(
name|that
operator|.
name|privilegeBits
argument_list|)
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|that
operator|.
name|path
argument_list|)
operator|&&
name|restriction
operator|.
name|equals
argument_list|(
name|that
operator|.
name|restriction
argument_list|)
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|int
name|hashCode
parameter_list|()
block|{
return|return
name|Objects
operator|.
name|hashCode
argument_list|(
name|privilegeBits
argument_list|,
name|index
argument_list|,
name|path
argument_list|,
name|isAllow
argument_list|,
name|restriction
argument_list|)
return|;
block|}
block|}
end_class

end_unit

