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
name|commit
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
name|Objects
operator|.
name|toStringHelper
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
name|Objects
import|;
end_import

begin_comment
comment|/**  * Commit info instances associate some meta data with a commit.  */
end_comment

begin_class
specifier|public
specifier|final
class|class
name|CommitInfo
block|{
specifier|public
specifier|static
specifier|final
name|String
name|OAK_UNKNOWN
init|=
literal|"oak:unknown"
decl_stmt|;
comment|/**      * Empty commit information object. Used as a dummy object when no      * metadata is known (or needed) about a commit.      */
specifier|public
specifier|static
specifier|final
name|CommitInfo
name|EMPTY
init|=
operator|new
name|CommitInfo
argument_list|(
name|OAK_UNKNOWN
argument_list|,
name|OAK_UNKNOWN
argument_list|,
literal|null
argument_list|)
decl_stmt|;
specifier|private
specifier|final
name|String
name|sessionId
decl_stmt|;
specifier|private
specifier|final
name|String
name|userId
decl_stmt|;
specifier|private
specifier|final
name|String
name|message
decl_stmt|;
specifier|private
specifier|final
name|long
name|date
init|=
name|System
operator|.
name|currentTimeMillis
argument_list|()
decl_stmt|;
specifier|private
specifier|final
name|String
name|path
decl_stmt|;
comment|/**      * Creates a commit info for the given session and user.      *      * @param sessionId session identifier      * @param userId The user id.      * @param message message attached to this commit, or {@code null}      */
specifier|public
name|CommitInfo
parameter_list|(
annotation|@
name|Nonnull
name|String
name|sessionId
parameter_list|,
annotation|@
name|Nullable
name|String
name|userId
parameter_list|,
annotation|@
name|Nullable
name|String
name|message
parameter_list|)
block|{
name|this
argument_list|(
name|sessionId
argument_list|,
name|userId
argument_list|,
name|message
argument_list|,
literal|"/"
argument_list|)
expr_stmt|;
block|}
specifier|public
name|CommitInfo
parameter_list|(
annotation|@
name|Nonnull
name|String
name|sessionId
parameter_list|,
annotation|@
name|Nullable
name|String
name|userId
parameter_list|,
annotation|@
name|Nullable
name|String
name|message
parameter_list|,
annotation|@
name|Nonnull
name|String
name|path
parameter_list|)
block|{
name|this
operator|.
name|sessionId
operator|=
name|checkNotNull
argument_list|(
name|sessionId
argument_list|)
expr_stmt|;
name|this
operator|.
name|userId
operator|=
operator|(
name|userId
operator|==
literal|null
operator|)
condition|?
name|OAK_UNKNOWN
else|:
name|userId
expr_stmt|;
name|this
operator|.
name|message
operator|=
name|message
expr_stmt|;
name|this
operator|.
name|path
operator|=
name|checkNotNull
argument_list|(
name|path
argument_list|)
expr_stmt|;
block|}
comment|/**      * @return  id of the committing session      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getSessionId
parameter_list|()
block|{
return|return
name|sessionId
return|;
block|}
comment|/**      * @return  user id of the committing user      */
annotation|@
name|Nonnull
specifier|public
name|String
name|getUserId
parameter_list|()
block|{
return|return
name|userId
return|;
block|}
comment|/**      * @return message attached to this commit      */
annotation|@
name|CheckForNull
specifier|public
name|String
name|getMessage
parameter_list|()
block|{
return|return
name|message
return|;
block|}
comment|/**      * @return  time stamp      */
specifier|public
name|long
name|getDate
parameter_list|()
block|{
return|return
name|date
return|;
block|}
specifier|public
name|String
name|getPath
parameter_list|()
block|{
return|return
name|path
return|;
block|}
comment|//------------------------------------------------------------< Object>--
annotation|@
name|Override
specifier|public
name|boolean
name|equals
parameter_list|(
name|Object
name|object
parameter_list|)
block|{
if|if
condition|(
name|this
operator|==
name|object
condition|)
block|{
return|return
literal|true
return|;
block|}
elseif|else
if|if
condition|(
name|object
operator|instanceof
name|CommitInfo
condition|)
block|{
name|CommitInfo
name|that
init|=
operator|(
name|CommitInfo
operator|)
name|object
decl_stmt|;
return|return
name|sessionId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|sessionId
argument_list|)
operator|&&
name|userId
operator|.
name|equals
argument_list|(
name|that
operator|.
name|userId
argument_list|)
operator|&&
name|Objects
operator|.
name|equal
argument_list|(
name|this
operator|.
name|message
argument_list|,
name|that
operator|.
name|message
argument_list|)
operator|&&
name|this
operator|.
name|date
operator|==
name|that
operator|.
name|date
operator|&&
name|path
operator|.
name|equals
argument_list|(
name|that
operator|.
name|path
argument_list|)
return|;
block|}
else|else
block|{
return|return
literal|false
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
name|Objects
operator|.
name|hashCode
argument_list|(
name|sessionId
argument_list|,
name|userId
argument_list|,
name|message
argument_list|,
name|date
argument_list|,
name|path
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
name|toStringHelper
argument_list|(
name|this
argument_list|)
operator|.
name|omitNullValues
argument_list|()
operator|.
name|add
argument_list|(
literal|"sessionId"
argument_list|,
name|sessionId
argument_list|)
operator|.
name|add
argument_list|(
literal|"userId"
argument_list|,
name|userId
argument_list|)
operator|.
name|add
argument_list|(
literal|"userData"
argument_list|,
name|message
argument_list|)
operator|.
name|add
argument_list|(
literal|"date"
argument_list|,
name|date
argument_list|)
operator|.
name|add
argument_list|(
literal|"path"
argument_list|,
name|path
argument_list|)
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

