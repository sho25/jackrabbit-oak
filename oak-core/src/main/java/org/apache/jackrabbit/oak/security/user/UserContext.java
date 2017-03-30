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
name|user
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
name|plugins
operator|.
name|tree
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
name|spi
operator|.
name|security
operator|.
name|Context
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
name|user
operator|.
name|UserConstants
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
name|util
operator|.
name|TreeUtil
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
name|UserContext
implements|implements
name|Context
implements|,
name|UserConstants
block|{
specifier|private
specifier|static
specifier|final
name|Context
name|INSTANCE
init|=
operator|new
name|UserContext
argument_list|()
decl_stmt|;
specifier|private
name|UserContext
parameter_list|()
block|{     }
specifier|static
name|Context
name|getInstance
parameter_list|()
block|{
return|return
name|INSTANCE
return|;
block|}
comment|//------------------------------------------------------------< Context>---
annotation|@
name|Override
specifier|public
name|boolean
name|definesProperty
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|parent
parameter_list|,
annotation|@
name|Nonnull
name|PropertyState
name|property
parameter_list|)
block|{
name|String
name|propName
init|=
name|property
operator|.
name|getName
argument_list|()
decl_stmt|;
name|String
name|ntName
init|=
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|parent
argument_list|)
decl_stmt|;
if|if
condition|(
name|NT_REP_USER
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
operator|||
name|NT_REP_SYSTEM_USER
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
return|return
name|USER_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|propName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_GROUP
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
return|return
name|GROUP_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|propName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_PASSWORD
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
return|return
name|PWD_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|propName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_MEMBER_REFERENCES
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
return|return
name|REP_MEMBERS
operator|.
name|equals
argument_list|(
name|propName
argument_list|)
return|;
block|}
elseif|else
if|if
condition|(
name|NT_REP_MEMBERS
operator|.
name|equals
argument_list|(
name|ntName
argument_list|)
condition|)
block|{
return|return
literal|true
return|;
block|}
return|return
literal|false
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesContextRoot
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
name|definesTree
argument_list|(
name|tree
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesTree
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
name|String
name|ntName
init|=
name|TreeUtil
operator|.
name|getPrimaryTypeName
argument_list|(
name|tree
argument_list|)
decl_stmt|;
return|return
name|NT_NAMES
operator|.
name|contains
argument_list|(
name|ntName
argument_list|)
return|;
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesLocation
parameter_list|(
annotation|@
name|Nonnull
name|TreeLocation
name|location
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|location
operator|.
name|getTree
argument_list|()
decl_stmt|;
if|if
condition|(
name|tree
operator|!=
literal|null
operator|&&
name|location
operator|.
name|exists
argument_list|()
condition|)
block|{
name|PropertyState
name|p
init|=
name|location
operator|.
name|getProperty
argument_list|()
decl_stmt|;
return|return
operator|(
name|p
operator|==
literal|null
operator|)
condition|?
name|definesTree
argument_list|(
name|tree
argument_list|)
else|:
name|definesProperty
argument_list|(
name|tree
argument_list|,
name|p
argument_list|)
return|;
block|}
else|else
block|{
name|String
name|path
init|=
name|location
operator|.
name|getPath
argument_list|()
decl_stmt|;
name|String
name|name
init|=
name|Text
operator|.
name|getName
argument_list|(
name|path
argument_list|)
decl_stmt|;
comment|// NOTE: if none of the conditions below match, we are not able to
comment|// reliably determine if the specified location defines a user or
comment|// group node (missing node type information on non-existing location)
return|return
name|USER_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|||
name|GROUP_PROPERTY_NAMES
operator|.
name|contains
argument_list|(
name|name
argument_list|)
operator|||
name|path
operator|.
name|contains
argument_list|(
name|REP_MEMBERS
argument_list|)
operator|||
name|path
operator|.
name|contains
argument_list|(
name|REP_MEMBERS_LIST
argument_list|)
operator|||
name|path
operator|.
name|contains
argument_list|(
name|REP_PWD
argument_list|)
return|;
block|}
block|}
annotation|@
name|Override
specifier|public
name|boolean
name|definesInternal
parameter_list|(
annotation|@
name|Nonnull
name|Tree
name|tree
parameter_list|)
block|{
return|return
literal|false
return|;
block|}
block|}
end_class

end_unit

