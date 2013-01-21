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
name|Root
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
name|identifier
operator|.
name|IdentifierManager
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
name|ConfigurationParameters
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
name|AuthorizableType
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
name|spi
operator|.
name|security
operator|.
name|user
operator|.
name|util
operator|.
name|UserUtility
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
comment|/**  * Base class for {@link UserProvider} and {@link MembershipProvider}.  */
end_comment

begin_class
specifier|abstract
class|class
name|AuthorizableBaseProvider
implements|implements
name|UserConstants
block|{
specifier|final
name|ConfigurationParameters
name|config
decl_stmt|;
specifier|final
name|Root
name|root
decl_stmt|;
specifier|final
name|IdentifierManager
name|identifierManager
decl_stmt|;
name|AuthorizableBaseProvider
parameter_list|(
name|Root
name|root
parameter_list|,
name|ConfigurationParameters
name|config
parameter_list|)
block|{
name|this
operator|.
name|root
operator|=
name|checkNotNull
argument_list|(
name|root
argument_list|)
expr_stmt|;
name|this
operator|.
name|config
operator|=
name|checkNotNull
argument_list|(
name|config
argument_list|)
expr_stmt|;
name|this
operator|.
name|identifierManager
operator|=
operator|new
name|IdentifierManager
argument_list|(
name|root
argument_list|)
expr_stmt|;
block|}
annotation|@
name|CheckForNull
name|Tree
name|getByID
parameter_list|(
name|String
name|authorizableId
parameter_list|,
name|AuthorizableType
name|authorizableType
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|identifierManager
operator|.
name|getTree
argument_list|(
name|getContentID
argument_list|(
name|authorizableId
argument_list|)
argument_list|)
decl_stmt|;
if|if
condition|(
name|UserUtility
operator|.
name|isType
argument_list|(
name|tree
argument_list|,
name|authorizableType
argument_list|)
condition|)
block|{
return|return
name|tree
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|CheckForNull
name|Tree
name|getByPath
parameter_list|(
name|String
name|authorizableOakPath
parameter_list|)
block|{
name|Tree
name|tree
init|=
name|root
operator|.
name|getTree
argument_list|(
name|authorizableOakPath
argument_list|)
decl_stmt|;
if|if
condition|(
name|UserUtility
operator|.
name|isType
argument_list|(
name|tree
argument_list|,
name|AuthorizableType
operator|.
name|AUTHORIZABLE
argument_list|)
condition|)
block|{
return|return
name|tree
return|;
block|}
else|else
block|{
return|return
literal|null
return|;
block|}
block|}
annotation|@
name|Nonnull
name|String
name|getContentID
parameter_list|(
name|Tree
name|authorizableTree
parameter_list|)
block|{
return|return
name|identifierManager
operator|.
name|getIdentifier
argument_list|(
name|authorizableTree
argument_list|)
return|;
block|}
annotation|@
name|Nonnull
specifier|static
name|String
name|getContentID
parameter_list|(
name|String
name|authorizableId
parameter_list|)
block|{
return|return
name|IdentifierManager
operator|.
name|generateUUID
argument_list|(
name|authorizableId
operator|.
name|toLowerCase
argument_list|()
argument_list|)
return|;
block|}
block|}
end_class

end_unit

