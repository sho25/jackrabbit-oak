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
name|accesscontrol
package|;
end_package

begin_import
import|import
name|java
operator|.
name|security
operator|.
name|Principal
import|;
end_import

begin_import
import|import
name|java
operator|.
name|util
operator|.
name|Set
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlException
import|;
end_import

begin_import
import|import
name|javax
operator|.
name|jcr
operator|.
name|security
operator|.
name|AccessControlPolicy
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
name|api
operator|.
name|security
operator|.
name|principal
operator|.
name|PrincipalManager
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
name|nodetype
operator|.
name|ReadOnlyNodeTypeManager
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
name|AuthorizationConfiguration
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
name|accesscontrol
operator|.
name|ACE
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
name|accesscontrol
operator|.
name|AccessControlConstants
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
name|principal
operator|.
name|PrincipalImpl
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
name|xml
operator|.
name|ImportBehavior
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
name|xml
operator|.
name|ProtectedItemImporter
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|NotNull
import|;
end_import

begin_import
import|import
name|org
operator|.
name|jetbrains
operator|.
name|annotations
operator|.
name|Nullable
import|;
end_import

begin_comment
comment|/**  * Implementation specific access control utility methods  */
end_comment

begin_class
specifier|final
class|class
name|Util
implements|implements
name|AccessControlConstants
block|{
comment|/**      *  Private constructor to avoid instantiation      */
specifier|private
name|Util
parameter_list|()
block|{}
specifier|static
name|void
name|checkValidPrincipal
parameter_list|(
annotation|@
name|Nullable
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|PrincipalManager
name|principalManager
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|checkValidPrincipal
argument_list|(
name|principal
argument_list|,
name|principalManager
argument_list|,
name|ImportBehavior
operator|.
name|ABORT
argument_list|)
expr_stmt|;
block|}
specifier|static
name|boolean
name|checkValidPrincipal
parameter_list|(
annotation|@
name|Nullable
name|Principal
name|principal
parameter_list|,
annotation|@
name|NotNull
name|PrincipalManager
name|principalManager
parameter_list|,
name|int
name|importBehavior
parameter_list|)
throws|throws
name|AccessControlException
block|{
name|String
name|name
init|=
operator|(
name|principal
operator|==
literal|null
operator|)
condition|?
literal|null
else|:
name|principal
operator|.
name|getName
argument_list|()
decl_stmt|;
if|if
condition|(
name|name
operator|==
literal|null
operator|||
name|name
operator|.
name|isEmpty
argument_list|()
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Invalid principal "
operator|+
name|name
argument_list|)
throw|;
block|}
if|if
condition|(
name|importBehavior
operator|==
name|ImportBehavior
operator|.
name|BESTEFFORT
condition|)
block|{
return|return
literal|true
return|;
block|}
else|else
block|{
if|if
condition|(
operator|!
operator|(
name|principal
operator|instanceof
name|PrincipalImpl
operator|)
operator|&&
operator|!
name|principalManager
operator|.
name|hasPrincipal
argument_list|(
name|name
argument_list|)
condition|)
block|{
switch|switch
condition|(
name|importBehavior
condition|)
block|{
case|case
name|ImportBehavior
operator|.
name|ABORT
case|:
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Unknown principal "
operator|+
name|name
argument_list|)
throw|;
case|case
name|ImportBehavior
operator|.
name|IGNORE
case|:
return|return
literal|false
return|;
default|default:
throw|throw
operator|new
name|IllegalArgumentException
argument_list|(
literal|"Invalid import behavior "
operator|+
name|importBehavior
argument_list|)
throw|;
block|}
block|}
return|return
literal|true
return|;
block|}
block|}
specifier|static
name|void
name|checkValidPrincipals
parameter_list|(
annotation|@
name|Nullable
name|Set
argument_list|<
name|Principal
argument_list|>
name|principals
parameter_list|,
annotation|@
name|NotNull
name|PrincipalManager
name|principalManager
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
name|principals
operator|==
literal|null
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Valid principals expected. Found null."
argument_list|)
throw|;
block|}
for|for
control|(
name|Principal
name|principal
range|:
name|principals
control|)
block|{
name|checkValidPrincipal
argument_list|(
name|principal
argument_list|,
name|principalManager
argument_list|)
expr_stmt|;
block|}
block|}
specifier|static
name|boolean
name|isValidPolicy
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|AccessControlPolicy
name|policy
parameter_list|)
block|{
if|if
condition|(
name|policy
operator|instanceof
name|ACL
condition|)
block|{
name|String
name|path
init|=
operator|(
operator|(
name|ACL
operator|)
name|policy
operator|)
operator|.
name|getOakPath
argument_list|()
decl_stmt|;
return|return
operator|!
operator|(
operator|(
name|path
operator|==
literal|null
operator|&&
name|oakPath
operator|!=
literal|null
operator|)
operator|||
operator|(
name|path
operator|!=
literal|null
operator|&&
operator|!
name|path
operator|.
name|equals
argument_list|(
name|oakPath
argument_list|)
operator|)
operator|)
return|;
block|}
return|return
literal|false
return|;
block|}
specifier|static
name|void
name|checkValidPolicy
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|AccessControlPolicy
name|policy
parameter_list|)
throws|throws
name|AccessControlException
block|{
if|if
condition|(
operator|!
name|isValidPolicy
argument_list|(
name|oakPath
argument_list|,
name|policy
argument_list|)
condition|)
block|{
throw|throw
operator|new
name|AccessControlException
argument_list|(
literal|"Invalid access control policy "
operator|+
name|policy
argument_list|)
throw|;
block|}
block|}
specifier|static
name|boolean
name|isAccessControlled
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|,
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
block|{
name|String
name|mixinName
init|=
name|getMixinName
argument_list|(
name|oakPath
argument_list|)
decl_stmt|;
return|return
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|mixinName
argument_list|)
return|;
block|}
specifier|static
name|boolean
name|isACE
parameter_list|(
annotation|@
name|NotNull
name|Tree
name|tree
parameter_list|,
annotation|@
name|NotNull
name|ReadOnlyNodeTypeManager
name|ntMgr
parameter_list|)
block|{
return|return
name|tree
operator|.
name|exists
argument_list|()
operator|&&
name|ntMgr
operator|.
name|isNodeType
argument_list|(
name|tree
argument_list|,
name|NT_REP_ACE
argument_list|)
return|;
block|}
annotation|@
name|NotNull
specifier|static
name|String
name|getMixinName
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|)
block|{
return|return
operator|(
name|oakPath
operator|==
literal|null
operator|)
condition|?
name|MIX_REP_REPO_ACCESS_CONTROLLABLE
else|:
name|MIX_REP_ACCESS_CONTROLLABLE
return|;
block|}
annotation|@
name|NotNull
specifier|static
name|String
name|getAclName
parameter_list|(
annotation|@
name|Nullable
name|String
name|oakPath
parameter_list|)
block|{
return|return
operator|(
name|oakPath
operator|==
literal|null
operator|)
condition|?
name|REP_REPO_POLICY
else|:
name|REP_POLICY
return|;
block|}
comment|/**      * Create a valid name for the ACE node based on the entry and it's index.      *      * @param ace The access control entry.      * @param index The index of the entry in the list      * @return the name of the ACE node.      */
annotation|@
name|NotNull
specifier|static
name|String
name|generateAceName
parameter_list|(
annotation|@
name|NotNull
name|ACE
name|ace
parameter_list|,
name|int
name|index
parameter_list|)
block|{
name|String
name|hint
init|=
operator|(
name|ace
operator|.
name|isAllow
argument_list|()
operator|)
condition|?
literal|"allow"
else|:
literal|"deny"
decl_stmt|;
if|if
condition|(
name|index
operator|==
literal|0
condition|)
block|{
return|return
name|hint
return|;
block|}
else|else
block|{
return|return
name|hint
operator|+
name|index
return|;
block|}
block|}
specifier|static
name|int
name|getImportBehavior
parameter_list|(
name|AuthorizationConfiguration
name|config
parameter_list|)
block|{
name|String
name|importBehaviorStr
init|=
name|config
operator|.
name|getParameters
argument_list|()
operator|.
name|getConfigValue
argument_list|(
name|ProtectedItemImporter
operator|.
name|PARAM_IMPORT_BEHAVIOR
argument_list|,
name|ImportBehavior
operator|.
name|NAME_ABORT
argument_list|)
decl_stmt|;
return|return
name|ImportBehavior
operator|.
name|valueFromString
argument_list|(
name|importBehaviorStr
argument_list|)
return|;
block|}
block|}
end_class

end_unit

