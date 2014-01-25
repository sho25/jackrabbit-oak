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
name|observation
package|;
end_package

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
name|namepath
operator|.
name|NamePathMapper
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
name|commit
operator|.
name|CommitInfo
import|;
end_import

begin_comment
comment|/**  * Information shared by all events coming from a single commit.  */
end_comment

begin_class
specifier|final
class|class
name|EventContext
block|{
comment|/**      * Dummy session identifier used to identify external commits.      */
specifier|private
specifier|static
specifier|final
name|String
name|OAK_EXTERNAL
init|=
literal|"oak:external"
decl_stmt|;
specifier|private
specifier|final
name|NamePathMapper
name|mapper
decl_stmt|;
specifier|private
specifier|final
name|CommitInfo
name|info
decl_stmt|;
name|EventContext
parameter_list|(
name|NamePathMapper
name|mapper
parameter_list|,
name|CommitInfo
name|info
parameter_list|)
block|{
name|this
operator|.
name|mapper
operator|=
name|mapper
expr_stmt|;
if|if
condition|(
name|info
operator|!=
literal|null
condition|)
block|{
name|this
operator|.
name|info
operator|=
name|info
expr_stmt|;
block|}
else|else
block|{
comment|// Generate a dummy CommitInfo object to avoid extra null checks.
comment|// The current time is used as a rough estimate of the commit time.
name|this
operator|.
name|info
operator|=
operator|new
name|CommitInfo
argument_list|(
name|OAK_EXTERNAL
argument_list|,
literal|null
argument_list|,
literal|null
argument_list|)
expr_stmt|;
block|}
block|}
name|String
name|getJcrName
parameter_list|(
name|String
name|name
parameter_list|)
block|{
return|return
name|mapper
operator|.
name|getJcrName
argument_list|(
name|name
argument_list|)
return|;
block|}
name|String
name|getJcrPath
parameter_list|(
name|String
name|path
parameter_list|)
block|{
return|return
name|mapper
operator|.
name|getJcrPath
argument_list|(
name|path
argument_list|)
return|;
block|}
name|String
name|getUserID
parameter_list|()
block|{
return|return
name|info
operator|.
name|getUserId
argument_list|()
return|;
block|}
name|String
name|getUserData
parameter_list|()
block|{
return|return
name|info
operator|.
name|getMessage
argument_list|()
return|;
block|}
name|long
name|getDate
parameter_list|()
block|{
return|return
name|info
operator|.
name|getDate
argument_list|()
return|;
block|}
name|boolean
name|isExternal
parameter_list|()
block|{
return|return
name|info
operator|.
name|getSessionId
argument_list|()
operator|==
name|OAK_EXTERNAL
return|;
block|}
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
name|EventContext
condition|)
block|{
name|EventContext
name|that
init|=
operator|(
name|EventContext
operator|)
name|object
decl_stmt|;
return|return
name|info
operator|.
name|equals
argument_list|(
name|that
operator|.
name|info
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
name|info
operator|.
name|hashCode
argument_list|()
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
name|info
operator|.
name|toString
argument_list|()
return|;
block|}
block|}
end_class

end_unit

