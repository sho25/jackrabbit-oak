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
name|spi
operator|.
name|state
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
import|import static
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
name|state
operator|.
name|NodeStateUtils
operator|.
name|isHidden
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

begin_comment
comment|/**  * {@code NodeStateDiff} wrapper that passes only changes to non-hidden nodes and properties  * (i.e. ones whose names don't start with a colon) to the given delegate diff.  *  * @since Oak 0.9  */
end_comment

begin_class
specifier|public
class|class
name|VisibleDiff
implements|implements
name|NodeStateDiff
block|{
specifier|private
specifier|final
name|NodeStateDiff
name|diff
decl_stmt|;
annotation|@
name|Nonnull
specifier|public
specifier|static
name|NodeStateDiff
name|wrap
parameter_list|(
annotation|@
name|Nonnull
name|NodeStateDiff
name|diff
parameter_list|)
block|{
return|return
operator|new
name|VisibleDiff
argument_list|(
name|checkNotNull
argument_list|(
name|diff
argument_list|)
argument_list|)
return|;
block|}
specifier|public
name|VisibleDiff
parameter_list|(
name|NodeStateDiff
name|diff
parameter_list|)
block|{
name|this
operator|.
name|diff
operator|=
name|checkNotNull
argument_list|(
name|diff
argument_list|)
expr_stmt|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyAdded
parameter_list|(
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|diff
operator|.
name|propertyAdded
argument_list|(
name|after
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyChanged
parameter_list|(
name|PropertyState
name|before
parameter_list|,
name|PropertyState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|after
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|diff
operator|.
name|propertyChanged
argument_list|(
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|propertyDeleted
parameter_list|(
name|PropertyState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|before
operator|.
name|getName
argument_list|()
argument_list|)
condition|)
block|{
return|return
name|diff
operator|.
name|propertyDeleted
argument_list|(
name|before
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeAdded
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|diff
operator|.
name|childNodeAdded
argument_list|(
name|name
argument_list|,
name|after
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeChanged
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|,
name|NodeState
name|after
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|diff
operator|.
name|childNodeChanged
argument_list|(
name|name
argument_list|,
name|before
argument_list|,
name|after
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|childNodeDeleted
parameter_list|(
name|String
name|name
parameter_list|,
name|NodeState
name|before
parameter_list|)
block|{
if|if
condition|(
operator|!
name|isHidden
argument_list|(
name|name
argument_list|)
condition|)
block|{
return|return
name|diff
operator|.
name|childNodeDeleted
argument_list|(
name|name
argument_list|,
name|before
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|true
return|;
block|}
block|}
block|}
end_class

end_unit

