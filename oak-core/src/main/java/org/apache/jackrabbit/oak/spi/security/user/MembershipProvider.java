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
name|spi
operator|.
name|security
operator|.
name|user
package|;
end_package

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
name|Tree
import|;
end_import

begin_comment
comment|/**  * MembershipProvider... TODO  */
end_comment

begin_interface
specifier|public
interface|interface
name|MembershipProvider
block|{
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembership
parameter_list|(
name|String
name|authorizableId
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
function_decl|;
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembership
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
function_decl|;
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembers
parameter_list|(
name|String
name|groupId
parameter_list|,
name|Type
name|authorizableType
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
function_decl|;
annotation|@
name|Nonnull
name|Iterator
argument_list|<
name|String
argument_list|>
name|getMembers
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|Type
name|authorizableType
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
function_decl|;
name|boolean
name|isMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|Tree
name|authorizableTree
parameter_list|,
name|boolean
name|includeInherited
parameter_list|)
function_decl|;
name|boolean
name|addMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|Tree
name|newMemberTree
parameter_list|)
function_decl|;
name|boolean
name|removeMember
parameter_list|(
name|Tree
name|groupTree
parameter_list|,
name|Tree
name|memberTree
parameter_list|)
function_decl|;
block|}
end_interface

end_unit

